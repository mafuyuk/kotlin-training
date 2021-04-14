package enqueue

import java.time.Instant
import java.time.format.DateTimeFormatter
import com.squareup.moshi.Moshi
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder
import com.amazonaws.services.stepfunctions.model.ExecutionDoesNotExistException
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest
import com.amazonaws.services.stepfunctions.model.StopExecutionRequest

data class RawPush(
        val PushId: String,
        val PublishedAt: Long? = null,
        val ScheduleFor: Long? = null
)

class SendPush(
        val PushId: String,
        val ScheduleFor: String? = null,
        val PublishedAt: String,
        val ChoiceState: Int, // 1: 即時プッシュ, 2: スケジュールプッシュ
        val Message: Message
)

class Message(
        val default: String
)

class Handler : RequestHandler<DynamodbEvent, String> {
    companion object {
        private val STATE_MACHINE_ARN = System.getenv("STATE_MACHINE_ARN")
        private val EXECUTION_ARN_PREFIX = System.getenv("EXECUTION_ARN_PREFIX")
    }

    private val sfClient = AWSStepFunctionsClientBuilder.standard().build()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    override fun handleRequest(input: DynamodbEvent, context: Context): String {
        input.records.map { record ->
            when (record.eventName) {
                "INSERT" -> {
                    println("INSERT")
                    val rawPush = newRawPush(record.dynamodb.newImage)
                    println(rawPush)

                    // step functionへのenqueue
                    val nowUnixTime :Long = Instant.now().getEpochSecond()
                    if (rawPush.ScheduleFor != null && rawPush.ScheduleFor < nowUnixTime) {
                        // 配信時刻が過去のため配信はしない
                        return "failed"
                    }

                    val sendPush: SendPush
                    if (rawPush.ScheduleFor != null) {
                        // スケジュールプッシュを行う
                        sendPush = SendPush(
                                PushId = rawPush.PushId,
                                ScheduleFor = unixtimeToUTCTimestamp(rawPush.ScheduleFor),
                                PublishedAt = rawPush.ScheduleFor.toString(),
                                ChoiceState = 2,
                                Message = Message("test schedule push")
                        )
                    } else {
                        // 即時にプッシュを行う
                        sendPush = SendPush(
                                PushId = rawPush.PushId,
                                PublishedAt = nowUnixTime.toString(),
                                ChoiceState = 1,
                                Message = Message("test push")
                        )
                    }

                    // step function実行
                    startExecution(sendPush)

                }
                "REMOVE" -> {
                    println("REMOVE")
                    val rawPush = newRawPush(record.dynamodb.oldImage)
                    println(rawPush)

                    // 未配信の場合はstep functionの操作を止める
                    if (rawPush.PublishedAt == null) {
                        // step functionの操作を止める
                        stopExecution(rawPush)
                    }
                }
            }
        }
        return "success"
    }

    private fun newRawPush(image: Map<String, AttributeValue>): RawPush {
        return RawPush(
                PushId = image.get("PushId")!!.getS(),
                PublishedAt = image.get("PublishedAt")?.getN()?.toLong(),
                ScheduleFor = image.get("ScheduleFor")?.getN()?.toLong()
        )
    }

    private fun unixtimeToUTCTimestamp(unixtime: Long): String {
        return DateTimeFormatter.ISO_INSTANT
                .format(Instant.ofEpochSecond(unixtime))
    }

    private fun stopExecution(rawPush: RawPush) {
        println("exec stopExecution")
        try {
            sfClient.stopExecution(
                    StopExecutionRequest().withExecutionArn(EXECUTION_ARN_PREFIX + rawPush.PushId)
            )
        } catch (e: ExecutionDoesNotExistException) {
            println("throw Exception when stopExecution")
        }
    }

    private fun startExecution(push: SendPush) {
        println("exec startExecution")

        val decodeJson = moshi.adapter(SendPush::class.java).toJson(push)
        println(decodeJson)

        sfClient.startExecution(
                StartExecutionRequest()
                        .withStateMachineArn(STATE_MACHINE_ARN)
                        .withInput(decodeJson)
                        .withName(push.PushId)
        )
    }
}


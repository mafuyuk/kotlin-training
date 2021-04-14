package push

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler


class Handler : RequestHandler<String,String> {
    override fun handleRequest(input :String, context :Context) :String {
        return "Hello world."
    }
}

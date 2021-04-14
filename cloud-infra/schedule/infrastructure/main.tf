terraform {
  required_version = "0.12.9"
}

provider "aws" {
  version                 = "~> 2.7"
  region                  = "ap-northeast-1"
  shared_credentials_file = "~/.aws/credentials"
  profile                 = "dummy"
}
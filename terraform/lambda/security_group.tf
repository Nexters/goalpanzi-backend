locals {
  api_lambda_security_group_name = "goalpanzi-api-lambda-sg"
}

resource "aws_security_group" "api_lambda_sg" {
  name   = local.api_lambda_security_group_name
  vpc_id = var.vpc_id

  # outbound
  egress {
    protocol  = "-1"
    from_port = 0
    to_port   = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = local.api_lambda_security_group_name
  }
}
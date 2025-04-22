locals {
  rds_mysql_sg_name = "goalpanzi-mysql-sg"
}

resource "aws_security_group" "mysql_sg" {
  name   = local.rds_mysql_sg_name
  vpc_id = var.vpc_id

  # outbound
  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # inbound
  ingress {
    from_port = var.mysql_port
    to_port   = var.mysql_port
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = local.rds_mysql_sg_name
  }
}
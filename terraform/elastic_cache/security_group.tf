locals {
  elasticcache_redis_sg = "goalpanzi-redis-sg"
}

resource "aws_security_group" "redis_sg" {
  name   = local.elasticcache_redis_sg
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
    from_port = var.redis_port
    to_port   = var.redis_port
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = local.elasticcache_redis_sg
  }
}
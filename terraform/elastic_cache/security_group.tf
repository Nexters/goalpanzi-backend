resource "aws_security_group" "redis_sg" {
  name   = "goalpanzi-redis-sg"
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
    Name = "goalpanzi-redis-sg"
  }
}
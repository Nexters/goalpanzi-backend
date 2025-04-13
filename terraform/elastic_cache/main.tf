resource "aws_elasticache_cluster" "redis" {
  cluster_id           = "goalpanzi-redis"
  engine               = "redis"
  engine_version       = "7.1"
  node_type            = "cache.t4g.micro"
  num_cache_nodes      = 1
  port                 = var.redis_port
  parameter_group_name = "default.redis7"
  subnet_group_name    = aws_elasticache_subnet_group.elasticcache_subnet_group.name
  security_group_ids = [aws_security_group.redis_sg.id]
  availability_zone    = "ap-northeast-2a"

  tags = {
    Name = "goalpanzi-elasticcache-redis"
  }
}
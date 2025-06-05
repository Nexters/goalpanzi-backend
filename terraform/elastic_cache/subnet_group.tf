locals {
  elasticcache_subnet_group_name = "goalpanzi-elasticcache-subnet-group"
}

resource "aws_elasticache_subnet_group" "elasticcache_subnet_group" {
  name       = local.elasticcache_subnet_group_name
  subnet_ids = var.private_subnet_ids

  tags = {
    Name = local.elasticcache_subnet_group_name
  }
}
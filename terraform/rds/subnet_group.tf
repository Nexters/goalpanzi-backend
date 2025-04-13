locals {
  db_subnet_group_name = "goalpanzi-rds-subnet-group"
}

resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = local.db_subnet_group_name
  subnet_ids = var.private_subnet_ids

  tags = {
    Name = local.db_subnet_group_name
  }
}
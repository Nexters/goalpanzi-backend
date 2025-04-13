resource "aws_db_instance" "mysql" {
  identifier           = "goalpanzi-mysql"
  engine               = "mysql"
  engine_version       = "8.0"
  instance_class       = "db.t3.micro"
  allocated_storage    = 20
  db_name              = var.mysql_db_name
  username             = var.mysql_username
  password             = var.mysql_password
  port                 = var.mysql_port
  vpc_security_group_ids = [aws_security_group.mysql_sg.id]
  db_subnet_group_name = aws_db_subnet_group.rds_subnet_group.name
  skip_final_snapshot  = true
  publicly_accessible  = false
  multi_az             = false

  tags = {
    Name = "goalpanzi-rds-mysql"
  }
}
output "mysql_db_name" {
  value = aws_db_instance.mysql.db_name
}

output "mysql_username" {
  value = aws_db_instance.mysql.username
}

output "mysql_password" {
  value = aws_db_instance.mysql.password
}

output "mysql_port" {
  value = aws_db_instance.mysql.port
}
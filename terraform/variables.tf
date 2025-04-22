variable "region" {
  type        = string
  default     = "ap-northeast-2"
  description = "AWS region"
}

# Credentials
variable "access_key" {
  sensitive   = true
  description = "AWS access key"
}

variable "secret_key" {
  sensitive   = true
  description = "AWS secret key"
}

# SSH
variable "ssh_port" {
  sensitive   = true
  description = "SSH port"
}

# RDS
variable "mysql_db_name" {
  sensitive   = true
  type        = string
  default     = ""
  description = "MySQL Database Name"
}

variable "mysql_username" {
  sensitive   = true
  type        = string
  default     = ""
  description = "MySQL Database Username"
}

variable "mysql_password" {
  sensitive   = true
  type        = string
  default     = ""
  description = "MySQL Database Password"
}

variable "mysql_port" {
  type        = number
  default     = 3306
  description = "MySQL Port"
}

# Elastic Cache
variable "redis_port" {
  type    = number
  default = 6379
}

# SSL Certificate
variable "ssl_certificate" {
  sensitive   = true
  type        = string
  default     = ""
  description = ""
}

variable "ssl_certificate_key" {
  sensitive   = true
  type        = string
  default     = ""
  description = ""
}
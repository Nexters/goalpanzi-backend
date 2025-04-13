variable "vpc_cidr" {
  type        = string
  default     = "10.0.0.0/24"
  description = "VPC CIDR"
}

variable "azs" {
  type = list(string)
  default = ["ap-northeast-2a", "ap-northeast-2c"]
  description = "The List of Availability Zone"
}

variable "mysql_port" {
}

variable "redis_port" {
}
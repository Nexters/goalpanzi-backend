variable "ami_ubuntu_22_04" {
  type        = string
  default     = "ami-05a7f3469a7653972"
  description = "Ubuntu Server 22.04 LTS AMI"
}

variable "ssh_port" {}

variable "mysql_port" {}

variable "redis_port" {}

variable "vpc_id" {}

variable "public_subnet_id" {}
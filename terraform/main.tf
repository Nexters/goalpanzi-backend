terraform {
  required_version = ">= 1.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.54.1"
    }
  }
}

provider "aws" {
  region     = var.region
  access_key = var.access_key
  secret_key = var.secret_key
}

module "iam" {
  source = "./iam"
}

module "budget" {
  source = "./budget"
}

module "vpc" {
  source     = "./vpc"
  ssh_port   = var.ssh_port
  mysql_port = var.mysql_port
  redis_port = var.redis_port
}

module "ec2" {
  source           = "./ec2"
  ssh_port         = var.ssh_port
  vpc_id           = module.vpc.vpc_id
  public_subnet_id = module.vpc.public_subnet_id
  mysql_port       = var.mysql_port
  redis_port       = var.redis_port
}

module "s3" {
  source = "./s3"
}

module "rds" {
  source             = "./rds"
  mysql_db_name      = var.mysql_db_name
  mysql_username     = var.mysql_username
  mysql_password     = var.mysql_password
  mysql_port         = var.mysql_port
  vpc_id             = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnet_ids
}

module "elastic_cache" {
  source             = "./elastic_cache"
  vpc_id             = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnet_ids
  redis_port         = var.redis_port
}
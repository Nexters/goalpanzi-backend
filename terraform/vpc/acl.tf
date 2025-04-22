resource "aws_network_acl" "public" {
  vpc_id = aws_vpc.vpc.id

  # outbound
  egress {
    rule_no    = 100
    action     = "allow"
    protocol   = "-1"
    from_port  = 0
    to_port    = 0
    cidr_block = "0.0.0.0/0"
  }

  # inbound
  ingress {
    rule_no    = 100
    action     = "allow"
    protocol   = "tcp"
    from_port  = var.ssh_port
    to_port    = var.ssh_port
    cidr_block = "0.0.0.0/0"
  }

  ingress {
    rule_no    = 110
    action     = "allow"
    protocol   = "tcp"
    from_port  = var.mysql_port
    to_port    = var.mysql_port
    cidr_block = "0.0.0.0/0"
  }

  ingress {
    rule_no    = 120
    action     = "allow"
    protocol   = "tcp"
    from_port  = var.redis_port
    to_port    = var.redis_port
    cidr_block = "0.0.0.0/0"
  }

  ingress {
    rule_no    = 130
    action     = "allow"
    protocol   = "tcp"
    from_port  = 80
    to_port    = 80
    cidr_block = "0.0.0.0/0"
  }

  ingress {
    rule_no    = 140
    action     = "allow"
    protocol   = "tcp"
    from_port  = 443
    to_port    = 443
    cidr_block = "0.0.0.0/0"
  }

  ingress {
    rule_no    = 200
    action     = "allow"
    protocol   = "tcp"
    from_port  = 1024
    to_port    = 65535
    cidr_block = "0.0.0.0/0"
  }
}

resource "aws_network_acl_association" "public" {
  count = length(var.azs)
  network_acl_id = aws_network_acl.public.id
  subnet_id      = aws_subnet.public[count.index].id
}
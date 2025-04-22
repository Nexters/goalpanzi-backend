resource "aws_instance" "api" {
  ami                         = var.ami_ubuntu_22_04
  instance_type               = "t2.micro"
  subnet_id                   = var.public_subnet_id
  vpc_security_group_ids = [aws_security_group.ec2_sg.id]
  associate_public_ip_address = false
  key_name                    = aws_key_pair.api.key_name

  tags = {
    Name = "goalpanzi-api-ec2"
  }
}

resource "aws_key_pair" "api" {
  key_name = "goalpanzi-api-ec2-pem"
  public_key = file("~/.ssh/id_rsa.pub")
}

resource "aws_eip" "api" {
  domain = "vpc"

  tags = {
    Name = "goalpanzi-api-eip"
  }
}

resource "aws_eip_association" "api" {
  instance_id   = aws_instance.api.id
  allocation_id = aws_eip.api.id
}
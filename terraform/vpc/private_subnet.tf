resource "aws_subnet" "private" {
  vpc_id = aws_vpc.vpc.id

  count = length(var.azs)
  cidr_block = cidrsubnet(aws_vpc.vpc.cidr_block, 4, count.index + length(var.azs))
  availability_zone = var.azs[count.index]

  tags = {
    Name = "goalpanzi-private-${count.index + 1}"
  }
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "goalpanzi-private-rt"
  }
}
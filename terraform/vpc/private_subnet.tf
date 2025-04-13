resource "aws_subnet" "private" {
  vpc_id = aws_vpc.vpc.id

  count = length(var.azs)
  cidr_block = cidrsubnet(aws_vpc.vpc.cidr_block, 4, count.index + length(var.azs))
  availability_zone = var.azs[count.index]

  tags = {
    Name = "goalpanzi-private-${count.index + 1}"
  }
}

resource "aws_nat_gateway" "nat" {
  subnet_id     = aws_subnet.public[0].id
  allocation_id = aws_eip.nat.id
  depends_on = [aws_internet_gateway.igw]

  tags = {
    Name = "goalpanzi-nat-gateway"
  }
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "goalpanzi-private-rt"
  }
}

resource "aws_route" "private_nat_access" {
  route_table_id         = aws_route_table.private.id
  gateway_id             = aws_nat_gateway.nat.id
  destination_cidr_block = "0.0.0.0/0"
}

resource "aws_route_table_association" "private" {
  route_table_id = aws_route_table.private.id

  count = length(var.azs)
  subnet_id = aws_subnet.private[count.index].id
}
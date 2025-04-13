resource "aws_subnet" "public" {
  vpc_id = aws_vpc.vpc.id

  count = length(var.azs)
  cidr_block = cidrsubnet(aws_vpc.vpc.cidr_block, 4, count.index)
  availability_zone = var.azs[count.index]

  tags = {
    Name = "goalpanzi-public-${count.index + 1}"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "goalpanzi-public-rt"
  }
}

resource "aws_route" "internet_access" {
  route_table_id         = aws_route_table.public.id
  gateway_id             = aws_internet_gateway.igw.id
  destination_cidr_block = "0.0.0.0/0"
}

resource "aws_route_table_association" "public" {
  route_table_id = aws_route_table.public.id

  count = length(var.azs)
  subnet_id = aws_subnet.public[count.index].id
}
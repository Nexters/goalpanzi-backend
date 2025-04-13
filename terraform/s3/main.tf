resource "aws_s3_bucket" "s3" {
  bucket = "goalpanzi-bucket"

  tags = {
    Name = "goalpanzi-s3"
  }
}
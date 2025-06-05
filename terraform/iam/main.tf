resource "aws_iam_user" "admin" {
  name = "goalpanzi-team-admin"
  path = "/"
}

resource "aws_iam_user_policy_attachment" "admin_access" {
  policy_arn = "arn:aws:iam::aws:policy/AdministratorAccess"
  user       = aws_iam_user.admin.name
}
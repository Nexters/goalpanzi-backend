resource "aws_budgets_budget" "free_tier_cost_alert" {
  budget_type  = "COST"
  time_unit    = "MONTHLY"
  limit_amount = "1.00"
  limit_unit   = "USD"

  notification {
    comparison_operator = "GREATER_THAN"
    notification_type   = "ACTUAL"
    threshold           = 100
    threshold_type      = "PERCENTAGE"
    subscriber_email_addresses = ["goalpanzi.team@gmail.com", "serena35@naver.com"]
  }

  tags = {
    Name = "goalpanzi-budget-cost-alert"
  }
}

resource "aws_budgets_budget" "free_tier_cost_alert_forecasted" {
  budget_type  = "COST"
  time_unit    = "MONTHLY"
  limit_amount = "1.00"
  limit_unit   = "USD"

  notification {
    comparison_operator = "GREATER_THAN"
    notification_type   = "FORECASTED"
    threshold           = 95
    threshold_type      = "PERCENTAGE"
    subscriber_email_addresses = ["goalpanzi.team@gmail.com", "serena35@naver.com"]
  }

  tags = {
    Name = "goalpanzi-budget-cost-alert-forecasted"
  }
}
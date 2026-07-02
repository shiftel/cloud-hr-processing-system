locals {
  app_names = [
    "candidate-service",
    "document-analysis-service",
    "hr-notification-service",
    "skill-extractor-service",
    "verification-service"
  ]
}

resource "aws_ecr_repository" "microservices" {
  for_each             = toset(local.app_names)
  name                 = each.key
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Environment = "production"
  }
}

output "ecr_repository_urls" {
  value = { for k, v in aws_ecr_repository.microservices : k => v.repository_url }
}
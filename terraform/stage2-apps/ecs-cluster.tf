resource "aws_ecs_cluster" "hr_cluster" {
  name = "hr-microservices-cluster"
}

resource "aws_iam_role" "ecs_execution_role" {
  name = "hr-ecs-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_execution_attachment" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_cloudwatch_log_group" "ecs_logs" {
  name              = "/ecs/hr-system"
  retention_in_days = 7 
}

resource "aws_security_group" "app_sg" {
  name        = "hr-apps-security-group"
  description = "Zgoda na ruch dla mikroserwisow"
  vpc_id      = var.vpc_id

  ingress {
    description = "Ruch wewnetrzny miedzy mikroserwisami"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    self        = true
  }

  ingress {
    description = "Zezwol na dostep do mikroserwisow z internetu"
    from_port   = 8080
    to_port     = 8085
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "hr-app-sg" }
}

resource "aws_service_discovery_private_dns_namespace" "hr_dns" {
  name        = "local"
  description = "Prywatna domena dla komunikacji miedzy mikroserwisami"
  vpc         = var.vpc_id 
}

resource "aws_service_discovery_service" "sd" {
  for_each = toset(local.app_names)
  name     = each.key

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.hr_dns.id
    dns_records {
      ttl  = 10
      type = "A"
    }
  }
}


# ------------------------------------------------------------------------------
# [1/5] CANDIDATE-SERVICE (Port 8081)
# ------------------------------------------------------------------------------
resource "aws_ecs_task_definition" "candidate" {
  family                   = "candidate-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256" # 0.25 vCPU - idealne pod testy
  memory                   = "512" # 512 MB RAM
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([{
    name      = "candidate-service"
    image     = aws_ecr_repository.microservices["candidate-service"].repository_url
    essential = true
    portMappings = [{ containerPort = 8081, hostPort = 8081 }]
    
    environment = [
      { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://${var.db_host}:5432/candidate_db" },
      { name = "SPRING_DATASOURCE_USERNAME", value = var.db_username },
      { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
      { name = "RABBITMQ_URI", value = var.rabbitmq_uri },
      { name = "S3_BUCKET_NAME", value = var.s3_bucket_name },
      { name = "AWS_ACCESS_KEY", value = var.aws_access_key },
      { name = "AWS_SECRET_KEY", value = var.aws_secret_key },
      { name = "AWS_REGION", value = var.aws_region },
    ]

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.ecs_logs.name
        "awslogs-region"        = var.aws_region
        "awslogs-stream-prefix" = "candidate"
      }
    }
  }])
}

resource "aws_ecs_service" "candidate" {
  name            = "candidate-service"
  cluster         = aws_ecs_cluster.hr_cluster.id
  task_definition = aws_ecs_task_definition.candidate.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.public_subnets
    security_groups  = [aws_security_group.app_sg.id]
    assign_public_ip = true
  }

  service_registries {
    registry_arn = aws_service_discovery_service.sd["candidate-service"].arn
  }
}

# ------------------------------------------------------------------------------
# [2/5] DOCUMENT-ANALYSIS-SERVICE (Port 8082)
# ------------------------------------------------------------------------------
resource "aws_ecs_task_definition" "document" {
  family                   = "document-analysis-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([{
    name      = "document-analysis-service"
    image     = aws_ecr_repository.microservices["document-analysis-service"].repository_url
    essential = true
    portMappings = [{ containerPort = 8082, hostPort = 8082 }]
    
    environment = [
      { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://${var.db_host}:5432/document_analysis_db" },
      { name = "SPRING_DATASOURCE_USERNAME", value = var.db_username },
      { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
      { name = "RABBITMQ_URI", value = var.rabbitmq_uri },
      { name = "CANDIDATE_SERVICE_URL", value = "http://candidate-service.local:8080" }
    ]

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.ecs_logs.name
        "awslogs-region"        = var.aws_region
        "awslogs-stream-prefix" = "document"
      }
    }
  }])
}

resource "aws_ecs_service" "document" {
  name            = "document-analysis-service"
  cluster         = aws_ecs_cluster.hr_cluster.id
  task_definition = aws_ecs_task_definition.document.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.public_subnets
    security_groups  = [aws_security_group.app_sg.id]
    assign_public_ip = true
  }

  service_registries {
    registry_arn = aws_service_discovery_service.sd["document-analysis-service"].arn
  }
}

# ------------------------------------------------------------------------------
# [3/5] HR-NOTIFICATION-SERVICE (Port 8083)
# ------------------------------------------------------------------------------
resource "aws_ecs_task_definition" "notification" {
  family                   = "hr-notification-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([{
    name      = "hr-notification-service"
    image     = aws_ecr_repository.microservices["hr-notification-service"].repository_url
    essential = true
    portMappings = [{ containerPort = 8083, hostPort = 8083 }]
    
    environment = [
      { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://${var.db_host}:5432/hr_notification_db" },
      { name = "SPRING_DATASOURCE_USERNAME", value = var.db_username },
      { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
      { name = "RABBITMQ_URI", value = var.rabbitmq_uri }
    ]

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.ecs_logs.name
        "awslogs-region"        = var.aws_region
        "awslogs-stream-prefix" = "notification"
      }
    }
  }])
}

resource "aws_ecs_service" "notification" {
  name            = "hr-notification-service"
  cluster         = aws_ecs_cluster.hr_cluster.id
  task_definition = aws_ecs_task_definition.notification.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.public_subnets
    security_groups  = [aws_security_group.app_sg.id]
    assign_public_ip = true
  }

  service_registries {
    registry_arn = aws_service_discovery_service.sd["hr-notification-service"].arn
  }
}

# ------------------------------------------------------------------------------
# [4/5] SKILL-EXTRACTOR-SERVICE (Port 8084)
# ------------------------------------------------------------------------------
resource "aws_ecs_task_definition" "skill" {
  family                   = "skill-extractor-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([{
    name      = "skill-extractor-service"
    image     = aws_ecr_repository.microservices["skill-extractor-service"].repository_url
    essential = true
    portMappings = [{ containerPort = 8084, hostPort = 8084 }]
    
    environment = [
      { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://${var.db_host}:5432/skill_extractor_db" },
      { name = "SPRING_DATASOURCE_USERNAME", value = var.db_username },
      { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
      { name = "RABBITMQ_URI", value = var.rabbitmq_uri }
    ]

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.ecs_logs.name
        "awslogs-region"        = var.aws_region
        "awslogs-stream-prefix" = "skill"
      }
    }
  }])
}

resource "aws_ecs_service" "skill" {
  name            = "skill-extractor-service"
  cluster         = aws_ecs_cluster.hr_cluster.id
  task_definition = aws_ecs_task_definition.skill.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.public_subnets
    security_groups  = [aws_security_group.app_sg.id]
    assign_public_ip = true
  }
  service_registries {
    registry_arn = aws_service_discovery_service.sd["skill-extractor-service"].arn
  }
}

# ------------------------------------------------------------------------------
# [5/5] VERIFICATION-SERVICE (Port 8085)
# ------------------------------------------------------------------------------
resource "aws_ecs_task_definition" "verification" {
  family                   = "verification-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([{
    name      = "verification-service"
    image     = aws_ecr_repository.microservices["verification-service"].repository_url
    essential = true
    portMappings = [{ containerPort = 8085, hostPort = 8085 }]
    
    environment = [
      { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://${var.db_host}:5432/verification_db" },
      { name = "SPRING_DATASOURCE_USERNAME", value = var.db_username },
      { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password },
      { name = "RABBITMQ_URI", value = var.rabbitmq_uri }
    ]

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.ecs_logs.name
        "awslogs-region"        = var.aws_region
        "awslogs-stream-prefix" = "verification"
      }
    }
  }])
}

resource "aws_ecs_service" "verification" {
  name            = "verification-service"
  cluster         = aws_ecs_cluster.hr_cluster.id
  task_definition = aws_ecs_task_definition.verification.arn
  desired_count   = 1
  launch_type     = "FARGATE"

 network_configuration {
    subnets          = var.public_subnets
    security_groups  = [aws_security_group.app_sg.id]
    assign_public_ip = true
  }

  service_registries {
    registry_arn = aws_service_discovery_service.sd["verification-service"].arn
  }
}

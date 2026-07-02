
## Tech Stack

| Warstwa           | Technologia                                |
|-------------------|--------------------------------------------|
| Język / Framework | Java 17, Spring Boot 3.4                   |
| Baza danych       | PostgreSQL (AWS RDS)                       |
| Messaging         | RabbitMQ (CloudAMQP)                       |
| Storage           | AWS S3                                     |
| Konteneryzacja    | Docker, Docker Compose                     |
| Orkiestracja      | Kubernetes                                 |
| IaC               | Terraform (VPC, ECS, ECR, RDS, S3)         |
| Build             | Maven (multi-module)                       |

## Struktura projektu

```
cloud_2/
├── candidate-service/           # Mikroserwis kandydatów (REST + S3)
├── document-analysis-service/   # Mikroserwis analizy dokumentów
├── hr-notification-service/     # Mikroserwis powiadomień HR
├── skill-extractor-service/     # Mikroserwis ekstrakcji umiejętności
├── verification-service/        # Mikroserwis weryfikacji
├── kubernetes/                  # Manifesty K8s (Deployments, Services, Secrets)
├── terraform/
│   ├── stage1-infra/            # VPC, subnety, security groups, RabbitMQ
│   └── stage2-apps/             # ECR, ECS cluster, task definitions
├── docker-compose.yml           # Lokalne uruchomienie wszystkich serwisów
├── init-db.sql                  # Inicjalizacja baz danych
└── pom.xml                      # Parent POM (multi-module Maven)
```

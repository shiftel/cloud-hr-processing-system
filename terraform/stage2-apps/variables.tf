variable "aws_region" {
  type    = string
  default = "eu-north-1"
}


variable "vpc_id" {
  type        = string
  description = "ID sieci VPC z Etapu 1"
}

variable "public_subnets" {
  type        = list(string)
  description = "Lista podsieci publicznych z Etapu 1"
}


variable "aws_access_key" {
  type        = string
  description = "Klucz dostepu AWS Access Key"
}

variable "aws_secret_key" {
  type        = string
  description = "Klucz tajny AWS Secret Key"
}

variable "private_subnets" {
  type        = list(string)
  description = "Lista podsieci prywatnych z Etapu 1"
}

variable "db_host" {
  type        = string
  description = "Host bazy danych RDS z Etapu 1"
}

variable "rabbitmq_uri" {
  type        = string
  description = "URI do kolejki RabbitMQ z Etapu 1"
}


variable "s3_bucket_name" {
  type        = string
  description = "Nazwa Twojego istniejacego juz bucketa S3 dla candidate-service"
}

variable "db_password" {
  type        = string
  sensitive   = true
  description = "Haslo do bazy danych (musi byc takie samo jak db_admin_password z Etapu 1)"
}

variable "db_username" {
  type        = string
  default     = "postgres"
  description = "Nazwa uzytkownika bazy danych"
}
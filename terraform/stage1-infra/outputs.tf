output "vpc_id" {
  value       = aws_vpc.main.id
  description = "ID sieci VPC, w której musimy uruchomić mikroserwisy"
}

output "public_subnets" {
  value       = [aws_subnet.public_1.id, aws_subnet.public_2.id]
  description = "ID podsieci publicznych (dla Load Balancera)"
}

output "private_subnets" {
  value       = [aws_subnet.private_1.id, aws_subnet.private_2.id]
  description = "ID podsieci prywatnych (dla kontenerów z aplikacjami)"
}

output "db_host" {
  value       = "database-1.cluster-cfwkgaeacvgh.eu-north-1.rds.amazonaws.com"
  description = "Adres sieciowy (Host) dla bazy danych RDS"
}

output "rabbitmq_uri" {
  value       = "amqp://mqadmin:SecureMQPassword123!@${aws_instance.rabbitmq_server.private_ip}:5672"
  description = "Pełny URI do połączenia z RabbitMQ dla mikroserwisów"
}
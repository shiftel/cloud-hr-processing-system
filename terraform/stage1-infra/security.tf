resource "aws_security_group" "rds_sg" {
  name        = "hr-rds-security-group"
  description = "Kontrola ruchu do bazy danych PostgreSQL"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "Zezwol na ruch na porcie 5432 (PostgreSQL) tylko z wnetrza VPC"
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = [aws_vpc.main.cidr_block]
  }

  egress {
    description = "Zezwol na caly ruch wyjsciowy z bazy w swiat"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "hr-rds-sg" }
}

resource "aws_security_group" "rabbitmq_sg" {
  name        = "hr-rabbitmq-security-group"
  description = "Kontrola ruchu do kolejki RabbitMQ"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "Zezwol na ruch AMQP (5672) z wnetrza VPC"
    from_port   = 5672
    to_port     = 5672
    protocol    = "tcp"
    cidr_blocks = [aws_vpc.main.cidr_block]
  }

  ingress {
    description = "Zezwol na dostep do konsoli RabbitMQ (15672) z internetu"
    from_port   = 15672
    to_port     = 15672
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Zezwol na SSH z internetu (np. do EC2 Instance Connect)"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Zezwol na ruch wyjsciowy"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "hr-rabbitmq-sg" }
}
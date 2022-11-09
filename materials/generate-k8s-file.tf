variable "url" {
  type    = string
}

variable "port" {
  type    = string
  default = "3306"
}

variable "db_name" {
  default = "todo"
  type    = string
}

variable "db_user" {
  default = "root"
  type    = string
}

variable "db_pass" {
  type    = string
}

resource "local_file" "flyway_datasource" {
  content  = <<-EOL
  apiVersion: v1
  kind: Secret
  metadata:
    name: flyway-secret
    type: Opaque
  data:
    FLYWAY_USER: '${base64encode(var.db_user)}'
    FLYWAY_PASSWORD: '${base64encode(var.db_pass)}'
    FLYWAY_URL: '${base64encode("jdbc:mysql://${var.url}:${var.port}/${var.db_name}")}'
  EOL
  filename = "./deploy/flyway-config.yml"
}

resource "local_file" "web_datasource" {
  content  = <<-EOL
  apiVersion: v1
  kind: Secret
  metadata:
    name: web-datasource-secret
    type: Opaque
  data:
    SPRING_DATASOURCE_USERNAME: '${base64encode(var.db_user)}'
    SPRING_DATASOURCE_PASSWORD: '${base64encode(var.db_pass)}'
    SPRING_DATASOURCE_URL: '${base64encode("jdbc:mysql://${var.url}:${var.port}/${var.db_name}")}'
  EOL
  filename = "./deploy/spring-datasource-config.yml"
}

resource "local_file" "aws" {
  content  = <<-EOL
  apiVersion: v1
  kind: Secret
  metadata:
    name: aws-secret
    type: Opaque
  data:
    AWS_ACCESS_KEY_ID: '${base64encode(var.aws_ak)}'
    AWS_SECRET_ACCESS_KEY: '${base64encode(var.aws_sk)}'
    AWS_DEFAULT_REGION: '${base64encode("us-east-1")}'
  EOL
  filename = "./deploy/aws-config.yml"
}

variable "aws_ak" {
  type = string
}

variable "aws_sk" {
  type = string
}

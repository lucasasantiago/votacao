# API de Votação

## Descrição
Aplicação back-end para gerenciamento de sessões de votação em pautas.

A aplicação permite:
- Cadastro de pautas
- Abertura de sessões de votação com tempo determinado
- Registro de votos (Sim / Não)
- Apuração do resultado da votação

## Decisões de Projeto

- Utilizado **Spring Boot + Java 17**
- Arquitetura em camadas:
    - Controller
    - Service
    - Repository
- Banco de dados **PostgreSQL**
- Persistência com **Spring Data JPA**
- Cada associado pode votar apenas uma vez por pauta
- O tempo padrão de uma sessão de votação é **1 minuto**, conforme especificado no desafio
- Segurança foi abstraída conforme permitido no enunciado
- Os desafios bônus não foram implementados a pedido da recrutadora
- O uso de **Lombok foi evitado** para manter o código explícito, reduzir dependências externas e facilitar a avaliação técnica, uma vez que não traria ganhos relevantes para o escopo do desafio
- A adição do actuator foi feita por ser padrão em ambientes cloud.

## Execução da aplicação
mvn spring-boot:run

### Banco de Dados
O banco PostgreSQL é executado via Docker.

docker-compose up -d

### A aplicação estará disponível em:
http://localhost:8080

### Endpoint de health check
GET /actuator/health
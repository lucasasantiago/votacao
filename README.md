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

## Relatório de Performance (Teste de Carga)

Foi realizado um teste de carga utilizando **Apache JMeter** para validar o comportamento do sistema sob concorrência.

### Cenário do Teste
- **Volume**: 1.000 requisições (votos).
- **Usuários Simultâneos**: 100 threads.
- **Ramp-up**: 10 segundos.
- **Payload**: Randomização de `associadoId` (UUID) e `voto` (SIM/NAO) via funções Groovy.

### Resultados Obtidos
| Métrica | Resultado |
| :--- | :--- |
| **Amostras (Samples)** | 1.000 |
| **Tempo de Resposta Médio (Average)** | **14 ms** |
| **Vazão (Throughput)** | 100.0/sec |
| **Taxa de Erro** | 0.00% |

#### Screenshot do Relatório (JMeter Summary Report)
![Resultado JMeter](docs/images/jmeter_summary_report.png)
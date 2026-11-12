# ColetaAgendada - Microsserviços (Global Solution FIAP)

Solução de **economia circular / ESG** baseada em microsserviços: geradores (indústrias, comércios) cadastram **lotes de resíduos recicláveis**, coletores (cooperativas, recicladoras) **reservam** os lotes disponíveis na sua região e confirmam a coleta com um **código PIN**, gerando um **recibo de impacto ambiental** (CO2 evitado e pontos ESG).

## Integrantes

| Nome | RM |
|------|----|
| Clara de Fonte Almeida | RM554321 |
| Felipe Boveri | RM555055 |

## Arquitetura

| Módulo | Porta | Responsabilidade |
|--------|-------|------------------|
| `eureka-server` | 8761 | Service Discovery |
| `api-gateway` | 8082 | Ponto único de entrada (roteamento por service-id) |
| `ms-gerador-lotes` | aleatória | Geradores e lotes de resíduos (banco próprio) |
| `ms-coleta-impacto` | aleatória | Coletores, reservas/coletas e impacto ESG (banco próprio) |


### Comunicação entre microsserviços

`ms-coleta-impacto` chama `ms-gerador-lotes` via **OpenFeign** usando apenas o nome do serviço no Eureka (sem URL fixa, balanceado pelo LoadBalancer):

- reservar coleta -> `PUT /api/v1/lotes/{protocolo}/reservar` (lote `DISPONIVEL` -> `RESERVADO`)
- cancelar coleta -> `PUT /api/v1/lotes/{protocolo}/liberar` (`RESERVADO` -> `DISPONIVEL`)
- baixa com PIN -> `PUT /api/v1/lotes/{protocolo}/coletar` (`RESERVADO` -> `COLETADO`)

Há **Circuit Breaker** (`geradorLotes`, Resilience4j) com **fallback** retornando `503 Service Unavailable` quando o `ms-gerador-lotes` está indisponível.

## Pré-requisitos

- JDK 25
- Docker Desktop (para os bancos MySQL)
- Acesso à internet (consulta ao ViaCEP na criação de lotes)

## Como executar

### Opção A - Tudo em Docker

```powershell
docker compose up -d --build
```

Sobe os 2 MySQL, Eureka, Gateway e os 2 microsserviços. Para derrubar: `docker compose down` (use `-v` para apagar os dados).

### Opção B - Bancos em Docker, serviços via Maven

1. Bancos de dados:
   ```powershell
   docker compose up -d mysql-gerador mysql-coleta
   ```
2. Em terminais separados, **nesta ordem**:
   ```powershell
   cd eureka-server;      .\mvnw.cmd spring-boot:run
   cd api-gateway;        .\mvnw.cmd spring-boot:run
   cd ms-gerador-lotes;   .\mvnw.cmd spring-boot:run
   cd ms-coleta-impacto;  .\mvnw.cmd spring-boot:run
   ```
   (Linux/macOS: `./mvnw spring-boot:run`)

As migrations **Flyway** criam o schema e populam os dados de exemplo automaticamente na primeira execução.

### Bancos de dados

| Banco | Host | Usuário / Senha | Microsserviço |
|-------|------|-----------------|---------------|
| `gerador_lotes_db` | `localhost:3307` | root / root | ms-gerador-lotes |
| `coleta_impacto_db` | `localhost:3308` | root / root | ms-coleta-impacto |

Variáveis de ambiente opcionais: `EUREKA_URL`, `DB_URL`, `DB_USER`, `DB_PASSWORD`.

## Portas aleatórias, Eureka e Swagger

Os microsserviços usam `server.port=0`. Para descobrir a porta de cada instância, abra o dashboard do Eureka: <http://localhost:8761> (ou veja o log de inicialização).

- Swagger UI: `http://localhost:<porta-da-instancia>/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:<porta-da-instancia>/v3/api-docs`

## Acesso via API Gateway

Padrão: `http://localhost:8082/<service-id>/api/v1/...`

- `http://localhost:8082/ms-gerador-lotes/api/v1/geradores`
- `http://localhost:8082/ms-coleta-impacto/api/v1/coletas/disponiveis?cep=01310100`

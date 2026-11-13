# ColetaAgendada - Global Solution (Microsserviços)

Solução de economia circular/ESG baseada em microsserviços. Geradores (indústrias e comércios)
cadastram lotes de resíduos recicláveis, e coletores (cooperativas/recicladoras) reservam e
confirmam a coleta por PIN, gerando recibo de impacto ambiental (CO2 evitado e pontos ESG).

## Integrantes

| Nome | RM |
|------|----|
| Clara de Fonte Almeida | RM554321 |
| Felipe Boveri Serra | RM555055 |


## Descrição da arquitetura

| Módulo | Porta | Responsabilidade |
|--------|-------|------------------|
| `eureka-server` | `8761` | Service Discovery |
| `api-gateway` | `8082` | Ponto único de entrada e roteamento por `service-id` |
| `ms-gerador-lotes` | aleatória (`server.port=0`) | Gestão de geradores e lotes (MySQL próprio) |
| `ms-coleta-impacto` | aleatória (`server.port=0`) | Gestão de coletas/recibo ESG (MySQL próprio) |

### Comunicação síncrona entre microsserviços

- `ms-coleta-impacto` consome `ms-gerador-lotes` via OpenFeign + Eureka (sem URL fixa).
- Dependências de comunicação presentes: Eureka Client, OpenFeign, LoadBalancer.
- Circuit Breaker (Resilience4j) + fallback implementados no `ms-coleta-impacto` para chamadas remotas ao `ms-gerador-lotes`.

Fluxo principal de status:

- reservar coleta: `PUT /api/v1/lotes/{protocolo}/reservar` (`DISPONIVEL -> RESERVADO`)
- cancelar coleta: `PUT /api/v1/lotes/{protocolo}/liberar` (`RESERVADO -> DISPONIVEL`)
- baixa por PIN: `PUT /api/v1/lotes/{protocolo}/coletar` (`RESERVADO -> COLETADO`)

## Pre-requisitos

- JDK 25
- Docker Desktop (para os bancos MySQL)
- Maven Wrapper (`mvnw`/`mvnw.cmd`, ja incluso nos módulos)
- Acesso a internet (consulta ViaCEP no `ms-gerador-lotes`)

## Execução da solução (ambiente limpo)

> Execute os comandos abaixo a partir da raiz do repositório (`gs-microserviceshub`).

### 1) Subir os dois MySQL

```powershell
Set-Location .\ms-gerador-lotes
docker compose up -d

Set-Location ..\ms-coleta-impacto
docker compose up -d
```

### 2) Subir os serviços (em 4 terminais separados)

```powershell
Set-Location .\eureka-server
.\mvnw.cmd spring-boot:run
```

```powershell
Set-Location .\api-gateway
.\mvnw.cmd spring-boot:run
```

```powershell
Set-Location .\ms-gerador-lotes
.\mvnw.cmd spring-boot:run
```

```powershell
Set-Location .\ms-coleta-impacto
.\mvnw.cmd spring-boot:run
```

As migrations Flyway executam automaticamente na inicialização dos microsserviços.

## Reset para correção (zerar bancos e subir novamente)

```powershell
Set-Location .\ms-gerador-lotes
docker compose down -v

Set-Location ..\ms-coleta-impacto
docker compose down -v
```

Depois, repita os passos de execução da seção anterior.

## Bancos de dados

| Banco | Host | Usuário/Senha | Microsserviço |
|-------|------|----------------|---------------|
| `gerador_lotes_db` | `localhost:3307` | `root/root` | `ms-gerador-lotes` |
| `coleta_impacto_db` | `localhost:3308` | `root/root` | `ms-coleta-impacto` |

Variáveis de ambiente opcionais:

- `EUREKA_URL`
- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

## Swagger e descoberta das portas aleatórias

- Eureka dashboard: `http://localhost:8761`
- Gateway: `http://localhost:8082`
- Swagger UI (cada microsserviço): `http://localhost:<porta-da-instancia>/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:<porta-da-instancia>/v3/api-docs`

## Endpoints principais para teste (via Gateway)

Padrão: `http://localhost:8082/<service-id>/api/v1/...`

### ms-gerador-lotes

- `GET /ms-gerador-lotes/api/v1/lotes/disponiveis?cep=01310100`
- `GET /ms-gerador-lotes/api/v1/lotes/{protocolo}`
- `POST /ms-gerador-lotes/api/v1/lotes`
- `PUT /ms-gerador-lotes/api/v1/lotes/{protocolo}`
- `DELETE /ms-gerador-lotes/api/v1/lotes/{protocolo}`

### ms-coleta-impacto

- `GET /ms-coleta-impacto/api/v1/coletas`
- `GET /ms-coleta-impacto/api/v1/coletas/{id}`
- `POST /ms-coleta-impacto/api/v1/coletas`
- `PUT /ms-coleta-impacto/api/v1/coletas/{id}`
- `DELETE /ms-coleta-impacto/api/v1/coletas/{id}`
- `POST /ms-coleta-impacto/api/v1/coletas/{protocolo}/baixa`

## Códigos HTTP esperados

- `200 OK`: consultas/atualizações executadas com sucesso
- `201 Created`: criação de recurso com cabeçalho `Location`
- `204 No Content`: exclusão sem corpo de resposta
- `400 Bad Request`: validação de entrada/regra de negócio
- `404 Not Found`: recurso não encontrado
- `409 Conflict`: conflito de estado/regra
- `503 Service Unavailable`: fallback de comunicação remota


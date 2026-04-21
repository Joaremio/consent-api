# 🧾 Consent API

API REST para gerenciamento do ciclo de vida de consentimentos.

---

## 🚀 Tecnologias utilizadas

* Java 21
* Spring Boot
* Spring Data MongoDB
* MapStruct
* Lombok
* Swagger / OpenAPI
* Testcontainers
* Docker / Docker Compose
* JUnit + Mockito

---

## 📌 Funcionalidades

* ✅ Criar consentimento
* ✅ Idempotência via header `X-Idempotency-Key`
* ✅ Consulta por ID
* ✅ Listagem paginada
* ✅ Atualização total (PUT)
* ✅ Atualização parcial (PATCH)
* ✅ Revogação (soft delete)
* ✅ Expiração automática via scheduler
* ✅ Histórico de alterações
* ✅ Tratamento global de exceções
* ✅ Logs estruturados

---

## 🔁 Idempotência

A criação de consentimento utiliza o header:

```
X-Idempotency-Key
```

### Comportamento:

| Cenário                         | Resultado    |
| ------------------------------- | ------------ |
| Nova chave                      | 201 CREATED  |
| Mesma chave + mesmo payload     | 200 OK       |
| Mesma chave + payload diferente | 409 CONFLICT |

---

## 🗂️ Estrutura do projeto

```
controller/
service/
repository/
domain/
dto/
mapper/
exception/
config/
```

---

## ▶️ Como rodar o projeto

### 🔧 Pré-requisitos

* Java 21
* Maven
* Docker

---

### 🚀 Rodar com Docker

```
docker-compose up --build
```

A aplicação estará disponível em:

```
http://localhost:8080
```

---

### 🧪 Rodar build + testes

```
mvn clean install
```

---

### 📚 Swagger

```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Testes

O projeto possui:

* ✔ Testes unitários (Service)
* ✔ Testes de integração com Testcontainers (MongoDB)

Para executar:

```
mvn test
```

---

## ⏱️ Expiração automática

Consentimentos com data de expiração são automaticamente atualizados para `EXPIRED` por um job agendado.

---

## 📜 Histórico de consentimento

Todas as alterações são registradas em uma coleção separada:

```
consents_history
```

Incluindo:

* criação
* atualização
* patch
* revogação
* expiração

---

## ⚠️ Tratamento de erros

A API retorna respostas padronizadas:

```json
{
  "message": "Descrição do erro",
  "status": 400,
  "timestamp": "2026-01-01T10:00:00"
}
```

### Status utilizados:

* 400 → Dados inválidos
* 404 → Recurso não encontrado
* 409 → Conflito de idempotência

---

## 📦 Variáveis de ambiente

| Variável                | Descrição      |
| ----------------------- | -------------- |
| SPRING_DATA_MONGODB_URI | URI do MongoDB |

Default:

```
mongodb://localhost:27017/consent_db
```

---

## 🐳 Docker

### Serviços:

* API
* MongoDB

---

## 👨‍💻 Autor

Desenvolvido por **Joaremio Neto**

---

## 📄 Considerações finais

Este projeto foi desenvolvido seguindo boas práticas de:

* Arquitetura em camadas
* RESTful APIs
* Idempotência
* Observabilidade (logs)
* Testes automatizados
* Containerização

---

# 03217-kotlin-docnum-conversion

## Visão Geral

O projeto **kotlin-docnum-conversion** é uma API desenvolvida em **Kotlin** com **Spring Boot** que permite a conversão de números de documentos (CPF ou CNPJ) em UUIDs de forma segura. A API é completamente **stateless**, ou seja, não utiliza banco de dados nem armazena informações na memória. Todo o processo de conversão é realizado de forma determinística através de uma chave de criptografia única.

## Como Funciona

- Um **número de documento** (CPF ou CNPJ) é convertido para um **UUID** utilizando um algoritmo de criptografia simétrica (**AES**).
- A conversão é **reversível**, permitindo que um UUID gerado possa ser convertido de volta ao número de documento original.
- Como a API não armazena dados, a reversão só é possível se a mesma **chave de criptografia** for utilizada.

## Tecnologias Utilizadas

- **Kotlin**
- **Spring Boot**
- **AES (Advanced Encryption Standard)** para criptografia

## Endpoints da API

### Converter Número de Documento para UUID

**Endpoint:**
```
POST /api/document/num-to-uuid
```
**Parâmetro:**
- `document_number` (String) - CPF ou CNPJ sem formatação

**Exemplo de Requisição:**
```sh
curl -X POST "http://localhost:8080/api/document/num-to-uuid" -d "document_number=12345678901"
```

**Resposta:**
```json
"550e8400-e29b-41d4-a716-446655440000"
```

---

### Converter UUID de volta para Número de Documento

**Endpoint:**
```
POST /api/document/from-uuid
```
**Parâmetro:**
- `uuid` (String) - UUID gerado anteriormente

**Exemplo de Requisição:**
```sh
curl -X POST "http://localhost:8080/api/document/from-uuid" -d "uuid=550e8400-e29b-41d4-a716-446655440000"
```

**Resposta:**
```json
"12345678901"
```

## Segurança

A API utiliza criptografia **AES** para garantir que os números de documento sejam convertidos de maneira segura. Como o processo é **determinístico** e depende de uma **chave única**, a conversão será sempre a mesma para um mesmo número de documento, possibilitando a reversão sem necessidade de armazenamento.

## Como Executar o Projeto

### Pré-requisitos
- **JDK 17+**
- **Maven ou Gradle**
- **Docker** (opcional, caso queira rodar dentro de um container)

### Rodando Localmente
```sh
git clone <URL_DO_REPOSITORIO>
cd kotlin-docnum-conversion
./gradlew bootRun
```
A API estará disponível em `http://localhost:8080`.

## Considerações

Essa API é útil para garantir anonimização e identificação segura de documentos sem expô-los diretamente. O uso de uma chave única garante a integridade do processo de conversão e reversão.
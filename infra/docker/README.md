## Instalação do Docker Compose (Outra Feature)


Este projeto utiliza **Docker Compose** para gerenciar os serviços necessários. Siga os passos abaixo para configurar e executar o ambiente.


### Requisitos


- Docker instalado ([Instruções de instalação](https://docs.docker.com/get-docker/))

- Docker Compose instalado ([Instruções de instalação](https://docs.docker.com/compose/install/))


### Como instalar e rodar


1. Clone este repositório:

   ```sh

   git clone <URL_DO_REPOSITORIO>

   cd <NOME_DO_REPOSITORIO>

   ```


2. Suba os containers com o Docker Compose:

   ```sh

   docker-compose up -d

   ```


3. Verifique se os containers estão rodando:

   ```sh

   docker ps

   ```


### Serviços Disponíveis


- **PostgreSQL**

    - Porta: `5432`

    - Usuário: `guest`

    - Senha: `guest`

    - Banco de Dados: `demo_db`

- **Adminer** (Interface gráfica para gerenciar o banco de dados)

    - URL: [http://localhost:8090](http://localhost:8090)


### Parar e Remover os Containers


Para parar os containers, execute:

```sh

docker-compose down

```


Para remover volumes persistentes (se habilitado), execute:

```sh

docker-compose down -v

```


### Considerações


- O volume persistente do PostgreSQL está comentado no `docker-compose.yml`. Se desejar manter os dados entre reinicializações, descomente a seção `volumes`.

- O Adminer é utilizado para gerenciar o banco de forma simples.


---

Agora, seu ambiente está pronto para uso! 🚀



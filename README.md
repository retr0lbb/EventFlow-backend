# EventFlow
---
## Descrição do Repositório

### Tecnologias Utilizadas

- Linguagem: Java 25
- Framework Principal: Spring Boot
- Gerenciamento de Dependências e Construção (Build): Apache Maven
- Persistência e Mapeamento Objeto-Relacional (ORM): Spring Data JPA integrado ao Hibernate
- Sistema Gerenciador de Banco de Dados (SGBD): PostgreSQL
- Versionamento de Esquema de Banco de Dados: Flyway
- Conteinerização: Docker e Docker Compose
- Load Balancer / Proxy Reverso: Nginx

## 2. Arquitetura da Infraestrutura

A aplicação utiliza **Nginx como Load Balancer** na frente de múltiplas instâncias da API Spring Boot, todas orquestradas via Docker Compose.

```
                          ┌──────────────────┐
                          │   Cliente HTTP    │
                          └────────┬─────────┘
                                   │ :80
                          ┌────────▼─────────┐
                          │   Nginx (LB)     │
                          │   Round Robin     │
                          └────────┬─────────┘
                    ┌──────────────┼──────────────┐
                    │              │              │
           ┌────────▼───┐  ┌──────▼─────┐  ┌────▼────────┐
           │  API :8080  │  │  API :8080  │  │  API :8080  │
           │ (réplica 1) │  │ (réplica 2) │  │ (réplica 3) │
           └──────┬──────┘  └──────┬──────┘  └──────┬──────┘
                  │                │                │
                  └────────────────┼────────────────┘
                          ┌────────▼─────────┐
                          │   PostgreSQL     │
                          │     :5432        │
                          └──────────────────┘
```

**Serviços:**

| Serviço    | Porta    | Descrição                                   |
|------------|----------|---------------------------------------------|
| Nginx      | 80       | Load balancer (ponto de entrada)            |
| API        | 8080*    | Aplicação Spring Boot (3 réplicas)          |
| PostgreSQL | 5432     | Banco de dados relacional                   |
| pgAdmin    | 5050     | Interface web de administração do PostgreSQL|

> *A porta 8080 é interna à rede Docker. O acesso externo é feito via Nginx na porta 80.

## 3. Diretrizes para Inicialização do Ambiente

### Pré-requisitos

É imperativo que o ambiente de desenvolvimento local possua as seguintes ferramentas instaladas e com suas respectivas variáveis de ambiente devidamente configuradas:

- Docker Engine
- Docker Compose

> **Nota:** Java e Maven não são mais necessários localmente — a compilação é realizada dentro do container Docker (multi-stage build).

### Procedimentos para Execução em Ambiente Local

**Passo 1: Clonagem do repositório**
Faça a cópia local do repositório por meio do sistema de controle de versão Git.

```
git clone https://github.com/seu-usuario/nome-do-repositorio.git
cd nome-do-repositorio
```

**Passo 2: Inicialização completa da infraestrutura**
O comando a seguir irá compilar a API, criar as imagens Docker e inicializar todos os serviços (PostgreSQL, API com 3 réplicas, Nginx e pgAdmin):

```bash
docker compose up --build -d
```

Após a conclusão com êxito da inicialização, a API estará acessível via Load Balancer em: **http://localhost**

**Passo 3: Verificar o status dos serviços**
```bash
docker compose ps
```

**Passo 4: Escalar a quantidade de instâncias (opcional)**
Para alterar o número de réplicas da API em tempo de execução:

```bash
# Escalar para 5 instâncias
docker compose up -d --scale api=5

# Reduzir para 2 instâncias
docker compose up -d --scale api=2
```

**Passo 5: Visualizar os logs**
```bash
# Logs de todos os serviços
docker compose logs -f

# Logs apenas do Nginx
docker compose logs -f nginx

# Logs apenas das APIs
docker compose logs -f api
```

**Passo 6: Parar todos os serviços**
```bash
docker compose down
```

## 4. Estrutura Organizacional do Código-Fonte

A arquitetura de diretórios obedece ao padrão de separação de responsabilidades em camadas, garantindo alta coesão e baixo acoplamento para um monolito bem definido:

```
src/
 |-- main/
 |   |-- java/com/suaempresa/api/
 |   |   |-- controllers/      # Interfaces de entrada (Controladores REST)
 |   |   |-- services/         # Encapsulamento de regras de negócio e orquestração
 |   |   |-- repositories/     # Interfaces de acesso a dados (Contratos Spring Data)
 |   |   |-- models/           # Entidades de domínio mapeadas via ORM
 |   |   |-- dtos/             # Objetos para transferência de estado (Data Transfer Objects)
 |   |   |-- exceptions/       # Interceptadores e tratamentos globais de exceções
 |   |   `-- config/           # Configurações sistêmicas (Segurança, CORS, Instanciação de Beans)
 |   `-- resources/
 |       |-- db/migration/     # Scripts de versionamento SQL do Flyway (ex: V1__Create_table.sql)
 |       `-- application.yml   # Arquivo de propriedades e parâmetros de ambiente do Spring Boot
```

### Infraestrutura Docker

```
./
 |-- Dockerfile                # Multi-stage build (Maven + JRE Alpine)
 |-- .dockerignore             # Exclusões do contexto de build
 |-- compose.yaml              # Orquestração de todos os serviços
 |-- nginx/
 |   `-- nginx.conf            # Configuração do Load Balancer
 `-- .env                      # Variáveis de ambiente
```

## 5. Governança do Banco de Dados (Flyway)

A integridade e a evolução estrutural do esquema de banco de dados são asseguradas pela ferramenta Flyway. Para a manipulação segura da base de dados e mitigação de inconsistências entre ambientes, é estritamente necessário observar as seguintes diretrizes:

**Princípio da Imutabilidade:** É terminantemente proibido alterar o conteúdo de um script SQL (.sql) que já tenha sido consolidado (commit) e executado em qualquer ambiente (Desenvolvimento, Homologação ou Produção).

**Evolução Progressiva:** Para a adição de tabelas, modificação de colunas ou mutações de dados em lote, deve-se obrigatoriamente criar um novo arquivo de migração.

**Padrão de Nomenclatura:** A nomenclatura dos arquivos deve observar estritamente o padrão exigido pelo Flyway: V{numero_da_versao}__{descricao_curta}.sql (exemplo: V2__Add_email_to_users.sql). Ressalta-se a obrigatoriedade do uso de dois sublinhados para separar a versão da descrição.

> **Nota sobre múltiplas réplicas:** O Flyway utiliza locks no banco de dados para garantir que apenas uma instância execute as migrações. As demais instâncias aguardam a conclusão antes de iniciar.

## 6. Protocolo de Contribuição

Para propor alterações ao projeto, solicita-se a observância do seguinte fluxo de trabalho:

1. Efetue um fork do repositório principal.

2. Crie uma branch isolada para o desenvolvimento da nova funcionalidade ou correção: git checkout -b feature/nomenclatura-da-funcionalidade

3. Registre as alterações (commit) aderindo às convenções estabelecidas pelo padrão Conventional Commits: git commit -m "feat: implementa integração com serviço de pagamento"

4. Submeta as alterações para a sua ramificação remota: git push origin feature/nomenclatura-da-funcionalidade

5. Solicite a integração por meio de um Pull Request, fornecendo documentação detalhada acerca das modificações e impactos propostos.

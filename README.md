<<<<<<< HEAD
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

## 2. Diretrizes para Inicialização do Ambiente

### Pré-requisitos

É imperativo que o ambiente de desenvolvimento local possua as seguintes ferramentas instaladas e com suas respectivas variáveis de ambiente devidamente configuradas:

- Java Development Kit (JDK) 17 ou superior
- Apache Maven 3.8 ou superior
- Docker Engine
- Docker Compose

### Procedimentos para Execução em Ambiente Local

**Passo 1: Clonagem do repositório**
Faça a cópia local do repositório por meio do sistema de controle de versão Git.

```
git clone [https://github.com/seu-usuario/nome-do-repositorio.git](https://github.com/seu-usuario/nome-do-repositorio.git)
cd nome-do-repositorio
```

**Passo 2: Provisionamento da Infraestrutura de Banco de Dados**
O projeto dispõe de um arquivo docker-compose.yml previamente configurado para provisionar e isolar a instância do SGBD PostgreSQL. Execute o comando a seguir para inicializar o contêiner em segundo plano.

```
docker-compose up -d
```

**Passo 3: Compilação e Resolução de Dependências**
Empregue o utilitário Maven para a limpeza de artefatos provenientes de compilações anteriores e para o download das dependências declaradas.

```
mvn clean install
```

**Passo 4: Inicialização da Aplicação**
Durante o processo de inicialização, o framework Spring Boot, atuando em conjunto com a ferramenta Flyway, detectará os scripts SQL alocados no diretório src/main/resources/db/migration e aplicará as alterações estruturais no PostgreSQL de maneira autônoma.

```
mvn spring-boot:run
```

Após a conclusão com êxito da inicialização, a API estará acessível para requisições na porta padrão: http://localhost:8080.

## 3. Estrutura Organizacional do Código-Fonte

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

## 4. Governança do Banco de Dados (Flyway)

A integridade e a evolução estrutural do esquema de banco de dados são asseguradas pela ferramenta Flyway. Para a manipulação segura da base de dados e mitigação de inconsistências entre ambientes, é estritamente necessário observar as seguintes diretrizes:

**Princípio da Imutabilidade:** É terminantemente proibido alterar o conteúdo de um script SQL (.sql) que já tenha sido consolidado (commit) e executado em qualquer ambiente (Desenvolvimento, Homologação ou Produção).

**Evolução Progressiva:** Para a adição de tabelas, modificação de colunas ou mutações de dados em lote, deve-se obrigatoriamente criar um novo arquivo de migração.

**Padrão de Nomenclatura:** A nomenclatura dos arquivos deve observar estritamente o padrão exigido pelo Flyway: V{numero_da_versao}__{descricao_curta}.sql (exemplo: V2__Add_email_to_users.sql). Ressalta-se a obrigatoriedade do uso de dois sublinhados para separar a versão da descrição.

## 5. Protocolo de Contribuição

Para propor alterações ao projeto, solicita-se a observância do seguinte fluxo de trabalho:

1. Efetue um fork do repositório principal.

2. Crie uma branch isolada para o desenvolvimento da nova funcionalidade ou correção: git checkout -b feature/nomenclatura-da-funcionalidade

3. Registre as alterações (commit) aderindo às convenções estabelecidas pelo padrão Conventional Commits: git commit -m "feat: implementa integração com serviço de pagamento"

4. Submeta as alterações para a sua ramificação remota: git push origin feature/nomenclatura-da-funcionalidade

5. Solicite a integração por meio de um Pull Request, fornecendo documentação detalhada acerca das modificações e impactos propostos.

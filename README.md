📝 TaskBoard

Projeto de Conclusão do Bootcamp Java Backend da DIO
 em parceria com o Santander.

Um gerenciador de boards e cards que permite criar, mover, bloquear, desbloquear e cancelar cards dentro de colunas, simulando um fluxo de trabalho real.

🚀 Funcionalidades

✅ Criar Boards e Colunas

✅ Criar Cards e atribuir a Colunas

✅ Mover Cards entre Colunas seguindo o fluxo

✅ Bloquear e Desbloquear Cards

✅ Cancelar Cards

✅ Visualizar detalhes e histórico de bloqueios

🛠 Tecnologias utilizadas

Java 21

JDBC para conexão com banco MySQL

MySQL como banco de dados relacional

Liquibase para versionamento de banco e migrations

Maven / Gradle para build e gerenciamento de dependências

Arquitetura em camadas: DAO, Service Layer, DTOs

📁 Estrutura do projeto
src/
 ├─ br.com.dio.persistence.dao       # Acesso ao banco (CardDAO, BlockDAO)
 ├─ br.com.dio.persistence.entity    # Entidades do sistema (CardEntity, BlockEntity)
 ├─ br.com.dio.service               # Regras de negócio (CardService)
 ├─ br.com.dio.ui                    # Interface de console
 └─ br.com.dio.dto                   # Objetos de transferência de dados
Como executar

Configure o MySQL e execute os scripts do Liquibase para criar as tabelas.

Ajuste a conexão no projeto (ConnectionFactory ou similar).

Compile o projeto com Maven ou Gradle.

Execute a classe Main para iniciar a interface de console.

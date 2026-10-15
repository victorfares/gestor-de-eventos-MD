# Roteiro de compilação, execução e testes

Gestor de Eventos MD — Meninas Digitais, UTFPR Campus Cornélio Procópio.

Este roteiro permite salvar o código, criar a base de dados, compilar, executar e testar o sistema em outra máquina.

## Para compilar e executar o sistema

### Ferramentas para codificar, compilar e executar

| Ferramenta | Versão | Uso | Link |
|---|---|---|---|
| Java JDK | 17 (testado com 17.0.12) | Compilar e executar a API | https://adoptium.net/temurin/releases/?version=17 |
| Apache Maven | 3.9, pelo Maven Wrapper incluso em `backend` | Baixar dependências e compilar o backend | https://maven.apache.org/ |
| Node.js | 20 ou superior (testado com 24.12.0) | Instalar e executar o frontend | https://nodejs.org/ |
| npm | incluso no Node.js (testado com 11.6.2) | Instalar as bibliotecas do Vue | https://docs.npmjs.com/ |
| Git | atual | Salvar e obter o código | https://git-scm.com/ |
| Docker Desktop | atual, com Docker Compose | Subir o PostgreSQL local | https://www.docker.com/products/docker-desktop/ |

Editor usado no desenvolvimento: Cursor (https://cursor.com/). O projeto também abre no IntelliJ IDEA ou no Visual Studio Code. O editor não é obrigatório para compilar e executar pelos comandos abaixo.

### Ferramentas da base de dados

| Ferramenta | Versão | Uso | Link |
|---|---|---|---|
| PostgreSQL | 15, imagem `postgres:15` | Banco local | https://hub.docker.com/_/postgres |
| Docker Compose | formato 3.8, incluso no Docker Desktop | Criar o banco local | https://docs.docker.com/compose/ |
| Neon | PostgreSQL gerenciado, plano free | Banco publicado | https://neon.tech/ |

O Hibernate cria e atualiza as tabelas na primeira execução da API (`spring.jpa.hibernate.ddl-auto=update`). Não é necessário rodar um script SQL manual.

### Bibliotecas e ferramentas complementares

Backend (`backend/pom.xml`). As versões das bibliotecas Spring são as trazidas pelo Spring Boot 4.1.1 (https://spring.io/projects/spring-boot).

| Biblioteca | Uso |
|---|---|
| Spring Boot Web | API REST |
| Spring Data JPA e Hibernate | Persistência |
| Spring Security e OAuth2 Resource Server | Login JWT e proteção das rotas |
| Spring Validation | Validação dos dados recebidos |
| PostgreSQL JDBC | Conexão com o banco |
| Lombok | Redução de código repetido nas classes |

Frontend (`frontend/package.json`):

| Biblioteca | Versão declarada | Uso | Link |
|---|---|---|---|
| Vue | 3.5 | Interface | https://vuejs.org/ |
| Vue Router | 4.6 | Navegação entre telas | https://router.vuejs.org/ |
| Vuetify | 3.10 | Componentes de tela | https://vuetifyjs.com/ |
| Axios | 1.13 | Chamadas à API | https://axios-http.com/ |
| Vite | 7.1 | Servidor de desenvolvimento e build | https://vite.dev/ |
| Material Design Icons | 7.4 | Ícones | https://pictogrammers.com/library/mdi/ |

Hospedagem da aplicação publicada:

| Peça | Ferramenta | Link |
|---|---|---|
| API | Render, serviço web free, imagem Docker com Java 17 | https://render.com/ |
| Site | Vercel, plano Hobby | https://vercel.com/ |

### Roteiro da base de dados

1. Instale o Docker Desktop e confirme que ele está em execução.
2. Na pasta `backend`, crie o banco local com:

```powershell
docker run -d --name gestor_eventos_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=gestor_eventos_md -p 5432:5432 postgres:15
```

3. Confirme com `docker ps`. O contêiner `gestor_eventos_db` deve aparecer com a porta `5432`.
4. Não crie tabelas à mão. Ao iniciar a API, o Hibernate cria `eventos`, `inscricoes` e `usuarios`.
5. Na primeira subida, a API também cria o administrador padrão, se esse e-mail ainda não existir.

Para parar o banco: `docker stop gestor_eventos_db`. Para iniciar de novo: `docker start gestor_eventos_db`.

### Roteiro para salvar o código, compilar e executar

1. Salve o código com Git, ou obtenha uma cópia:

```powershell
git clone <endereco-do-repositorio>
cd "gestor de eventos MD"
```

2. Confirme `java -version` (17) e `node -v` (20 ou superior).
3. Crie o banco pelo roteiro anterior.
4. Compile e execute a API:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

A primeira execução baixa as dependências. Quando o log mostrar a aplicação iniciada, a API está em `http://localhost:8080`.

5. Em outro terminal, instale e execute o site:

```powershell
cd frontend
npm ci
npm run dev
```

6. Abra `http://localhost:5173`. O arquivo `frontend/.env` deve conter `VITE_API_URL=http://localhost:8080`. Se esse arquivo não existir, copie `frontend/.env.example` para `frontend/.env`.

Para só compilar, sem deixar a API rodando:

```powershell
cd backend
.\mvnw.cmd -DskipTests package
```

O arquivo gerado fica em `backend/target/gestor-eventos-md-0.0.1-SNAPSHOT.jar`.

Para gerar o site estático:

```powershell
cd frontend
npm run build
```

Os arquivos ficam em `frontend/dist`.

## Para testar o sistema

### Equipe

Gestor de Eventos MD, projeto de extensão Meninas Digitais da UTFPR Campus Cornélio Procópio.

### Objetivo do sistema

Publicar a agenda de oficinas, minicursos e rodas de conversa do Meninas Digitais e permitir que alunas se inscrevam sem criar conta. A equipe do projeto entra em uma área autenticada para cadastrar eventos, controlar vagas e exportar a lista de presença.

### Funcionalidades desenvolvidas

- Vitrine pública dos próximos eventos e dos eventos já realizados, com vagas restantes.
- Inscrição com nome completo, e-mail e idade ou série, sem login.
- Bloqueio de inscrição duplicada, de evento lotado e de evento que já começou.
- Login da equipe com JWT. A senha é armazenada com BCrypt.
- Cadastro, edição e exclusão de eventos. O criador é o membro autenticado.
- Lista de inscritas e exportação em CSV para a chamada.
- Cadastro de outros membros da equipe.
- Respostas de erro padronizadas: 400 para dados inválidos, 401 para falta de login, 404 para registro inexistente e 409 para regra de negócio.

### Ordem do teste

Siga esta ordem. A inscrição depende de um evento já cadastrado, e o evento depende do login.

1. Suba o banco, a API e o site, como no roteiro de execução.
2. Abra `http://localhost:5173`. A vitrine deve abrir sem pedir senha.
3. Entre em **Área da equipe** e use a conta padrão abaixo.
4. Em **Eventos**, crie um evento com data futura, local e limite de vagas maior que zero.
5. Saia do painel. Na vitrine, abra o evento criado e inscreva uma aluna com nome e sobrenome, e-mail válido e idade ou série.
6. A tela deve confirmar a inscrição e a quantidade de vagas deve diminuir.
7. Tente inscrever o mesmo e-mail de novo no mesmo evento. A inscrição deve ser recusada.
8. Tente inscrever usando só o primeiro nome. O formulário deve pedir o nome completo.
9. Entre outra vez na área da equipe, abra **Inscritas** desse evento e confira nome, e-mail e idade ou série.
10. Use **Exportar CSV** e abra o arquivo no Excel. As colunas esperadas são Nome, E-mail, Idade/Série, Data da inscrição e Presença.
11. Em **Membros**, cadastre outro membro. Saia e entre com o e-mail e a senha desse membro.
12. Em **Eventos**, use o filtro de já realizados. O evento futuro não deve aparecer nessa lista.
13. Exclua o evento de teste. A exclusão também remove as inscrições dele.

Teste rápido da API, sem o site: abra `http://localhost:8080/api/eventos`. A resposta é uma lista JSON. Criar evento por essa URL sem o cabeçalho `Authorization: Bearer <token>` deve responder 401.

### Contas de acesso padrão

Uso local, criadas automaticamente na primeira execução da API:

| Campo | Valor |
|---|---|
| Nome | Admin Meninas Digitais |
| E-mail | victorfarescorrea@gmail.com |
| Senha | victor123 |

A aluna não tem conta. A inscrição é feita na página do evento, sem login.

No ambiente publicado, o e-mail e a senha do primeiro administrador são os valores de `ADMIN_EMAIL` e `ADMIN_SENHA` configurados no Render antes da primeira subida. Se essas variáveis não forem definidas, vale a mesma conta da tabela acima.

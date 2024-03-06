# Golden Raspberry Awards API

# Descrição 
Esta API fornece informações sobre os indicados e vencedores da categoria Pior Filme do Golden Raspberry Awards.

# 1 - Clonar o repositorio
  ./git clone https://github.com/danielortegaalves/raspberry-awards.git
  Navegue até o diretório do projeto: cd nome-do-repositorio
  Entre na branch 'master': ./git checkout master

# 2 - Como Testar

  # 2.1 - Via Postman
    Abra o Postman.
    No canto superior esquerdo, clique em "Import".
    Selecione a opção "Upload Files" e faça o upload do arquivo JSON da coleção: path do arquivo: raspberry-awards/Golden Raspberry Awards.postman_collection.json
    Após importar, você verá a coleção "Golden Raspberry Awards" no painel esquerdo.
    
   # Executando a aplicação
    Navegue até o diretório do projeto: cd nome-do-repositorio
    Execute a aplicação: ./mvnw spring-boot:run
    Volte ao postman, selecione a requisição desejada no painel esquerdo.
    Clique no botão "Send" para executar a requisição.

  # 2.2 - Via teste de integração:
    Navegue até o diretório do projeto: cd nome-do-repositorio
    Execute os testes da aplicação: ./mvnw test

# 3 - Acessar o Banco de Dados via Browser
Com a aplicação rodando, é possível acessar a base de dados em memória: 
URL do console H2: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Usuário: sa
Senha: password

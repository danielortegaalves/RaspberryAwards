# Golden Raspberry Awards API

# Descrição 
Esta API fornece informações sobre os indicados e vencedores da categoria Pior Filme do Golden Raspberry Awards.

# 1 Clonar o repositorio e ir até a branch 'master'
  	git clone https://github.com/danielortegaalves/raspberry-awards.git
  	cd raspberry-awards
  	git checkout master	
  
# 2 Executar a aplicação
	./mvnw spring-boot:run

# 3 - Testar via CURL
	curl --location 'http://localhost:8080/filmes/intervalos-premio'
	
# 4 - Testar via Postman
    Importar a collection: raspberry-awards/Golden Raspberry Awards.postman_collection.json
    Chamar a requisição GET http://localhost:8080/filmes/intervalos-premio

# 5 - Executar teste de integração:
    ./mvnw test
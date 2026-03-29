### Trabalho 1 - Compiladores 

Alunos: 
Karys Cristina da Silva Barbosa - 811871

Nathália Brasilino Gimenes - 812076

Pedro Henrique Ghiotto - 812115


Este projeto foi desenvolvido utilizando a linguagem de programação Java criado com maven para facilitar os detalhes de compilação e build. 

### Como compilar o arquivo Jander.g4 para gerar a gramática que será lida pelo compilador 

> mvn clean compile

### Em seguida buildar o projeto inteiro com o maven
> mvn clean package 

Após compilar e buildar o projeto já é possível executar o código do nosso analisador léxico. 

### Como rodar o analisador léxico: 
java -jar target/trabalho1-1.0-SNAPSHOT-jar-with-dependencies.jar <caminho arquivo entrada> <caminho arquivo saida>




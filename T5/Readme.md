# Trabalho 5 - Compiladores
Alunos:
- Karys Cristina da Silva Barbosa - 811871
- Nathália Brasilino Gimenes - 812076
- Pedro Henrique Ghiotto - 812115

Este projeto foi desenvolvido utilizando a linguagem de programação Java, criado com Maven para facilitar os detalhes de compilação e build. 

O trabalho implementa um analisador léxico, sintático e semântico para a linguagem LA, utilizando ANTLR4 para geração do léxico e da gramática. O analisador detecta e reporta erros léxicos e sintáticos como símbolos não identificados, cadeias literais não fechadas, erros de atribuição e compatibilidade de variáveis entre outros. 

Essa etapa do projeto inclui-se a geração de códigos C. 

## Como compilar o arquivo Jander.g4 para gerar a gramática que será lida pelo compilador
```
mvn clean compile
```

## Em seguida buildar o projeto inteiro com o Maven
```
mvn clean package
```

Após compilar e buildar o projeto já é possível executar o código do nosso analisador sintático. 
Ao rodar este comando o seguinte arquivo aparecerá na pasta de target: 
```
target/T5-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Como rodar o analisador sintático
```
java -jar target/T5-1.0-SNAPSHOT-jar-with-dependencies.jar (caminho arquivo entrada) (caminho arquivo saida)
```

## Exemplo de uso
```
java -jar target/T5-1.0-SNAPSHOT-jar-with-dependencies.jar entrada.txt saida.txt
```

Em caso de erro, a saída conterá a linha e a descrição do problema encontrado, seguida de `Fim da compilacao`. Se nenhum erro for encontrado, o arquivo de saída será gerado vazio.

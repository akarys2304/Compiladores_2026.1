package br.ufscar.dc.compiladores.t3; 

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Principal {
    public static void main(String[] args) {
        // Verifica se os dois argumentos obrigatórios foram passados 
        if (args.length < 2) {
            System.out.println("Uso: java -jar compilador.jar <entrada> <saida>");
            return;
        }

        String arquivoEntrada = args[0]; // Argumento 1: entrada [cite: 43]
        String arquivoSaida = args[1];   // Argumento 2: saída [cite: 44]

        try (PrintWriter pw = new PrintWriter(new File(arquivoSaida))) {
            // 1. Lendo o arquivo de entrada
            CharStream cs = CharStreams.fromFileName(arquivoEntrada);
            
            // 2. Criando o Lexer com a sua gramática "Jander"
            JanderLexer lexer = new JanderLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            
            // 3. Criando o Parser
            JanderParser parser = new JanderParser(tokens);
            
            // 4. Gerando a árvore sintática a partir da regra inicial "programa"
            JanderParser.ProgramaContext tree = parser.programa();

            // 5. Instanciando o seu Analisador Semântico (que faremos no Passo 3)
            AnalisadorSemantico semantico = new AnalisadorSemantico();
            semantico.visitPrograma(tree);

            // 6. Gravando os erros no arquivo de saída [cite: 51, 52]
            //for (String erro : semantico.errosSemanticos) {
            //    pw.println(erro);
            //}
            
            // Finalização obrigatória conforme os casos de teste [cite: 40]
            pw.println("Fim da compilacao.");

        } catch (IOException e) {
            System.err.println("Erro ao abrir os arquivos: " + e.getMessage());
        }
    }
}
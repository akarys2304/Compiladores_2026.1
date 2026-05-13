package br.ufscar.dc.compiladores.t4; 

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Principal {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java -jar compilador.jar <entrada> <saida>");
            return;
        }

        String arquivoEntrada = args[0]; 
        String arquivoSaida = args[1];   

        try (PrintWriter pw = new PrintWriter(new File(arquivoSaida))) {
            CharStream cs = CharStreams.fromFileName(arquivoEntrada);
            JanderLexer lexer = new JanderLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JanderParser parser = new JanderParser(tokens);
            
            JanderParser.ProgramaContext tree = parser.programa();

            AnalisadorSemantico semantico = new AnalisadorSemantico();
            semantico.visitPrograma(tree);

            for (String erro : AnalisadorSemanticoUtils.errosSemanticos) {
                pw.println(erro);
            }
            
            pw.println("Fim da compilacao");

        } catch (IOException e) {
            System.err.println("Erro ao abrir os arquivos: " + e.getMessage());
        }
    }
}
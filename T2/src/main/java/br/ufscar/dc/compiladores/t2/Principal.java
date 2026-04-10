package br.ufscar.dc.compiladores.t2; 

import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;


public class Principal {
    public static void main(String args[]) throws IOException {
        // Verifica se o argumento foi passado para evitar erro de Array
        if (args.length == 0) {
            System.out.println("Caminho do arquivo de entrada não fornecido.");
            return;
        }

        CharStream cs = CharStreams.fromFileName(args[0]);
        JanderLexer lexer = new JanderLexer(cs);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JanderParser parser = new JanderParser(tokens);
        
        parser.programa();
    }
}
package br.ufscar.dc.compiladores.t2; 

import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.*;

public class Principal {
    public static void main(String args[]) throws IOException {

        if (args.length < 2) {
            System.out.println("Uso: java -jar <jar> <entrada> <saida>");
            return;
        }

        CharStream cs = CharStreams.fromFileName(args[0]);

        JanderLexer lexer = new JanderLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JanderParser parser = new JanderParser(tokens);

        PrintWriter writer = new PrintWriter(args[1]);

        parser.removeErrorListeners();
        lexer.removeErrorListeners();

        MyCustomErrorListener erro = new MyCustomErrorListener(writer);
        parser.addErrorListener(erro);
        lexer.addErrorListener(erro);

        try {
            parser.programa();
            writer.println("Fim da compilacao");
        } catch (RuntimeException e) {
            // erro já tratado no listener
        }

        writer.close();
    }
}
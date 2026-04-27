package br.ufscar.dc.compiladores.t3; 

import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.*;

public class Principal {
    public static void main(String args[]) throws IOException {
        //verificação arquivos de entrada e saida
        if (args.length < 2) {
            System.out.println("Uso: java -jar <jar> <entrada> <saida>");
            return;
        }

        //criação do fluxo de leitura do arquivo de entrada
        CharStream cs = CharStreams.fromFileName(args[0]);

        JanderLexer lexer = new JanderLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JanderParser parser = new JanderParser(tokens);
        
        //arquivo de saída
        PrintWriter writer = new PrintWriter(args[1]);
        
        //remover os listeners de erro padrão do ANTLR
        //para evitar mensagens de erro padrão e usar nosso listener personalizado
        parser.removeErrorListeners();
        lexer.removeErrorListeners();

        MyCustomErrorListener erro = new MyCustomErrorListener(writer);
        parser.addErrorListener(erro);
        lexer.addErrorListener(erro);

        //iniciar a análise sintática do programa
        try {
            parser.programa();
        } catch (RuntimeException e) {
            // erro já tratado no listener
        }

        writer.close();
    }
}

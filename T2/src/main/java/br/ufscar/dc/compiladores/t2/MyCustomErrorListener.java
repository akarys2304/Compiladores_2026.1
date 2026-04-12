package br.ufscar.dc.compiladores.t2;

import java.io.PrintWriter;
import org.antlr.v4.runtime.*;

public class MyCustomErrorListener extends BaseErrorListener {

    private PrintWriter pw;

    public MyCustomErrorListener(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {

        Token t = (Token) offendingSymbol;

        String tipo = JanderLexer.VOCABULARY.getDisplayName(t.getType());
        String mensagem;

        //classifica o tipo de erro com base no token e na mensagem de erro
        if ("ERRO".equals(tipo)) {
            mensagem = "Linha " + line + ": " + t.getText() + " - simbolo nao identificado";
        } 
        else if ("CADEIA_NAO_FECHADA".equals(tipo)) {
            mensagem = "Linha " + line + ": cadeia literal nao fechada";
        } 
        else if ("<EOF>".equals(t.getText())) {
            mensagem = "Linha " + line + ": erro sintatico proximo a EOF";
        }
        else if("COMENTARIO_NAO_FECHADO".equals(tipo)) {
            mensagem = "Linha " + line + ": comentario nao fechado";
        }
        else {
            mensagem = "Linha " + line + ": erro sintatico proximo a " + t.getText();
        }

        //escreve a mensagem de erro e encerrar a compilação
        pw.println(mensagem);
        pw.println("Fim da compilacao");

    
        throw new RuntimeException();
    }
}


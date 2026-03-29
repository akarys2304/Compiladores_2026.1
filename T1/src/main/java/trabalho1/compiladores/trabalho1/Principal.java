package trabalho1.compiladores.trabalho1;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import trabalho1.compiladores.trabalho1.parser.Jander;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

public class Principal {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java -jar compilador.jar <entrada> <saida>");
            return;
        }

        String arquivoEntrada = args[0];
        String arquivoSaida = args[1];

        try (PrintWriter escritor = new PrintWriter(arquivoSaida)) {
            CharStream cs = CharStreams.fromPath(Paths.get(arquivoEntrada));
            Jander lexer = new Jander(cs);
            
            Token t;
            while ((t = lexer.nextToken()).getType() != Token.EOF) {
                String nomeTipo = Jander.VOCABULARY.getDisplayName(t.getType());
                String lexema = t.getText();
                int linha = t.getLine();

                if (Erro(nomeTipo)) {
                    escritor.println(gerarMensagemErro(linha, lexema, nomeTipo));
                    escritor.flush();
                    return; 
                }

                escritor.println(formatarSaida(lexema, nomeTipo));
            }

        } catch (IOException e) {
            System.err.println("Erro ao processar arquivos: " + e.getMessage());
        }
    }

    
    private static boolean Erro(String nome) {
        return nome.equals("COMENTARIO_NAO_FECHADO") || nome.equals("CADEIA_NAO_FECHADA") || nome.equals("INVALIDO");
    }

    private static String gerarMensagemErro(int linha, String texto, String nome) {
        if (nome.equals("CADEIA_NAO_FECHADA")) {
            return "Linha " + linha + ": cadeia literal nao fechada";
        } else if (nome.equals("COMENTARIO_NAO_FECHADO")) {
            return "Linha " + linha + ": comentario nao fechado";
        }
        return "Linha " + linha + ": " + texto + " - simbolo nao identificado";
    }

    private static String formatarSaida(String lexema, String tipo) {
        if (tipo.equals("IDENT") || tipo.equals("CADEIA") || 
            tipo.equals("NUM_INT") || tipo.equals("NUM_REAL")) {
            return "<'" + lexema + "'," + tipo + ">";
        } 
        
        return "<'" + lexema + "','" + lexema + "'>";
    }
}
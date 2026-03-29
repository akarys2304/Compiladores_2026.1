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
        // O corretor automático sempre passa dois argumentos: entrada e saida
        if (args.length < 2) {
            System.err.println("Uso: java -jar compilador.jar <entrada> <saida>");
            return;
        }

        String arquivoEntrada = args[0];
        String arquivoSaida = args[1];

        try (PrintWriter escritor = new PrintWriter(arquivoSaida)) {
            // Leitura do arquivo de entrada
            CharStream cs = CharStreams.fromPath(Paths.get(arquivoEntrada));
            Jander lexer = new Jander(cs);
            
            Token t;
            while ((t = lexer.nextToken()).getType() != Token.EOF) {
                String nomeTipo = Jander.VOCABULARY.getDisplayName(t.getType());
                String lexema = t.getText();
                int linha = t.getLine();

                // 1. Tratamento de Erros (Para no primeiro erro encontrado)
                if (ehTokenDeErro(nomeTipo)) {
                    escritor.println(gerarMensagemErro(linha, lexema, nomeTipo));
                    return; // O manual do professor costuma pedir para parar no primeiro erro
                }

                // 2. Formatação da Saída de Sucesso
                escritor.println(formatarSaida(lexema, nomeTipo));
            }

        } catch (IOException e) {
            System.err.println("Erro ao processar arquivos: " + e.getMessage());
        }
    }

    /**
     * Identifica se o token é um dos tokens de erro definidos no seu .g4
     */
    private static boolean ehTokenDeErro(String nome) {
        return nome.contains("ERRO") || 
               nome.contains("INVALIDO") || 
               nome.contains("NAO_FECHAD");
    }

    /**
     * Formata a mensagem de erro conforme o padrão da apostila
     */
    private static String gerarMensagemErro(int linha, String texto, String nome) {
        if (nome.contains("CADEIA")) {
            return "Linha " + linha + ": cadeia literal nao fechada";
        } else if (nome.contains("COMENTARIO")) {
            return "Linha " + linha + ": comentario nao fechado";
        }
        // Para o token INVALIDO (.)
        return "Linha " + linha + ": " + texto + " - simbolo nao identificado";
    }

    /**
     * Lógica crucial: decide se mostra o nome do tipo ou se repete o lexema
     */
    private static String formatarSaida(String lexema, String tipo) {
        // Apenas IDENT, CADEIA e NUMEROS exibem o nome do tipo no segundo campo
        if (tipo.equals("IDENT") || tipo.equals("CADEIA") || 
            tipo.equals("NUM_INT") || tipo.equals("NUM_REAL")) {
            return "<'" + lexema + "'," + tipo + ">";
        } 
        
        // Para TODO O RESTO (palavras-chave como 'inteiro', 'logico', e símbolos como '+', '<-')
        // o padrão é repetir o próprio lexema entre aspas simples.
        return "<'" + lexema + "','" + lexema + "'>";
    }
}
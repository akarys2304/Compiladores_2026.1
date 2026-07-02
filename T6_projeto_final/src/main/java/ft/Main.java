package ft;

import ft.model.Aluno;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
//entrada do compilador da linguagem FichaTreino

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java -jar fichatreino.jar <arquivo.ft|.txt> [prefixo-saida]");
            System.exit(1);
        }

        String caminho = args[0];
        Path baseSaida = (args.length >= 2) ? Paths.get(args[1]) : caminhoBaseSaida(caminho);
        Path saidaTxt = Paths.get(baseSaida.toString() + ".txt");
        Path saidaHtml = Paths.get(baseSaida.toString() + ".html");

        try {
            //análise léxica/sintática 
            CommonTokenStream tokens = new CommonTokenStream(
                    new FichaTreinoLexer(CharStreams.fromFileName(caminho)));

            FichaTreinoParser parser = new FichaTreinoParser(tokens);

            final List<String> erros = new ArrayList<>();
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                         int line, int charPositionInLine, String msg,
                                         RecognitionException e) {
                    erros.add("[ERRO SINTATICO] linha " + line + ":" + charPositionInLine + " " + msg);
                }
            });

            FichaTreinoParser.ProgramaContext arvore = parser.programa();

            if (!erros.isEmpty()) {
                escreverErros(saidaTxt, "Erro(s) sintatico(s) encontrado(s):", erros);
                System.exit(2);
            }

            // construção do modelo 
            ModeloBuilder builder = new ModeloBuilder();
            builder.visit(arvore);
            List<Aluno> alunos = builder.getAlunos();

            // análise semântica
            SemanticChecker checker = new SemanticChecker();
            checker.verificar(alunos);

            if (checker.temErros()) {
                List<String> mensagens = new ArrayList<>();
                for (ErroSemantico erro : checker.getErros()) {
                    mensagens.add(erro.toString());
                }
                escreverErros(saidaTxt, "Erro(s) semantico(s) encontrado(s):", mensagens);
                System.exit(3);
            }

            // geração / interpretação 
            FichaGenerator gerador = new FichaGenerator();
            String texto = gerador.gerarTexto(alunos);
            String html = gerador.gerarHtml(alunos);

            Files.write(saidaTxt, texto.getBytes(StandardCharsets.UTF_8));
            Files.write(saidaHtml, html.getBytes(StandardCharsets.UTF_8));

            System.out.println("Ficha gerada com sucesso:");
            System.out.println("  - " + saidaTxt.toAbsolutePath());
            System.out.println("  - " + saidaHtml.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Erro ao ler/escrever arquivo: " + e.getMessage());
            System.exit(1);
        }
    }

    // Grava as mensagens de erro no arquivo .txt de saída e também as imprime no terminal
    private static void escreverErros(Path saidaTxt, String titulo, List<String> mensagens) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(titulo).append("\n");
        for (String m : mensagens) {
            sb.append("  - ").append(m).append("\n");
        }

        Files.write(saidaTxt, sb.toString().getBytes(StandardCharsets.UTF_8));

        System.err.println(sb);
        System.err.println("Compilacao interrompida. Detalhes em: " + saidaTxt.toAbsolutePath());
    }
    //gera o caminho de saída
    private static Path caminhoBaseSaida(String caminhoEntrada) {
        Path entrada = Paths.get(caminhoEntrada);
        String nome = entrada.getFileName().toString();
        int ponto = nome.lastIndexOf('.');
        String semExtensao = (ponto >= 0) ? nome.substring(0, ponto) : nome;

        Path pai = entrada.toAbsolutePath().getParent();
        String base = semExtensao + "_saida";
        return (pai != null) ? pai.resolve(base) : Paths.get(base);
    }
}

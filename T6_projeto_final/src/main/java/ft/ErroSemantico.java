package ft;

/**
 * Representa um erro encontrado durante a análise semântica,
 * contendo a linha do código-fonte onde ocorreu e uma mensagem descritiva.
 */
public class ErroSemantico {

    private final int linha;
    private final String mensagem;

    public ErroSemantico(int linha, String mensagem) {
        this.linha = linha;
        this.mensagem = mensagem;
    }

    public int getLinha() {
        return linha;
    }

    public String getMensagem() {
        return mensagem;
    }

    @Override
    public String toString() {
        return "[linha " + linha + "] " + mensagem;
    }
}

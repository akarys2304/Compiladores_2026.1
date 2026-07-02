package ft;

//erro durante a análise semântica
//linha onde ocorreu o erro e mensagem
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

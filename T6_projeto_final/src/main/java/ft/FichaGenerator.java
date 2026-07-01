package ft;

import ft.model.Aluno;
import ft.model.Exercicio;
import ft.model.Treino;

import java.util.List;

/**
 * Fase de geração de código / interpretação: percorre o modelo já validado
 * e produz uma página HTML organizada (a "ficha de treino" formatada),
 * incluindo estatísticas como volume total por exercício, por treino e
 * resumo geral.
 *
 * Isso representa o "algo útil ou interessante feito com a linguagem"
 * exigido pela especificação do trabalho (item GCI).
 */
public class FichaGenerator {

    /**
     * Gera a página HTML completa para todos os alunos do programa.
     */
    public String gerarHtml(List<Aluno> alunos) {
        int totalAlunos = alunos.size();
        int totalTreinos = 0;
        int totalExercicios = 0;
        int volumeGeral = 0;

        StringBuilder corpo = new StringBuilder();

        for (Aluno aluno : alunos) {
            corpo.append("  <section class=\"aluno\">\n");
            corpo.append("    <h2>").append(escapar(aluno.getNome())).append("</h2>\n");

            if (aluno.getIdade() != null || aluno.getPeso() != null || aluno.getObjetivo() != null) {
                corpo.append("    <p class=\"dados\">");
                boolean primeiro = true;
                if (aluno.getIdade() != null) {
                    corpo.append("Idade: <strong>").append(aluno.getIdade()).append(" anos</strong>");
                    primeiro = false;
                }
                if (aluno.getPeso() != null) {
                    if (!primeiro) corpo.append(" &nbsp;|&nbsp; ");
                    corpo.append("Peso: <strong>").append(formatarNumero(aluno.getPeso())).append(" kg</strong>");
                    primeiro = false;
                }
                if (aluno.getObjetivo() != null) {
                    if (!primeiro) corpo.append(" &nbsp;|&nbsp; ");
                    corpo.append("Objetivo: <strong>").append(escapar(aluno.getObjetivo())).append("</strong>");
                }
                corpo.append("</p>\n");
            }

            for (Treino treino : aluno.getTreinos()) {
                totalTreinos++;
                corpo.append("    <div class=\"treino\">\n");
                corpo.append("      <h3>Treino ").append(escapar(treino.getNome())).append("</h3>\n");
                corpo.append("      <table>\n");
                corpo.append("        <thead><tr><th>#</th><th>Exercício</th><th>Séries</th><th>Repetições</th><th>Volume</th></tr></thead>\n");
                corpo.append("        <tbody>\n");

                int idx = 1;
                for (Exercicio ex : treino.getExercicios()) {
                    totalExercicios++;
                    corpo.append("          <tr>")
                         .append("<td>").append(idx++).append("</td>")
                         .append("<td>").append(escapar(ex.getNome())).append("</td>")
                         .append("<td>").append(ex.getSeries()).append("</td>")
                         .append("<td>").append(ex.getRepeticoes()).append("</td>")
                         .append("<td>").append(ex.getVolume()).append("</td>")
                         .append("</tr>\n");
                }

                corpo.append("        </tbody>\n");
                corpo.append("      </table>\n");
                corpo.append("      <p class=\"totais\">Séries totais do treino: <strong>")
                     .append(treino.seriesTotais())
                     .append("</strong> &nbsp;|&nbsp; Volume total: <strong>")
                     .append(treino.volumeTotal())
                     .append("</strong></p>\n");
                corpo.append("    </div>\n");

                volumeGeral += treino.volumeTotal();
            }

            corpo.append("  </section>\n");
        }

        String resumo = "  <section class=\"resumo\">\n"
                + "    <h2>Resumo geral</h2>\n"
                + "    <ul>\n"
                + "      <li>Alunos: <strong>" + totalAlunos + "</strong></li>\n"
                + "      <li>Treinos: <strong>" + totalTreinos + "</strong></li>\n"
                + "      <li>Exercícios: <strong>" + totalExercicios + "</strong></li>\n"
                + "      <li>Volume total (séries × repetições, somado): <strong>" + volumeGeral + "</strong></li>\n"
                + "    </ul>\n"
                + "  </section>\n";

        return montarPagina(corpo.toString(), resumo);
    }

    /**
     * Gera a versão em texto puro (.txt) da ficha de treino, com a mesma
     * informação apresentada no HTML, em formato tabular simples.
     */
    public String gerarTexto(List<Aluno> alunos) {
        StringBuilder sb = new StringBuilder();

        int totalAlunos = alunos.size();
        int totalTreinos = 0;
        int totalExercicios = 0;
        int volumeGeral = 0;

        for (Aluno aluno : alunos) {
            sb.append("=========================================\n");
            sb.append("FICHA DE TREINO - ").append(aluno.getNome()).append("\n");
            sb.append("=========================================\n");

            if (aluno.getIdade() != null || aluno.getPeso() != null || aluno.getObjetivo() != null) {
                sb.append("Dados: ");
                boolean primeiro = true;
                if (aluno.getIdade() != null) {
                    sb.append("idade=").append(aluno.getIdade()).append(" anos");
                    primeiro = false;
                }
                if (aluno.getPeso() != null) {
                    if (!primeiro) sb.append(" | ");
                    sb.append("peso=").append(formatarNumero(aluno.getPeso())).append(" kg");
                    primeiro = false;
                }
                if (aluno.getObjetivo() != null) {
                    if (!primeiro) sb.append(" | ");
                    sb.append("objetivo=").append(aluno.getObjetivo());
                }
                sb.append("\n");
            }
            sb.append("\n");

            for (Treino treino : aluno.getTreinos()) {
                totalTreinos++;
                sb.append("-- Treino ").append(treino.getNome()).append(" --\n");

                int idx = 1;
                for (Exercicio ex : treino.getExercicios()) {
                    totalExercicios++;
                    sb.append(String.format("%2d. %-18s | %2d series x %2d repeticoes | volume: %3d%n",
                            idx++, ex.getNome(), ex.getSeries(), ex.getRepeticoes(), ex.getVolume()));
                }

                sb.append(String.format("   Series totais do treino: %d | Volume total: %d%n",
                        treino.seriesTotais(), treino.volumeTotal()));
                volumeGeral += treino.volumeTotal();
                sb.append("\n");
            }
        }

        sb.append("=========================================\n");
        sb.append("RESUMO GERAL\n");
        sb.append("=========================================\n");
        sb.append("Alunos: ").append(totalAlunos).append("\n");
        sb.append("Treinos: ").append(totalTreinos).append("\n");
        sb.append("Exercicios: ").append(totalExercicios).append("\n");
        sb.append("Volume total (series x repeticoes, somado): ").append(volumeGeral).append("\n");

        return sb.toString();
    }

    private String montarPagina(String corpo, String resumo) {
        return "<!DOCTYPE html>\n"
                + "<html lang=\"pt-br\">\n"
                + "<head>\n"
                + "  <meta charset=\"UTF-8\">\n"
                + "  <title>Ficha de Treino</title>\n"
                + "  <style>\n"
                + "    body { font-family: Arial, Helvetica, sans-serif; max-width: 800px; margin: 2rem auto; padding: 0 1rem; color: #222; background: #f7f7f9; }\n"
                + "    h1 { text-align: center; }\n"
                + "    section.aluno { background: #fff; border: 1px solid #ddd; border-radius: 8px; padding: 1rem 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }\n"
                + "    section.resumo { background: #eef5ff; border: 1px solid #cfe2ff; border-radius: 8px; padding: 1rem 1.5rem; }\n"
                + "    h2 { margin-top: 0; border-bottom: 2px solid #4a90d9; padding-bottom: 0.3rem; }\n"
                + "    .dados { color: #555; margin-top: -0.5rem; }\n"
                + "    .treino { margin-top: 1rem; }\n"
                + "    .treino h3 { margin-bottom: 0.3rem; color: #4a90d9; }\n"
                + "    table { width: 100%; border-collapse: collapse; margin-bottom: 0.5rem; }\n"
                + "    th, td { border: 1px solid #ddd; padding: 0.4rem 0.6rem; text-align: center; }\n"
                + "    th { background: #f0f0f5; }\n"
                + "    tr:nth-child(even) { background: #fafafa; }\n"
                + "    .totais { margin: 0.3rem 0 0; font-size: 0.95rem; }\n"
                + "    .resumo ul { list-style: none; padding-left: 0; }\n"
                + "    .resumo li { padding: 0.2rem 0; }\n"
                + "  </style>\n"
                + "</head>\n"
                + "<body>\n"
                + "  <h1>Ficha de Treino</h1>\n"
                + corpo
                + resumo
                + "</body>\n"
                + "</html>\n";
    }

    private String formatarNumero(double valor) {
        if (valor == Math.floor(valor)) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }

    /** Escapa caracteres especiais de HTML para evitar quebra de markup ou XSS. */
    private String escapar(String texto) {
        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}

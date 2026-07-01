package ft;

import ft.model.Aluno;
import ft.model.Exercicio;
import ft.model.Treino;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Realiza a análise semântica do programa, ou seja, verificações que vão
 * além do que a gramática (análise léxica/sintática) consegue garantir.
 *
 * Verificações implementadas:
 *  1) Nome de exercício não pode se repetir dentro do mesmo treino.
 *  2) O valor de "series" deve ser um número inteiro maior que zero.
 *  3) O valor de "repeticoes" deve ser um número inteiro maior que zero.
 *  4) Todo treino deve conter pelo menos um exercício (treino não pode ser vazio).
 *
 * Verificações extras (bônus):
 *  5) Nome de treino não pode se repetir dentro do mesmo aluno.
 *  6) Se o atributo "objetivo" for informado, deve ser um dos valores
 *     reconhecidos pela linguagem (hipertrofia, resistencia, emagrecimento,
 *     condicionamento, mobilidade).
 */
public class SemanticChecker {

    /** Conjunto de objetivos válidos para o atributo opcional "objetivo". */
    private static final Set<String> OBJETIVOS_VALIDOS = new HashSet<>(List.of(
            "hipertrofia", "resistencia", "emagrecimento", "condicionamento", "mobilidade"
    ));

    private final List<ErroSemantico> erros = new ArrayList<>();

    public List<ErroSemantico> getErros() {
        return erros;
    }

    public boolean temErros() {
        return !erros.isEmpty();
    }

    /**
     * Executa todas as verificações semânticas sobre a lista de alunos.
     */
    public void verificar(List<Aluno> alunos) {
        for (Aluno aluno : alunos) {
            verificarAluno(aluno);
        }
    }

    private void verificarAluno(Aluno aluno) {
        // Verificação extra (5): nomes de treino não podem repetir dentro do aluno
        Set<String> nomesTreinos = new HashSet<>();
        for (Treino treino : aluno.getTreinos()) {
            if (!nomesTreinos.add(treino.getNome())) {
                erros.add(new ErroSemantico(treino.getLinha(),
                        "Treino '" + treino.getNome() + "' duplicado para o aluno '" + aluno.getNome() + "'."));
            }
            verificarTreino(aluno, treino);
        }

        // Verificação extra (6): objetivo, se informado, deve ser válido
        if (aluno.getObjetivo() != null && !OBJETIVOS_VALIDOS.contains(aluno.getObjetivo())) {
            erros.add(new ErroSemantico(aluno.getLinha(),
                    "Objetivo '" + aluno.getObjetivo() + "' inválido para o aluno '" + aluno.getNome()
                            + "'. Valores aceitos: " + OBJETIVOS_VALIDOS));
        }
    }

    private void verificarTreino(Aluno aluno, Treino treino) {

        // Verificação (4): treino não pode estar vazio
        if (treino.getExercicios().isEmpty()) {
            erros.add(new ErroSemantico(treino.getLinha(),
                    "Treino '" + treino.getNome() + "' do aluno '" + aluno.getNome()
                            + "' não possui nenhum exercício."));
            // não há mais nada a verificar neste treino
            return;
        }

        // Verificação (1): exercício não pode repetir dentro do treino
        Set<String> nomesExercicios = new HashSet<>();
        for (Exercicio ex : treino.getExercicios()) {
            if (!nomesExercicios.add(ex.getNome())) {
                erros.add(new ErroSemantico(ex.getLinha(),
                        "Exercício '" + ex.getNome() + "' duplicado no treino '" + treino.getNome()
                                + "' do aluno '" + aluno.getNome() + "'."));
            }

            // Verificação (2): series > 0
            if (ex.getSeries() <= 0) {
                erros.add(new ErroSemantico(ex.getLinha(),
                        "Exercício '" + ex.getNome() + "' (treino '" + treino.getNome()
                                + "', aluno '" + aluno.getNome() + "') tem número de séries inválido: "
                                + ex.getSeries() + ". O valor deve ser maior que zero."));
            }

            // Verificação (3): repeticoes > 0
            if (ex.getRepeticoes() <= 0) {
                erros.add(new ErroSemantico(ex.getLinha(),
                        "Exercício '" + ex.getNome() + "' (treino '" + treino.getNome()
                                + "', aluno '" + aluno.getNome() + "') tem número de repetições inválido: "
                                + ex.getRepeticoes() + ". O valor deve ser maior que zero."));
            }
        }
    }
}

package ft;

import ft.model.Aluno;
import ft.model.Exercicio;
import ft.model.Treino;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
// realiza a análise semântica do programa, as verificações que vão além do que a gramática garante

public class SemanticChecker {

    //objetivos válidos para o atributo opcional "objetivo"
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

    //executa todas as verificações semânticas sobre a lista de alunos
    public void verificar(List<Aluno> alunos) {
        for (Aluno aluno : alunos) {
            verificarAluno(aluno);
        }
    }

    private void verificarAluno(Aluno aluno) {
        //verificação: nomes de treino não podem repetir dentro do aluno
        Set<String> nomesTreinos = new HashSet<>();
        for (Treino treino : aluno.getTreinos()) {
            if (!nomesTreinos.add(treino.getNome())) {
                erros.add(new ErroSemantico(treino.getLinha(),
                        "Treino '" + treino.getNome() + "' duplicado para o aluno '" + aluno.getNome() + "'."));
            }
            verificarTreino(aluno, treino);
        }

        //verificação extra:objetivo, se informado, deve ser válido
        if (aluno.getObjetivo() != null && !OBJETIVOS_VALIDOS.contains(aluno.getObjetivo())) {
            erros.add(new ErroSemantico(aluno.getLinha(),
                    "Objetivo '" + aluno.getObjetivo() + "' inválido para o aluno '" + aluno.getNome()
                            + "'. Valores aceitos: " + OBJETIVOS_VALIDOS));
        }
    }

    private void verificarTreino(Aluno aluno, Treino treino) {

        // treino não pode estar vazio
        if (treino.getExercicios().isEmpty()) {
            erros.add(new ErroSemantico(treino.getLinha(),
                    "Treino '" + treino.getNome() + "' do aluno '" + aluno.getNome()
                            + "' não possui nenhum exercício."));
            // não há mais nada a verificar neste treino
            return;
        }

        //exercício não pode repetir dentro do treino
        Set<String> nomesExercicios = new HashSet<>();
        for (Exercicio ex : treino.getExercicios()) {
            if (!nomesExercicios.add(ex.getNome())) {
                erros.add(new ErroSemantico(ex.getLinha(),
                        "Exercício '" + ex.getNome() + "' duplicado no treino '" + treino.getNome()
                                + "' do aluno '" + aluno.getNome() + "'."));
            }

            //series > 0
            if (ex.getSeries() <= 0) {
                erros.add(new ErroSemantico(ex.getLinha(),
                        "Exercício '" + ex.getNome() + "' (treino '" + treino.getNome()
                                + "', aluno '" + aluno.getNome() + "') tem número de séries inválido: "
                                + ex.getSeries() + ". O valor deve ser maior que zero."));
            }

            //repeticoes > 0
            if (ex.getRepeticoes() <= 0) {
                erros.add(new ErroSemantico(ex.getLinha(),
                        "Exercício '" + ex.getNome() + "' (treino '" + treino.getNome()
                                + "', aluno '" + aluno.getNome() + "') tem número de repetições inválido: "
                                + ex.getRepeticoes() + ". O valor deve ser maior que zero."));
            }
        }
    }
}

package ft;

import ft.model.Aluno;
import ft.model.Exercicio;
import ft.model.Treino;

import java.util.ArrayList;
import java.util.List;
//percorre a árvore sintática gerada pelo ANTLR
//traduz a árvore concreta em um modelo de domínio

public class ModeloBuilder extends FichaTreinoBaseVisitor<Object> {

    private final List<Aluno> alunos = new ArrayList<>();

    public List<Aluno> getAlunos() {
        return alunos;
    }

    @Override
    public Object visitPrograma(FichaTreinoParser.ProgramaContext ctx) {
        for (FichaTreinoParser.AlunoContext alunoCtx : ctx.aluno()) {
            visit(alunoCtx);
        }
        return null;
    }

    @Override
    public Object visitAluno(FichaTreinoParser.AlunoContext ctx) {
        String nome = ctx.ID().getText();
        Aluno aluno = new Aluno(nome, ctx.getStart().getLine());

        for (FichaTreinoParser.AtributoAlunoContext attrCtx : ctx.atributoAluno()) {
            if (attrCtx instanceof FichaTreinoParser.AtribIdadeContext) {
                FichaTreinoParser.AtribIdadeContext idadeCtx = (FichaTreinoParser.AtribIdadeContext) attrCtx;
                aluno.setIdade(Integer.parseInt(idadeCtx.INT().getText()));
            } else if (attrCtx instanceof FichaTreinoParser.AtribPesoContext) {
                FichaTreinoParser.AtribPesoContext pesoCtx = (FichaTreinoParser.AtribPesoContext) attrCtx;
                aluno.setPeso(parseNumero(pesoCtx.numero()));
            } else if (attrCtx instanceof FichaTreinoParser.AtribObjetivoContext) {
                FichaTreinoParser.AtribObjetivoContext objCtx = (FichaTreinoParser.AtribObjetivoContext) attrCtx;
                aluno.setObjetivo(objCtx.ID().getText());
            }
        }

        for (FichaTreinoParser.TreinoContext treinoCtx : ctx.treino()) {
            aluno.addTreino((Treino) visit(treinoCtx));
        }

        alunos.add(aluno);
        return aluno;
    }

    @Override
    public Object visitTreino(FichaTreinoParser.TreinoContext ctx) {
        String nome = ctx.ID().getText();
        Treino treino = new Treino(nome, ctx.getStart().getLine());

        for (FichaTreinoParser.ExercicioContext exCtx : ctx.exercicio()) {
            treino.addExercicio((Exercicio) visit(exCtx));
        }

        return treino;
    }

    @Override
    public Object visitExercicio(FichaTreinoParser.ExercicioContext ctx) {
        String nome = ctx.ID().getText();
        int series = parseValorInt(ctx.valorInt(0));
        int repeticoes = parseValorInt(ctx.valorInt(1));
        return new Exercicio(nome, series, repeticoes, ctx.getStart().getLine());
    }

    private int parseValorInt(FichaTreinoParser.ValorIntContext ctx) {
        // o sinal de '-' é opcional na gramática; se presente, o valor fica negativo
        String texto = ctx.getText();
        return Integer.parseInt(texto);
    }

    private double parseNumero(FichaTreinoParser.NumeroContext ctx) {
        String texto = ctx.getText();
        return Double.parseDouble(texto);
    }
}

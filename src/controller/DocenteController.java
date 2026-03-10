package controller;

import model.*;

public class DocenteController {

    private SistemaAcademico sistema;

    public DocenteController(SistemaAcademico sistema) {
        this.sistema = sistema;
    }

    // Adiciona nota a um estudante numa UC
    public void adicionarNota(Estudante estudante, UnidadeCurricular uc, int numeroAvaliacao, double nota) {
        PercursoAcademico pa = estudante.getPercursoAcademico();
        if (pa == null) {
            pa = new PercursoAcademico(estudante);
            estudante.setPercursoAcademico(pa);
        }

        Avaliacao avaliacao = pa.getAvaliacao(uc);
        if (avaliacao == null) {
            avaliacao = new Avaliacao(estudante, uc, sistema.getAnoAtual());
            pa.adicionarAvaliacao(avaliacao);
        }

        avaliacao.adicionarNota(nota);
        System.out.println("Nota registada: " + nota + " para " + estudante.getNome() + " na UC " + uc.getNome());
    }

    // Lista UCs que o docente leciona
    public UnidadeCurricular[] listarUCsDocente(Docente d) {
        UnidadeCurricular[] todas = sistema.getUcs();
        UnidadeCurricular[] lecionadas = new UnidadeCurricular[100];
        int count = 0;
        for (int i = 0; i < sistema.getTotalUCs(); i++) {
            if (todas[i].getDocenteResponsavel() == d) {
                lecionadas[count++] = todas[i];
            }
        }
        UnidadeCurricular[] result = new UnidadeCurricular[count];
        for (int i = 0; i < count; i++) result[i] = lecionadas[i];
        return result;
    }

    public Avaliacao getAvaliacao(Estudante e, UnidadeCurricular uc) {
        PercursoAcademico pa = e.getPercursoAcademico();
        if (pa != null) return pa.getAvaliacao(uc);
        return null;
    }

    public void adicionarAvaliacao(Estudante e, UnidadeCurricular uc, double nota) {
        PercursoAcademico pa = e.getPercursoAcademico();
        if (pa == null) {
            pa = new PercursoAcademico(e);
            e.setPercursoAcademico(pa);
        }

        Avaliacao a = pa.getAvaliacao(uc);
        if (a == null) {
            a = new Avaliacao(e, uc, 2026);
            pa.adicionarAvaliacao(a);
        }
        a.adicionarNota(nota);
    }
}
package utils;

import model.RepositorioDados;
import model.Estudante;
import model.Curso;
import model.Docente;
import model.Avaliacao;

public class Estatisticas {

    private Estatisticas() {}

    // =========================================================
    // ESTATÍSTICAS GLOBAIS (PARA O GESTOR)
    // =========================================================

    /**
     * Calcula a média de todas as avaliações registadas na instituição (Ano atual + Histórico).
     */
    public static double calcularMediaGlobalInstituicao(RepositorioDados repo) {
        double somaTotal = 0;
        int contadorAvaliacoes = 0;

        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            Estudante e = repo.getEstudantes()[i];

            // Notas do ano corrente
            for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                somaTotal += e.getAvaliacoes()[j].calcularMedia();
                contadorAvaliacoes++;
            }
            // Notas do histórico
            for (int j = 0; j < e.getTotalHistorico(); j++) {
                somaTotal += e.getHistoricoAvaliacoes()[j].calcularMedia();
                contadorAvaliacoes++;
            }
        }

        if (contadorAvaliacoes == 0) return 0.0;

        // Arredondar a 2 casas decimais
        return Math.round((somaTotal / contadorAvaliacoes) * 100.0) / 100.0;
    }

    /**
     * Encontra o estudante com a melhor média global (juntando o ano atual e o histórico).
     */
    public static String identificarMelhorAluno(RepositorioDados repo) {
        if (repo.getTotalEstudantes() == 0) return "Sem alunos registados.";

        Estudante melhorEstudante = null;
        double melhorMedia = -1.0;

        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            Estudante e = repo.getEstudantes()[i];
            double somaAluno = 0;
            int contAluno = 0;

            for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                somaAluno += e.getAvaliacoes()[j].calcularMedia();
                contAluno++;
            }
            for (int j = 0; j < e.getTotalHistorico(); j++) {
                somaAluno += e.getHistoricoAvaliacoes()[j].calcularMedia();
                contAluno++;
            }

            if (contAluno > 0) {
                double mediaAluno = somaAluno / contAluno;
                if (mediaAluno > melhorMedia) {
                    melhorMedia = mediaAluno;
                    melhorEstudante = e;
                }
            }
        }

        if (melhorEstudante == null) return "Ainda não existem notas lançadas.";

        double mediaArredondada = Math.round(melhorMedia * 100.0) / 100.0;
        return melhorEstudante.getNome() + " (" + melhorEstudante.getNumeroMecanografico() + ") com média de " + mediaArredondada;
    }

    /**
     * Identifica qual é o Curso que tem o maior número de alunos ativos.
     */
    public static Curso obterCursoComMaisAlunos(RepositorioDados repo) {
        if (repo.getTotalCursos() == 0) return null;

        Curso cursoVencedor = null;
        int maxAlunos = -1;

        for (int i = 0; i < repo.getTotalCursos(); i++) {
            Curso c = repo.getCursos()[i];
            int contadorAlunos = 0;

            for (int j = 0; j < repo.getTotalEstudantes(); j++) {
                Estudante e = repo.getEstudantes()[j];
                if (e.getCurso() != null && e.getCurso().getSigla().equals(c.getSigla())) {
                    contadorAlunos++;
                }
            }

            if (contadorAlunos > maxAlunos) {
                maxAlunos = contadorAlunos;
                cursoVencedor = c;
            }
        }
        return cursoVencedor;
    }


    // =========================================================
    // ESTATÍSTICAS FILTRADAS (PARA O DOCENTE)
    // =========================================================

    /**
     * Calcula a média de notas APENAS nas Unidades Curriculares que o Docente leciona.
     */
    public static double calcularMediaUCsDocente(Docente docente, RepositorioDados repo) {
        if (docente.getTotalUcsLecionadas() == 0) return 0.0;

        double somaTotal = 0;
        int contadorAvaliacoes = 0;

        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            Estudante e = repo.getEstudantes()[i];

            // Analisar notas atuais do aluno
            for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                Avaliacao av = e.getAvaliacoes()[j];
                if (docenteLecionaUC(docente, av.getUnidadeCurricular().getSigla())) {
                    somaTotal += av.calcularMedia();
                    contadorAvaliacoes++;
                }
            }
        }

        if (contadorAvaliacoes == 0) return 0.0;
        return Math.round((somaTotal / contadorAvaliacoes) * 100.0) / 100.0;
    }

    /**
     * Calcula quantos alunos ÚNICOS têm notas lançadas nas disciplinas do Docente.
     */
    public static int contarAlunosAvaliadosDoDocente(Docente docente, RepositorioDados repo) {
        int totalAlunosUnicos = 0;

        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            Estudante e = repo.getEstudantes()[i];
            boolean alunoAvaliadoPeloDocente = false;

            for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                Avaliacao av = e.getAvaliacoes()[j];
                if (docenteLecionaUC(docente, av.getUnidadeCurricular().getSigla())) {
                    alunoAvaliadoPeloDocente = true;
                    break; // Basta uma avaliação para o aluno contar
                }
            }

            if (alunoAvaliadoPeloDocente) {
                totalAlunosUnicos++;
            }
        }
        return totalAlunosUnicos;
    }

    // Método auxiliar privado para verificar se o docente leciona uma determinada UC
    private static boolean docenteLecionaUC(Docente docente, String siglaUC) {
        for (int i = 0; i < docente.getTotalUcsLecionadas(); i++) {
            if (docente.getUcsLecionadas()[i].getSigla().equals(siglaUC)) {
                return true;
            }
        }
        return false;
    }
}
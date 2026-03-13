package utils;

import model.RepositorioDados;
import model.UnidadeCurricular;
import model.Curso;

public class Estatisticas {

    // ---------- CONSTRUTOR ----------
    /**
     * Construtor privado para evitar instanciação.
     * Esta é uma classe utilitária estática.
     */
    private Estatisticas() {}

    // ---------- MÉTODOS DE CÁLCULO (ESQUELETOS / STUBS) ----------

    /**
     * Calcula a média global de todas as notas lançadas em toda a instituição.
     * @param repo O repositório central de dados.
     * @return O valor da média global.
     */
    public static double calcularMediaGlobalInstituicao(RepositorioDados repo) {
        // TODO: Percorrer todos os alunos e todas as suas notas para fazer a média geral.
        return 0.0; // Devolve 0.0 provisoriamente
    }

    /**
     * Identifica qual é o Curso que tem o maior número de alunos inscritos.
     * @param repo O repositório central de dados.
     * @return O objeto Curso com mais alunos.
     */
    public static Curso obterCursoComMaisAlunos(RepositorioDados repo) {
        // TODO: Contar os alunos por curso e descobrir o máximo.
        return null; // Devolve null provisoriamente
    }

    /**
     * Calcula a taxa de aprovação (em percentagem) de uma determinada UC.
     * @param uc A Unidade Curricular a analisar.
     * @param repo O repositório central de dados.
     * @return A percentagem de alunos com nota >= 9.5.
     */
    public static double calcularTaxaAprovacaoUC(UnidadeCurricular uc, RepositorioDados repo) {
        // TODO: Percorrer as notas dos alunos nesta UC e ver quantos tiveram positiva.
        return 0.0; // Devolve 0.0 provisoriamente
    }

    /**
     * Identifica o aluno com a melhor média global da faculdade.
     * @param repo O repositório central de dados.
     * @return O nome e número mecanográfico do melhor aluno formatado em String.
     */
    public static String identificarMelhorAluno(RepositorioDados repo) {
        // TODO: Calcular a média individual de cada aluno e encontrar a maior.
        return "Em desenvolvimento...";
    }
}
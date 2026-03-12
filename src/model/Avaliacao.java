// Ficheiro: model/Avaliacao.java
package model;

public class Avaliacao {
    private Estudante estudante;
    private UnidadeCurricular unidadeCurricular;
    private int anoAvaliacao;

    // Array fixo para no máximo 3 avaliações
    private double[] resultadosAvaliacoes;
    private int totalAvaliacoesLancadas;

    public Avaliacao(Estudante estudante, UnidadeCurricular unidadeCurricular, int anoAvaliacao) {
        this.estudante = estudante;
        this.unidadeCurricular = unidadeCurricular;
        this.anoAvaliacao = anoAvaliacao;

        // Regra de negócio: máximo 3 avaliações
        this.resultadosAvaliacoes = new double[3];
        this.totalAvaliacoesLancadas = 0;
    }

    // ---------- GETTERS ----------
    public Estudante getEstudante() { return estudante; }
    public UnidadeCurricular getUnidadeCurricular() { return unidadeCurricular; }
    public int getAnoAvaliacao() { return anoAvaliacao; }
    public double[] getResultadosAvaliacoes() { return resultadosAvaliacoes; }
    public int getTotalAvaliacoesLancadas() { return totalAvaliacoesLancadas; }

    // ---------- MÉTODOS ÚTEIS ----------
    public boolean adicionarResultado(double nota) {
        if (totalAvaliacoesLancadas < resultadosAvaliacoes.length) {
            resultadosAvaliacoes[totalAvaliacoesLancadas] = nota;
            totalAvaliacoesLancadas++;
            return true; // Sucesso ao lançar nota
        }
        return false; // Já lançou as 3 avaliações
    }

    // Calcula a média usando apenas as notas que já foram lançadas
    public double calcularMedia() {
        if (totalAvaliacoesLancadas == 0) {
            return 0.0;
        }

        double soma = 0;
        for (int i = 0; i < totalAvaliacoesLancadas; i++) {
            soma += resultadosAvaliacoes[i];
        }
        return soma / totalAvaliacoesLancadas;
    }
}
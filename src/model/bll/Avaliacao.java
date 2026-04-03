package model.bll;

public class Avaliacao {

    // ---------- ATRIBUTOS ----------
    private Estudante estudante;
    private UnidadeCurricular unidadeCurricular;
    private int anoAvaliacao;
    private double[] resultadosAvaliacoes;
    private int totalAvaliacoesLancadas;

    // ---------- CONSTRUTOR ----------
    public Avaliacao(Estudante estudante, UnidadeCurricular unidadeCurricular, int anoAvaliacao) {
        this.estudante = estudante;
        this.unidadeCurricular = unidadeCurricular;
        this.anoAvaliacao = anoAvaliacao;
        this.resultadosAvaliacoes = new double[3];
        this.totalAvaliacoesLancadas = 0;
    }

    // ---------- GETTERS ----------
    public Estudante getEstudante() { return estudante; }
    public UnidadeCurricular getUnidadeCurricular() { return unidadeCurricular; }
    public UnidadeCurricular getUc() { return unidadeCurricular; } // Mantido para retrocompatibilidade
    public int getAnoAvaliacao() { return anoAvaliacao; }
    public double[] getResultadosAvaliacoes() { return resultadosAvaliacoes; }
    public int getTotalAvaliacoesLancadas() { return totalAvaliacoesLancadas; }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Adiciona um novo resultado (nota) ao array de avaliações do aluno nesta UC.
     * Permite um máximo de 3 notas por avaliação.
     * * @param nota Valor da avaliação a registar.
     * @return true se a nota foi adicionada com sucesso, false se o limite de notas foi atingido.
     */
    public boolean adicionarResultado(double nota) {
        if (totalAvaliacoesLancadas < resultadosAvaliacoes.length) {
            resultadosAvaliacoes[totalAvaliacoesLancadas] = nota;
            totalAvaliacoesLancadas++;
            return true;
        }
        return false;
    }

    /**
     * Calcula a média aritmética das notas que já foram lançadas nesta avaliação.
     * * @return O valor da média ponderada, ou 0.0 se não existirem notas.
     */
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
package model.bll;

/**
 * Representa a avaliação contínua ou final de um estudante numa específica Unidade Curricular.
 * Regista os resultados de avaliação durante um determinado ano letivo e processa o cálculo da respetiva média.
 */
public class Avaliacao {

    // ---------- ATRIBUTOS ----------
    private Estudante estudante;
    private UnidadeCurricular unidadeCurricular;
    private int anoAvaliacao;
    private double[] resultadosAvaliacoes;
    private int totalAvaliacoesLancadas;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Avaliacao.
     * Inicializa a estrutura de dados preparada para armazenar um máximo de 3 notas.
     *
     * @param estudante         A instância do estudante avaliado.
     * @param unidadeCurricular A unidade curricular correspondente à avaliação.
     * @param anoAvaliacao      O ano letivo (civil) em que ocorre a avaliação.
     */
    public Avaliacao(Estudante estudante, UnidadeCurricular unidadeCurricular, int anoAvaliacao) {
        this.estudante = estudante;
        this.unidadeCurricular = unidadeCurricular;
        this.anoAvaliacao = anoAvaliacao;
        this.resultadosAvaliacoes = new double[3]; // Limite fixo estrutural de 3 avaliações
        this.totalAvaliacoesLancadas = 0;
    }

    // ---------- GETTERS ----------

    public Estudante getEstudante() { return estudante; }

    public UnidadeCurricular getUnidadeCurricular() { return unidadeCurricular; }

    /**
     * @return A unidade curricular (método mantido para garantir a retrocompatibilidade com versões anteriores).
     */
    public UnidadeCurricular getUc() { return unidadeCurricular; }

    public int getAnoAvaliacao() { return anoAvaliacao; }

    public double[] getResultadosAvaliacoes() { return resultadosAvaliacoes; }

    public int getTotalAvaliacoesLancadas() { return totalAvaliacoesLancadas; }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Adiciona um novo resultado (nota) ao histórico de avaliações do aluno nesta UC.
     * O sistema permite o lançamento de um máximo de 3 notas por cada bloco de avaliação.
     *
     * @param nota O valor da avaliação a registar.
     * @return true se a nota foi adicionada com sucesso; false se o limite máximo de notas já foi atingido.
     */
    public boolean adicionarResultado(double nota) {
        // Valida se ainda existe espaço no array antes de registar a nota
        if (totalAvaliacoesLancadas < resultadosAvaliacoes.length) {
            resultadosAvaliacoes[totalAvaliacoesLancadas] = nota;
            totalAvaliacoesLancadas++;
            return true;
        }
        return false;
    }

    /**
     * Calcula a média aritmética das notas que já foram registadas nesta avaliação.
     *
     * @return O valor da média calculada, ou 0.0 se não existirem avaliações lançadas.
     */
    public double calcularMedia() {
        // Interrompe o cálculo caso o contador seja zero, prevenindo a ocorrência de divisão por zero
        if (totalAvaliacoesLancadas == 0) {
            return 0.0;
        }

        double soma = 0;

        // Itera exclusivamente sobre as notas efetivamente lançadas, ignorando as posições nulas do array
        for (int i = 0; i < totalAvaliacoesLancadas; i++) {
            soma += resultadosAvaliacoes[i];
        }

        return soma / totalAvaliacoesLancadas;
    }
}
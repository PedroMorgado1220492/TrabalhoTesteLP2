package model.bll;

/**
 * Representa a avaliação contínua ou final de um estudante numa específica Unidade Curricular.
 * Regista os resultados parciais obtidos durante um determinado ano letivo e processa o cálculo da respetiva média.
 * No padrão MVC, esta classe atua como um puro Model, garantindo as regras matemáticas
 * e os limites estruturais do percurso do estudante sem qualquer interação com a interface.
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
     * Inicializa a estrutura de dados preparada para armazenar um máximo absoluto de 3 notas.
     *
     * @param estudante         A instância do estudante a ser avaliado.
     * @param unidadeCurricular A unidade curricular correspondente à avaliação.
     * @param anoAvaliacao      O ano letivo (civil) em que ocorre a avaliação.
     */
    public Avaliacao(Estudante estudante, UnidadeCurricular unidadeCurricular, int anoAvaliacao) {
        this.estudante = estudante;
        this.unidadeCurricular = unidadeCurricular;
        this.anoAvaliacao = anoAvaliacao;

        // O limite estrutural máximo do sistema para qualquer UC é de 3 avaliações.
        // O limite real de cada UC é gerido pela variável 'numAvaliacoes' da própria UnidadeCurricular.
        this.resultadosAvaliacoes = new double[3];
        this.totalAvaliacoesLancadas = 0;
    }

    // ---------- GETTERS ----------

    public Estudante getEstudante() { return estudante; }
    public UnidadeCurricular getUnidadeCurricular() { return unidadeCurricular; }

    /**
     * @return A unidade curricular (método auxiliar mantido para garantir a compatibilidade com a classe Pauta/Relatórios).
     */
    public UnidadeCurricular getUc() { return unidadeCurricular; }

    public int getAnoAvaliacao() { return anoAvaliacao; }
    public double[] getResultadosAvaliacoes() { return resultadosAvaliacoes; }
    public int getTotalAvaliacoesLancadas() { return totalAvaliacoesLancadas; }


    // ---------- MÉTODOS DE LÓGICA DE NEGÓCIO (MODEL) ----------

    /**
     * Adiciona um novo resultado (nota parcial) ao histórico de avaliações do aluno nesta UC.
     * Regra de Negócio: O aluno não pode ter mais notas lançadas do que o limite estipulado
     * pelo Docente na própria Unidade Curricular (entre 1 e 3 avaliações).
     *
     * @param nota O valor da avaliação parcial a registar.
     * @return true se a nota foi adicionada com sucesso; false se o limite de notas da UC já foi atingido.
     */
    public boolean adicionarResultado(double nota) {
        // Valida simultaneamente o espaço no array e o limite definido ativamente pela UC
        if (totalAvaliacoesLancadas < unidadeCurricular.getNumAvaliacoes() && totalAvaliacoesLancadas < resultadosAvaliacoes.length) {
            resultadosAvaliacoes[totalAvaliacoesLancadas] = nota;
            totalAvaliacoesLancadas++;
            return true;
        }
        return false;
    }

    /**
     * Calcula a média aritmética simples das notas parciais que já foram efetivamente registadas nesta avaliação.
     *
     * @return O valor da média calculada, ou 0.0 se ainda não existirem avaliações lançadas.
     */
    public double calcularMedia() {
        // Interrompe o cálculo caso o contador seja zero, prevenindo a ocorrência de divisão por zero (Erro matemático 'NaN')
        if (totalAvaliacoesLancadas == 0) {
            return 0.0;
        }

        double soma = 0;

        // Itera exclusivamente sobre as notas que já foram lançadas, ignorando as posições vazias (0.0) do array
        for (int i = 0; i < totalAvaliacoesLancadas; i++) {
            soma += resultadosAvaliacoes[i];
        }

        return soma / totalAvaliacoesLancadas;
    }
}
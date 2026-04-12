package model.bll;

/**
 * Representa o estado corrente das inscrições e avaliações de um Estudante durante um ano letivo.
 * Funciona como um "buffer" anual que regista as disciplinas em que o aluno está ativamente
 * matriculado antes de as notas transitarem para o registo histórico no final do ano.
 */
public class PercursoAcademico {

    // ---------- ATRIBUTOS ----------
    private Estudante estudante;

    // Estruturas de dados para o ano letivo corrente
    private UnidadeCurricular[] ucsInscrito;
    private int totalUcsInscrito;

    private Avaliacao[] avaliacoes;
    private int totalAvaliacoes;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe PercursoAcademico.
     * Aloca memória para armazenar as inscrições do ano corrente, estabelecendo
     * um teto máximo compatível com a carga de um plano de estudos normal.
     *
     * @param estudante A instância do estudante titular deste percurso.
     */
    public PercursoAcademico(Estudante estudante) {
        this.estudante = estudante;

        // Limite estrutural estabelecido em 15 UCs por ano letivo
        this.ucsInscrito = new UnidadeCurricular[15];
        this.totalUcsInscrito = 0;

        this.avaliacoes = new Avaliacao[15];
        this.totalAvaliacoes = 0;
    }

    // ---------- GETTERS ----------

    public Estudante getEstudante() { return estudante; }

    public UnidadeCurricular[] getUcsInscrito() { return ucsInscrito; }

    public int getTotalUcsInscrito() { return totalUcsInscrito; }

    public Avaliacao[] getAvaliacoes() { return avaliacoes; }

    public int getTotalAvaliacoes() { return totalAvaliacoes; }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Efetiva a inscrição (matrícula) do estudante numa específica Unidade Curricular para o ano letivo corrente.
     *
     * @param uc A Unidade Curricular na qual o estudante será inscrito.
     * @return true se a inscrição for processada com sucesso; false caso o limite anual do vetor (15) seja atingido.
     */
    public boolean inscreverEmUc(UnidadeCurricular uc) {
        // Valida a disponibilidade de "vagas" no array do percurso do aluno
        if (totalUcsInscrito < ucsInscrito.length) {
            ucsInscrito[totalUcsInscrito] = uc;
            totalUcsInscrito++;
            return true;
        }
        return false;
    }

    /**
     * Vincula um boletim de avaliação relativo a uma Unidade Curricular ao registo corrente do estudante.
     *
     * @param avaliacao A instância de Avaliacao contendo as notas parciais ou finais da UC.
     * @return true se a operação for concluída; false se a matriz de avaliações anuais estiver cheia.
     */
    public boolean registarAvaliacao(Avaliacao avaliacao) {
        // Verifica limite de espaço no buffer do ano letivo
        if (totalAvaliacoes < avaliacoes.length) {
            avaliacoes[totalAvaliacoes] = avaliacao;
            totalAvaliacoes++;
            return true;
        }
        return false;
    }

    /**
     * Repõe o estado do percurso académico (inscrições e notas correntes).
     * Este método é acionado estritamente no final do ano letivo (após a transição de notas para o Histórico)
     * para preparar o processo de auto-matrícula do ano civil seguinte.
     */
    public void limparInscricoesAtivas() {
        // Recria as matrizes, repondo os apontadores para a posição zero
        this.ucsInscrito = new UnidadeCurricular[20];
        this.totalUcsInscrito = 0;

        this.avaliacoes = new Avaliacao[20];
        this.totalAvaliacoes = 0;
    }
}
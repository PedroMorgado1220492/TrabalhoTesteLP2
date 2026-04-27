package model.bll;

/**
 * Representa o estado corrente das inscrições e avaliações de um Estudante num determinado ano letivo.
 * No padrão MVC, atua como um sub-modelo agregado ao Estudante.
 * Funciona como um "buffer" (memória temporária) anual que regista as disciplinas em que o aluno
 * está ativamente matriculado, isolando-as do registo histórico permanente até ao encerramento do ano.
 */
public class PercursoAcademico {

    // ---------- ATRIBUTOS ----------

    private Estudante estudante; // O titular deste registo académico

    // Estruturas de dados voláteis (referentes apenas ao ano letivo corrente)
    private UnidadeCurricular[] ucsInscrito;
    private int totalUcsInscrito;

    private Avaliacao[] avaliacoes;
    private int totalAvaliacoes;


    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe PercursoAcademico.
     * Aloca memória para armazenar as inscrições do ano corrente, estabelecendo
     * um teto máximo de salvaguarda compatível com um plano de estudos muito sobrecarregado.
     *
     * @param estudante A instância do estudante titular deste percurso.
     */
    public PercursoAcademico(Estudante estudante) {
        this.estudante = estudante;

        // Limite estrutural estabelecido em 15 UCs por ano letivo
        // (suficiente para cobrir cadeiras normais + repetições de anos anteriores)
        this.ucsInscrito = new UnidadeCurricular[15];
        this.totalUcsInscrito = 0;

        this.avaliacoes = new Avaliacao[15];
        this.totalAvaliacoes = 0;
    }


    // ---------- GETTERS ----------

    public Estudante getEstudante() { return estudante; }
    public UnidadeCurricular[] getUcsInscrito() { return ucsInscrito; }
    public int getTotalUcsInscrito() { return totalUcsInscrito; }


    // =========================================================
    // LÓGICA DE NEGÓCIO: GESTÃO DO ANO LETIVO CORRENTE
    // =========================================================

    /**
     * Efetiva a inscrição (matrícula) do estudante numa específica Unidade Curricular.
     * Regista a intenção do aluno de frequentar a cadeira durante o atual ano letivo.
     *
     * @param uc A Unidade Curricular na qual o estudante será inscrito.
     * @return true se a inscrição for processada com sucesso; false caso o limite anual estrutural seja atingido.
     */
    public boolean inscreverEmUc(UnidadeCurricular uc) {
        // Valida a disponibilidade de "vagas" no array limite do percurso do aluno
        if (totalUcsInscrito < ucsInscrito.length) {
            ucsInscrito[totalUcsInscrito] = uc;
            totalUcsInscrito++;
            return true;
        }
        return false;
    }

    /**
     * Vincula um boletim de avaliação (relativo a uma UC) ao registo corrente do estudante.
     * Este método liga a folha de notas à matrícula ativa do aluno.
     *
     * @param avaliacao A instância de Avaliacao contendo as notas parciais ou finais da UC.
     * @return true se a operação for concluída; false se a matriz de avaliações anuais estiver cheia.
     */
    public boolean registarAvaliacao(Avaliacao avaliacao) {
        // Verifica o limite de espaço no buffer do ano letivo
        if (totalAvaliacoes < avaliacoes.length) {
            avaliacoes[totalAvaliacoes] = avaliacao;
            totalAvaliacoes++;
            return true;
        }
        return false;
    }

    /**
     * Repõe o estado do percurso académico (eliminando inscrições e notas correntes da memória de curto prazo).
     * Regra de Negócio: Este método é acionado estritamente no final do ano letivo pela classe Estudante
     * (logo após a transição das notas para o Histórico de longo prazo) com o intuito de
     * preparar e libertar espaço para o processo de auto-matrícula do ano civil seguinte.
     */
    public void limparInscricoesAtivas() {
        // Recria fisicamente as matrizes na memória, repondo os contadores a zero
        this.ucsInscrito = new UnidadeCurricular[15];
        this.totalUcsInscrito = 0;

        this.avaliacoes = new Avaliacao[15];
        this.totalAvaliacoes = 0;
    }
}
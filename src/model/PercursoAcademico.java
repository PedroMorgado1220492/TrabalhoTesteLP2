package model;

public class PercursoAcademico {

    // ---------- ATRIBUTOS ----------
    private Estudante estudante;

    private UnidadeCurricular[] ucsInscrito;
    private int totalUcsInscrito;

    private Avaliacao[] avaliacoes;
    private int totalAvaliacoes;

    // ---------- CONSTRUTOR ----------
    public PercursoAcademico(Estudante estudante) {
        this.estudante = estudante;

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
     * Regista a inscrição do estudante numa nova Unidade Curricular.
     * * @param uc Unidade Curricular onde pretende inscrever-se.
     * @return true se a inscrição for bem sucedida, false se tiver atingido o limite do curso.
     */
    public boolean inscreverEmUc(UnidadeCurricular uc) {
        if (totalUcsInscrito < ucsInscrito.length) {
            ucsInscrito[totalUcsInscrito] = uc;
            totalUcsInscrito++;
            return true;
        }
        return false;
    }

    /**
     * Associa um registo de avaliação de uma UC ao percurso do estudante.
     * * @param avaliacao Objeto contendo as notas da UC.
     * @return true se registada com sucesso, false se o limite de avaliações foi atingido.
     */
    public boolean registarAvaliacao(Avaliacao avaliacao) {
        if (totalAvaliacoes < avaliacoes.length) {
            avaliacoes[totalAvaliacoes] = avaliacao;
            totalAvaliacoes++;
            return true;
        }
        return false;
    }
}
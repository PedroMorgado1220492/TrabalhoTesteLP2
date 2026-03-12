// Ficheiro: model/PercursoAcademico.java
package model;

public class PercursoAcademico {
    private Estudante estudante;

    // Assumindo um curso de 3 anos com 5 UCs por ano = 15 UCs no máximo
    private UnidadeCurricular[] ucsInscrito;
    private int totalUcsInscrito;

    // Guardamos um objeto de Avaliacao para cada UC
    private Avaliacao[] avaliacoes;
    private int totalAvaliacoes;

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

    // ---------- MÉTODOS ÚTEIS ----------
    public boolean inscreverEmUc(UnidadeCurricular uc) {
        if (totalUcsInscrito < ucsInscrito.length) {
            ucsInscrito[totalUcsInscrito] = uc;
            totalUcsInscrito++;
            return true;
        }
        return false; // Já atingiu o limite de UCs do curso
    }

    public boolean registarAvaliacao(Avaliacao avaliacao) {
        if (totalAvaliacoes < avaliacoes.length) {
            avaliacoes[totalAvaliacoes] = avaliacao;
            totalAvaliacoes++;
            return true;
        }
        return false;
    }
}
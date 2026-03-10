package model;

public class UnidadeCurricular {

    private String sigla;
    private String nome;
    private int anoCurricular;
    private Docente docenteResponsavel;

    public UnidadeCurricular(String sigla, String nome, int anoCurricular) {
        this.sigla = sigla;
        this.nome = nome;
        this.anoCurricular = anoCurricular;
    }

    public String getSigla() {
        return sigla;
    }

    public String getNome() {
        return nome;
    }

    public int getAnoCurricular() {
        return anoCurricular;
    }

    public Docente getDocenteResponsavel() {
        return docenteResponsavel;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAnoCurricular(int anoCurricular) {
        this.anoCurricular = anoCurricular;
    }

    public void setDocenteResponsavel(Docente docenteResponsavel) {
        this.docenteResponsavel = docenteResponsavel;
    }

    @Override
    public String toString() {
        return sigla + " - " + nome +
                " | Ano: " + anoCurricular +
                (docenteResponsavel != null ? " | Docente: " + docenteResponsavel.getNome() : "");
    }
}
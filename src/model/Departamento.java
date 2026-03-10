package model;

public class Departamento {

    private static final int MAX = 200;

    private String sigla;
    private String nome;

    private UnidadeCurricular[] ucs = new UnidadeCurricular[MAX];
    private int totalUCs = 0;

    private Docente docenteResponsavel;

    public Departamento(String sigla, String nome) {
        this.sigla = sigla;
        this.nome = nome;
    }

    // ---------------------------
    // Getters e Setters
    // ---------------------------
    public String getSigla() { return sigla; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Docente getDocenteResponsavel() { return docenteResponsavel; }
    public void setDocenteResponsavel(Docente docenteResponsavel) {
        this.docenteResponsavel = docenteResponsavel;
    }

    public void adicionarUC(UnidadeCurricular uc) {
        if (totalUCs < MAX) {
            ucs[totalUCs++] = uc;
        }
    }

    public UnidadeCurricular[] getUcs() { return ucs; }
    public int getTotalUCs() { return totalUCs; }

    @Override
    public String toString() {
        return sigla + " - " + nome +
                (docenteResponsavel != null ? " (Responsável: " + docenteResponsavel.getNome() + ")" : "");
    }
}
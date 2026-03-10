package model;

public class Curso {

    private static final int MAX_UC = 15;

    private String sigla;
    private String nome;
    private Departamento departamento; // Relacionamento com departamento

    private UnidadeCurricular[] ucs = new UnidadeCurricular[MAX_UC];
    private int totalUCs = 0;

    private Docente docenteResponsavel;

    public Curso(String sigla, String nome) {
        this.sigla = sigla;
        this.nome = nome;
    }

    // ---------------------------
    // Getters e Setters
    // ---------------------------
    public String getSigla() { return sigla; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    public Docente getDocenteResponsavel() { return docenteResponsavel; }
    public void setDocenteResponsavel(Docente docenteResponsavel) { this.docenteResponsavel = docenteResponsavel; }

    public void adicionarUC(UnidadeCurricular uc) {
        if (totalUCs < MAX_UC) {
            ucs[totalUCs++] = uc;
        }
    }

    public UnidadeCurricular[] getUcs() { return ucs; }
    public int getTotalUCs() { return totalUCs; }

    @Override
    public String toString() {
        return sigla + " - " + nome +
                (departamento != null ? " (" + departamento.getNome() + ")" : "") +
                (docenteResponsavel != null ? " - Docente: " + docenteResponsavel.getNome() : "");
    }

}
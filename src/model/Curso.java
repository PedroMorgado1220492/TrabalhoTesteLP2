// Ficheiro: model/Curso.java
package model;

public class Curso {

    private String sigla;
    private String nome;
    private Departamento departamento;
    private Docente docenteResponsavel;
    private final int duracaoAnos = 3;

    // Array para as Unidades Curriculares
    private UnidadeCurricular[] unidadesCurriculares;
    private int totalUCs;

    public Curso(String sigla, String nome, Departamento departamento) {
        this.sigla = sigla;
        this.nome = nome;
        this.departamento = departamento;

        // Pela regra de negócio: Max 5 UCs por ano. Duração de 3 anos.
        // Logo, no máximo, um curso terá 15 UCs no total.
        this.unidadesCurriculares = new UnidadeCurricular[15];
        this.totalUCs = 0;
    }

    // ---------- GETTERS ----------
    public String getSigla() { return sigla; }
    public String getNome() { return nome; }
    public Departamento getDepartamento() { return departamento; }
    public Docente getDocenteResponsavel() { return docenteResponsavel; }
    public int getDuracaoAnos() { return duracaoAnos; }
    public UnidadeCurricular[] getUnidadesCurriculares() { return unidadesCurriculares; }
    public int getTotalUCs() { return totalUCs; }

    // ---------- SETTERS ----------
    public void setSigla(String sigla) { this.sigla = sigla; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }
    public void setDocenteResponsavel(Docente docenteResponsavel) { this.docenteResponsavel = docenteResponsavel; }

    // ---------- MÉTODOS PARA ADICIONAR AOS ARRAYS ----------
    public boolean adicionarUnidadeCurricular(UnidadeCurricular uc) {
        if (totalUCs < unidadesCurriculares.length) {
            unidadesCurriculares[totalUCs] = uc;
            totalUCs++;
            return true;
        }
        return false; // Array cheio (já tem as 15 UCs)
    }
}
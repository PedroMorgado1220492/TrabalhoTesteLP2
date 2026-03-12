// Ficheiro: model/UnidadeCurricular.java
package model;

public class UnidadeCurricular {
    private String sigla;
    private String nome;
    private int anoCurricular;
    private Docente docenteResponsavel; // Apenas um docente é responsável

    // Uma unidade curricular pode estar registada em vários cursos
    private Curso[] cursos;
    private int totalCursos;

    public UnidadeCurricular(String sigla, String nome, int anoCurricular, Docente docenteResponsavel) {
        this.sigla = sigla;
        this.nome = nome;
        this.anoCurricular = anoCurricular;
        this.docenteResponsavel = docenteResponsavel;

        // Vamos assumir que uma UC pode estar em até 10 cursos diferentes
        this.cursos = new Curso[10];
        this.totalCursos = 0;
    }

    // ---------- GETTERS ----------
    public String getSigla() { return sigla; }
    public String getNome() { return nome; }
    public int getAnoCurricular() { return anoCurricular; }
    public Docente getDocenteResponsavel() { return docenteResponsavel; }
    public Curso[] getCursos() { return cursos; }
    public int getTotalCursos() { return totalCursos; }

    // ---------- SETTERS ----------
    public void setSigla(String sigla) { this.sigla = sigla; }
    public void setNome(String nome) { this.nome = nome; }
    public void setAnoCurricular(int anoCurricular) { this.anoCurricular = anoCurricular; }
    public void setDocenteResponsavel(Docente docenteResponsavel) { this.docenteResponsavel = docenteResponsavel; }

    // ---------- MÉTODOS ÚTEIS ----------
    public boolean adicionarCurso(Curso curso) {
        if (totalCursos < cursos.length) {
            cursos[totalCursos] = curso;
            totalCursos++;
            return true;
        }
        return false; // Limite de cursos atingido
    }
}
// Ficheiro: model/Departamento.java
package model;

public class Departamento {

    private String sigla;
    private String nome;
    private Docente docenteResponsavel;

    // Arrays em vez de ArrayList
    private Curso[] cursos;
    private int totalCursos;

    public Departamento(String sigla, String nome) {
        this.sigla = sigla;
        this.nome = nome;

        // Vamos assumir um máximo de 10 cursos por departamento
        this.cursos = new Curso[10];
        this.totalCursos = 0;
    }

    // ---------- GETTERS ----------
    public String getSigla() { return sigla; }
    public String getNome() { return nome; }
    public Docente getDocenteResponsavel() { return docenteResponsavel; }
    public Curso[] getCursos() { return cursos; }
    public int getTotalCursos() { return totalCursos; }

    // ---------- SETTERS ----------
    public void setSigla(String sigla) { this.sigla = sigla; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDocenteResponsavel(Docente docenteResponsavel) { this.docenteResponsavel = docenteResponsavel; }

    // ---------- MÉTODOS PARA ADICIONAR AOS ARRAYS ----------
    public boolean adicionarCurso(Curso curso) {
        if (totalCursos < cursos.length) {
            cursos[totalCursos] = curso;
            totalCursos++;
            return true;
        }
        return false; // Array cheio
    }
}
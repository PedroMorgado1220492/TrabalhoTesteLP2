package model.bll;

/**
 * Representa um Departamento da instituição de ensino.
 * Atua como a unidade orgânica agregadora de múltiplos Cursos e pode ser gerida por um Docente Responsável.
 */
public class Departamento {

    // ---------- ATRIBUTOS ----------
    private String sigla;
    private String nome;
    private Docente docenteResponsavel;

    private Curso[] cursos;
    private int totalCursos;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Departamento.
     * Prepara a estrutura do departamento estabelecendo uma capacidade máxima de 10 Cursos.
     *
     * @param sigla A sigla identificativa do departamento (ex: DEP-INF).
     * @param nome  O nome por extenso do departamento (ex: Departamento de Informática).
     */
    public Departamento(String sigla, String nome) {
        this.sigla = sigla;
        this.nome = nome;
        this.cursos = new Curso[10]; // Limite estrutural fixado em 10 cursos por departamento
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

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Associa um novo Curso a este Departamento.
     *
     * @param curso O Curso a ser integrado na oferta formativa do departamento.
     * @return true se a associação for concluída com sucesso; false se o limite departamental (10 cursos) já tiver sido atingido.
     */
    public boolean adicionarCurso(Curso curso) {
        // Valida se o departamento ainda dispõe de espaço na matriz de cursos
        if (totalCursos < cursos.length) {
            cursos[totalCursos] = curso;
            totalCursos++;
            return true;
        }
        return false; // Rejeita a adição se o array estiver cheio
    }
}
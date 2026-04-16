package model.bll;

/**
 * Representa um Gestor (Administrador do Backoffice) no sistema.
 * Herda as credenciais base da classe Utilizador.
 */
public class Gestor extends Utilizador {

    private String morada;
    private boolean ativo;

    /**
     * Construtor da classe Gestor.
     */
    public Gestor(String email, String password, String nome, String morada) {
        super(email, password, nome, "N/A", morada, "N/A", "N/A");
        this.ativo = true; // O Gestor é criado ativo por defeito
        this.morada = morada;
    }

    // ---------- GETTERS E SETTERS ----------

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // ---------- MÉTODOS DE CRIAÇÃO (FÁBRICA) ----------

    public Departamento criarDepartamento(String sigla, String nome) {
        return new Departamento(sigla, nome);
    }

    public Curso criarCurso(String sigla, String nome, Departamento departamento) {
        return new Curso(sigla, nome, departamento);
    }

    public UnidadeCurricular criarUnidadeCurricular(String sigla, String nome, int anoCurricular, Docente docenteResponsavel, int numAvaliacoes) {
        return new UnidadeCurricular(sigla, nome, anoCurricular, docenteResponsavel, numAvaliacoes);
    }
}
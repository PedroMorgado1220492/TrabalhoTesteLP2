package model;

public class Estudante extends Utilizador {

    private final int numeroMecanografico;
    private Curso curso;
    private final int anoPrimeiraInscricao;
    private int anoCurricular;
    private PercursoAcademico percursoAcademico;

    public Estudante(int numeroMecanografico,
                     String email,
                     String password,
                     String nome,
                     String nif,
                     String morada,
                     String dataNascimento,
                     Curso curso,
                     int anoPrimeiraInscricao) {

        super(email, password, nome, nif, morada, dataNascimento);

        this.numeroMecanografico = numeroMecanografico;
        this.curso = curso;
        this.anoPrimeiraInscricao = anoPrimeiraInscricao;
        this.anoCurricular = 1;

        this.percursoAcademico = new PercursoAcademico(this);
    }

    // ---------- GETTERS ----------

    public int getNumeroMecanografico() {
        return numeroMecanografico;
    }

    public Curso getCurso() {
        return curso;
    }

    public int getAnoPrimeiraInscricao() {
        return anoPrimeiraInscricao;
    }

    public int getAnoCurricular() {
        return anoCurricular;
    }

    public PercursoAcademico getPercursoAcademico() {
        return percursoAcademico;
    }

    public String getDataNascimento() {
        return this.dataNascimento;
    }

    // ---------- SETTERS ----------

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setAnoCurricular(int anoCurricular) {
        this.anoCurricular = anoCurricular;
    }

    public void setPercursoAcademico(PercursoAcademico percursoAcademico) {
        this.percursoAcademico = percursoAcademico;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }


    // ---------- MÉTODOS ÚTEIS ----------

    public void avancarAno() {
        anoCurricular++;
    }

    @Override
    public String toString() {
        return numeroMecanografico + " - " + nome +
                (curso != null ? " (" + curso.getNome() + ")" : "");
    }
}
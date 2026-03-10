package model;

public class Docente extends Utilizador {

    private String sigla;

    public Docente(String sigla, String email, String password,
                   String nome, String nif, String morada,
                   String dataNascimento) {

        super(email, password, nome, nif, morada, dataNascimento);
        this.sigla = sigla;
    }

    // ----- GETTERS -----
    public String getSigla() {
        return sigla;
    }

    public String getDataNascimento() {
        return this.dataNascimento;
    }

    // ----- SETTERS -----
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

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String toString() {
        return sigla + " - " + nome + " | Email: " + email;
    }
}
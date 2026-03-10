package model;

public abstract class Utilizador {

    protected String email;
    protected String password;
    protected String nome;
    protected String nif;
    protected String morada;
    protected String dataNascimento;

    public Utilizador(String email, String password, String nome,
                      String nif, String morada, String dataNascimento) {

        this.email = email;
        this.password = password;
        this.nome = nome;
        this.nif = nif;
        this.morada = morada;
        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public boolean verificarPassword(String password) {
        return this.password.equals(password);
    }

    public String getNome() {
        return nome;
    }

    public String getPassword() {
        return this.password;
    }

    public void alterarPassword(String novaPassword) {
        this.password = novaPassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
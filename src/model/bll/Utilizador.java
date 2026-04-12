package model.bll;

/**
 * Representa a base abstrata de todos os utilizadores do sistema ISSMF.
 * Esta classe define os atributos e comportamentos comuns a qualquer pessoa registada,
 * servindo de modelo para a especialização de perfis (Estudante, Docente e Gestor).
 */
public abstract class Utilizador {

    // ---------- ATRIBUTOS ----------
    /** Identificador institucional (login) do utilizador. */
    protected String email;
    /** Password de acesso (armazenada em formato encriptado/hash). */
    protected String password;
    /** Nome completo do utilizador. */
    protected String nome;
    /** Número de Identificação Fiscal. */
    protected String nif;
    /** Morada de residência ou localização profissional. */
    protected String morada;
    /** Data de nascimento em formato String (ex: DD-MM-AAAA). */
    protected String dataNascimento;
    /** Email de contacto externo para comunicações e recuperação de conta. */
    protected String emailPessoal;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe abstrata Utilizador.
     * Centraliza a inicialização dos dados demográficos e de acesso.
     *
     * @param email           O email institucional único.
     * @param password        A password já encriptada.
     * @param nome            O nome de registo.
     * @param nif             O Número de Identificação Fiscal.
     * @param morada          A morada completa.
     * @param dataNascimento  A data de nascimento.
     * @param emailPessoal    O email pessoal de contacto.
     */
    public Utilizador(String email, String password, String nome, String nif, String morada, String dataNascimento, String emailPessoal) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.nif = nif;
        this.morada = morada;
        this.dataNascimento = dataNascimento;
        this.emailPessoal = emailPessoal;
    }

    // ---------- GETTERS ----------

    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public String getNome() { return nome; }

    public String getNif() { return nif; }

    public String getMorada() { return morada; }

    public String getDataNascimento() { return dataNascimento; }

    public String getEmailPessoal() { return emailPessoal; }

    // ---------- SETTERS ----------

    /**
     * Atualiza a password do utilizador.
     * @param password A nova password (deve ser passada já encriptada).
     */
    public void setPassword(String password) { this.password = password; }

    public void setNome(String nome) { this.nome = nome; }

    public void setNif(String nif) { this.nif = nif; }

    public void setMorada(String morada) { this.morada = morada; }

    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public void setEmailPessoal(String emailPessoal) { this.emailPessoal = emailPessoal; }
}
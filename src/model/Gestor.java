package model;

import utils.EmailGenerator;
import utils.PasswordGenerator;

public class Gestor extends Utilizador {

    // ---------- CONSTRUTOR ----------
    public Gestor(String email, String password, String nome, String nif, String morada, String dataNascimento) {
        super(email, password, nome, nif, morada, dataNascimento);
    }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO (FÁBRICA DE OBJETOS) ----------

    /**
     * Instancia um novo Estudante gerando automaticamente as suas credenciais.
     */
    public Estudante criarEstudante(int numeroMecanografico, String nome, String nif, String morada, String dataNascimento, Curso curso, int anoPrimeiraInscricao) {
        String emailGerado = EmailGenerator.gerarEmailEstudante(numeroMecanografico);
        String passwordGerada = PasswordGenerator.generatePassword();
        return new Estudante(numeroMecanografico, emailGerado, passwordGerada, nome, nif, morada, dataNascimento, curso, anoPrimeiraInscricao);
    }

    /**
     * Instancia um novo Docente gerando automaticamente o email com base na sigla e a password.
     */
    public Docente criarDocente(String sigla, String nome, String nif, String morada, String dataNascimento) {
        String emailGerado = EmailGenerator.gerarEmailDocente(sigla);
        String passwordGerada = PasswordGenerator.generatePassword();
        return new Docente(sigla, emailGerado, passwordGerada, nome, nif, morada, dataNascimento);
    }

    /**
     * Instancia um novo Departamento.
     */
    public Departamento criarDepartamento(String sigla, String nome) {
        return new Departamento(sigla, nome);
    }

    /**
     * Instancia um novo Curso associando-o desde logo a um Departamento.
     */
    public Curso criarCurso(String sigla, String nome, Departamento departamento) {
        return new Curso(sigla, nome, departamento);
    }

    /**
     * Instancia uma nova Unidade Curricular associando-lhe o respetivo Docente Responsável.
     */
    public UnidadeCurricular criarUnidadeCurricular(String sigla, String nome, int anoCurricular, Docente docenteResponsavel) {
        return new UnidadeCurricular(sigla, nome, anoCurricular, docenteResponsavel);
    }
}
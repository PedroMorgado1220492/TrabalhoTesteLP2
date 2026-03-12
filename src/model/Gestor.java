package model;

import utils.EmailGenerator;

public class Gestor extends Utilizador {

    // ---------- CONSTRUTOR ----------
    public Gestor(String email, String password, String nome, String nif, String morada, String dataNascimento) {
        super(email, password, nome, nif, morada, dataNascimento);
    }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Lógica de negócio: O Gestor tem o privilégio de instanciar um novo Estudante
     * no sistema com os dados recolhidos, gerando automaticamente o email institucional.
     * * @param numeroMecanografico Identificador único gerado pelo sistema.
     * @param password Password inicial gerada pelo sistema.
     * @param nome Nome completo do estudante.
     * @param nif NIF do estudante.
     * @param morada Morada do estudante.
     * @param dataNascimento Data de nascimento no formato DD-MM-AAAA.
     * @param curso Objeto do Curso ao qual será inscrito.
     * @param anoPrimeiraInscricao Ano letivo de ingresso.
     * @return Uma nova instância da classe Estudante.
     */
    public Estudante criarEstudante(int numeroMecanografico, String password, String nome, String nif, String morada, String dataNascimento, Curso curso, int anoPrimeiraInscricao) {
        String emailGerado = EmailGenerator.gerarEmailEstudante(numeroMecanografico);
        return new Estudante(numeroMecanografico, emailGerado, password, nome, nif, morada, dataNascimento, curso, anoPrimeiraInscricao);
    }
}
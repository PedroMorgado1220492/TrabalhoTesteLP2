// Ficheiro: model/Gestor.java
package model;

import utils.EmailGenerator;

public class Gestor extends Utilizador {

    public Gestor(String email, String password, String nome, String nif, String morada, String dataNascimento) {
        super(email, password, nome, nif, morada, dataNascimento);
    }

    // Lógica de negócio: O Gestor cria o Estudante, mas quem o guarda é o Controller no Repositório
    public Estudante criarEstudante(int numeroMecanografico, String password, String nome, String nif, String morada, String dataNascimento, Curso curso, int anoPrimeiraInscricao) {
        String emailGerado = EmailGenerator.gerarEmailEstudante(numeroMecanografico);
        return new Estudante(numeroMecanografico, emailGerado, password, nome, nif, morada, dataNascimento, curso, anoPrimeiraInscricao);
    }
}
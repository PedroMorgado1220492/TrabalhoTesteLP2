package controller;

import model.*;
import util.PasswordGenerator;

public class GestorController {

    private SistemaAcademico sistema;

    public GestorController(SistemaAcademico sistema) {
        this.sistema = sistema;
    }

    // -----------------------------
    // DEPARTAMENTO
    // -----------------------------
    public void criarDepartamento(String sigla, String nome, Docente docente) {
        Departamento d = new Departamento(sigla, nome);
        d.setDocenteResponsavel(docente); // adiciona o docente
        sistema.adicionarDepartamento(d);
        System.out.println("Departamento criado.");
    }

    public void alterarDepartamento(Departamento d, String nome, Docente docenteResponsavel) {
        d.setNome(nome);
        d.setDocenteResponsavel(docenteResponsavel);
        System.out.println("Departamento atualizado: " + nome);
    }

    public Departamento[] listarDepartamentos() {
        return sistema.getDepartamentos();
    }

    // -----------------------------
    // CURSO
    // -----------------------------
    public void criarCurso(String sigla, String nome, Departamento departamento, Docente docente) {
        Curso c = new Curso(sigla, nome);
        c.setDepartamento(departamento);
        c.setDocenteResponsavel(docente);
        sistema.adicionarCurso(c);
        System.out.println("Curso criado.");
    }

    public void alterarCurso(Curso c, String nome, Departamento departamento, Docente docenteResponsavel) {
        c.setNome(nome);
        c.setDepartamento(departamento);
        c.setDocenteResponsavel(docenteResponsavel);
        System.out.println("Curso atualizado: " + nome);
    }

    public Curso[] listarCursos() {
        return sistema.getCursos();
    }

    // -----------------------------
    // UNIDADE CURRICULAR
    // -----------------------------
    public void criarUC(String sigla, String nome, int ano, Curso curso, Docente docente) {
        UnidadeCurricular uc = new UnidadeCurricular(sigla, nome, ano);
        uc.setDocenteResponsavel(docente);
        sistema.adicionarUC(uc);
        if (curso != null) curso.adicionarUC(uc);
        System.out.println("UC criada: " + nome + " (" + sigla + ")");
    }

    public void alterarUC(UnidadeCurricular uc, String nome, int ano, Curso curso, Docente docente) {
        uc.setNome(nome);
        uc.setAnoCurricular(ano);
        uc.setDocenteResponsavel(docente);
        if (curso != null) curso.adicionarUC(uc);
        System.out.println("UC atualizada: " + nome);
    }

    public UnidadeCurricular[] listarUCs() {
        return sistema.getUcs();
    }

    // -----------------------------
    // DOCENTE
    // -----------------------------
    public void criarDocente(String sigla, String nome, String email, String password) {
        Docente d = new Docente(sigla, email, password, nome, "", "", "");
        sistema.adicionarDocente(d);
        System.out.println("Docente criado.");
    }

    public void alterarDocente(Docente d, String nome, String nif, String morada, String dataNascimento) {
        d.setNome(nome);
        d.setNif(nif);
        d.setMorada(morada);
        d.setDataNascimento(dataNascimento);
        System.out.println("Docente atualizado: " + nome);
    }

    public Docente[] listarDocentes() {
        return sistema.getDocentes();
    }

    // -----------------------------
    // ESTUDANTE
    // -----------------------------
    public Estudante criarEstudante(int numero,
                                    String nome,
                                    String nif,
                                    String morada,
                                    String dataNascimento,
                                    Curso curso) {

        String email = numero + "@issmf.ipp.pt";
        String password = PasswordGenerator.generatePassword();
        int anoInscricao = sistema.getAnoAtual();

        Estudante e = new Estudante(numero, email, password, nome, nif, morada, dataNascimento, curso, anoInscricao);
        sistema.adicionarEstudante(e);

        System.out.println("Estudante criado: " + nome + " | Email: " + email + " | Password: " + password);
        return e;
    }

    public void alterarEstudante(Estudante e, String nome, String nif, String morada, String dataNascimento, Curso curso) {
        e.setNome(nome);
        e.setNif(nif);
        e.setMorada(morada);
        e.setDataNascimento(dataNascimento);
        e.setCurso(curso);
        System.out.println("Estudante atualizado: " + nome);
    }

    public Estudante[] listarEstudantes() {
        return sistema.getEstudantes();
    }

    // -----------------------------
    // ALTERAR PASSWORD
    // -----------------------------
    public void alterarPassword(Gestor gestor, String novaPassword) {
        gestor.alterarPassword(novaPassword);
        System.out.println("Password alterada com sucesso.");
    }
}
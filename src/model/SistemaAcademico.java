package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SistemaAcademico {

    private static final int MAX = 200;

    private Estudante[] estudantes = new Estudante[MAX];
    private int totalEstudantes = 0;

    private Docente[] docentes = new Docente[MAX];
    private int totalDocentes = 0;

    private Curso[] cursos = new Curso[MAX];
    private int totalCursos = 0;

    private Departamento[] departamentos = new Departamento[MAX];
    private int totalDepartamentos = 0;

    private UnidadeCurricular[] ucs = new UnidadeCurricular[MAX];
    private int totalUCs = 0;

    private int anoAtual = 2026;

    private int nextNumeroMecanografico = 1001; // incremento automático

    // -------------------------
    // LOGIN
    // -------------------------
    public Utilizador login(String email, String password) {
        // Estudantes
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i].getEmail().equals(email) &&
                    estudantes[i].verificarPassword(password)) {
                return estudantes[i];
            }
        }
        // Docentes
        for (int i = 0; i < totalDocentes; i++) {
            if (docentes[i].getEmail().equals(email) &&
                    docentes[i].verificarPassword(password)) {
                return docentes[i];
            }
        }
        // Gestor fixo
        if (email.equals("backoffice@issmf.ipp.pt") && password.equals("admin")) {
            return new Gestor(email, password);
        }
        return null;
    }

    // -------------------------
    // Número mecanográfico
    // -------------------------
    public int gerarNumeroMecanografico() {
        return nextNumeroMecanografico++;
    }

    // -------------------------
    // Adicionar entidades
    // -------------------------
    public void adicionarEstudante(Estudante e) {
        if (totalEstudantes < MAX) {
            estudantes[totalEstudantes++] = e;
        }
    }

    public void adicionarDocente(Docente d) {
        if (totalDocentes < MAX) {
            docentes[totalDocentes++] = d;
        }
    }

    public void adicionarCurso(Curso c) {
        if (totalCursos < MAX) cursos[totalCursos++] = c;
    }

    public void adicionarDepartamento(Departamento d) {
        if (totalDepartamentos < MAX) departamentos[totalDepartamentos++] = d;
    }

    public void adicionarUC(UnidadeCurricular uc) {
        if (totalUCs < MAX) ucs[totalUCs++] = uc;
    }

    // -------------------------
    // Getters
    // -------------------------
    public Estudante[] getEstudantes() { return estudantes; }
    public int getTotalEstudantes() { return totalEstudantes; }

    public Docente[] getDocentes() { return docentes; }
    public int getTotalDocentes() { return totalDocentes; }

    public Curso[] getCursos() { return cursos; }
    public int getTotalCursos() { return totalCursos; }

    public Departamento[] getDepartamentos() { return departamentos; }
    public int getTotalDepartamentos() { return totalDepartamentos; }

    public UnidadeCurricular[] getUcs() { return ucs; }
    public int getTotalUCs() { return totalUCs; }

    public int getAnoAtual() { return anoAtual; }

    public void avancarAno() {
        anoAtual++;
        System.out.println("Ano atual: " + anoAtual);
    }

    // -------------------------
    // Importar CSV
    // -------------------------
    public void importarCSV(String ficheiro) {
        try (BufferedReader br = new BufferedReader(new FileReader(ficheiro))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) continue;

                String[] dados = linha.split(",");

                switch (dados[0]) {
                    case "DEPARTAMENTO":
                        adicionarDepartamento(new Departamento(dados[1], dados[2]));
                        break;

                    case "CURSO":
                        Departamento d = procurarDepartamento(dados[3]);
                        Curso c = new Curso(dados[1], dados[2]);
                        if (d != null) c.setDepartamento(d);
                        adicionarCurso(c);
                        break;

                    case "UC":
                        Curso cursoUC = procurarCurso(dados[4]);
                        UnidadeCurricular uc = new UnidadeCurricular(dados[1], dados[2], Integer.parseInt(dados[3]));
                        if (cursoUC != null) cursoUC.adicionarUC(uc);
                        adicionarUC(uc);
                        break;

                    case "DOCENTE":
                        Docente docente = new Docente(dados[1], dados[3], dados[4], dados[2], "", "", "");
                        adicionarDocente(docente);
                        break;

                    case "UC_DOC":
                        UnidadeCurricular ucAssoc = procurarUC(dados[1]);
                        Docente dAssoc = procurarDocenteSigla(dados[2]);
                        if (ucAssoc != null && dAssoc != null) ucAssoc.setDocenteResponsavel(dAssoc);
                        break;

                    case "ESTUDANTE":
                        Curso cursoEst = procurarCurso(dados[5]);
                        Estudante est = new Estudante(Integer.parseInt(dados[1]), dados[3], dados[4], dados[2], "", "", "", cursoEst, 2026);
                        adicionarEstudante(est);
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler CSV: " + e.getMessage());
        }
    }

    // -------------------------
    // Métodos auxiliares
    // -------------------------
    public Departamento procurarDepartamento(String sigla) {
        for (int i = 0; i < totalDepartamentos; i++) {
            if (departamentos[i].getSigla().equals(sigla)) return departamentos[i];
        }
        return null;
    }

    public Curso procurarCurso(String sigla) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i].getSigla().equals(sigla)) return cursos[i];
        }
        return null;
    }

    public UnidadeCurricular procurarUC(String sigla) {
        for (int i = 0; i < totalUCs; i++) {
            if (ucs[i].getSigla().equals(sigla)) return ucs[i];
        }
        return null;
    }

    public Docente procurarDocenteSigla(String sigla) {
        for (int i = 0; i < totalDocentes; i++) {
            if (docentes[i].getSigla().equals(sigla)) return docentes[i];
        }
        return null;
    }

    public Estudante procurarEstudante(int numero) {
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i].getNumeroMecanografico() == numero) return estudantes[i];
        }
        return null;
    }
}
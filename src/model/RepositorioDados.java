// Ficheiro: model/RepositorioDados.java
package model;

public class RepositorioDados {
    // Arrays fixos para guardar os dados em memória
    private Estudante[] estudantes;
    private int totalEstudantes;

    private Gestor[] gestores;
    private int totalGestores;

    private Docente[] docentes = new Docente[100];
    private int totalDocentes = 0;

    private Departamento[] departamentos = new Departamento[20]; // Máximo 20 departamentos
    private int totalDepartamentos = 0;

    private Curso[] cursos = new Curso[50]; // Máximo 50 cursos
    private int totalCursos = 0;

    private UnidadeCurricular[] ucs = new UnidadeCurricular[150]; // Limite genérico para o sistema
    private int totalUcs = 0;

    private int anoAtual;

    public RepositorioDados() {
        // Inicializamos os arrays com um tamanho máximo razoável
        this.estudantes = new Estudante[1000];
        this.totalEstudantes = 0;

        this.gestores = new Gestor[10];
        this.totalGestores = 0;

        this.anoAtual = 2026; // O sistema arranca em 2026 [cite: 4]


    }

    // Métodos para adicionar dados aos arrays
    public boolean adicionarEstudante(Estudante estudante) {
        if (totalEstudantes < estudantes.length) {
            estudantes[totalEstudantes] = estudante;
            totalEstudantes++;
            return true;
        }
        return false; // Array cheio
    }

    public boolean adicionarGestor(Gestor gestor) {
        if (totalGestores < gestores.length) {
            gestores[totalGestores] = gestor;
            totalGestores++;
            return true;
        }
        return false;
    }

    public boolean adicionarDocente(Docente docente) {
        if (totalDocentes < docentes.length) {
            docentes[totalDocentes] = docente;
            totalDocentes++;
            return true;
        }
        return false;
    }

    public boolean adicionarDepartamento(Departamento dep) {
        if (totalDepartamentos < departamentos.length) {
            departamentos[totalDepartamentos] = dep;
            totalDepartamentos++;
            return true;
        }
        return false;
    }

    public boolean adicionarUnidadeCurricular(UnidadeCurricular uc) {
        if (totalUcs < ucs.length) {
            ucs[totalUcs] = uc;
            totalUcs++;
            return true;
        }
        return false;
    }

    public Departamento[] getDepartamentos() { return departamentos; }
    public int getTotalDepartamentos() { return totalDepartamentos; }

    public boolean adicionarCurso(Curso curso) {
        if (totalCursos < cursos.length) {
            cursos[totalCursos] = curso;
            totalCursos++;
            return true;
        }
        return false;
    }

    public int getAnoAtual() {
        return anoAtual;
    }

    public void avancarAno() {
        this.anoAtual++;
    }

    public Curso[] getCursos() { return cursos; }
    public int getTotalCursos() { return totalCursos; }

    public Utilizador autenticar(String email, String password) {
        // 1. Procurar nos Gestores
        for (int i = 0; i < totalGestores; i++) {
            if (gestores[i].getEmail().equals(email) && gestores[i].getPassword().equals(password)) {
                return gestores[i];
            }
        }

        // 2. Procurar nos Docentes
        for (int i = 0; i < totalDocentes; i++) {
            if (docentes[i].getEmail().equals(email) && docentes[i].getPassword().equals(password)) {
                return docentes[i];
            }
        }

        // 3. Procurar nos Estudantes
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i].getEmail().equals(email) && estudantes[i].getPassword().equals(password)) {
                return estudantes[i];
            }
        }

        // Se chegou aqui e não encontrou ninguém, devolve null (login falhou)
        return null;
    }

    // Getters para os contadores e arrays
    public Estudante[] getEstudantes() { return estudantes; }
    public int getTotalEstudantes() { return totalEstudantes; }

    public Docente[] getDocentes() { return docentes; }
    public int getTotalDocentes() { return totalDocentes; }

    public UnidadeCurricular[] getUcs() { return ucs; }
    public int getTotalUcs() { return totalUcs; }
}
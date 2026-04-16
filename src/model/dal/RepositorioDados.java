package model.dal;

import model.bll.*;

/**
 * Repositório central de dados em memória (In-Memory Database).
 * Atua como a camada de persistência volátil da aplicação. Concentra todas as coleções
 * de instâncias (Utilizadores, Cursos, UCs, etc.) e fornece os métodos fundamentais de
 * pesquisa, validação de unicidade e algoritmos de transição de estado global.
 */
public class RepositorioDados {

    // ---------- ATRIBUTOS DE ESTADO GLOBAL ----------
    private int anoAtual;

    // Coleções (Arrays de tamanho fixo para simular restrições de memória estática)
    private Estudante[] estudantes;
    private int totalEstudantes;

    private Gestor[] gestores;
    private int totalGestores;

    private Docente[] docentes;
    private int totalDocentes;

    private Departamento[] departamentos;
    private int totalDepartamentos;

    private Curso[] cursos;
    private int totalCursos;

    private UnidadeCurricular[] ucs;
    private int totalUcs;


    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe RepositorioDados.
     * Inicializa as estruturas de dados preparendas para os limites operacionais do sistema.
     */
    public RepositorioDados() {
        this.anoAtual = 2026;

        this.estudantes = new Estudante[1000];
        this.totalEstudantes = 0;

        this.gestores = new Gestor[10];
        this.totalGestores = 0;

        this.docentes = new Docente[100];
        this.totalDocentes = 0;

        this.departamentos = new Departamento[20];
        this.totalDepartamentos = 0;

        this.cursos = new Curso[50];
        this.totalCursos = 0;

        this.ucs = new UnidadeCurricular[150];
        this.totalUcs = 0;
    }


    // ---------- GETTERS DE COLEÇÃO ----------

    public int getAnoAtual() { return anoAtual; }
    public Estudante[] getEstudantes() { return estudantes; }
    public int getTotalEstudantes() { return totalEstudantes; }
    public Gestor[] getGestores() { return gestores; }
    public int getTotalGestores() { return totalGestores; }
    public Docente[] getDocentes() { return docentes; }
    public int getTotalDocentes() { return totalDocentes; }
    public Departamento[] getDepartamentos() { return departamentos; }
    public int getTotalDepartamentos() { return totalDepartamentos; }
    public Curso[] getCursos() { return cursos; }
    public int getTotalCursos() { return totalCursos; }
    public UnidadeCurricular[] getUcs() { return ucs; }
    public int getTotalUcs() { return totalUcs; }


    // =========================================================
    // 1. MÉTODOS DE ESCRITA (CRUD BASE)
    // =========================================================

    public boolean adicionarEstudante(Estudante estudante) {
        if (totalEstudantes < estudantes.length) {
            estudantes[totalEstudantes++] = estudante;
            return true;
        }
        return false;
    }

    public boolean adicionarGestor(Gestor gestor) {
        if (totalGestores < gestores.length) {
            gestores[totalGestores++] = gestor;
            return true;
        }
        return false;
    }

    public boolean adicionarDocente(Docente docente) {
        if (totalDocentes < docentes.length) {
            docentes[totalDocentes++] = docente;
            return true;
        }
        return false;
    }

    public boolean adicionarDepartamento(Departamento dep) {
        if (totalDepartamentos < departamentos.length) {
            departamentos[totalDepartamentos++] = dep;
            return true;
        }
        return false;
    }

    public boolean adicionarCurso(Curso curso) {
        if (totalCursos < cursos.length) {
            cursos[totalCursos++] = curso;
            return true;
        }
        return false;
    }

    public boolean adicionarUnidadeCurricular(UnidadeCurricular uc) {
        if (totalUcs < ucs.length) {
            ucs[totalUcs++] = uc;
            return true;
        }
        return false;
    }

    /**
     * Remove fisicamente um estudante do repositório através de uma compactação do vetor (Shift-Left).
     * @param numMec O número mecanográfico do aluno a remover.
     * @return true se a remoção for sucedida; false se o aluno não existir.
     */
    public boolean removerEstudante(int numMec) {
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i].getNumeroMecanografico() == numMec) {
                for (int j = i; j < totalEstudantes - 1; j++) {
                    estudantes[j] = estudantes[j + 1];
                }
                estudantes[totalEstudantes - 1] = null;
                totalEstudantes--;
                return true;
            }
        }
        return false;
    }


    // =========================================================
    // 2. MÉTODOS DE AUTENTICAÇÃO E LOGARITMOS DE ID
    // =========================================================

    /**
     * Valida as credenciais de acesso contra todas as coleções de utilizadores.
     * @return O objeto Utilizador autenticado (Polimorfismo) ou null.
     */
    public Utilizador autenticar(String email, String password) {
        for (int i = 0; i < totalGestores; i++) {
            if (gestores[i].getEmail().equals(email) && gestores[i].getPassword().equals(password)) return gestores[i];
        }
        for (int i = 0; i < totalDocentes; i++) {
            if (docentes[i].getEmail().equals(email) && docentes[i].getPassword().equals(password)) return docentes[i];
        }
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i].getEmail().equals(email) && estudantes[i].getPassword().equals(password)) return estudantes[i];
        }
        return null;
    }

    /**
     * Algoritmo de geração de ID: Prefixo do Ano (YYYY) + Sufixo Sequencial (XXXX).
     */
    public int gerarNumeroMecanografico(int anoInscricao) {
        int contadorAno = 0;
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i] != null && estudantes[i].getAnoPrimeiraInscricao() == anoInscricao) contadorAno++;
        }
        return (anoInscricao * 10000) + (contadorAno + 1);
    }

    /**
     * Gera uma sigla única para um docente (Letra Inicial + 2 Caracteres Aleatórios).
     */
    public String gerarSiglaDocente(String nome) {
        char prefixo = nome.trim().toUpperCase().charAt(0);
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        while (true) {
            String sigla = "" + prefixo + abc.charAt((int)(Math.random() * 26)) + abc.charAt((int)(Math.random() * 26));
            if (!existeSiglaDocente(sigla)) return sigla;
        }
    }


    // =========================================================
    // 3. QUERIES DE PESQUISA E FILTRAGEM (DATA QUERIES)
    // =========================================================

    /**
     * Filtra e devolve os cursos que estão aptos a receber novas matrículas.
     */
    public Curso[] obterCursosDisponiveisParaMatricula() {
        Curso[] ativos = new Curso[totalCursos];
        int cont = 0;
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i] != null && cursos[i].isAtivo() && cursos[i].temEstruturaValida()) ativos[cont++] = cursos[i];
        }
        Curso[] resultado = new Curso[cont];
        System.arraycopy(ativos, 0, resultado, 0, cont);
        return resultado;
    }

    /**
     * Devolve todos os estudantes inscritos numa UC específica.
     */
    public Estudante[] obterEstudantesPorUC(String siglaUC) {
        int cont = 0;
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i] != null && estudantes[i].estaInscrito(siglaUC)) cont++;
        }
        Estudante[] resultado = new Estudante[cont];
        int idx = 0;
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i] != null && estudantes[i].estaInscrito(siglaUC)) resultado[idx++] = estudantes[i];
        }
        return resultado;
    }

    public Curso obterCursoPorSigla(String sigla) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i].getSigla().equalsIgnoreCase(sigla)) return cursos[i];
        }
        return null;
    }

    public UnidadeCurricular obterUCPorSigla(String sigla) {
        for (int i = 0; i < totalUcs; i++) {
            if (ucs[i].getSigla().equalsIgnoreCase(sigla)) return ucs[i];
        }
        return null;
    }

    public Estudante obterEstudantePorNumMec(int numMec) {
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i].getNumeroMecanografico() == numMec) return estudantes[i];
        }
        return null;
    }

    public Docente obterDocentePorSigla(String sigla) {
        for (int i = 0; i < totalDocentes; i++) {
            if (docentes[i].getSigla().equalsIgnoreCase(sigla)) return docentes[i];
        }
        return null;
    }


    // =========================================================
    // 4. MÉTODOS DE INTEGRIDADE E TRANSIÇÃO GLOBAL
    // =========================================================

    public boolean existeNif(String nif) {
        for (int i = 0; i < totalGestores; i++) { if (gestores[i].getNif().equals(nif)) return true; }
        for (int i = 0; i < totalDocentes; i++) { if (docentes[i].getNif().equals(nif)) return true; }
        for (int i = 0; i < totalEstudantes; i++) { if (estudantes[i].getNif().equals(nif)) return true; }
        return false;
    }

    public boolean existeSiglaDepartamento(String sigla) {
        for (int i = 0; i < totalDepartamentos; i++) { if (departamentos[i].getSigla().equalsIgnoreCase(sigla)) return true; }
        return false;
    }

    public boolean existeSiglaCurso(String sigla) {
        for (int i = 0; i < totalCursos; i++) { if (cursos[i].getSigla().equalsIgnoreCase(sigla)) return true; }
        return false;
    }

    public boolean existeSiglaUC(String sigla) {
        for (int i = 0; i < totalUcs; i++) { if (ucs[i].getSigla().equalsIgnoreCase(sigla)) return true; }
        return false;
    }

    public boolean existeSiglaDocente(String sigla) {
        for (int i = 0; i < totalDocentes; i++) { if (docentes[i].getSigla().equalsIgnoreCase(sigla)) return true; }
        return false;
    }

    /**
     * Incrementa o ano letivo institucional e despoleta o processamento de fim de ciclo
     * em todos os estudantes ativos (transição de ano, arquivo e novas dívidas).
     */
    public void avancarAno() {
        this.anoAtual++;
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i] != null && estudantes[i].isAtivo()) {
                estudantes[i].processarFimDeAno(this.anoAtual);
            }
        }
    }

    public int contarInscritosPrimeiroAno(String siglaCurso, int ano) {
        int conta = 0;
        for (int i = 0; i < totalEstudantes; i++) {
            Estudante e = estudantes[i];
            if (e != null && e.getCurso() != null && e.getCurso().getSigla().equalsIgnoreCase(siglaCurso) && e.getAnoPrimeiraInscricao() == ano) conta++;
        }
        return conta;
    }

    public void anularMatriculasPrimeiroAno(String siglaCurso, int ano) {
        for (int i = 0; i < totalEstudantes; i++) {
            Estudante e = estudantes[i];
            if (e != null && e.getCurso() != null && e.getCurso().getSigla().equalsIgnoreCase(siglaCurso) && e.getAnoPrimeiraInscricao() == ano) {
                removerEstudante(e.getNumeroMecanografico());
                i--;
            }
        }
    }

    /**
     * Efetua uma busca transversal em todas as coleções para devolver um utilizador
     * genérico com base no seu endereço de email.
     * Essencial para o processo de recuperação de palavra-passe.
     *
     * @param email O endereço de login procurado.
     * @return A instância de Utilizador encontrada ou null caso não exista.
     */
    public Utilizador procurarUtilizadorPorEmail(String email) {
        // Pesquisa na coleção de Gestores
        for (int i = 0; i < totalGestores; i++) {
            if (gestores[i] != null && gestores[i].getEmail().equalsIgnoreCase(email)) {
                return gestores[i];
            }
        }
        // Pesquisa na coleção de Docentes
        for (int i = 0; i < totalDocentes; i++) {
            if (docentes[i] != null && docentes[i].getEmail().equalsIgnoreCase(email)) {
                return docentes[i];
            }
        }
        // Pesquisa na coleção de Estudantes
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i] != null && estudantes[i].getEmail().equalsIgnoreCase(email)) {
                return estudantes[i];
            }
        }
        return null; // Utilizador não localizado
    }
}
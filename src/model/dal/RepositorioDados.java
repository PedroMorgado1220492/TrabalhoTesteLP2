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
        this.anoAtual = ImportadorCSV.importarAno("bd/ano.csv");

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
    /**
     * Adiciona um estudante ao repositório, verificando previamente se já existe
     * outro com o mesmo número mecanográfico ou o mesmo email.
     *
     * @param estudante O estudante a ser adicionado.
     * @return {@code true} se o estudante foi adicionado com sucesso;
     *         {@code false} se já existir um estudante duplicado ou se o limite
     *         da capacidade foi atingido.
     */
    public boolean adicionarEstudante(Estudante estudante) {
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i] != null &&
                    (estudantes[i].getNumeroMecanografico() == estudante.getNumeroMecanografico() ||
                            estudantes[i].getEmail().equals(estudante.getEmail()))) {
                return false;
            }
        }
        if (totalEstudantes < estudantes.length) {
            estudantes[totalEstudantes++] = estudante;
            return true;
        }
        return false;
    }
    /**
     * Adiciona um gestor ao repositório, verificando previamente se já existe
     * outro com o mesmo email.
     *
     * @param gestor O gestor a ser adicionado.
     * @return {@code true} se o gestor foi adicionado com sucesso;
     *         {@code false} se já existir um gestor duplicado ou se o limite
     *         da capacidade foi atingido.
     */
    public boolean adicionarGestor(Gestor gestor) {
        for (int i = 0; i < totalGestores; i++) {
            if (gestores[i] != null && gestores[i].getEmail().equals(gestor.getEmail())) {
                return false;
            }
        }
        if (totalGestores < gestores.length) {
            gestores[totalGestores++] = gestor;
            return true;
        }
        return false;
    }
    /**
     * Adiciona um docente ao repositório, verificando previamente se já existe
     * outro com a mesma sigla ou o mesmo email.
     *
     * @param docente O docente a ser adicionado.
     * @return {@code true} se o docente foi adicionado com sucesso;
     *         {@code false} se já existir um docente duplicado ou se o limite
     *         da capacidade foi atingido.
     */
    public boolean adicionarDocente(Docente docente) {
        // Verifica duplicados por sigla ou email
        for (int i = 0; i < totalDocentes; i++) {
            if (docentes[i] != null &&
                    (docentes[i].getSigla().equals(docente.getSigla()) ||
                            docentes[i].getEmail().equals(docente.getEmail()))) {
                return false;
            }
        }
        if (totalDocentes < docentes.length) {
            docentes[totalDocentes++] = docente;
            return true;
        }
        return false;
    }
    /**
     * Adiciona um departamento ao repositório, verificando previamente se já existe
     * outro com a mesma sigla.
     *
     * @param dep O departamento a ser adicionado.
     * @return {@code true} se o departamento foi adicionado com sucesso;
     *         {@code false} se já existir um departamento duplicado ou se o limite
     *         da capacidade foi atingido.
     */
    public boolean adicionarDepartamento(Departamento dep) {
        for (int i = 0; i < totalDepartamentos; i++) {
            if (departamentos[i] != null && departamentos[i].getSigla().equals(dep.getSigla())) {
                return false;
            }
        }
        if (totalDepartamentos < departamentos.length) {
            departamentos[totalDepartamentos++] = dep;
            return true;
        }
        return false;
    }
    /**
     * Adiciona um curso ao repositório, verificando previamente se já existe
     * outro com a mesma sigla.
     *
     * @param curso O curso a ser adicionado.
     * @return {@code true} se o curso foi adicionado com sucesso;
     *         {@code false} se já existir um curso duplicado ou se o limite
     *         da capacidade foi atingido.
     */
    public boolean adicionarCurso(Curso curso) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i] != null && cursos[i].getSigla().equals(curso.getSigla())) {
                return false;
            }
        }
        if (totalCursos < cursos.length) {
            cursos[totalCursos++] = curso;
            return true;
        }
        return false;
    }
    /**
     * Adiciona uma unidade curricular ao repositório, verificando previamente se já existe
     * outra com a mesma sigla.
     *
     * @param uc A unidade curricular a ser adicionada.
     * @return {@code true} se a UC foi adicionada com sucesso;
     *         {@code false} se já existir uma UC duplicada ou se o limite
     *         da capacidade foi atingido.
     */
    public boolean adicionarUnidadeCurricular(UnidadeCurricular uc) {
        for (int i = 0; i < totalUcs; i++) {
            if (ucs[i] != null && ucs[i].getSigla().equals(uc.getSigla())) {
                return false;
            }
        }
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

        if (cursos == null || totalCursos == 0) {
            return new Curso[0];
        }

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
        int anoAntigo = this.anoAtual;   // guarda o ano que termina
        this.anoAtual++;                 // passa para o novo ano

        for (int i = 0; i < totalEstudantes; i++) {
            Estudante e = estudantes[i];
            if (e != null && e.isAtivo()) {
                if (Propina.temDividas(e, anoAntigo)) {
                    e.setAtivo(false);
                } else {
                    e.processarFimDeAno(this.anoAtual);
                }
            }
        }
        ExportadorCSV.exportarAno("bd", this.anoAtual);
    }
    /**
     * Define o ano letivo corrente do sistema.
     * Normalmente utilizado durante o arranque da aplicação para restaurar o
     * ano persistido em ficheiro.
     *
     * @param anoAtual O novo ano letivo (ex: 2026).
     */
    public void setAnoAtual(int anoAtual) {
        this.anoAtual = anoAtual;
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
     * Conta os alunos ativos que estão a frequentar o 1º ano de um determinado curso
     * no ano letivo atual (considerando retidos).
     */
    public int contarAlunosNoPrimeiroAno(String siglaCurso) {
        int conta = 0;
        for (int i = 0; i < totalEstudantes; i++) {
            Estudante e = estudantes[i];
            if (e != null && e.isAtivo() && e.getCurso() != null &&
                    e.getCurso().getSigla().equalsIgnoreCase(siglaCurso) &&
                    e.getAnoFrequencia() == 1) {
                conta++;
            }
        }
        return conta;
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

    /**
     * Reinicializa todas as coleções do repositório, removendo todos os dados
     * em memória e libertando espaço para uma nova carga de dados.
     * <p>
     * Os arrays são recriados com as mesmas capacidades máximas definidas no
     * construtor. O ano letivo atual NÃO é alterado por este método.
     * </p>
     */
    public void limpar() {
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
}
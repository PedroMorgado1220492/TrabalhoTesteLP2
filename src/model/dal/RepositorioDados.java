package model.dal;

import model.bll.*;

/**
 * Repositório central de dados em memória (In-Memory Database).
 * Concentra todas as instâncias vivas da aplicação (Utilizadores, Cursos, UCs, etc.)
 * e fornece os métodos essenciais de pesquisa, validação de unicidade e transição de estado global.
 */
public class RepositorioDados {

    // ---------- ATRIBUTOS ----------
    private int anoAtual;

    // Coleções (Arrays de tamanho fixo para controlo de memória estática)
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
     * Inicializa a estrutura da base de dados e define o ano letivo de arranque.
     * Os tamanhos dos arrays representam os limites máximos suportados pelo sistema.
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

    // ---------- GETTERS ----------

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

    // ---------- MÉTODOS DE ADIÇÃO (CRUD BASE) ----------

    public boolean adicionarEstudante(Estudante estudante) {
        if (totalEstudantes < estudantes.length) {
            estudantes[totalEstudantes] = estudante;
            totalEstudantes++;
            return true;
        }
        return false;
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

    public boolean adicionarCurso(Curso curso) {
        if (totalCursos < cursos.length) {
            cursos[totalCursos] = curso;
            totalCursos++;
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

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Incrementa o ano civil do sistema e despacha o comando de fim de ciclo
     * para toda a árvore de estudantes em memória.
     */
    public void avancarAno() {
        this.anoAtual++;

        // Itera pelo corpo estudantil, ativando a rotina de encerramento, arquivo e matrículas
        for (int i = 0; i < totalEstudantes; i++) {
            Estudante est = estudantes[i];
            if (est != null && est.isAtivo()) {
                est.processarFimDeAno(this.anoAtual);
            }
        }

    }

    /**
     * Verifica as credenciais submetidas no Login.
     * Itera sequencialmente pelos diferentes perfis até encontrar correspondência.
     *
     * @param email    Email introduzido pelo utilizador.
     * @param password Password introduzida (já em formato hash).
     * @return O objeto polimórfico Utilizador (que fará o downcast mais tarde) ou null se as credenciais falharem.
     */
    public Utilizador autenticar(String email, String password) {
        // Pesquisa em Gestores
        for (int i = 0; i < totalGestores; i++) {
            if (gestores[i].getEmail().equals(email) && gestores[i].getPassword().equals(password)) {
                return gestores[i];
            }
        }
        // Pesquisa em Docentes
        for (int i = 0; i < totalDocentes; i++) {
            if (docentes[i].getEmail().equals(email) && docentes[i].getPassword().equals(password)) {
                return docentes[i];
            }
        }
        // Pesquisa em Estudantes
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i].getEmail().equals(email) && estudantes[i].getPassword().equals(password)) {
                return estudantes[i];
            }
        }

        return null;
    }

    /**
     * Gera dinamicamente um Número Mecanográfico para um novo aluno.
     * Formato: Prefixo do Ano (YYYY) + Sufixo Sequencial de Inscrições nesse ano (XXXX).
     * Exemplo prático: O primeiro matriculado de 2026 recebe 20260001.
     *
     * @param anoInscricao O ano em que a matrícula ocorre.
     * @return O inteiro correspondente à matrícula mecanográfica.
     */
    public int gerarNumeroMecanografico(int anoInscricao) {
        int contadorAno = 0;

        // Contabiliza quantos alunos já foram inseridos neste exato ano civil
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i] != null && estudantes[i].getAnoPrimeiraInscricao() == anoInscricao) {
                contadorAno++;
            }
        }

        int numeroSequencial = contadorAno + 1; // Incremento para a nova vaga

        // Multiplicação por 10000 para reservar 4 casas decimais para o sequencial
        return (anoInscricao * 10000) + numeroSequencial;
    }

    /**
     * Extrai uma listagem exata dos estudantes matriculados numa dada Unidade Curricular.
     *
     * @param siglaUC A sigla a pesquisar.
     * @return Um vetor ajustado (sem posições null) com os alunos inscritos.
     */
    public Estudante[] obterEstudantesPorUC(String siglaUC) {
        int contador = 0;

        // 1ª Passagem: Sondagem do tamanho necessário para o vetor resultante
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i] != null && estudantes[i].estaInscrito(siglaUC)) contador++;
        }

        // 2ª Passagem: Alocação exata e extração dos objetos
        Estudante[] inscritos = new Estudante[contador];
        int index = 0;
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i] != null && estudantes[i].estaInscrito(siglaUC)) {
                inscritos[index++] = estudantes[i];
            }
        }
        return inscritos;
    }

    // ---------- MÉTODOS DE VERIFICAÇÃO E INTEGRIDADE (UNICIDADE) ----------

    public boolean existeNif(String nif) {
        for (int i = 0; i < totalGestores; i++) { if (gestores[i] != null && gestores[i].getNif().equals(nif)) return true; }
        for (int i = 0; i < totalDocentes; i++) { if (docentes[i] != null && docentes[i].getNif().equals(nif)) return true; }
        for (int i = 0; i < totalEstudantes; i++) { if (estudantes[i] != null && estudantes[i].getNif().equals(nif)) return true; }
        return false;
    }

    public boolean existeSiglaDepartamento(String sigla) {
        for (int i = 0; i < totalDepartamentos; i++) {
            if (departamentos[i] != null && departamentos[i].getSigla().equalsIgnoreCase(sigla)) return true;
        }
        return false;
    }

    public boolean existeSiglaCurso(String sigla) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i] != null && cursos[i].getSigla().equalsIgnoreCase(sigla)) return true;
        }
        return false;
    }

    public boolean existeSiglaUC(String sigla) {
        for (int i = 0; i < totalUcs; i++) {
            if (ucs[i] != null && ucs[i].getSigla().equalsIgnoreCase(sigla)) return true;
        }
        return false;
    }

    public boolean existeSiglaDocente(String sigla) {
        for (int i = 0; i < totalDocentes; i++) {
            if (docentes[i] != null && docentes[i].getSigla().equalsIgnoreCase(sigla)) return true;
        }
        return false;
    }

    // ---------- MANIPULAÇÃO COMPLEXA DA ÁRVORE ----------

    /**
     * Remove fisicamente um estudante do repositório através de uma compactação do vetor.
     * Utilizado para desativar alunos quando o "Numerus Clausus" mínimo do curso não é atingido.
     *
     * @param numMec O número mecanográfico do aluno.
     * @return true se a remoção for sucedida; false se o aluno não for encontrado.
     */
    public boolean removerEstudante(int numMec) {
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i].getNumeroMecanografico() == numMec) {
                // Procedimento de Shift-Left: Desloca a cauda do array uma posição à esquerda
                for (int j = i; j < totalEstudantes - 1; j++) {
                    estudantes[j] = estudantes[j + 1];
                }
                // Anula a última posição para permitir a libertação de memória (Garbage Collection)
                estudantes[totalEstudantes - 1] = null;
                totalEstudantes--;
                return true;
            }
        }
        return false;
    }

    /**
     * Remove um Gestor do sistema através do seu email.
     * @param email O email do gestor a ser removido.
     * @return true se removido com sucesso, false caso contrário.
     */
    public boolean removerGestor(String email) {
        for (int i = 0; i < totalGestores; i++) {
            if (gestores[i] != null && gestores[i].getEmail().equalsIgnoreCase(email)) {
                // Desloca o resto do array uma posição à esquerda para não deixar buracos (null)
                for (int j = i; j < totalGestores - 1; j++) {
                    gestores[j] = gestores[j + 1];
                }
                gestores[totalGestores - 1] = null;
                totalGestores--;
                return true;
            }
        }
        return false;
    }

    /**
     * Efetua uma busca transversal nas coleções para devolver um utilizador genérico pelo seu email.
     * Essencial para o processo de recuperação de palavra-passe.
     *
     * @param email O endereço de login procurado.
     * @return A instância de Utilizador ou null.
     */
    public Utilizador procurarUtilizadorPorEmail(String email) {
        for (int i = 0; i < totalGestores; i++) {
            if (gestores[i] != null && gestores[i].getEmail().equalsIgnoreCase(email)) return gestores[i];
        }
        for (int i = 0; i < totalDocentes; i++) {
            if (docentes[i] != null && docentes[i].getEmail().equalsIgnoreCase(email)) return docentes[i];
        }
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i] != null && estudantes[i].getEmail().equalsIgnoreCase(email)) return estudantes[i];
        }
        return null;
    }
}
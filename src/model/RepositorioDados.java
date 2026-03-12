package model;

public class RepositorioDados {

    // ---------- ATRIBUTOS ----------
    private int anoAtual;

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
     * Executa o processo de transição de ano letivo.
     * Incrementa o ano letivo global, verifica o aproveitamento de cada estudante para
     * possível progressão de ano e arquiva as avaliações correntes no histórico permanente.
     */
    public void avancarAno() {
        this.anoAtual++;

        for (int i = 0; i < totalEstudantes; i++) {
            Estudante est = estudantes[i];
            if (est != null) {
                if (est.temAproveitamentoParaProgredir()) {
                    if (est.getAnoFrequencia() < 3) {
                        est.setAnoFrequencia(est.getAnoFrequencia() + 1);
                    } else {
                        System.out.println(">> Parabéns! O aluno " + est.getNome() + " concluiu o curso!");
                    }
                }

                // As notas vão para o histórico e o array atual é limpo.
                est.arquivarAvaliacoes();
            }
        }
        System.out.println(">> Sucesso: O ano letivo avançou para " + this.anoAtual + "!");
    }

    /**
     * Verifica as credenciais fornecidas iterando por todas as coleções de utilizadores.
     * * @param email Email de login.
     * @param password Palavra-passe de login.
     * @return O objeto Utilizador correspondente se a autenticação for bem-sucedida, null caso contrário.
     */
    public Utilizador autenticar(String email, String password) {
        for (int i = 0; i < totalGestores; i++) {
            if (gestores[i].getEmail().equals(email) && gestores[i].getPassword().equals(password)) {
                return gestores[i];
            }
        }

        for (int i = 0; i < totalDocentes; i++) {
            if (docentes[i].getEmail().equals(email) && docentes[i].getPassword().equals(password)) {
                return docentes[i];
            }
        }

        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i].getEmail().equals(email) && estudantes[i].getPassword().equals(password)) {
                return estudantes[i];
            }
        }

        return null;
    }

    /**
     * Gera um número mecanográfico baseado no ano de inscrição e num número sequencial.
     * Formato: YYYYXXXX (ex: 20260001, 20260002).
     * @param anoInscricao Ano letivo de ingresso do estudante.
     * @return O número mecanográfico gerado.
     */
    public int gerarNumeroMecanografico(int anoInscricao) {
        int contadorAno = 0;

        // Percorre todos os estudantes para ver quantos se inscreveram neste ano específico
        for (int i = 0; i < totalEstudantes; i++) {
            if (estudantes[i] != null && estudantes[i].getAnoPrimeiraInscricao() == anoInscricao) {
                contadorAno++;
            }
        }

        int numeroSequencial = contadorAno + 1; // O próximo aluno a entrar

        // Exemplo: 2026 * 10000 = 20260000. 20260000 + 1 = 20260001
        return (anoInscricao * 10000) + numeroSequencial;
    }

    // ---------- MÉTODOS DE VERIFICAÇÃO (UNICIDADE) ----------

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
}
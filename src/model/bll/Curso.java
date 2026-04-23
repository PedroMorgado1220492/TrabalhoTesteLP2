package model.bll;

/**
 * Representa um Curso (ou plano de estudos) lecionado na instituição.
 * No padrão MVC, esta classe atua como um Model puro de Domínio.
 * Agrupa Unidades Curriculares, gere o estado de funcionamento (ativo/inativo),
 * define a componente financeira (propina) e garante o cumprimento das rigorosas
 * regras de negócio académicas (limites de UCs por ano, bloqueios de edição, etc.).
 */
public class Curso {

    // ---------- ATRIBUTOS ----------
    private String sigla;
    private String nome;
    private Departamento departamento;
    private Docente docenteResponsavel; // Coordenador do Curso

    // Regras estruturais fixas
    private final int duracaoAnos = 3; // Estrutura fixa de 3 anos (Licenciatura)
    private double valorPropinaAnual = 1000.00; // Valor base por defeito

    // Arrays de agregação de componentes do curso
    private UnidadeCurricular[] unidadesCurriculares;
    private int totalUCs;
    private boolean ativo;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Curso.
     * Prepara a estrutura base do plano de estudos definindo o limite de UCs.
     *
     * @param sigla        A sigla identificativa única do curso (ex: LEI).
     * @param nome         O nome completo e descritivo do curso.
     * @param departamento O departamento institucional ao qual o curso está alocado.
     */
    public Curso(String sigla, String nome, Departamento departamento) {
        this.sigla = sigla;
        this.nome = nome;
        this.departamento = departamento;
        this.unidadesCurriculares = new UnidadeCurricular[15]; // Limite estrutural de 15 UCs por curso (5 por ano)
        this.totalUCs = 0;
        this.ativo = true; // Por defeito, um curso é criado pronto a funcionar
    }

    // ---------- GETTERS ----------

    public String getSigla() {
        return sigla;
    }

    public String getNome() {
        return nome;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public Docente getDocenteResponsavel() {
        return docenteResponsavel;
    }

    public int getDuracaoAnos() {
        return duracaoAnos;
    }

    public UnidadeCurricular[] getUnidadesCurriculares() {
        return unidadesCurriculares;
    }

    public int getTotalUCs() {
        return totalUCs;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public double getValorPropinaAnual() {
        return valorPropinaAnual;
    }

    // ---------- SETTERS ----------

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public void setDocenteResponsavel(Docente docenteResponsavel) {
        this.docenteResponsavel = docenteResponsavel;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public void setValorPropinaAnual(double valorPropinaAnual) {
        this.valorPropinaAnual = valorPropinaAnual;
    }


    // =========================================================
    // MÉTODOS DE LÓGICA DE NEGÓCIO: GESTÃO DE UCS
    // =========================================================

    /**
     * Adiciona uma unidade curricular ao plano de estudos do curso,
     * evitando a inserção de duplicados (mesma sigla).
     *
     * @param uc A unidade curricular a ser adicionada.
     * @return {@code true} se a UC foi adicionada com sucesso;
     *         {@code false} se a UC já existe no curso ou se o limite
     *         máximo de UCs (15) foi atingido.
     */
    public boolean adicionarUnidadeCurricular(UnidadeCurricular uc) {
        // Verifica se a UC já existe no curso (evita duplicados)
        for (int i = 0; i < totalUCs; i++) {
            if (unidadesCurriculares[i] != null && unidadesCurriculares[i].getSigla().equals(uc.getSigla())) {
                return false; // Já existe, não adiciona novamente
            }
        }
        if (totalUCs < unidadesCurriculares.length) {
            unidadesCurriculares[totalUCs] = uc;
            totalUCs++;
            return true;
        }
        return false;
    }


    /**
     * Remove uma Unidade Curricular do plano de estudos deste curso, reorganizando o array.
     * * @param siglaUC A sigla da Unidade Curricular a ser removida.
     *
     * @return true se a remoção for efetuada com sucesso; false caso a UC não seja encontrada.
     */
    public boolean removerUnidadeCurricular(String siglaUC) {
        for (int i = 0; i < totalUCs; i++) {
            if (unidadesCurriculares[i].getSigla().equalsIgnoreCase(siglaUC)) {
                // Efetua um Shift-Left para não deixar posições nulas no meio do array
                for (int j = i; j < totalUCs - 1; j++) {
                    unidadesCurriculares[j] = unidadesCurriculares[j + 1];
                }
                unidadesCurriculares[totalUCs - 1] = null;
                totalUCs--;
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se o curso já possui uma determinada Unidade Curricular integrada na sua matriz.
     * * @param siglaUC A sigla da UC a verificar.
     *
     * @return true se a UC já fizer parte do curso, false caso contrário.
     */
    public boolean temUnidadeCurricular(String siglaUC) {
        for (int i = 0; i < totalUCs; i++) {
            if (unidadesCurriculares[i] != null && unidadesCurriculares[i].getSigla().equalsIgnoreCase(siglaUC)) {
                return true;
            }
        }
        return false;
    }


    // =========================================================
    // MÉTODOS DE LÓGICA DE NEGÓCIO: VALIDAÇÕES DE REGRAS
    // =========================================================

    /**
     * Valida o limite de carga letiva por ano.
     * Regra de Negócio: Um curso não pode ter mais do que 5 Unidades Curriculares por ano curricular.
     * * @param anoCurricular O ano alvo a ser verificado (1, 2 ou 3).
     *
     * @return true se o ano ainda suportar mais disciplinas; false se já atingiu o limite de 5.
     */
    public boolean podeAdicionarUcNoAno(int anoCurricular) {
        int contador = 0;
        for (int i = 0; i < totalUCs; i++) {
            if (unidadesCurriculares[i].getAnoCurricular() == anoCurricular) {
                contador++;
            }
        }
        return contador < 5;
    }

    /**
     * Verifica a viabilidade de funcionamento do curso.
     * Regra de Negócio: Para ser considerado válido e aceitar matrículas, um curso tem de ter
     * obrigatoriamente pelo menos uma UC ATIVA em cada um dos 3 anos letivos.
     * * @return true se a estrutura for válida, false se algum dos anos não tiver UCs ativas.
     */
    public boolean temEstruturaValida() {
        boolean temAno1 = false, temAno2 = false, temAno3 = false;

        for (int i = 0; i < totalUCs; i++) {
            UnidadeCurricular uc = unidadesCurriculares[i];
            if (uc != null && uc.isAtivo()) {
                if (uc.getAnoCurricular() == 1) temAno1 = true;
                if (uc.getAnoCurricular() == 2) temAno2 = true;
                if (uc.getAnoCurricular() == 3) temAno3 = true;
            }
        }
        return temAno1 && temAno2 && temAno3;
    }

    /**
     * Avalia se as propriedades vitais do curso (como a Sigla) estão bloqueadas a edições.
     * Regra de Negócio: Um curso não pode sofrer alterações profundas se já tiver UCs na matriz
     * ou estudantes a frequentá-lo, de forma a não corromper o histórico do sistema.
     * * @param todosEstudantes Lista global de estudantes do sistema a ser analisada.
     *
     * @param totalEstudantes O total de estudantes atualmente registados.
     * @return true se a edição estiver bloqueada, false se for seguro alterar.
     */
    public boolean isBloqueado(Estudante[] todosEstudantes, int totalEstudantes) {
        // Bloqueia se já tiver UCs
        if (this.totalUCs > 0) return true;

        // Bloqueia se encontrar algum aluno associado a este curso
        for (int i = 0; i < totalEstudantes; i++) {
            Estudante e = todosEstudantes[i];
            if (e != null && e.getCurso() != null && e.getCurso().getSigla().equals(this.sigla)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Avalia se é seguro desativar o curso da instituição.
     * Regra de Negócio: Um curso não pode ser desativado ou suspenso se ainda tiver
     * estudantes no estado "Ativo" a frequentá-lo (para não prejudicar percursos em andamento).
     * * @param todosEstudantes Lista global de estudantes do sistema a ser analisada.
     *
     * @param totalEstudantes O total de estudantes atualmente registados.
     * @return true se a desativação for segura, false se o model a recusar.
     */
    public boolean podeSerDesativado(Estudante[] todosEstudantes, int totalEstudantes) {
        for (int i = 0; i < totalEstudantes; i++) {
            Estudante e = todosEstudantes[i];
            if (e != null && e.getCurso() != null && e.getCurso().getSigla().equals(this.sigla) && e.isAtivo()) {
                return false; // Model recusa a desativação se encontrar um aluno ativo no curso
            }
        }
        return true;
    }
}
package model.bll;

/**
 * Representa uma Unidade Curricular (disciplina) lecionada na instituição.
 * No padrão MVC, esta classe atua como um Model de Domínio.
 * Guarda as informações estruturais, define o docente regente e suporta a
 * partilha interdepartamental, permitindo que a mesma disciplina pertença a
 * múltiplos cursos simultaneamente, otimizando a gestão de recursos docentes.
 */
public class UnidadeCurricular {

    // ---------- ATRIBUTOS ----------
    private String sigla;
    private String nome;
    private int anoCurricular;
    private Docente docenteResponsavel; // Regente principal da disciplina
    private boolean ativo;
    private int numAvaliacoes;

    // Estruturas de agregação para suportar a partilha da UC
    private Curso[] cursos;
    private int totalCursos;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe UnidadeCurricular.
     * Prepara a disciplina definindo a sua estrutura base e estabelecendo um limite
     * máximo de partilha entre 10 cursos diferentes.
     *
     * @param sigla              A sigla identificadora da UC (ex: LP1).
     * @param nome               O nome completo da disciplina.
     * @param anoCurricular      O ano letivo padrão em que a disciplina é lecionada (1, 2 ou 3).
     * @param docenteResponsavel O docente encarregue da regência/coordenação.
     * @param numAvaliacoes      O número inicial de momentos de avaliação (testes/trabalhos).
     */
    public UnidadeCurricular(String sigla, String nome, int anoCurricular, Docente docenteResponsavel, int numAvaliacoes) {
        this.sigla = sigla;
        this.nome = nome;
        this.anoCurricular = anoCurricular;
        this.docenteResponsavel = docenteResponsavel;
        this.numAvaliacoes = numAvaliacoes;
        this.ativo = true; // A UC inicia o seu ciclo de vida em estado ativo
        this.cursos = new Curso[10]; // Limite de partilha fixado em 10 cursos em simultâneo
        this.totalCursos = 0;
    }

    // ---------- GETTERS SIMPLES ----------

    public String getSigla() { return sigla; }
    public String getNome() { return nome; }
    public int getAnoCurricular() { return anoCurricular; }
    public Docente getDocenteResponsavel() { return docenteResponsavel; }
    public Curso[] getCursos() { return cursos; }
    public boolean isAtivo() { return ativo; }
    public int getNumAvaliacoes() { return numAvaliacoes; }

    // ---------- SETTERS SIMPLES ----------

    public void setSigla(String sigla) { this.sigla = sigla; }
    public void setNome(String nome) { this.nome = nome; }
    public void setAnoCurricular(int anoCurricular) { this.anoCurricular = anoCurricular; }
    public void setDocenteResponsavel(Docente docenteResponsavel) { this.docenteResponsavel = docenteResponsavel; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public void setNumAvaliacoes(int numAvaliacoes) { this.numAvaliacoes = numAvaliacoes; }


    // =========================================================
    // LÓGICA DE NEGÓCIO: GESTÃO DE VÍNCULOS (CURSOS)
    // =========================================================

    /**
     * Regista esta Unidade Curricular como pertencente a um dado Curso.
     * Permite criar o vínculo necessário para que a disciplina apareça no plano de estudos do curso.
     *
     * @param curso O Curso ao qual a UC passa a estar associada.
     * @return true se a associação for bem-sucedida; false se o limite estrutural (10 cursos) for atingido.
     */
    public boolean adicionarCurso(Curso curso) {
        if (totalCursos < cursos.length) {
            cursos[totalCursos] = curso;
            totalCursos++;
            return true;
        }
        return false;
    }

    /**
     * Remove o vínculo de um curso específico a esta UC, reorganizando o arquivo.
     *
     * @param siglaCurso A sigla do curso a desassociar.
     * @return true se a remoção for concluída; false caso o curso não estivesse vinculado.
     */
    public boolean removerCurso(String siglaCurso) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i] != null && cursos[i].getSigla().equalsIgnoreCase(siglaCurso)) {
                // Efetua Shift-Left para manter a integridade do array
                for (int j = i; j < totalCursos - 1; j++) {
                    cursos[j] = cursos[j + 1];
                }
                cursos[totalCursos - 1] = null;
                totalCursos--;
                return true;
            }
        }
        return false;
    }


    // =========================================================
    // LÓGICA DE NEGÓCIO: GESTÃO DE DOCÊNCIA E AVALIAÇÃO
    // =========================================================

    /**
     * Substitui o docente responsável pela UC, gerindo automaticamente as referências cruzadas.
     * Regra de Negócio: Garante que o docente antigo deixa de ter a UC na sua lista de responsabilidades
     * e o novo docente assume o vínculo, evitando "órfãos" no sistema.
     *
     * @param novoDocente A instância do novo docente regente.
     */
    public void trocarDocenteResponsavel(Docente novoDocente) {
        // 1. Remove o vínculo do docente anterior, se existir
        if (this.docenteResponsavel != null) {
            this.docenteResponsavel.removerUcResponsavel(this.sigla);
        }

        // 2. Atualiza o atributo local
        this.docenteResponsavel = novoDocente;

        // 3. Estabelece o vínculo no novo docente
        if (novoDocente != null) {
            novoDocente.adicionarUcResponsavel(this);
        }
    }

    /**
     * Tenta alterar o número de avaliações planeadas para o semestre/ano.
     * Regra de Negócio: Por diretiva académica, uma UC deve ter entre 1 e 3 momentos de avaliação.
     *
     * @param novoNum O novo número de avaliações pretendido.
     * @return true se alterado com sucesso; false se violar os limites institucionais.
     */
    public boolean alterarNumeroAvaliacoes(int novoNum) {
        if (novoNum >= 1 && novoNum <= 3) {
            this.numAvaliacoes = novoNum;
            return true;
        }
        return false;
    }


    // =========================================================
    // LÓGICA DE NEGÓCIO: VALIDAÇÕES E CICLO DE VIDA
    // =========================================================

    /**
     * Estabelece as ligações bidirecionais iniciais entre UC, Curso e Docente.
     * Adicionada proteção contra valores nulos para evitar NullPointerException.
     */
    public void estabelecerVinculosIniciais(Curso cursoAlvo) {
        // Proteção: Só vincula se o curso alvo existir de facto
        if (cursoAlvo != null) {
            this.adicionarCurso(cursoAlvo);
            cursoAlvo.adicionarUnidadeCurricular(this);
        }

        // Vincula UC <-> Docente
        if (this.docenteResponsavel != null) {
            this.docenteResponsavel.adicionarUcResponsavel(this);
            this.docenteResponsavel.adicionarUcLecionada(this);
        }
    }

    /**
     * Avalia se a UC pode ser desativada/inativada do sistema.
     * Regra de Negócio: Uma disciplina não pode ser inativada se ainda estiver vinculada
     * a qualquer curso (para não quebrar planos de estudos ativos).
     *
     * @return true se a UC estiver livre de vínculos e puder ser desativada; false caso contrário.
     */
    public boolean podeSerDesativada() {
        return this.totalCursos == 0;
    }
}
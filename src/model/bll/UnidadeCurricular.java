package model.bll;

/**
 * Representa uma Unidade Curricular (disciplina) lecionada na instituição.
 * Guarda as informações estruturais, define o docente regente e suporta a
 * partilha interdepartamental, podendo estar associada a múltiplos cursos simultaneamente.
 */
public class UnidadeCurricular {

    // ---------- ATRIBUTOS ----------
    private String sigla;
    private String nome;
    private int anoCurricular;
    private Docente docenteResponsavel;
    private boolean ativo;
    private int numAvaliacoes;

    // Estruturas para suportar a partilha da UC por vários cursos
    private Curso[] cursos;
    private int totalCursos;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe UnidadeCurricular.
     * Prepara a disciplina definindo a sua estrutura e estabelecendo um limite
     * máximo de partilha entre 10 cursos diferentes.
     *
     * @param sigla              A sigla identificadora da UC (ex: LP1).
     * @param nome               O nome completo da disciplina.
     * @param anoCurricular      O ano letivo padrão em que a disciplina é lecionada.
     * @param docenteResponsavel O docente encarregue da regência/coordenação.
     */
    public UnidadeCurricular(String sigla, String nome, int anoCurricular, Docente docenteResponsavel, int numAvaliacoes) {
        this.sigla = sigla;
        this.nome = nome;
        this.anoCurricular = anoCurricular;
        this.docenteResponsavel = docenteResponsavel;
        this.numAvaliacoes = numAvaliacoes;
        this.ativo = true; // A UC é criada ativa por defeito
        this.cursos = new Curso[10]; // Limite de partilha fixado em 10 cursos
        this.totalCursos = 0;
    }

    // ---------- GETTERS ----------

    public String getSigla() { return sigla; }

    public String getNome() { return nome; }

    public int getAnoCurricular() { return anoCurricular; }

    public Docente getDocenteResponsavel() { return docenteResponsavel; }

    public Curso[] getCursos() { return cursos; }

    public boolean isAtivo() { return ativo; }

    public int getNumAvaliacoes() { return numAvaliacoes; }

    // ---------- SETTERS ----------

    public void setSigla(String sigla) { this.sigla = sigla; }

    public void setNome(String nome) { this.nome = nome; }

    public void setAnoCurricular(int anoCurricular) { this.anoCurricular = anoCurricular; }

    public void setDocenteResponsavel(Docente docenteResponsavel) { this.docenteResponsavel = docenteResponsavel; }

    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public void setNumAvaliacoes(int numAvaliacoes) { this.numAvaliacoes = numAvaliacoes; }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Regista esta Unidade Curricular como pertencente a um dado Curso.
     * Utilizado para criar vínculos e partilhar a disciplina por diferentes planos de estudos.
     *
     * @param curso O Curso ao qual a UC passa a estar associada.
     * @return true se a associação for bem-sucedida; false se o limite estrutural (10 cursos) já tiver sido atingido.
     */
    public boolean adicionarCurso(Curso curso) {
        // Valida se ainda existe espaço no array de cursos associados
        if (totalCursos < cursos.length) {
            cursos[totalCursos] = curso;
            totalCursos++;
            return true;
        }
        return false;
    }

    /**
     * Remove o vínculo de um curso a esta UC, reorganizando o array.
     * @param siglaCurso A sigla do curso a desassociar.
     * @return true se removeu com sucesso, false caso contrário.
     */
    public boolean removerCurso(String siglaCurso) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i] != null && cursos[i].getSigla().equalsIgnoreCase(siglaCurso)) {
                // Desliza os restantes cursos para a esquerda para não deixar buracos nulos no meio
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
}
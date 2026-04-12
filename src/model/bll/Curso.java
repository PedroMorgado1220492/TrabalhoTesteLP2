package model.bll;

/**
 * Representa um Curso (ou plano de estudos) lecionado na instituição.
 * Agrupa Unidades Curriculares, gere o estado de funcionamento (ativo/inativo)
 * e define a componente financeira associada (valor da propina anual).
 */
public class Curso {

    // ---------- ATRIBUTOS ----------
    private String sigla;
    private String nome;
    private Departamento departamento;
    private Docente docenteResponsavel;
    private final int duracaoAnos = 3; // Estrutura fixa de 3 anos (Licenciatura)
    private double valorPropinaAnual = 1000.00; // Valor por defeito
    private UnidadeCurricular[] unidadesCurriculares;
    private int totalUCs;
    private boolean ativo;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Curso.
     * Prepara a estrutura do plano de estudos com um limite global de 15 Unidades Curriculares
     * (3 anos curriculares com um limite máximo de 5 UCs cada).
     *
     * @param sigla        A sigla identificativa única do curso (ex: LEI).
     * @param nome         O nome completo do curso.
     * @param departamento O departamento institucional a que o curso pertence.
     */
    public Curso(String sigla, String nome, Departamento departamento) {
        this.sigla = sigla;
        this.nome = nome;
        this.departamento = departamento;
        this.unidadesCurriculares = new UnidadeCurricular[15]; // Limite estrutural do plano de estudos
        this.totalUCs = 0;
        this.ativo = true; // Por defeito, um curso é criado em estado ativo
    }

    // ---------- GETTERS ----------

    public String getSigla() { return sigla; }

    public String getNome() { return nome; }

    public Departamento getDepartamento() { return departamento; }

    public Docente getDocenteResponsavel() { return docenteResponsavel; }

    public int getDuracaoAnos() { return duracaoAnos; }

    public UnidadeCurricular[] getUnidadesCurriculares() { return unidadesCurriculares; }

    public int getTotalUCs() { return totalUCs; }

    public boolean isAtivo() { return ativo; }

    public double getValorPropinaAnual() { return valorPropinaAnual; }

    // ---------- SETTERS ----------

    public void setSigla(String sigla) { this.sigla = sigla; }

    public void setNome(String nome) { this.nome = nome; }

    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    public void setDocenteResponsavel(Docente docenteResponsavel) { this.docenteResponsavel = docenteResponsavel; }

    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public void setValorPropinaAnual(double valorPropinaAnual) { this.valorPropinaAnual = valorPropinaAnual; }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Associa uma nova Unidade Curricular à estrutura do Curso.
     *
     * @param uc A Unidade Curricular a ser adicionada ao plano de estudos.
     * @return true se a operação for bem-sucedida; false se o limite global de UCs do curso (15) já tiver sido atingido.
     */
    public boolean adicionarUnidadeCurricular(UnidadeCurricular uc) {
        // Valida a disponibilidade de espaço na matriz do plano de estudos
        if (totalUCs < unidadesCurriculares.length) {
            unidadesCurriculares[totalUCs] = uc;
            totalUCs++;
            return true;
        }
        return false;
    }

    /**
     * Valida a viabilidade de adicionar uma nova Unidade Curricular a um ano letivo específico.
     * Assegura o cumprimento da regra de negócio que impõe um teto máximo de carga letiva por ano.
     *
     * @param anoCurricular O ano letivo a ser validado (1, 2 ou 3).
     * @return true se o número de UCs alocadas a esse ano for inferior a 5; false caso contrário.
     */
    public boolean podeAdicionarUcNoAno(int anoCurricular) {
        int contadorUcsNesteAno = 0;

        // Itera sobre as UCs atualmente associadas ao curso para contabilizar a carga do ano especificado
        for (int i = 0; i < totalUCs; i++) {
            if (unidadesCurriculares[i].getAnoCurricular() == anoCurricular) {
                contadorUcsNesteAno++;
            }
        }

        // Retorna a verificação da regra estrita: máximo de 5 UCs por ano curricular
        return contadorUcsNesteAno < 5;
    }
}
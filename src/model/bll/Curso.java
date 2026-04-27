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
        this.sigla = sigla.toUpperCase();
        this.nome = nome;
        this.departamento = departamento;
        this.unidadesCurriculares = new UnidadeCurricular[15]; // Limite estrutural de 15 UCs por curso (5 por ano)
        this.totalUCs = 0;
        this.ativo = true; // Por defeito, um curso é criado pronto a funcionar
    }

    // ---------- GETTERS ----------
    public String getSigla() { return sigla.toUpperCase(); }
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


    // =========================================================
    // MÉTODOS DE LÓGICA DE NEGÓCIO: GESTÃO DE UCS
    // =========================================================

    public boolean adicionarUnidadeCurricular(UnidadeCurricular uc) {
        for (int i = 0; i < totalUCs; i++) {
            if (unidadesCurriculares[i] != null && unidadesCurriculares[i].getSigla().equals(uc.getSigla())) {
                return false;
            }
        }
        if (totalUCs < unidadesCurriculares.length) {
            unidadesCurriculares[totalUCs] = uc;
            totalUCs++;
            return true;
        }
        return false;
    }

    public boolean removerUnidadeCurricular(String siglaUC) {
        for (int i = 0; i < totalUCs; i++) {
            if (unidadesCurriculares[i].getSigla().equalsIgnoreCase(siglaUC)) {
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

    public boolean podeAdicionarUcNoAno(int anoCurricular) {
        int contador = 0;
        for (int i = 0; i < totalUCs; i++) {
            if (unidadesCurriculares[i].getAnoCurricular() == anoCurricular) {
                contador++;
            }
        }
        return contador < 5;
    }

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

    public boolean isBloqueado(Estudante[] todosEstudantes, int totalEstudantes) {
        // Apenas bloqueia se existir pelo menos um estudante associado a este curso
        for (int i = 0; i < totalEstudantes; i++) {
            Estudante e = todosEstudantes[i];
            if (e != null && e.getCurso() != null && e.getCurso().getSigla().equals(this.sigla)) {
                return true; // há pelo menos um aluno, curso em funcionamento
            }
        }
        return false; // sem alunos, pode alterar
    }

    public boolean podeSerDesativado(Estudante[] todosEstudantes, int totalEstudantes) {
        for (int i = 0; i < totalEstudantes; i++) {
            Estudante e = todosEstudantes[i];
            if (e != null && e.getCurso() != null && e.getCurso().getSigla().equals(this.sigla) && e.isAtivo()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se todas as Unidades Curriculares associadas a este curso estão ativas.
     * @return true se todas as UCs estiverem ativas (ou se não houver UCs), false caso contrário.
     */
    public boolean todasUcsAtivas() {
        for (int i = 0; i < totalUCs; i++) {
            UnidadeCurricular uc = unidadesCurriculares[i];
            if (uc != null && !uc.isAtivo()) {
                return false;
            }
        }
        return true;
    }
}
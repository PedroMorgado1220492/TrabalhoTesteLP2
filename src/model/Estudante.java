package model;

public class Estudante extends Utilizador {

    // ---------- ATRIBUTOS ----------
    private final int numeroMecanografico;
    private final int anoPrimeiraInscricao;
    private Curso curso;
    private int anoCurricular;
    private int anoFrequencia;
    private PercursoAcademico percursoAcademico;

    // Arrays de Avaliações
    private Avaliacao[] avaliacoes;
    private int totalAvaliacoes;
    private Avaliacao[] historicoAvaliacoes;
    private int totalHistorico;

    // ---------- CONSTRUTOR ----------
    public Estudante(int numeroMecanografico, String email, String password, String nome, String nif, String morada, String dataNascimento, Curso curso, int anoPrimeiraInscricao) {
        super(email, password, nome, nif, morada, dataNascimento);
        this.numeroMecanografico = numeroMecanografico;
        this.curso = curso;
        this.anoPrimeiraInscricao = anoPrimeiraInscricao;

        this.anoCurricular = 1;
        this.anoFrequencia = 1;
        this.percursoAcademico = new PercursoAcademico(this);

        this.avaliacoes = new Avaliacao[20];
        this.totalAvaliacoes = 0;

        this.historicoAvaliacoes = new Avaliacao[150];
        this.totalHistorico = 0;
    }

    // ---------- GETTERS ----------
    public int getNumeroMecanografico() { return numeroMecanografico; }
    public Curso getCurso() { return curso; }
    public int getAnoPrimeiraInscricao() { return anoPrimeiraInscricao; }
    public int getAnoCurricular() { return anoCurricular; }
    public int getAnoFrequencia() { return anoFrequencia; }
    public PercursoAcademico getPercursoAcademico() { return percursoAcademico; }
    public String getDataNascimento() { return this.dataNascimento; }
    public Avaliacao[] getAvaliacoes() { return this.avaliacoes; }
    public int getTotalAvaliacoes() { return this.totalAvaliacoes; }
    public Avaliacao[] getHistoricoAvaliacoes() { return historicoAvaliacoes; }
    public int getTotalHistorico() { return totalHistorico; }

    // ---------- SETTERS ----------
    public void setNome(String nome) { this.nome = nome; }
    public void setNif(String nif) { this.nif = nif; }
    public void setMorada(String morada) { this.morada = morada; }
    public void setPassword(String password) { this.password = password; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public void setAnoCurricular(int anoCurricular) { this.anoCurricular = anoCurricular; }
    public void setAnoFrequencia(int anoFrequencia) { this.anoFrequencia = anoFrequencia; }
    public void setPercursoAcademico(PercursoAcademico percursoAcademico) { this.percursoAcademico = percursoAcademico; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Regista uma nova nota para o estudante numa determinada Unidade Curricular.
     * Se o estudante já tiver uma avaliação para a UC, adiciona a nota ao registo existente.
     * * @param uc Unidade Curricular a avaliar.
     * @param nota Valor da nota (0 a 20).
     * @param anoAtual Ano letivo em que a nota é lançada.
     */
    public void adicionarNota(UnidadeCurricular uc, double nota, int anoAtual) {
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].getUc().getSigla().equals(uc.getSigla())) {
                boolean sucesso = avaliacoes[i].adicionarResultado(nota);
                if (!sucesso) {
                    System.out.println("Erro: Já foram lançadas as 3 notas máximas para esta UC.");
                }
                return;
            }
        }

        if (totalAvaliacoes < avaliacoes.length) {
            Avaliacao novaAvaliacao = new Avaliacao(this, uc, anoAtual);
            novaAvaliacao.adicionarResultado(nota);
            avaliacoes[totalAvaliacoes] = novaAvaliacao;
            totalAvaliacoes++;
        }
    }

    /**
     * Transfere as avaliações do ano letivo corrente para o histórico permanente
     * e limpa as avaliações atuais para preparar o novo ano letivo.
     */
    public void arquivarAvaliacoes() {
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (totalHistorico < historicoAvaliacoes.length) {
                historicoAvaliacoes[totalHistorico] = avaliacoes[i];
                totalHistorico++;
            }
        }
        this.avaliacoes = new Avaliacao[20];
        this.totalAvaliacoes = 0;
    }

    /**
     * Adiciona diretamente uma avaliação ao histórico do estudante.
     * Utilizado principalmente durante a importação de dados antigos via CSV.
     * * @param av Avaliação a ser adicionada ao histórico.
     */
    public void adicionarAoHistorico(Avaliacao av) {
        if (totalHistorico < historicoAvaliacoes.length) {
            historicoAvaliacoes[totalHistorico] = av;
            totalHistorico++;
        } else {
            System.out.println(">> Aviso: Limite de histórico atingido para o aluno " + this.getNome());
        }
    }

    /**
     * Verifica se o estudante tem aproveitamento suficiente para progredir de ano letivo.
     * A regra define que o rácio de avaliações positivas (>= 9.5) deve ser de pelo menos 60%.
     * * @return true se o estudante cumpre os requisitos de progressão, false caso contrário.
     */
    public boolean temAproveitamentoParaProgredir() {
        if (totalAvaliacoes == 0) return false;

        int positivas = 0;
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].calcularMedia() >= 9.5) {
                positivas++;
            }
        }

        double aproveitamento = (double) positivas / totalAvaliacoes;
        return aproveitamento >= 0.60;
    }

    /**
     * Avança o ano curricular do estudante (Lógica simples de incremento).
     */
    public void avancarAno() {
        anoCurricular++;
    }

    @Override
    public String toString() {
        return numeroMecanografico + " - " + nome + (curso != null ? " (" + curso.getNome() + ")" : "");
    }
}
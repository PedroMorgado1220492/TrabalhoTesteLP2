package model;

public class Estudante extends Utilizador {
    private final int numeroMecanografico;
    private Curso curso;
    private final int anoPrimeiraInscricao;
    private int anoCurricular;
    private PercursoAcademico percursoAcademico;
    private int anoFrequencia = 1; // Inicia sempre no 1º ano
    private Avaliacao[] historicoAvaliacoes = new Avaliacao[150]; // Espaço para todo o curso
    private int totalHistorico = 0;
    private Avaliacao[] avaliacoes = new Avaliacao[20];
    private int totalAvaliacoes = 0;


    public Estudante(int numeroMecanografico, String email, String password, String nome, String nif, String morada, String dataNascimento, Curso curso, int anoPrimeiraInscricao) {
        super(email, password, nome, nif, morada, dataNascimento);
        this.numeroMecanografico = numeroMecanografico;
        this.curso = curso;
        this.anoPrimeiraInscricao = anoPrimeiraInscricao;
        this.anoCurricular = 1; // Começa sempre no 1º ano
        this.percursoAcademico = new PercursoAcademico(this);
    }


    public void adicionarNota(UnidadeCurricular uc, double nota, int anoAtual) {
        // 1. Procurar se o aluno já tem um registo de avaliação para esta UC
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].getUc().getSigla().equals(uc.getSigla())) {
                // Tenta adicionar ao array de 3 notas
                boolean sucesso = avaliacoes[i].adicionarResultado(nota);
                if (!sucesso) {
                    System.out.println("Erro: Já foram lançadas as 3 notas máximas para esta UC.");
                }
                return;
            }
        }

        // 2. Se não encontrou, cria um novo objeto Avaliacao
        if (totalAvaliacoes < avaliacoes.length) {
            Avaliacao novaAvaliacao = new Avaliacao(this, uc, anoAtual);
            novaAvaliacao.adicionarResultado(nota);
            avaliacoes[totalAvaliacoes] = novaAvaliacao;
            totalAvaliacoes++;
        }
    }

    public void arquivarAvaliacoes() {
        // 1. Copiar as avaliações atuais para o array de histórico
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (totalHistorico < historicoAvaliacoes.length) {
                historicoAvaliacoes[totalHistorico] = avaliacoes[i];
                totalHistorico++;
            }
        }

        // 2. Limpar o ano letivo atual (Reset)
        // Assumindo que o teu array original tinha tamanho 20
        this.avaliacoes = new Avaliacao[20];
        this.totalAvaliacoes = 0;
    }
    // ---------- GETTERS ----------

    public int getNumeroMecanografico() {
        return numeroMecanografico;
    }

    public Curso getCurso() {
        return curso;
    }

    public int getAnoPrimeiraInscricao() {
        return anoPrimeiraInscricao;
    }

    public int getAnoCurricular() {
        return anoCurricular;
    }

    public PercursoAcademico getPercursoAcademico() {
        return percursoAcademico;
    }

    public String getDataNascimento() {
        return this.dataNascimento;
    }

    public Avaliacao[] getAvaliacoes() {
        return this.avaliacoes;
    }

    public int getTotalAvaliacoes() {
        return this.totalAvaliacoes;
    }

    public int getAnoFrequencia() { return anoFrequencia; }

    public Avaliacao[] getHistoricoAvaliacoes() { return historicoAvaliacoes; }

    public int getTotalHistorico() { return totalHistorico; }
    // ---------- SETTERS ----------

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setAnoCurricular(int anoCurricular) {
        this.anoCurricular = anoCurricular;
    }

    public void setPercursoAcademico(PercursoAcademico percursoAcademico) {
        this.percursoAcademico = percursoAcademico;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setAnoFrequencia(int anoFrequencia) { this.anoFrequencia = anoFrequencia; }
    // ---------- MÉTODOS ÚTEIS ----------

    public void avancarAno() {
        anoCurricular++;
    }

    /**
     * Lógica: (UCs com nota >= 9.5) / (Total de UCs inscritas pelo aluno)
     */
    public boolean temAproveitamentoParaProgredir() {
        if (totalAvaliacoes == 0) return false;

        int positivas = 0;
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].calcularMedia() >= 9.5) {
                positivas++;
            }
        }

        // Exemplo: 3 positivas / 5 inscritas = 0.6 (60%) -> Passa
        // Exemplo: 5 positivas / 7 inscritas = 0.71 (71%) -> Passa
        double aproveitamento = (double) positivas / totalAvaliacoes;

        return aproveitamento >= 0.60;
    }

    public void adicionarAoHistorico(Avaliacao av) {
        // Verifica se ainda há espaço no array
        if (totalHistorico < historicoAvaliacoes.length) {
            historicoAvaliacoes[totalHistorico] = av;
            totalHistorico++;
        } else {
            // Apenas um aviso de segurança (num cenário real, o array devia crescer)
            System.out.println(">> Aviso: Limite de histórico atingido para o aluno " + this.getNome());
        }
    }

    @Override
    public String toString() {
        return numeroMecanografico + " - " + nome +
                (curso != null ? " (" + curso.getNome() + ")" : "");
    }
}
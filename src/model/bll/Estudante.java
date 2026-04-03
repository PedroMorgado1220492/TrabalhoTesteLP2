package model.bll;

public class Estudante extends Utilizador {

    // ---------- ATRIBUTOS ----------
    private final int numeroMecanografico;
    private final int anoPrimeiraInscricao;
    private Curso curso;
    private int anoCurricular;
    private int anoFrequencia;
    private PercursoAcademico percursoAcademico;
    private Propina[] propinas;
    private int totalPropinas;
    private double valorPropinaBase;

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

        this.propinas = new Propina[10];
        this.totalPropinas = 0;

        if (this.curso != null) {
            this.valorPropinaBase = curso.getValorPropinaAnual();
            adicionarPropina(anoPrimeiraInscricao, this.valorPropinaBase);
        }
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
    public double getValorPropinaBase() { return valorPropinaBase; }

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
    public void setValorPropinaBase(double ValorPropinaBase) { this.valorPropinaBase = ValorPropinaBase; }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Regista uma nova nota para o estudante numa determinada Unidade Curricular.
     * Se o estudante já tiver uma avaliação para a UC, adiciona a nota ao registo existente.
     * * @param uc Unidade Curricular a avaliar.
     * @param nota Valor da nota (0 a 20).
     * @param anoAtual Ano letivo em que a nota é lançada.
     */
    /**
     * Descobre qual é o número da avaliação que o aluno vai fazer a seguir (1, 2 ou 3).
     */
    public int obterNumeroProximaAvaliacao(String siglaUC) {
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return avaliacoes[i].getTotalAvaliacoesLancadas() + 1;
            }
        }
        return 1;
    }

    /**
     * Regista uma nova nota. Agora devolve um boolean em vez de ser void.
     */
    public boolean adicionarNota(UnidadeCurricular uc, double nota, int anoAtual) {
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(uc.getSigla())) {
                return avaliacoes[i].adicionarResultado(nota);
            }
        }

        if (totalAvaliacoes < avaliacoes.length) {
            Avaliacao novaAvaliacao = new Avaliacao(this, uc, anoAtual);
            boolean sucesso = novaAvaliacao.adicionarResultado(nota);
            avaliacoes[totalAvaliacoes] = novaAvaliacao;
            totalAvaliacoes++;
            return sucesso;
        }
        return false;
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
     * Regra dos 60%: O rácio é calculado com base nas cadeiras em que ESTÁ INSCRITO,
     * e não apenas nas cadeiras em que teve notas lançadas.
     */
    public boolean temAproveitamentoParaProgredir() {
        if (percursoAcademico == null || percursoAcademico.getTotalUcsInscrito() == 0) {
            return false;
        }

        int positivas = 0;
        int totalInscritas = percursoAcademico.getTotalUcsInscrito();

        // Verifica quantas das cadeiras INSCRITAS o aluno efetivamente passou
        for (int i = 0; i < totalInscritas; i++) {
            UnidadeCurricular uc = percursoAcademico.getUcsInscrito()[i];
            if (teveAprovacao(uc.getSigla())) {
                positivas++;
            }
        }

        double aproveitamento = (double) positivas / totalInscritas;
        return aproveitamento >= 0.60;
    }

    public boolean estaInscrito(String siglaUC) {
        if (percursoAcademico == null) return false;
        for (int i = 0; i < percursoAcademico.getTotalUcsInscrito(); i++) {
            if (percursoAcademico.getUcsInscrito()[i].getSigla().equalsIgnoreCase(siglaUC)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Processa o final do ano letivo para este estudante específico.
     */
    public void processarFimDeAno() {
        if (curso == null || percursoAcademico == null) return;

        // 1. Descobrir quais cadeiras ele chumbou (para repetir)
        UnidadeCurricular[] ucsParaRepetir = new UnidadeCurricular[20];
        int totalRepetir = 0;

        for (int j = 0; j < percursoAcademico.getTotalUcsInscrito(); j++) {
            UnidadeCurricular uc = percursoAcademico.getUcsInscrito()[j];
            if (!teveAprovacao(uc.getSigla())) {
                ucsParaRepetir[totalRepetir++] = uc;
            }
        }

        // 2. A LÓGICA DE ARQUIVO
        arquivarAvaliacoes();

        // 3. Limpar as inscrições do ano que acabou de terminar
        percursoAcademico.limparInscricoesAtivas();

        // 4. AUTO-MATRÍCULA: Primeiro, reinscrever nas que chumbou
        for (int j = 0; j < totalRepetir; j++) {
            percursoAcademico.inscreverEmUc(ucsParaRepetir[j]);
        }

        // 5. AUTO-MATRÍCULA: Inscrever nas cadeiras novas do seu ano atual
        for (int j = 0; j < curso.getTotalUCs(); j++) {
            UnidadeCurricular ucCurso = curso.getUnidadesCurriculares()[j];

            if (ucCurso.getAnoCurricular() == anoFrequencia) {
                if (!estaInscrito(ucCurso.getSigla()) && !jaConcluiuUC(ucCurso.getSigla())) {
                    percursoAcademico.inscreverEmUc(ucCurso);
                }
            }
        }
        // 6. A LÓGICA DE PROGRESSÃO (COM VALIDAÇÃO FINANCEIRA)
        if (temDividas()) {
            System.out.println(">> BLOQUEADO: O aluno " + nome + " não pode progredir de ano devido a propinas em atraso.");
        } else if (temAproveitamentoParaProgredir()) {
            if (anoFrequencia < 3) {
                anoFrequencia++;
            } else {
                System.out.println(">> Parabéns! O aluno " + nome + " concluiu o curso!");
            }
        }
    }

    /**
     * Verifica se o aluno teve nota positiva a uma determinada UC no ano corrente.
     */
    public boolean teveAprovacao(String siglaUC) {
        for (int i = 0; i < this.totalAvaliacoes; i++) {
            Avaliacao av = this.avaliacoes[i];

            if (av.getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return av.calcularMedia() >= 9.5;
            }
        }
        return false; // Se não encontrou a nota lançada, reprova automaticamente.
    }

    /**
     * Verifica se o aluno já concluiu com sucesso esta UC num ano anterior (Histórico).
     */
    public boolean jaConcluiuUC(String siglaUC) {
        for (int i = 0; i < totalHistorico; i++) {
            Avaliacao avHist = historicoAvaliacoes[i];
            if (avHist.getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                if (avHist.calcularMedia() >= 9.5) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String infoCurso;

        // Verificamos se o aluno tem um curso atribuído
        if (curso != null) {
            infoCurso = " (" + curso.getNome() + ")";
        } else {
            infoCurso = "";
        }

        return numeroMecanografico + " - " + nome + infoCurso;
    }

    public void adicionarPropina(int anoLetivo, double valor) {
        if (totalPropinas < propinas.length) {
            propinas[totalPropinas++] = new Propina(anoLetivo, valor);
        }
    }

    public Propina getPropinaDoAno(int ano) {
        for (int i = 0; i < totalPropinas; i++) {
            if (propinas[i].getAnoLetivo() == ano) return propinas[i];
        }
        return null;
    }

    public boolean temDividas() {
        for (int i = 0; i < totalPropinas; i++) {
            if (!propinas[i].isPagaTotalmente()) return true;
        }
        return false;
    }
}
package model.bll;

/**
 * Representa um Estudante matriculado na instituição.
 * No padrão MVC, atua como o Model central das operações académicas de um aluno.
 * Herda da classe base Utilizador e engloba toda a complexidade do percurso,
 * centralizando regras rígidas como transições de ano, retenções, auto-matrículas,
 * gestão financeira (propinas) e o cálculo de médias/histórico a longo prazo.
 */
public class Estudante extends Utilizador {

    // ---------- ATRIBUTOS ACADÉMICOS ----------
    private final int numeroMecanografico;
    private final int anoPrimeiraInscricao;
    private Curso curso;
    private int anoCurricular;
    private int anoFrequencia;
    private PercursoAcademico percursoAcademico;
    private boolean ativo;

    // ---------- ATRIBUTOS DE AVALIAÇÃO ----------
    // Avaliações a decorrer no ano letivo atual (Buffer)
    private Avaliacao[] avaliacoes;
    private int totalAvaliacoes;
    // Registo permanente de anos letivos transatos (Arquivo)
    private Avaliacao[] historicoAvaliacoes;
    private int totalHistorico;

    // ---------- ATRIBUTOS FINANCEIROS ----------
    private Propina[] propinas;
    private int totalPropinas;
    private double valorPropinaBase;


    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Estudante.
     * Prepara a alocação de memória para o buffer de avaliações (anual) e o histórico de longo prazo.
     * Gera automaticamente a primeira dívida (propina) baseada no preçário do curso atribuído.
     *
     * @param numeroMecanografico Identificador único numérico gerado pelo sistema.
     * @param email               Email institucional gerado automaticamente.
     * @param password            Password (já encriptada).
     * @param nome                Nome completo do estudante.
     * @param nif                 Número de Identificação Fiscal.
     * @param morada              Morada de residência.
     * @param dataNascimento      Data de nascimento.
     * @param curso               Curso no qual o estudante efetuou matrícula.
     * @param anoPrimeiraInscricao O ano letivo (civil) em que ocorre o ingresso.
     * @param emailPessoal        Email pessoal do aluno para contacto e recuperação de credenciais.
     */
    public Estudante(int numeroMecanografico, String email, String password, String nome,
                     String nif, String morada, String dataNascimento, Curso curso, int anoPrimeiraInscricao, String emailPessoal) {
        super(email, password, nome, nif, morada, dataNascimento, emailPessoal);

        this.numeroMecanografico = numeroMecanografico;
        this.curso = curso;
        this.anoPrimeiraInscricao = anoPrimeiraInscricao;
        this.ativo = false;
        this.anoCurricular = 1;
        this.anoFrequencia = 1;
        this.percursoAcademico = new PercursoAcademico(this);

        // Limites estruturais para garantir o isolamento da memória
        this.avaliacoes = new Avaliacao[15];
        this.totalAvaliacoes = 0;

        this.historicoAvaliacoes = new Avaliacao[150]; // Histórico com capacidade estendida
        this.totalHistorico = 0;

        this.propinas = new Propina[10]; // Limite de 10 anos de propinas
        this.totalPropinas = 0;

        // Geração automática da infraestrutura financeira inicial
        if (this.curso != null) {
            this.valorPropinaBase = curso.getValorPropinaAnual();
            adicionarPropina(anoPrimeiraInscricao, this.valorPropinaBase);
        }
    }


    // ---------- GETTERS SIMPLES ----------

    public int getNumeroMecanografico() { return numeroMecanografico; }
    public Curso getCurso() { return curso; }
    public int getAnoPrimeiraInscricao() { return anoPrimeiraInscricao; }
    public int getAnoCurricular() { return anoCurricular; }
    public int getAnoFrequencia() { return anoFrequencia; }
    public PercursoAcademico getPercursoAcademico() { return percursoAcademico; }
    public Avaliacao[] getAvaliacoes() { return this.avaliacoes; }
    public int getTotalAvaliacoes() { return this.totalAvaliacoes; }
    public Avaliacao[] getHistoricoAvaliacoes() { return historicoAvaliacoes; }
    public int getTotalHistorico() { return totalHistorico; }
    public double getValorPropinaBase() { return valorPropinaBase; }
    public boolean isAtivo() { return ativo; }

    // ---------- SETTERS SIMPLES ----------

    public void setCurso(Curso curso) { this.curso = curso; }
    public void setAnoCurricular(int anoCurricular) { this.anoCurricular = anoCurricular; }
    public void setAnoFrequencia(int anoFrequencia) { this.anoFrequencia = anoFrequencia; }
    public void setPercursoAcademico(PercursoAcademico percursoAcademico) { this.percursoAcademico = percursoAcademico; }
    public void setValorPropinaBase(double valorPropinaBase) { this.valorPropinaBase = valorPropinaBase; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }


    // =========================================================
    // LÓGICA DE NEGÓCIO: PROGRESSÃO E INSCRIÇÕES
    // =========================================================

    /**
     * Inscreve automaticamente o estudante nas UCs correspondentes ao seu atual ano de frequência.
     * Método invocado no ato da primeira matrícula.
     */
    public void matricularNasUcsIniciais() {
        if (this.curso == null || this.percursoAcademico == null) return;

        for (int i = 0; i < this.curso.getTotalUCs(); i++) {
            UnidadeCurricular uc = this.curso.getUnidadesCurriculares()[i];
            if (uc != null && uc.getAnoCurricular() == this.anoFrequencia) {
                this.percursoAcademico.inscreverEmUc(uc);
            }
        }
    }

    /**
     * Confirma se o estudante se encontra atualmente matriculado e a frequentar a UC especificada.
     * @param siglaUC A sigla da disciplina.
     * @return true se pertencer às inscrições do ano corrente.
     */
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
     * Regra de Negócio: O aluno só pode transitar de ano se tiver aprovação a pelo menos 60%
     * das Unidades Curriculares nas quais efetuou matrícula no presente ano letivo.
     * @return true se a taxa de aprovação suportar a progressão; false caso retenha.
     */
    public boolean temAproveitamentoParaProgredir() {
        if (percursoAcademico == null || percursoAcademico.getTotalUcsInscrito() == 0) {
            return false;
        }
        int positivas = 0;
        int totalInscritas = percursoAcademico.getTotalUcsInscrito();
        for (int i = 0; i < totalInscritas; i++) {
            UnidadeCurricular uc = percursoAcademico.getUcsInscrito()[i];
            if (uc != null && teveAprovacao(uc.getSigla())) {
                positivas++;
            }
        }
        double aproveitamento = (double) positivas / totalInscritas;
        return aproveitamento >= 0.60;
    }

    /**
     * O Método "Core" do sistema. Orquestra de forma autónoma as complexas regras de encerramento de ciclo letivo.
     * O Estudante auto-audita-se (verifica aprovações e dívidas), transita de ano, arquiva as suas
     * próprias classificações e gera as suas matrículas/dívidas para o próximo ano.
     * * @param novoAnoLetivo O ano letivo que se vai iniciar.
     */
    public void processarFimDeAno(int novoAnoLetivo) {
        if (curso == null || percursoAcademico == null) return;

        // 1. Identificação das Unidades Curriculares em atraso (Reprovações a arrastar para o próximo ano)
        UnidadeCurricular[] ucsParaRepetir = new UnidadeCurricular[15];
        int totalRepetir = 0;

        for (int j = 0; j < percursoAcademico.getTotalUcsInscrito(); j++) {
            UnidadeCurricular uc = percursoAcademico.getUcsInscrito()[j];
            if (uc != null && !teveAprovacao(uc.getSigla())) {
                ucsParaRepetir[totalRepetir++] = uc;
            }
        }

        // 2. Transição de Grau / Bloqueio Administrativo
        // Regra Académica: Só avança se não tiver dívidas e se superar 60% das UCs
        if (!temDividas() && temAproveitamentoParaProgredir()) {
            if (anoFrequencia < 3) {
                anoFrequencia++;
            }
        }

        // 3. Movimentação do Buffer anual de avaliações para o Histórico Permanente
        arquivarAvaliacoes();

        // 4. Purga das inscrições passadas
        percursoAcademico.limparInscricoesAtivas();

        // 5. Auto-Matrícula Fase A: Inscrição forçada nas cadeiras em atraso
        for (int j = 0; j < totalRepetir; j++) {
            percursoAcademico.inscreverEmUc(ucsParaRepetir[j]);
        }

        // 6. Auto-Matrícula Fase B: Integração nas novas cadeiras do nível correspondente
        for (int j = 0; j < curso.getTotalUCs(); j++) {
            UnidadeCurricular ucCurso = curso.getUnidadesCurriculares()[j];

            if (ucCurso != null && ucCurso.getAnoCurricular() == anoFrequencia) {
                // Previne dupla inscrição e verifica se já não concluiu a disciplina noutro ano transato
                if (!estaInscrito(ucCurso.getSigla()) && !jaConcluiuUC(ucCurso.getSigla())) {
                    percursoAcademico.inscreverEmUc(ucCurso);
                }
            }
        }

        // 7. Geração da nova infraestrutura financeira
        // Só fatura se o aluno continuar a estudar (inscrito em > 0 disciplinas)
        if (percursoAcademico.getTotalUcsInscrito() > 0) {
            adicionarPropina(novoAnoLetivo, this.valorPropinaBase);
        }
    }


    // =========================================================
    // LÓGICA DE NEGÓCIO: GESTÃO DE AVALIAÇÕES CORRENTES
    // =========================================================

    /**
     * Tenta anexar uma nova nota ao registo do estudante na presente Unidade Curricular.
     * Cria a folha de avaliação caso seja a primeira nota lançada.
     * @param uc       A disciplina avaliada.
     * @param nota     O valor de avaliação.
     * @param anoAtual O ano civil/letivo corrente.
     * @return true se for aceite pelo model da Avaliacao; false se limites forem quebrados.
     */
    public boolean adicionarNota(UnidadeCurricular uc, double nota, int anoAtual) {
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(uc.getSigla())) {
                return avaliacoes[i].adicionarResultado(nota); // Delega limite à Avaliação
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
     * Determina que avaliação (Nº1, Nº2 ou Nº3) o estudante está prestes a realizar na UC especificada.
     */
    public int obterNumeroProximaAvaliacao(String siglaUC) {
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return avaliacoes[i].getTotalAvaliacoesLancadas() + 1;
            }
        }
        return 1;
    }

    public Avaliacao getAvaliacaoAtual(String siglaUC) {
        for (int i = 0; i < this.totalAvaliacoes; i++) {
            if (this.avaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return this.avaliacoes[i];
            }
        }
        return null;
    }

    public boolean teveAprovacao(String siglaUC) {
        for (int i = 0; i < this.totalAvaliacoes; i++) {
            Avaliacao av = this.avaliacoes[i];
            if (av.getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return av.calcularMedia() >= 9.5;
            }
        }
        return false; // Reprova por falta de nota (ausência de avaliação)
    }

    /**
     * Avalia o estado global do estudante face a uma Unidade Curricular para exibição.
     * @return 1 (Inscrito c/ nota), 2 (Inscrito s/ nota), 3 (Concluída no histórico), 0 (Sem vínculo).
     */
    public int obterCodigoEstadoUc(String sigla) {
        if (estaInscrito(sigla)) {
            return (getAvaliacaoAtual(sigla) != null) ? 1 : 2;
        } else {
            Avaliacao hist = getAvaliacaoHistorico(sigla);
            if (hist != null && hist.calcularMedia() >= 9.5) return 3;
        }
        return 0;
    }

    /**
     * Recupera a média atual obtida numa UC, procurando no ano letivo em curso ou,
     * em alternativa, no histórico se a cadeira já estiver feita.
     */
    public double obterNotaUc(String sigla) {
        if (estaInscrito(sigla)) {
            Avaliacao av = getAvaliacaoAtual(sigla);
            return (av != null) ? av.calcularMedia() : 0.0;
        } else {
            Avaliacao hist = getAvaliacaoHistorico(sigla);
            return (hist != null && hist.calcularMedia() >= 9.5) ? hist.calcularMedia() : 0.0;
        }
    }


    // =========================================================
    // LÓGICA DE NEGÓCIO: ARQUIVO HISTÓRICO E CONCLUSÃO
    // =========================================================

    /**
     * Purga o ano letivo em curso, arrastando os registos válidos para o array de Longo Prazo (Histórico).
     */
    public void arquivarAvaliacoes() {
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (totalHistorico < historicoAvaliacoes.length) {
                historicoAvaliacoes[totalHistorico] = avaliacoes[i];
                totalHistorico++;
            }
        }
        // Reseta a estrutura do ano letivo corrente
        this.avaliacoes = new Avaliacao[20];
        this.totalAvaliacoes = 0;
    }

    public void adicionarAoHistorico(Avaliacao av) {
        if (totalHistorico < historicoAvaliacoes.length) {
            historicoAvaliacoes[totalHistorico] = av;
            totalHistorico++;
        }
    }

    public Avaliacao getAvaliacaoHistorico(String siglaUC) {
        for (int i = 0; i < this.totalHistorico; i++) {
            if (this.historicoAvaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return this.historicoAvaliacoes[i];
            }
        }
        return null;
    }

    public boolean jaConcluiuUC(String siglaUC) {
        for (int i = 0; i < totalHistorico; i++) {
            Avaliacao avHist = historicoAvaliacoes[i];
            if (avHist.getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC) && avHist.calcularMedia() >= 9.5) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calcula a média aritmética global de todas as disciplinas feitas na instituição.
     * Ignora as reprovações históricas.
     * @return A média do curso até ao momento (0.0 a 20.0).
     */
    public double calcularMediaFinal() {
        double soma = 0;
        int count = 0;

        for(int i = 0; i < this.totalHistorico; i++) {
            Avaliacao av = this.historicoAvaliacoes[i];
            if (av != null && av.calcularMedia() >= 9.5) {
                soma += av.calcularMedia();
                count++;
            }
        }
        return count > 0 ? soma / count : 0.0;
    }

    /**
     * Executa a vistoria final. Cruza a matriz do Curso com o arquivo de notas do estudante
     * para declarar se o aluno está apto a receber o diploma.
     * @return true se o plano de estudos do aluno estiver preenchido a 100% com positivas.
     */
    public boolean concluiuCurso() {
        if (this.curso == null || this.curso.getTotalUCs() == 0) return false;

        int ucsAprovadas = 0;
        for (int i = 0; i < this.curso.getTotalUCs(); i++) {
            UnidadeCurricular ucCurso = this.curso.getUnidadesCurriculares()[i];

            for (int j = 0; j < this.totalHistorico; j++) {
                Avaliacao av = this.historicoAvaliacoes[j];
                if (av != null && av.getUc() != null
                        && av.getUc().getSigla().equals(ucCurso.getSigla())
                        && av.calcularMedia() >= 9.5) {
                    ucsAprovadas++;
                    break;
                }
            }
        }
        // Se o número de disciplinas feitas bater certo com a estrutura do curso, formou-se!
        return ucsAprovadas == this.curso.getTotalUCs();
    }


    // =========================================================
    // LÓGICA DE NEGÓCIO: GESTÃO FINANCEIRA
    // =========================================================

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

    /**
     * Vistoria todo o trajeto histórico do aluno e determina se este tem pagamentos pendentes.
     * Esta infração bloqueia progressões académicas (transições de ano).
     * @return true se o aluno tiver propinas abertas.
     */
    public boolean temDividas() {
        for (int i = 0; i < totalPropinas; i++) {
            if (propinas[i] != null && !propinas[i].isPagaTotalmente()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calcula o montante financeiro total devido pela acumulação de juros/propinas em dívida.
     * @param anoAtual O ano letivo até ao qual a divida deve ser calculada.
     * @return O valor real que o estudante deve à instituição.
     */
    public double calcularDividaTotal(int anoAtual) {
        double dividaTotal = 0.0;
        for (int ano = this.anoPrimeiraInscricao; ano <= anoAtual; ano++) {
            Propina p = getPropinaDoAno(ano);
            if (p != null) dividaTotal += p.getValorEmDivida();
        }
        return dividaTotal;
    }


    // ---------- OVERRIDES ----------

    @Override
    public String toString() {
        String infoCurso = (curso != null) ? " (" + curso.getNome() + ")" : "";
        return numeroMecanografico + " - " + nome + infoCurso;
    }
}
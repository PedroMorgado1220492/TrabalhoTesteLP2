package model.bll;

/**
 * Representa um Estudante matriculado na instituição.
 * Herda da classe base Utilizador e engloba toda a complexidade do percurso académico,
 * incluindo matrículas em cursos, histórico de avaliações e gestão financeira (propinas).
 */
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
    private boolean ativo;

    // Arrays de Avaliações
    private Avaliacao[] avaliacoes; // Avaliações a decorrer no ano letivo atual
    private int totalAvaliacoes;
    private Avaliacao[] historicoAvaliacoes; // Registo permanente de anos letivos transatos
    private int totalHistorico;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Estudante.
     * Prepara a alocação de memória para as avaliações e propinas, e gera automaticamente
     * a primeira propina com base no valor do curso atribuído.
     *
     * @param numeroMecanografico Identificador único numérico gerado pelo sistema.
     * @param email               Email institucional de acesso.
     * @param password            Password (encriptada).
     * @param nome                Nome completo do estudante.
     * @param nif                 Número de Identificação Fiscal.
     * @param morada              Morada de residência.
     * @param dataNascimento      Data de nascimento.
     * @param curso               Curso no qual o estudante se matricula.
     * @param anoPrimeiraInscricao O ano letivo em que ocorre o ingresso.
     * @param emailPessoal        Email pessoal para recuperação de acesso.
     */
    public Estudante(int numeroMecanografico, String email, String password, String nome,
                     String nif, String morada, String dataNascimento, Curso curso, int anoPrimeiraInscricao, String emailPessoal) {
        super(email, password, nome, nif, morada, dataNascimento, emailPessoal);

        this.numeroMecanografico = numeroMecanografico;
        this.curso = curso;
        this.anoPrimeiraInscricao = anoPrimeiraInscricao;
        this.ativo = true;
        this.anoCurricular = 1;
        this.anoFrequencia = 1;
        this.percursoAcademico = new PercursoAcademico(this);

        // Limites estruturais definidos para garantir a gestão de memória (1 ano vs Histórico total)
        this.avaliacoes = new Avaliacao[15];
        this.totalAvaliacoes = 0;

        this.historicoAvaliacoes = new Avaliacao[150];
        this.totalHistorico = 0;

        this.propinas = new Propina[10];
        this.totalPropinas = 0;

        // Geração automática da dívida de propina no momento da matrícula inicial
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

    public Avaliacao[] getAvaliacoes() { return this.avaliacoes; }

    public int getTotalAvaliacoes() { return this.totalAvaliacoes; }

    public Avaliacao[] getHistoricoAvaliacoes() { return historicoAvaliacoes; }

    public int getTotalHistorico() { return totalHistorico; }

    public double getValorPropinaBase() { return valorPropinaBase; }

    public boolean isAtivo() { return ativo; }

    // ---------- SETTERS ----------

    public void setCurso(Curso curso) { this.curso = curso; }

    public void setAnoCurricular(int anoCurricular) { this.anoCurricular = anoCurricular; }

    public void setAnoFrequencia(int anoFrequencia) { this.anoFrequencia = anoFrequencia; }

    public void setPercursoAcademico(PercursoAcademico percursoAcademico) { this.percursoAcademico = percursoAcademico; }

    public void setValorPropinaBase(double valorPropinaBase) { this.valorPropinaBase = valorPropinaBase; }

    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO (AVALIAÇÕES) ----------

    /**
     * Determina o índice sequencial da próxima avaliação a ser realizada numa dada Unidade Curricular.
     *
     * @param siglaUC A sigla da UC em causa.
     * @return O número correspondente à próxima avaliação (ex: 1 para N1, 2 para N2).
     */
    public int obterNumeroProximaAvaliacao(String siglaUC) {
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return avaliacoes[i].getTotalAvaliacoesLancadas() + 1;
            }
        }
        // Se ainda não existir objeto de avaliação, será a 1ª nota a lançar
        return 1;
    }

    /**
     * Processa o registo de uma nota atribuída por um docente.
     * Atualiza o registo existente ou instancia um novo objeto de Avaliação se for a primeira nota do ano letivo.
     *
     * @param uc       A Unidade Curricular onde foi obtida a classificação.
     * @param nota     O valor numérico (0.0 a 20.0).
     * @param anoAtual O ano civil/letivo corrente.
     * @return true se o registo for gravado com sucesso; false se exceder limites estruturais.
     */
    public boolean adicionarNota(UnidadeCurricular uc, double nota, int anoAtual) {
        // Tenta adicionar à avaliação já existente
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(uc.getSigla())) {
                return avaliacoes[i].adicionarResultado(nota);
            }
        }

        // Caso não exista, instancia uma nova folha de avaliação para a UC e insere a nota
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
     * Devolve o objeto de Avaliação associado ao ano letivo em curso, para uma determinada UC.
     */
    public Avaliacao getAvaliacaoAtual(String siglaUC) {
        for (int i = 0; i < this.totalAvaliacoes; i++) {
            if (this.avaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return this.avaliacoes[i];
            }
        }
        return null;
    }

    // ---------- MÉTODOS DE HISTÓRICO E ARQUIVO ----------

    /**
     * Transfere em bloco as avaliações correntes para o arquivo histórico e limpa o buffer anual.
     * Executado exclusivamente no encerramento de cada ano letivo.
     */
    public void arquivarAvaliacoes() {
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (totalHistorico < historicoAvaliacoes.length) {
                historicoAvaliacoes[totalHistorico] = avaliacoes[i];
                totalHistorico++;
            }
        }
        // Redefine a matriz de trabalho para o próximo ano
        this.avaliacoes = new Avaliacao[20];
        this.totalAvaliacoes = 0;
    }

    /**
     * Insere diretamente um objeto de avaliação no registo histórico.
     * Metodologia útil para popular o sistema durante a importação assíncrona de CSVs antigos.
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
     * Devolve o objeto de Avaliação consolidado no histórico de anos anteriores.
     */
    public Avaliacao getAvaliacaoHistorico(String siglaUC) {
        for (int i = 0; i < this.totalHistorico; i++) {
            if (this.historicoAvaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return this.historicoAvaliacoes[i];
            }
        }
        return null;
    }

    // ---------- MÉTODOS DE PROGRESSÃO E INSCRIÇÕES ----------

    /**
     * Confirma a condição de matrícula corrente do estudante numa disciplina.
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
     * Valida se o aluno obteve aproveitamento na disciplina durante o ano letivo corrente (média $\ge 9.5$).
     */
    public boolean teveAprovacao(String siglaUC) {
        for (int i = 0; i < this.totalAvaliacoes; i++) {
            Avaliacao av = this.avaliacoes[i];

            if (av.getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return av.calcularMedia() >= 9.5;
            }
        }
        // Se a UC não consta no mapa de notas lançadas, o aluno reprova por defeito
        return false;
    }

    /**
     * Pesquisa no arquivo geral para certificar se a UC já foi superada num ciclo anterior.
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

    /**
     * Calcula se o estudante obteve aprovação a pelo menos 60% das Unidades Curriculares
     * em que se encontra atualmente inscrito.
     * * @return true se a taxa de aprovação for igual ou superior a 60%; false caso contrário.
     */
    public boolean temAproveitamentoParaProgredir() {
        if (percursoAcademico == null || percursoAcademico.getTotalUcsInscrito() == 0) {
            return false;
        }

        int positivas = 0;
        int totalInscritas = percursoAcademico.getTotalUcsInscrito();

        // Contabiliza as UCs em que o aluno obteve aproveitamento
        for (int i = 0; i < totalInscritas; i++) {
            UnidadeCurricular uc = percursoAcademico.getUcsInscrito()[i];

            // Programação Defensiva: Verifica se a UC não é nula antes de pedir a sigla
            if (uc != null && teveAprovacao(uc.getSigla())) {
                positivas++;
            }
        }

        double aproveitamento = (double) positivas / totalInscritas;
        return aproveitamento >= 0.60;
    }

    /**
     * Orquestra as operações lógicas do encerramento de ciclo do estudante.
     * Envolve a desagregação das inscrições, arquivamento de classificações e auto-matrícula com base nas regras de progressão.
     */
    public void processarFimDeAno(int novoAnoLetivo) {
        if (curso == null || percursoAcademico == null) return;

        // 1. Identificação das Unidades Curriculares em atraso (Reprovações)
        UnidadeCurricular[] ucsParaRepetir = new UnidadeCurricular[15];
        int totalRepetir = 0;

        for (int j = 0; j < percursoAcademico.getTotalUcsInscrito(); j++) {
            UnidadeCurricular uc = percursoAcademico.getUcsInscrito()[j];
            if (uc != null && !teveAprovacao(uc.getSigla())) {
                ucsParaRepetir[totalRepetir++] = uc;
            }
        }

        // 2. Transição de Grau e Auditoria Financeira (FEITO ANTES DA MATRÍCULA!)
        // O aluno só sobe de ano se NÃO tiver dívidas e se TIVER aproveitamento (>= 60%).
        if (!temDividas() && temAproveitamentoParaProgredir()) {
            if (anoFrequencia < 3) {
                anoFrequencia++; // O ano muda aqui. As próximas inscrições já vão ler o novo ano!
            }
        }

        // 3. Transferência de dados em memória para o Histórico de Longo Prazo
        arquivarAvaliacoes();

        // 4. Purga do registo de inscrições anuais
        percursoAcademico.limparInscricoesAtivas();

        // 5. Auto-Matrícula (Fase A): Obrigatoriedade de repetir as UCs em atraso
        for (int j = 0; j < totalRepetir; j++) {
            percursoAcademico.inscreverEmUc(ucsParaRepetir[j]);
        }

        // 6. Auto-Matrícula (Fase B): Integração nas novas UCs correspondentes ao seu atual nível
        for (int j = 0; j < curso.getTotalUCs(); j++) {
            UnidadeCurricular ucCurso = curso.getUnidadesCurriculares()[j];

            // Lê o anoFrequencia, que já foi atualizado no Passo 2 (se o aluno progrediu)
            if (ucCurso != null && ucCurso.getAnoCurricular() == anoFrequencia) {
                // Impede reinscrições em cadeiras validadas em anos transatos
                if (!estaInscrito(ucCurso.getSigla()) && !jaConcluiuUC(ucCurso.getSigla())) {
                    percursoAcademico.inscreverEmUc(ucCurso);
                }
            }
        }
        // 7. Geração de faturação para o novo ano letivo
        // Só gera propina se o aluno se inscreveu em alguma cadeira (continua ativo/a estudar)
        if (percursoAcademico.getTotalUcsInscrito() > 0) {
            adicionarPropina(novoAnoLetivo, this.valorPropinaBase);
        }
    }

    // ---------- MÉTODOS FINANCEIROS ----------

    /**
     * Cria e adiciona um novo registo de dívida correspondente ao ano letivo transato.
     */
    public void adicionarPropina(int anoLetivo, double valor) {
        if (totalPropinas < propinas.length) {
            propinas[totalPropinas++] = new Propina(anoLetivo, valor);
        }
    }

    /**
     * Extrai a folha financeira subjacente a um dado ano letivo.
     */
    public Propina getPropinaDoAno(int ano) {
        for (int i = 0; i < totalPropinas; i++) {
            if (propinas[i].getAnoLetivo() == ano) return propinas[i];
        }
        return null;
    }

    /**
     * Efetua a sondagem e avalia o mapa global de propinas à procura de pagamentos incompletos.
     *
     * @return true se o aluno incorrer em infrações financeiras; false caso contrário.
     */
    public boolean temDividas() {
        for (int i = 0; i < totalPropinas; i++) {
            if (!propinas[i].isPagaTotalmente()) return true;
        }
        return false;
    }

    // ---------- OVERRIDES ----------

    @Override
    public String toString() {
        String infoCurso = (curso != null) ? " (" + curso.getNome() + ")" : "";
        return numeroMecanografico + " - " + nome + infoCurso;
    }
}
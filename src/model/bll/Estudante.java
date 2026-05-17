package model.bll;

import model.dal.PrecoCursoDAL;

/**
 * Representa um Estudante matriculado na instituição.
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
    private Avaliacao[] avaliacoes;
    private int totalAvaliacoes;
    private Avaliacao[] historicoAvaliacoes;
    private int totalHistorico;

    // ---------- CONSTRUTOR ----------
    public Estudante(int numeroMecanografico, String email, String password, String nome,
                     String nif, String morada, String dataNascimento, Curso curso,
                     int anoPrimeiraInscricao, String emailPessoal) {
        super(email, password, nome, nif, morada, dataNascimento, emailPessoal);

        this.numeroMecanografico = numeroMecanografico;
        this.curso = curso;
        this.anoPrimeiraInscricao = anoPrimeiraInscricao;
        this.ativo = false;
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
    public Avaliacao[] getAvaliacoes() { return this.avaliacoes; }
    public int getTotalAvaliacoes() { return this.totalAvaliacoes; }
    public Avaliacao[] getHistoricoAvaliacoes() { return historicoAvaliacoes; }
    public int getTotalHistorico() { return totalHistorico; }
    public boolean isAtivo() { return ativo; }
    public PercursoAcademico getPercursoAcademico() { return percursoAcademico; }

    // ---------- SETTERS ----------
    public void setCurso(Curso curso) { this.curso = curso; }
    public void setAnoCurricular(int anoCurricular) { this.anoCurricular = anoCurricular; }
    public void setAnoFrequencia(int anoFrequencia) { this.anoFrequencia = anoFrequencia; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    // =========================================================
    // LÓGICA DE NEGÓCIO: PROGRESSÃO E INSCRIÇÕES
    // =========================================================

    public void matricularNasUcsIniciais() {
        if (this.curso == null || this.percursoAcademico == null) return;
        for (int i = 0; i < this.curso.getTotalUCs(); i++) {
            UnidadeCurricular uc = this.curso.getUnidadesCurriculares()[i];
            if (uc != null && uc.getAnoCurricular() == this.anoFrequencia) {
                if (!jaConcluiuUC(uc.getSigla())) {
                    this.percursoAcademico.inscreverEmUc(uc);
                }
            }
        }
    }

    public void reconstruirPercurso() {
        if (curso == null) return;

        percursoAcademico.limparInscricoesAtivas();

        // Inscrever em UCs do ano de frequência que não estejam concluídas
        for (int i = 0; i < curso.getTotalUCs(); i++) {
            UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];
            if (uc != null && uc.getAnoCurricular() == anoFrequencia) {
                if (!jaConcluiuUC(uc.getSigla())) {
                    percursoAcademico.inscreverEmUc(uc);
                }
            }
        }

        // Inscrever em UCs de anos anteriores que NÃO foram concluídas (reprovadas ou não feitas)
        for (int i = 0; i < curso.getTotalUCs(); i++) {
            UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];
            if (uc != null && uc.getAnoCurricular() < anoFrequencia) {
                if (!jaConcluiuUC(uc.getSigla())) {
                    percursoAcademico.inscreverEmUc(uc);
                }
            }
        }
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
    public void processarFimDeAno(int novoAnoLetivo) {
        if (curso == null || percursoAcademico == null) return;

        // 1. Identificar UCs reprovadas
        UnidadeCurricular[] ucsParaRepetir = new UnidadeCurricular[15];
        int totalRepetir = 0;
        int totalInscritas = percursoAcademico.getTotalUcsInscrito();
        int positivas = 0;

        for (int j = 0; j < totalInscritas; j++) {
            UnidadeCurricular uc = percursoAcademico.getUcsInscrito()[j];
            if (uc != null) {
                boolean aprovada = teveAprovacao(uc.getSigla());
                if (aprovada) {
                    positivas++;
                } else {
                    ucsParaRepetir[totalRepetir++] = uc;
                }
            }
        }

        // 2. Calcular aproveitamento
        double aproveitamento = (totalInscritas > 0) ? (double) positivas / totalInscritas : 0;

        // 3. Decidir progressão (>= 60%)
        if (aproveitamento >= 0.60 && anoFrequencia < 3) {
            anoFrequencia++;
            anoCurricular = anoFrequencia;
        }

        // 4. Arquivar avaliações do ano que terminou
        arquivarAvaliacoes();

        // 5. Limpar inscrições atuais
        percursoAcademico.limparInscricoesAtivas();

        // 6. Inscrever em UCs reprovadas (sempre, independente de passar ou não)
        for (int j = 0; j < totalRepetir; j++) {
            percursoAcademico.inscreverEmUc(ucsParaRepetir[j]);
        }

        // 7. Inscrever em novas UCs do ano de frequência
        for (int j = 0; j < curso.getTotalUCs(); j++) {
            UnidadeCurricular ucCurso = curso.getUnidadesCurriculares()[j];
            if (ucCurso != null && ucCurso.getAnoCurricular() == anoFrequencia) {
                if (!estaInscrito(ucCurso.getSigla()) && !jaConcluiuUC(ucCurso.getSigla())) {
                    percursoAcademico.inscreverEmUc(ucCurso);
                }
            }
        }
    }

    // =========================================================
    // GESTÃO DE AVALIAÇÕES
    // =========================================================

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
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return avaliacoes[i].calcularMedia() >= 9.5;
            }
        }
        for (int i = 0; i < totalHistorico; i++) {
            if (historicoAvaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return historicoAvaliacoes[i].calcularMedia() >= 9.5;
            }
        }
        return false;
    }

    public int obterCodigoEstadoUc(String sigla) {
        Avaliacao hist = getAvaliacaoHistorico(sigla);
        if (hist != null && hist.calcularMedia() >= 9.5) {
            return 3;
        }
        if (estaInscrito(sigla)) {
            return (getAvaliacaoAtual(sigla) != null) ? 1 : 2;
        }
        return 0;
    }

    public double obterNotaUc(String sigla) {
        Avaliacao hist = getAvaliacaoHistorico(sigla);
        if (hist != null && hist.calcularMedia() >= 9.5) {
            return hist.calcularMedia();
        }
        if (estaInscrito(sigla)) {
            Avaliacao av = getAvaliacaoAtual(sigla);
            return (av != null) ? av.calcularMedia() : 0.0;
        }
        return 0.0;
    }

    // =========================================================
    // ARQUIVO HISTÓRICO
    // =========================================================

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

    public void adicionarAoHistorico(Avaliacao av) {
        if (totalHistorico < historicoAvaliacoes.length) {
            historicoAvaliacoes[totalHistorico] = av;
            totalHistorico++;
        }
    }

    public Avaliacao getAvaliacaoHistorico(String siglaUC) {
        for (int i = 0; i < this.totalHistorico; i++) {
            if (this.historicoAvaliacoes[i] != null &&
                    this.historicoAvaliacoes[i].getUnidadeCurricular().getSigla().equalsIgnoreCase(siglaUC)) {
                return this.historicoAvaliacoes[i];
            }
        }
        return null;
    }

    public boolean jaConcluiuUC(String siglaUC) {
        Avaliacao av = getAvaliacaoHistorico(siglaUC);
        return av != null && av.calcularMedia() >= 9.5;
    }

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

    public boolean concluiuCurso() {
        if (this.curso == null || this.curso.getTotalUCs() == 0) return false;
        int ucsAprovadas = 0;
        for (int i = 0; i < this.curso.getTotalUCs(); i++) {
            UnidadeCurricular ucCurso = this.curso.getUnidadesCurriculares()[i];
            if (jaConcluiuUC(ucCurso.getSigla())) {
                ucsAprovadas++;
            }
        }
        return ucsAprovadas == this.curso.getTotalUCs();
    }

    public boolean reinscrever(int anoAtual) {
        if (Propina.temDividasAteAno(this, anoAtual - 1, anoAtual)) {
            return false;
        }
        if (!isAtivo()) {
            setAtivo(true);
        }

        // Arquivar avaliações existentes antes de reconstruir
        arquivarAvaliacoes();

        // Progressão baseada no curso e histórico (60% de aprovação nas UCs do ano de frequência)
        int totalUcsAno = 0;
        int aprovadas = 0;
        if (curso != null) {
            for (int i = 0; i < curso.getTotalUCs(); i++) {
                UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];
                if (uc != null && uc.getAnoCurricular() == anoFrequencia) {
                    totalUcsAno++;
                    if (jaConcluiuUC(uc.getSigla())) {
                        aprovadas++;
                    }
                }
            }
        }
        if (totalUcsAno > 0 && (double) aprovadas / totalUcsAno >= 0.6 && anoFrequencia < 3) {
            anoFrequencia++;
            anoCurricular = anoFrequencia;
        }

        // Reconstruir o percurso (inscrever UCs não concluídas)
        reconstruirPercurso();
        return true;
    }

    @Override
    public String toString() {
        String infoCurso = (curso != null) ? " (" + curso.getNome() + ")" : "";
        return numeroMecanografico + " - " + nome + infoCurso;
    }
}
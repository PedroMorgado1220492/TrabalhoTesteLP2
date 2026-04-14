package utils;

import model.dal.RepositorioDados;
import model.bll.Estudante;
import model.bll.Curso;
import model.bll.Docente;
import model.bll.Avaliacao;

/**
 * Classe utilitária dedicada ao processamento de métricas e indicadores de desempenho.
 * Agrupa funções estáticas para calcular médias globais da instituição, rácios de sucesso
 * e estatísticas específicas para a carga letiva de cada Docente.
 */
public class Estatisticas {

    /**
     * Construtor privado para impedir a instanciação da classe.
     * Segue o padrão Utility Class, já que todos os métodos expostos são estáticos.
     */
    private Estatisticas() {}

    // =========================================================
    // ESTATÍSTICAS GLOBAIS INSTITUCIONAIS (PARA O GESTOR)
    // =========================================================

    /**
     * Calcula a média aritmética global de todas as classificações registadas na instituição.
     * Processa a totalidade dos registos de avaliação, englobando tanto o ano letivo corrente
     * como o histórico consolidado de todos os estudantes.
     *
     * @param repo O repositório central contendo os dados dos alunos.
     * @return A média global da instituição, arredondada a duas casas decimais, ou 0.0 caso não existam registos.
     */
    public static double calcularMediaGlobalInstituicao(RepositorioDados repo) {
        double somaTotal = 0;
        int contadorAvaliacoes = 0;

        // Varre a totalidade do corpo estudantil
        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            Estudante e = repo.getEstudantes()[i];

            // Acumula as avaliações decorrentes do ano atual
            for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                somaTotal += e.getAvaliacoes()[j].calcularMedia();
                contadorAvaliacoes++;
            }
            // Acumula as avaliações arquivadas nos anos transatos
            for (int j = 0; j < e.getTotalHistorico(); j++) {
                somaTotal += e.getHistoricoAvaliacoes()[j].calcularMedia();
                contadorAvaliacoes++;
            }
        }

        // Previne erros de divisão por zero caso o sistema esteja vazio
        if (contadorAvaliacoes == 0) return 0.0;

        // Processa o arredondamento matemático para apresentação em formato de pauta (ex: 14.56)
        return Math.round((somaTotal / contadorAvaliacoes) * 100.0) / 100.0;
    }

    /**
     * Executa um algoritmo de procura para identificar o estudante detentor da melhor
     * classificação média ponderada (rácio entre a soma de todas as notas e o número de avaliações realizadas).
     *
     * @param repo O repositório central de dados.
     * @return Uma String formatada com o nome, número mecanográfico e média do melhor aluno,
     * ou uma mensagem de erro caso o cálculo não seja possível.
     */
    public static String identificarMelhorAluno(RepositorioDados repo) {
        if (repo.getTotalEstudantes() == 0) return "Sem alunos registados.";

        Estudante melhorEstudante = null;
        double melhorMedia = -1.0;

        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            Estudante e = repo.getEstudantes()[i];
            double somaAluno = 0;
            int contAluno = 0;

            // Extração do subtotal do ano corrente
            for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                somaAluno += e.getAvaliacoes()[j].calcularMedia();
                contAluno++;
            }
            // Extração do subtotal do histórico passado
            for (int j = 0; j < e.getTotalHistorico(); j++) {
                somaAluno += e.getHistoricoAvaliacoes()[j].calcularMedia();
                contAluno++;
            }

            // Regista o novo líder se a média atual superar a anterior
            if (contAluno > 0) {
                double mediaAluno = somaAluno / contAluno;
                if (mediaAluno > melhorMedia) {
                    melhorMedia = mediaAluno;
                    melhorEstudante = e;
                }
            }
        }

        if (melhorEstudante == null) return "Ainda não existem notas lançadas.";

        double mediaArredondada = Math.round(melhorMedia * 100.0) / 100.0;
        return melhorEstudante.getNome() + " (" + melhorEstudante.getNumeroMecanografico() + ") com média de " + mediaArredondada;
    }

    /**
     * Determina o Curso com maior adesão, contabilizando o número total de matrículas ativas.
     *
     * @param repo O repositório de dados.
     * @return A instância do Curso com maior volume de alunos inscritos, ou null se não existirem registos.
     */
    public static Curso obterCursoComMaisAlunos(RepositorioDados repo) {
        if (repo.getTotalCursos() == 0) return null;

        Curso cursoVencedor = null;
        int maxAlunos = -1;

        for (int i = 0; i < repo.getTotalCursos(); i++) {
            Curso c = repo.getCursos()[i];
            int contadorAlunos = 0;

            // Contabiliza apenas estudantes cuja matrícula atual referencie a sigla em análise
            for (int j = 0; j < repo.getTotalEstudantes(); j++) {
                Estudante e = repo.getEstudantes()[j];
                if (e.getCurso() != null && e.getCurso().getSigla().equals(c.getSigla())) {
                    contadorAlunos++;
                }
            }

            // Atualiza o curso líder se o volume superar o registo anterior
            if (contadorAlunos > maxAlunos) {
                maxAlunos = contadorAlunos;
                cursoVencedor = c;
            }
        }
        return cursoVencedor;
    }

    // =========================================================
    // ESTATÍSTICAS FILTRADAS (PARA O DOCENTE)
    // =========================================================

    /**
     * Calcula o indicador de desempenho de um docente específico.
     * Efetua o cálculo da média aritmética limitando a agregação de dados exclusivamente
     * às classificações obtidas pelos estudantes nas disciplinas que compõem a sua carga letiva.
     *
     * @param docente O docente titular alvo da análise.
     * @param repo    O repositório de dados para extração do universo estudantil.
     * @return A média global das suas turmas (arredondada a 2 casas decimais) ou 0.0.
     */
    public static double calcularMediaUCsDocente(Docente docente, RepositorioDados repo) {
        if (docente.getTotalUcsLecionadas() == 0) return 0.0;

        double somaTotal = 0;
        int contadorAvaliacoes = 0;

        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            Estudante e = repo.getEstudantes()[i];

            // Filtra as avaliações ativas do aluno, processando apenas as que cruzam com a carga letiva do docente
            for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                Avaliacao av = e.getAvaliacoes()[j];
                if (docenteLecionaUC(docente, av.getUnidadeCurricular().getSigla())) {
                    somaTotal += av.calcularMedia();
                    contadorAvaliacoes++;
                }
            }
        }

        if (contadorAvaliacoes == 0) return 0.0;
        return Math.round((somaTotal / contadorAvaliacoes) * 100.0) / 100.0;
    }

    /**
     * Calcula o total de estudantes distintos (únicos) que foram alvo de avaliação
     * por parte do docente durante o atual ciclo letivo.
     *
     * @param docente O docente alvo da verificação.
     * @param repo    O repositório de dados.
     * @return O número total de estudantes com pelo menos uma nota lançada nas disciplinas do docente.
     */
    public static int contarAlunosAvaliadosDoDocente(Docente docente, RepositorioDados repo) {
        int totalAlunosUnicos = 0;

        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            Estudante e = repo.getEstudantes()[i];
            boolean alunoAvaliadoPeloDocente = false;

            // Analisa as avaliações correntes; a iteração é interrompida no primeiro "match"
            // para garantir que um aluno com várias notas na mesma UC conta apenas como "um" (distinto)
            for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                Avaliacao av = e.getAvaliacoes()[j];
                if (docenteLecionaUC(docente, av.getUnidadeCurricular().getSigla())) {
                    alunoAvaliadoPeloDocente = true;
                    break;
                }
            }

            if (alunoAvaliadoPeloDocente) {
                totalAlunosUnicos++;
            }
        }
        return totalAlunosUnicos;
    }

    /**
     * Método auxiliar (Helper) para verificar a pertinência orgânica entre um docente e uma disciplina.
     *
     * @param docente O docente a validar.
     * @param siglaUC A sigla correspondente à Unidade Curricular.
     * @return true se a UC estiver na lista oficial de disciplinas lecionadas pelo docente; false caso contrário.
     */
    private static boolean docenteLecionaUC(Docente docente, String siglaUC) {
        for (int i = 0; i < docente.getTotalUcsLecionadas(); i++) {
            if (docente.getUcsLecionadas()[i].getSigla().equals(siglaUC)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calcula as estatísticas puras de uma UC para o ano letivo atual.
     * @return Um array de double com: [inscritos, avaliados, max, min, media, positivas, negativas]
     */
    public static double[] calcularEstatisticasUC(model.bll.UnidadeCurricular uc, model.dal.RepositorioDados repo) {
        double max = -1.0, min = 21.0, soma = 0;
        int countComNotas = 0, inscritos = 0;
        int positivas = 0, negativas = 0;

        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            model.bll.Estudante e = repo.getEstudantes()[i];

            if (e != null && e.isAtivo() && e.estaInscrito(uc.getSigla())) {
                inscritos++;
                model.bll.Avaliacao av = e.getAvaliacaoAtual(uc.getSigla());

                if (av != null && av.getTotalAvaliacoesLancadas() > 0) {
                    double media = av.calcularMedia();

                    if (media > max) max = media;
                    if (media < min) min = media;

                    soma += media;
                    countComNotas++;

                    if (media >= 9.5) positivas++;
                    else negativas++;
                }
            }
        }

        if (countComNotas == 0) {
            return new double[] { inscritos, 0, 0, 0, 0, 0, 0 };
        }

        double mediaGlobal = soma / countComNotas;

        return new double[] { inscritos, countComNotas, max, min, mediaGlobal, positivas, negativas };
    }
}
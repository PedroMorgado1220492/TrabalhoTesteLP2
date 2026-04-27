package view;

import model.bll.Docente;
import model.bll.Estudante;
import model.bll.UnidadeCurricular;

/**
 * Interface de utilizador (View) dedicada ao perfil de Docente.
 * <p>
 * No padrão MVC, esta classe é responsável exclusivamente pela apresentação de dados
 * e captura de inputs brutos. Gere a interação relacionada com a gestão de pautas,
 * lançamento de classificações, consulta de dados profissionais e estatísticas.
 * </p>
 */
public class DocenteView {

    /**
     * Construtor por defeito da DocenteView.
     */
    public DocenteView() { }

    // =========================================================
    // 1. MENUS DE NAVEGAÇÃO
    // =========================================================

    /**
     * Apresenta o menu principal da área docente.
     * @return A opção selecionada pelo utilizador.
     */
    public int mostrarMenuPrincipal() {
        System.out.println("\n========= ÁREA DO DOCENTE =========");
        System.out.println("1 - Ver Dados Pessoais e Profissionais");
        System.out.println("2 - Atualizar Dados de Perfil");
        System.out.println("3 - Gestão de Avaliações e Pautas");
        System.out.println("4 - Consultar Estatísticas de UCs");
        System.out.println("0 - Sair (Logout)");
        System.out.print("Escolha uma opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Menu para edição de dados biográficos do docente.
     * @return A opção selecionada.
     */
    public int mostrarMenuAtualizarDados() {
        System.out.println("\n--- ATUALIZAR DADOS PESSOAIS ---");
        System.out.println("1 - Alterar Nome Completo");
        System.out.println("2 - Alterar NIF");
        System.out.println("3 - Alterar Morada");
        System.out.println("4 - Alterar Palavra-passe");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Apresenta o submenu dedicado à gestão de classificações.
     * @return A opção selecionada.
     */
    public int mostrarMenuAvaliacoes() {
        System.out.println("\n--- GESTÃO DE AVALIAÇÕES ---");
        System.out.println("1 - Lançar Nota Individual");
        System.out.println("2 - Lançar Notas em Lote (Turma Completa)");
        System.out.println("3 - Visualizar Pauta da Disciplina");
        System.out.println("4 - Definir Momentos de Avaliação");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    // =========================================================
    // 2. INPUTS DE DADOS (FORMULÁRIOS)
    // =========================================================

    /** Solicita o novo nome completo. */
    public String pedirNovoNome() {
        return utils.Consola.lerString("Novo Nome Completo: ");
    }

    /** Solicita o novo NIF (9 dígitos). */
    public String pedirNovoNif() {
        return utils.Consola.lerString("Novo NIF: ");
    }

    /** Solicita a nova morada. */
    public String pedirNovaMorada() {
        return utils.Consola.lerString("Nova Morada: ");
    }

    /** Solicita a palavra‑passe atual. */
    public String pedirPassAtual() {
        return utils.Consola.lerString("Palavra-passe Atual: ");
    }

    /** Solicita a nova palavra‑passe. */
    public String pedirNovaPass() {
        return utils.Consola.lerString("Nova Palavra-passe: ");
    }

    /** Solicita a confirmação da nova palavra‑passe. */
    public String pedirConfirmacaoPass() {
        return utils.Consola.lerString("Confirme a Nova Palavra-passe: ");
    }

    /**
     * Solicita a seleção de uma UC da lista atribuída ao docente.
     * @param ucs Array de UCs lecionadas pelo docente.
     * @param total Número total de UCs.
     * @return Índice da UC selecionada (0‑based), ou -1 se inválido.
     */
    public int pedirUC(UnidadeCurricular[] ucs, int total) {
        System.out.println("\n--- SELECIONAR UNIDADE CURRICULAR ---");
        for (int i = 0; i < total; i++) {
            System.out.printf("%d - [%s] %s\n", (i + 1), ucs[i].getSigla(), ucs[i].getNome());
        }
        return utils.Consola.lerInt("Escolha o número da UC: ") - 1;
    }

    /**
     * Solicita a seleção de um aluno da lista de inscritos.
     * @param alunos Array de estudantes inscritos na UC.
     * @param nomeUC Nome da Unidade Curricular (para contexto).
     * @return Índice do aluno selecionado (0‑based), ou -1 se inválido.
     */
    public int pedirAluno(Estudante[] alunos, String nomeUC) {
        System.out.println("\n--- LISTA DE ALUNOS: " + nomeUC + " ---");
        for (int i = 0; i < alunos.length; i++) {
            System.out.printf("%d - (%d) %s\n", (i + 1), alunos[i].getNumeroMecanografico(), alunos[i].getNome());
        }
        return utils.Consola.lerInt("Escolha o número do Aluno: ") - 1;
    }

    /**
     * Pede o valor da nota para um lançamento individual.
     * @param nome Nome do aluno.
     * @param numAvaliacao Número da avaliação (1,2,3).
     * @param maxAvaliacoes Número máximo de avaliações da UC.
     * @return String contendo a nota ou '/' para cancelar.
     */
    public String pedirNotaIndividual(String nome, int numAvaliacao, int maxAvaliacoes) {
        return utils.Consola.lerString(String.format("Nota [%d/%d] para %s (0-20) ou '/' p/ cancelar: ", numAvaliacao, maxAvaliacoes, nome));
    }

    /**
     * Pede a definição do teto máximo de avaliações da UC.
     * @return Número de avaliações (1 a 3).
     */
    public int pedirNovoNumAvaliacoes() {
        return utils.Consola.lerInt("Defina o número de avaliações para esta UC (1 a 3): ");
    }

    /**
     * Captura o input durante o lançamento sequencial (lote).
     * @param atual Número do aluno atual (1‑based).
     * @param total Total de alunos na turma.
     * @param nome Nome do aluno.
     * @param numAvaliacao Número da avaliação (1,2,3).
     * @param maxAvaliacoes Número máximo de avaliações da UC.
     * @return String com a nota ou vazio para saltar, ou '/' para cancelar.
     */
    public String inputNotaLote(int atual, int total, String nome, int numAvaliacao, int maxAvaliacoes) {
        return utils.Consola.lerString(String.format("[%d/%d] %s (N%d/%d) ou '/' p/ cancelar: ", atual, total, nome, numAvaliacao, maxAvaliacoes));
    }

    // =========================================================
    // 3. EXIBIÇÃO DE DADOS E RELATÓRIOS
    // =========================================================

    /**
     * Imprime a ficha detalhada do docente.
     * @param d Instância do Docente logado.
     */
    public void mostrarFichaDocente(Docente d) {
        System.out.println("\n--------- FICHA PROFISSIONAL ---------");
        System.out.println("Sigla Institucional : " + d.getSigla());
        System.out.println("Nome Completo       : " + d.getNome());
        System.out.println("Email Institucional : " + d.getEmail());
        System.out.println("Email Pessoal       : " + d.getEmailPessoal());
        System.out.println("NIF                 : " + d.getNif());
        System.out.println("Morada de Residência: " + d.getMorada());
        System.out.println("Data de Nascimento  : " + d.getDataNascimento());
        System.out.println("--------------------------------------");
    }

    /**
     * Imprime um relatório visual das estatísticas de desempenho de uma turma.
     * @param siglaUC Sigla da Unidade Curricular.
     * @param stats Array com estatísticas: [inscritos, avaliados, max, min, media, positivas, negativas]
     */
    public void mostrarEstatisticas(String siglaUC, double[] stats) {
        int inscritos = (int) stats[0];
        int avaliados = (int) stats[1];

        if (avaliados == 0) {
            System.out.println("\n>> Informação: Não existem notas lançadas para os " + inscritos + " inscritos em " + siglaUC + ".");
            return;
        }

        double max = stats[2];
        double min = stats[3];
        double media = stats[4];
        int positivas = (int) stats[5];
        int negativas = (int) stats[6];

        double percPositivas = ((double) positivas / avaliados) * 100;
        double percNegativas = ((double) negativas / avaliados) * 100;

        System.out.println("\n================ ESTATÍSTICAS: " + siglaUC + " ================");
        System.out.println(" - Estudantes Inscritos  : " + inscritos);
        System.out.println(" - Estudantes Avaliados  : " + avaliados);
        System.out.printf(" - Nota Mais Alta        : %.2f Valores\n", max);
        System.out.printf(" - Nota Mais Baixa       : %.2f Valores\n", min);
        System.out.printf(" - Média da Turma        : %.2f Valores\n", media);
        System.out.printf(" - Positivas (>= 9.5)    : %d (%.1f%%)\n", positivas, percPositivas);
        System.out.printf(" - Negativas (< 9.5)     : %d (%.1f%%)\n", negativas, percNegativas);
        System.out.println("======================================================");
    }

    /** Cabeçalho da pauta de avaliações. */
    public void mostrarCabecalhoPauta(String nomeUc) {
        System.out.println("\n========== PAUTA DE AVALIAÇÕES: " + nomeUc + " ==========");
    }

    /**
     * Mostra um aluno na pauta de avaliações.
     * @param numMec Número mecanográfico.
     * @param nome Nome do aluno.
     * @param anoFrequencia Ano de frequência.
     * @param notasStr Notas formatadas.
     */
    public void mostrarAlunoNaPauta(int numMec, String nome, int anoFrequencia, String notasStr) {
        System.out.printf("Num: %-8d | %-20s | %dº Ano | Notas: [%s]\n", numMec, nome, anoFrequencia, notasStr);
    }

    // =========================================================
    // 4. FEEDBACK E MENSAGENS AO UTILIZADOR
    // =========================================================

    /** Cabeçalho do lançamento em lote. */
    public void cabecalhoLote(String uc) {
        System.out.println("\n--- LANÇAMENTO EM LOTE: " + uc + " ---");
        System.out.println("(Pressione ENTER sem valor para saltar aluno)");
    }

    /** Resumo do lançamento em lote. */
    public void resumoLote(int totalLancadas) {
        System.out.println(">> Processamento concluído: " + totalLancadas + " notas registadas.");
    }

    /** Mensagem de cancelamento de operação. */
    public void mostrarCancelamento(String menuDestino) {
        System.out.println("\n>> Operação cancelada. A regressar ao menu " + menuDestino + "...");
    }

    // --- Mensagens de estado e erro ---
    public void msgSaida() { System.out.println(">> Sessão terminada. Até breve!"); }
    public void msgOpcaoInvalida() { System.out.println(">> Erro: Opção inexistente."); }
    public void msgSucesso() { System.out.println(">> Operação concluída com sucesso."); }
    public void msgErroFormato() { System.out.println(">> Erro: Formato de dados inválido."); }
    public void msgErroNotaInvalida() { System.out.println(">> Erro: A nota deve estar entre 0.0 e 20.0."); }
    public void msgErroPassIncorreta() { System.out.println(">> Erro: A palavra-passe atual está incorreta."); }
    public void msgErroPassNaoCoincidem() { System.out.println(">> Erro: As novas palavras-passe não coincidem."); }
    public void msgAvisoSemUCs() { System.out.println(">> Aviso: Não tem unidades curriculares atribuídas."); }
    public void msgAvisoTurmaVazia() { System.out.println(">> Aviso: Esta turma não tem alunos inscritos."); }
    public void msgErroAlunoInativo() { System.out.println(">> Erro: Aluno inativo. Lançamento bloqueado."); }
    public void msgErroNumAvaliacoes() { System.out.println(">> Erro: O limite deve ser entre 1 e 3."); }
    public void msgErroLimiteNotas(int max) { System.out.println(">> Erro: Limite de " + max + " notas atingido para este aluno."); }
    public void msgPautaGeradaSucesso(String path) { System.out.println(">> Pauta exportada com sucesso para: " + path); }
    public void msgErroPauta() { System.out.println(">> Erro ao gerar o ficheiro físico da pauta."); }
    public void msgAvisoSemAlunosInscritos() { System.out.println(">> Aviso: Sem alunos inscritos no presente ano letivo."); }
    public void msgNotificacaoEnviada() { System.out.println(">> Estudante notificado por e-mail automaticamente."); }
}
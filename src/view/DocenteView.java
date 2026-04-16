package view;

import model.bll.Docente;
import model.bll.Estudante;
import model.bll.UnidadeCurricular;

/**
 * Interface de utilizador (View) dedicada ao perfil de Docente.
 * Gere toda a interação relacionada com a gestão de pautas, lançamento de notas,
 * consulta de dados profissionais e estatísticas de lecionação.
 */
public class DocenteView {

    public DocenteView() {

    }

    // ---------- MENUS DE NAVEGAÇÃO ----------

    /**
     * Apresenta o menu principal da área docente.
     * @return A opção inteira selecionada.
     */
    public int mostrarMenuPrincipal() {
        System.out.println("\n=== ÁREA DO DOCENTE ===");
        System.out.println("1 - Ver Dados Pessoais e Profissionais");
        System.out.println("2 - Atualizar Dados de Perfil");
        System.out.println("3 - Adicionar Avaliações");
        System.out.println("4 - Consultar Estatísticas das UCs");
        System.out.println("0 - Sair / Logout");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    /**
     * Menu para edição de dados demográficos do docente.
     * @return A opção selecionada.
     */
    public int mostrarMenuAtualizarDados() {
        System.out.println("\n--- ATUALIZAR DADOS PESSOAIS ---");
        System.out.println("1 - Alterar Nome");
        System.out.println("2 - Alterar NIF");
        System.out.println("3 - Alterar Morada");
        System.out.println("4 - Alterar Password");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    /**
     * Apresenta o submenu dedicado à gestão de avaliações.
     */
    public int mostrarMenuAvaliacoes() {
        System.out.println("\n--- MENU AVALIAÇÕES ---");
        System.out.println("1 - Lançar Nota Individual");
        System.out.println("2 - Lançar Notas em Lote");
        System.out.println("3 - Listar Avaliações da UC");
        System.out.println("4 - Definir N.º de Momentos de Avaliação");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");

        return utils.Consola.lerOpcaoMenu();
    }

    // ---------- INPUTS DE DADOS ----------

    public String pedirNovoNome() {
        return utils.Consola.lerString("Introduza o novo Nome e Sobrenome: ");
    }

    public String pedirNovoNif() {
        return utils.Consola.lerString("Introduza o novo NIF: ");
    }

    public String pedirNovaMorada() {
        return utils.Consola.lerString("Introduza a nova Morada: ");
    }

    public String pedirPassAtual() {
        return utils.Consola.lerString("Password Atual: ");
    }

    public String pedirNovaPass() {
        return utils.Consola.lerString("Nova Password: ");
    }

    public String pedirConfirmacaoPass() {
        return utils.Consola.lerString("Confirme Nova Password: ");
    }

    /**
     * Lista as UCs atribuídas ao docente para seleção.
     */
    public int pedirUC(UnidadeCurricular[] ucs, int total) {
        System.out.println("\n--- AS MINHAS UNIDADES CURRICULARES ---");
        for (int i = 0; i < total; i++) {
            System.out.println((i + 1) + " - " + ucs[i].getNome() + " [" + ucs[i].getSigla() + "]");
        }
        System.out.print("Escolha o número da UC: ");
        return lerOpcaoInteira() - 1;
    }

    /**
     * Lista os alunos inscritos numa UC para seleção individual.
     */
    public int pedirAluno(Estudante[] alunos, String nomeUC) {
        System.out.println("\n--- ALUNOS INSCRITOS EM " + nomeUC + " ---");
        for (int i = 0; i < alunos.length; i++) {
            System.out.println((i + 1) + " - " + alunos[i].getNome() + " (" + alunos[i].getNumeroMecanografico() + ")");
        }
        System.out.print("Escolha o número do Aluno: ");
        return lerOpcaoInteira() - 1;
    }

    /**
     * Pede a nota para um aluno específico, indicando qual a avaliação (N1, N2 ou N3).
     */
    public String pedirNotaIndividual(String nome, int numAvaliacao, int maxAvaliacoes) {
        return utils.Consola.lerString("Introduza a Nota (" + numAvaliacao + "/" + maxAvaliacoes + ") para " + nome + " [0.0 - 20.0] ou '/' para cancelar: ");
    }

    /**
     * Pede o numero de avaliações para a UC.
     */
    public int pedirNovoNumAvaliacoes() {
        return utils.Consola.lerInt("Introduza o novo número de avaliações (1 a 3): ");
    }

    /**
     * Input específico para a pauta rápida (lote).
     */
    public String inputNotaLote(int atual, int total, String nome, int numAvaliacao, int maxAvaliacoes) {
        return utils.Consola.lerString("[" + atual + "/" + total + "] Nota para " + nome + " (" + numAvaliacao + "/" + maxAvaliacoes + ") ou '/' para cancelar: ");
    }

    /**
     * Método auxiliar para garantir a leitura segura de inteiros.
     */
    private int lerOpcaoInteira() {
        return utils.Consola.lerOpcaoMenu();
    }

    // ---------- EXIBIÇÃO E SELEÇÃO DE DADOS ----------

    /**
     * Imprime a ficha biográfica do docente.
     */
    public void mostrarFichaDocente(Docente d) {
        System.out.println("\n--- FICHA DE DOCENTE ---");
        System.out.println("Sigla Institucional : " + d.getSigla());
        System.out.println("Nome Completo       : " + d.getNome());
        System.out.println("Email Institucional : " + d.getEmail());
        System.out.println("NIF                 : " + d.getNif());
        System.out.println("Morada de Residência: " + d.getMorada());
        System.out.println("Data de Nascimento  : " + d.getDataNascimento());
        System.out.println("------------------------");
    }

    // ---------- MÉTODOS PARA LANÇAMENTO EM LOTE ----------

    public void cabecalhoLote(String uc) {
        System.out.println("\n--- LANÇAMENTO DE PAUTA EM LOTE: " + uc + " ---");
        System.out.println("(Pressione ENTER sem digitar nada para saltar um aluno)");
    }

    public void resumoLote(int totalLancadas) {
        System.out.println(">> Processamento concluído: " + totalLancadas + " notas registadas no sistema.");
    }

    // ---------- MÉTODOS PARA AS ESTATISTICAS ----------

    /**
     * Imprime as estatísticas de forma visual para o docente.
     */
    public void mostrarEstatisticas(String siglaUC, double[] stats) {
        int inscritos = (int) stats[0];
        int avaliados = (int) stats[1];

        if (avaliados == 0) {
            System.out.println(">> Ainda não existem avaliações lançadas para os " + inscritos + " alunos inscritos nesta UC.");
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

    // Adiciona uma opção ao Menu Principal (se ainda não existir)
    public void mostrarEstatisticasFormatadas(String estatisticas) { System.out.println("\n" + estatisticas); }

    public void mostrarCabecalhoPauta(String nomeUc) { System.out.println("\n=== PAUTA DE AVALIAÇÕES: " + nomeUc + " ==="); }

    // Usado pela funcionalidade de Listar Avaliações
    public void mostrarAlunoNaPauta(int numMec, String nome, int anoFrequencia, String notasStr) { System.out.println("Num: " + numMec + " | Nome: " + nome + " | Ano: " + anoFrequencia + "º | Notas: " + notasStr); }

    // ---------- FEEDBACK E MENSAGENS DE PAUTA ----------

    public void msgSaida() { System.out.println(">> A terminar sessão de Docente..."); }

    public void msgOpcaoInvalida() { System.out.println(">> Erro: Opção inválida."); }

    public void msgSucesso() { System.out.println(">> Operação concluída com êxito."); }

    public void msgErroFormato() { System.out.println(">> Erro: O formato do dado introduzido é inválido."); }

    public void msgErroNotaInvalida() { System.out.println(">> Erro: A classificação deve situar-se entre 0.0 e 20.0."); }

    public void msgErroLimiteNotas() { System.out.println(">> Erro: O estudante já atingiu o limite máximo de 3 avaliações nesta UC."); }

    public void msgErroPassIncorreta() { System.out.println(">> Erro: A password atual não coincide com os nossos registos."); }

    public void msgErroPassNaoCoincidem() { System.out.println(">> Erro: A nova password e a confirmação não são idênticas."); }

    public void msgAvisoSemUCs() { System.out.println(">> AVISO: Atualmente não possui Unidades Curriculares atribuídas."); }

    public void msgAvisoTurmaVazia() { System.out.println(">> AVISO: Não existem alunos inscritos nesta turma."); }

    public void msgErroAlunoInativo() { System.out.println(">> Erro: O estudante está inativo. Não é possível lançar avaliações."); }

    public void msgErroNumAvaliacoes() { System.out.println(">> Erro: O número de momentos de avaliação deve situar-se entre 1 e 3."); }

    public void msgErroLimiteNotas(int maxAvaliacoes) { System.out.println(">> Erro: O estudante já atingiu o limite de " + maxAvaliacoes + " avaliações definido para esta UC.");}

    // ---------- FEEDBACK DE PAUTAS E LISTAGENS ----------

    public void msgPautaGeradaSucesso(String caminhoTxt) { System.out.println(">> Ficheiro de Pauta gerado em: " + caminhoTxt); }
    public void msgErroPauta() { System.out.println(">> Erro: Ocorreu um problema ao gerar o ficheiro TXT da pauta."); }
    public void msgAvisoSemAlunosInscritos() { System.out.println(">> Aviso: Não existem alunos inscritos nesta Unidade Curricular no ano letivo atual."); }
    public void msgNotificacaoEnviada() { System.out.println(">> Notificação enviada por e-mail para o estudante."); }
}
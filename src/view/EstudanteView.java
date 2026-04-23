package view;

import model.bll.Estudante;

/**
 * Interface de utilizador (View) dedicada ao perfil de Estudante.
 * No padrão MVC, esta classe é responsável pela apresentação de dados académicos
 * e financeiros ao aluno, bem como pela captura de inputs para atualizações de perfil
 * e processamento de pagamentos.
 */
public class EstudanteView {

    /**
     * Construtor por defeito da EstudanteView.
     */
    public EstudanteView() { }


    // =========================================================
    // 1. MENUS DE NAVEGAÇÃO
    // =========================================================

    /**
     * Apresenta o menu principal da área reservada ao aluno.
     * @return A opção selecionada.
     */
    public int mostrarMenuPrincipal() {
        System.out.println("\n========= ÁREA DO ESTUDANTE =========");
        System.out.println("1 - Ver Ficha de Estudante");
        System.out.println("2 - Atualizar Dados Pessoais");
        System.out.println("3 - Consultar Percurso Académico");
        System.out.println("4 - Gestão de Propinas e Pagamentos");
        System.out.println("5 - Desativar Conta de Utilizador");
        System.out.println("0 - Sair (Logout)");
        System.out.print("Escolha uma opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Apresenta o submenu dedicado à edição de dados biográficos.
     * @return A opção selecionada.
     */
    public int mostrarMenuAtualizarDados() {
        System.out.println("\n--- ATUALIZAR DADOS PESSOAIS ---");
        System.out.println("1 - Alterar Nome Completo");
        System.out.println("2 - Alterar NIF");
        System.out.println("3 - Alterar Morada");
        System.out.println("4 - Alterar Palavra-passe");
        System.out.println("5 - Alterar Email Pessoal");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Exibe as modalidades de pagamento disponíveis para a propina corrente.
     * @param divida Valor total remanescente em dívida.
     * @param prestacao Valor da prestação mínima calculada pelo sistema.
     * @return A opção de pagamento selecionada.
     */
    public int mostrarOpcoesPagamento(double divida, double prestacao) {
        System.out.println("\n--- LIQUIDAÇÃO DE VALORES ---");
        System.out.printf("1 - Pagamento Integral (%.2f€)\n", divida);
        System.out.printf("2 - Pagar Prestação Mínima (%.2f€)\n", prestacao);
        System.out.println("3 - Introduzir Valor Personalizado");
        System.out.println("0 - Cancelar Operação");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }


    // =========================================================
    // 2. INPUTS DE DADOS (FORMULÁRIOS)
    // =========================================================

    public String pedirNovoNome() {
        return utils.Consola.lerString("Novo Nome Completo: ");
    }

    public String pedirNovoNif() {
        return utils.Consola.lerString("Novo NIF: ");
    }

    public String pedirNovaMorada() {
        return utils.Consola.lerString("Nova Morada: ");
    }

    public String pedirPassAtual() {
        return utils.Consola.lerString("Palavra-passe Atual: ");
    }

    public String pedirNovaPass() {
        return utils.Consola.lerString("Nova Palavra-passe: ");
    }

    public String pedirConfirmacaoPass() {
        return utils.Consola.lerString("Confirme a Nova Palavra-passe: ");
    }

    public double pedirValorLivre() {
        return utils.Consola.lerDouble("Montante a liquidar (€): ");
    }

    public String pedirNovoEmailPessoal(String atual) { return utils.Consola.lerString("Novo Email Pessoal (Atual: " + atual + ") [Enter p/ manter]: "); }
    /**
     * Solicita confirmação explícita para a desativação da conta.
     * @return true se o utilizador confirmar com 'S'.
     */
    public boolean pedirConfirmacaoDesativacao() {
        System.out.println("\n[AVISO] A desativação é imediata. Perderá o acesso a todos os serviços.");
        String input = utils.Consola.lerString("Deseja mesmo desativar a sua conta? (S/N): ");
        return input.equalsIgnoreCase("S");
    }


    // =========================================================
    // 3. EXIBIÇÃO DE DADOS ACADÉMICOS E RELATÓRIOS
    // =========================================================

    /**
     * Imprime a ficha detalhada com os dados biográficos e escolares do aluno.
     * @param e Instância do Estudante logado.
     */
    public void mostrarDadosFicha(Estudante e) {
        System.out.println("\n---------- FICHA DE ESTUDANTE ----------");
        System.out.println("Nº Mecanográfico : " + e.getNumeroMecanografico());
        System.out.println("Nome Completo    : " + e.getNome());
        System.out.println("Email Instituc.  : " + e.getEmail());
        System.out.println("NIF              : " + e.getNif());
        System.out.println("Morada           : " + e.getMorada());
        System.out.println("Data Nascimento  : " + e.getDataNascimento());
        System.out.println("Ano de Ingresso  : " + e.getAnoPrimeiraInscricao());

        if (e.getCurso() != null) {
            System.out.println("Curso            : " + e.getCurso().getNome() + " (" + e.getCurso().getSigla() + ")");
            System.out.println("Ano de Frequência: " + e.getAnoFrequencia() + "º Ano");
        }
        System.out.println("----------------------------------------");
    }

    public void mostrarCabecalhoPercurso() {
        System.out.println("\n============= REGISTO ACADÉMICO GLOBAL =============");
    }

    public void mostrarAnoPercurso(int ano) {
        System.out.println("\n--- [ " + ano + "º Ano Curricular ] ---");
    }

    /**
     * Imprime o estado de aproveitamento numa disciplina específica.
     */
    public void mostrarLinhaUC(String sigla, String nome, int ano, String status) {
        System.out.printf(">> [%-6s] %-30s | Status: %s\n", sigla, nome, status);
    }

    /**
     * Converte códigos lógicos do Model em descrições legíveis e formatadas.
     * @param estado Código (1-Inscrito c/ nota, 2-Inscrito s/ nota, 3-Concluído).
     * @param nota Classificação numérica.
     * @return String formatada para o ecrã.
     */
    public String formatarStatusUC(int estado, double nota) {
        return switch (estado) {
            case 1 -> String.format("Em Curso (Inscrito) -> Média Atual: %.2f", nota);
            case 2 -> "Inscrito -> Aguarda Avaliação";
            case 3 -> String.format("Concluído -> Nota Final: %.2f", nota);
            default -> "Não Inscrito / Pendente";
        };
    }

    /**
     * Apresenta a média final do curso calculada pelo Modelo.
     */
    public void mostrarMediaGlobal(double media) {
        System.out.println("\n-----------------------------------------------------");
        System.out.printf(">> MÉDIA GLOBAL ESTIMADA: %.2f Valores\n", media);
        System.out.println("-----------------------------------------------------");
    }

    /**
     * Exibe o estado financeiro atual do aluno face à instituição.
     */
    public void mostrarDetalhesPropina(double total, double pago, double divida, double[] historico, int nPagamentos, boolean estaPaga) {
        System.out.println("\n----------- EXTRATO FINANCEIRO -----------");
        System.out.printf("Valor Total Anual : %.2f€\n", total);
        System.out.printf("Montante Liquidado: %.2f€\n", pago);
        System.out.printf("Montante em Dívida: %.2f€\n", divida);

        if (estaPaga) {
            System.out.println(">> ESTADO: REGULARIZADO. Obrigado.");
        } else {
            System.out.println(">> ESTADO: PAGAMENTO PENDENTE.");
        }
        System.out.println("------------------------------------------");
    }


    // =========================================================
    // 4. FEEDBACK E MENSAGENS DE SISTEMA
    // =========================================================

    public void msgSaida() { System.out.println(">> Sessão terminada. Até à próxima!"); }

    public void msgSucesso() { System.out.println(">> Sucesso: Alteração registada no sistema."); }

    public void msgErroOpcao() { System.out.println(">> Erro: Opção inválida."); }

    public void msgErroDados() { System.out.println(">> Erro: Formato de dados incorreto ou inválido."); }

    public void msgErroPassIncorreta() { System.out.println(">> Erro: A palavra-passe atual não coincide."); }

    public void msgErroPassNaoCoincidem() { System.out.println(">> Erro: A confirmação da palavra-passe falhou."); }

    public void msgErroSemCurso() { System.out.println(">> Erro: Não possui curso associado (Processo pendente)."); }

    public void msgErroSemPropina() { System.out.println(">> Erro: Não existe plano financeiro para o ano corrente."); }

    public void msgContaDesativada() { System.out.println(">> Conta desativada com sucesso. A encerrar aplicação..."); }

    public void mostrarCancelamento(String menuDestino) {
        System.out.println("\n>> Ação cancelada. A regressar ao menu " + menuDestino + "...");
    }

    /**
     * Mensagem de validação financeira específica.
     */
    public void msgErroValorMinimo(double valorMinimo) {
        System.out.printf("\nErro: Montante insuficiente. O pagamento mínimo aceite é de %.2f€.\n", valorMinimo);
    }

    public void mostrarAnoFrequencia(int ano) {
        System.out.println(">> Ano de Frequência Atual: " + ano + "º Ano");
        System.out.println("=================================================");
    }
}
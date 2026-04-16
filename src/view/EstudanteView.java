package view;

import model.bll.Estudante;

/**
 * Interface de utilizador (View) dedicada ao perfil de Estudante.
 * Gere a interação relacionada com a consulta de notas, percurso académico,
 * gestão de pagamentos e atualização de dados de perfil.
 */
public class EstudanteView {

    public EstudanteView() {

    }

    // ---------- MENUS DE NAVEGAÇÃO ----------

    /**
     * Apresenta o menu principal da área reservada ao aluno.
     * @return A opção selecionada.
     */
    public int mostrarMenuPrincipal() {
        System.out.println("\n=== ÁREA DO ESTUDANTE ===");
        System.out.println("1 - Ver Ficha de Estudante");
        System.out.println("2 - Atualizar Dados Pessoais");
        System.out.println("3 - Ver Percurso Académico");
        System.out.println("4 - Gerir Propinas e Pagamentos");
        System.out.println("5 - Desativar a minha Conta");
        System.out.println("0 - Sair / Logout");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    /**
     * Apresenta o submenu de edição de perfil.
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
     * Menu de opções para liquidação de valores.
     * @param divida Valor total em falta.
     * @param prestacao Valor sugerido para uma prestação simples.
     * @return Opção de pagamento selecionada.
     */
    public int mostrarOpcoesPagamento(double divida, double prestacao) {
        System.out.println("\n--- REALIZAR PAGAMENTO ---");
        System.out.println("1 - Pagamento Integral (" + divida + "€)");
        System.out.println("2 - Pagar 1 Prestação (" + prestacao + "€)");
        System.out.println("3 - Introduzir outro valor");
        System.out.println("0 - Cancelar");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
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

    public double pedirValorLivre() {
        return utils.Consola.lerDouble("Introduza o valor a liquidar (€): ");
    }

    /**
     * Solicita confirmação explícita para uma ação destrutiva (desativação de conta).
     * @return true se o utilizador confirmar com 'S'.
     */
    public boolean pedirConfirmacaoDesativacao() {
        System.out.println("\n[AVISO CRÍTICO] Se desativar a conta, perderá o acesso imediato ao sistema.");
        String input = utils.Consola.lerString("Tem a certeza que deseja prosseguir com a desativação? (S/N): ");
        return input.equalsIgnoreCase("S");
    }

    /**
     * Método auxiliar para garantir a leitura segura de inteiros nos menus.
     */
    private int lerOpcaoInteira() {
        return utils.Consola.lerOpcaoMenu();
    }

    // ---------- EXIBIÇÃO DE DADOS ACADÉMICOS E FINANCEIROS ----------

    /**
     * Imprime os dados demográficos e académicos contidos na ficha do aluno.
     * @param e O objeto Estudante carregado em memória.
     */
    public void mostrarDadosFicha(Estudante e) {
        System.out.println("\n--- FICHA DE ESTUDANTE ---");
        System.out.println("Nº Mecanográfico : " + e.getNumeroMecanografico());
        System.out.println("Nome             : " + e.getNome());
        System.out.println("Email Inst.      : " + e.getEmail());
        System.out.println("NIF              : " + e.getNif());
        System.out.println("Morada           : " + e.getMorada());
        System.out.println("Data Nascimento  : " + e.getDataNascimento());
        System.out.println("Ano de Ingresso  : " + e.getAnoPrimeiraInscricao());

        if (e.getCurso() != null) {
            System.out.println("Curso            : " + e.getCurso().getNome() + " (" + e.getCurso().getSigla() + ")");
            System.out.println("Ano de Frequência: " + e.getAnoFrequencia() + "º Ano");
        }
        System.out.println("--------------------------");
    }

    public void mostrarCabecalhoPercurso() { System.out.println("\n--- PERCURSO ACADÉMICO ---"); }

    public void mostrarAnoPercurso(int ano) { System.out.println("\n--- || " + ano + "º Ano Curricular || ---"); }

    /**
     * Imprime uma linha detalhada sobre o estado de uma Unidade Curricular no percurso.
     */
    public void mostrarLinhaUC(String sigla, String nome, int ano, String status) {
        System.out.println(">> [" + sigla + "] " + nome + " | Status: " + status);
    }

    /**
     * Transforma códigos de estado técnicos em mensagens legíveis para o aluno.
     * @param estado Código numérico do estado.
     * @param nota Classificação obtida.
     * @return String formatada com o estado e nota arredondada.
     */
    public String formatarStatusUC(int estado, double nota) {
        double notaArredondada = Math.round(nota * 100.0) / 100.0;
        switch (estado) {
            case 1: return "Em Curso (Inscrito) -> Média Atual: " + notaArredondada;
            case 2: return "Inscrito -> Aguarda Avaliação";
            case 3: return "Concluído -> Nota Final: " + notaArredondada;
            default: return "Não Inscrito / Pendente";
        }
    }

    /**
     * Imprime a média global atual do aluno formatada a 2 casas decimais.
     */
    public void mostrarMediaGlobal(double media) {
        System.out.println("\n-----------------------------------------------------");
        // O %.2f obriga o Java a mostrar exatamente 2 casas decimais (ex: 14,50)
        System.out.printf(">> MÉDIA GLOBAL ATUAL: %.2f Valores\n", media);
        System.out.println("-----------------------------------------------------");
    }

    /**
     * Apresenta o extrato financeiro detalhado do aluno para o ano corrente.
     */
    public void mostrarDetalhesPropina(double total, double pago, double divida, double[] historico, int nPagamentos, boolean estaPaga) {
        System.out.println("\n--- EXTRATO DE PROPINAS ---");
        System.out.println("Valor Total Anual : " + total + "€");
        System.out.println("Montante Liquidado: " + pago + "€");
        System.out.println("Montante em Dívida: " + divida + "€");

        if (estaPaga) {
            System.out.println(">> ESTADO: REGULARIZADO. Obrigado.");
        } else {
            System.out.println(">> ESTADO: PAGAMENTO PENDENTE.");
        }
    }

    // ---------- FEEDBACK E MENSAGENS DE SISTEMA ----------

    public void msgSaida() { System.out.println(">> A terminar sessão de Estudante..."); }

    public void msgSucesso() { System.out.println(">> SUCESSO: Operação realizada com êxito."); }

    public void msgErroOpcao() { System.out.println(">> Erro: Opção inválida ou inexistente."); }

    public void msgErroDados() { System.out.println(">> Erro: Dados inválidos ou formato incorreto detetado."); }

    public void msgErroPassIncorreta() { System.out.println(">> Erro: A password atual introduzida está incorreta."); }

    public void msgErroPassNaoCoincidem() { System.out.println(">> Erro: A nova password e a confirmação não coincidem."); }

    public void msgErroSemCurso() { System.out.println(">> Erro: Não possui curso associado para esta operação."); }

    public void msgErroSemPropina() { System.out.println(">> Erro: Não foi encontrada nenhuma propina gerada para o ano letivo atual."); }

    public void msgContaDesativada() { System.out.println(">> A sua conta foi desativada no sistema. A encerrar sessão..."); }

    /**
     * Exibe uma mensagem de erro informando que o valor inserido é inferior ao mínimo permitido.
     * @param valorMinimo O montante mínimo (10% ou o total da dívida restante).
     */
    public void msgErroValorMinimo(double valorMinimo) {
        System.out.println("\nErro: Valor inválido! O pagamento mínimo permitido é de " + String.format("%.2f", valorMinimo) + "€.");
        System.out.println("Por favor, introduza um valor igual ou superior.");
    }
}
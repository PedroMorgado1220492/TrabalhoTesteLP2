package view;

import java.util.Scanner;

public class EstudanteView {

    // ---------- ATRIBUTOS ----------
    private Scanner scanner;

    // ---------- CONSTRUTOR ----------
    public EstudanteView() {
        this.scanner = new Scanner(System.in);
    }

    // ---------- MÉTODOS DE APRESENTAÇÃO E LEITURA ----------

    /**
     * Apresenta o menu principal de interação para o utilizador Estudante.
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuPrincipal() {
        System.out.println("\n=== MENU ESTUDANTE ===");
        System.out.println("1 - Ver Dados Pessoais");
        System.out.println("2 - Atualizar Dados");
        System.out.println("3 - Percurso Académico");
        System.out.println("4 - Propinas");
        System.out.println("0 - Sair / Logout");
        System.out.print("Escolha uma opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Apresenta o submenu para a atualização dos dados pessoais do Estudante.
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuAtualizarDados() {
        System.out.println("\n--- ATUALIZAR DADOS PESSOAIS ---");
        System.out.println("1 - Alterar Nome");
        System.out.println("2 - Alterar NIF");
        System.out.println("3 - Alterar Morada");
        System.out.println("4 - Alterar Password");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Pede ao estudante que insira texto através da consola.
     * @param mensagem Texto de pedido de informação.
     * @return A string inserida.
     */
    public String pedirInputString(String mensagem) {
        System.out.print(mensagem + ": ");
        return scanner.nextLine();
    }

    /**
     * Apresenta uma mensagem de sistema/feedback ao estudante.
     * @param mensagem Texto a apresentar.
     */
    public void mostrarMensagem(String mensagem) {
        System.out.println(">> " + mensagem);
    }

    /**
     * Apresenta o resumo financeiro da propina do aluno.
     */
    public void mostrarDetalhesPropina(double valorTotal, double valorPago, double valorDivida, double[] historico, int totalPagamentos, boolean pagaTotalmente) {
        System.out.println("\n--- TESOURARIA: GESTÃO DE PROPINAS ---");
        System.out.println("Valor Total da Propina: " + valorTotal + "€");
        System.out.println("Valor Já Pago: " + valorPago + "€");
        System.out.println("Valor em Dívida: " + valorDivida + "€");

        if (totalPagamentos > 0) {
            System.out.println("\nHistórico de Pagamentos Efetuados:");
            for (int i = 0; i < totalPagamentos; i++) {
                System.out.println(" -> Pagamento " + (i + 1) + ": " + historico[i] + "€");
            }
        }

        if (pagaTotalmente) {
            System.out.println("\nSituação regularizada! Não tem pagamentos pendentes.");
        }
    }

    /**
     * Apresenta o menu de pagamentos com os valores calculados.
     */
    public int mostrarOpcoesPagamento(double divida, double prestacaoFixa) {
        System.out.println("\n--- OPÇÕES DE PAGAMENTO ---");
        System.out.println("1 - Pagamento Integral (" + divida + "€)");
        System.out.println("2 - Pagar 1 Prestação (10% = " + prestacaoFixa + "€)");
        System.out.println("3 - Introduzir outro valor");
        System.out.println("0 - Voltar");
        System.out.print("Escolha uma opção: ");

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Pede um valor monetário solto ao estudante.
     */
    public double pedirValorPagamento() {
        System.out.print("Introduza o montante a pagar (€): ");
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }

}

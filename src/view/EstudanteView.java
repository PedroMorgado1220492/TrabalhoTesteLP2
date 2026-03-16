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
        System.out.println("4 - Sair / Logout");
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
        System.out.println("5 - Recuar");
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
}
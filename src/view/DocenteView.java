package view;

import java.util.Scanner;

public class DocenteView {
    private Scanner scanner;

    public DocenteView() {
        this.scanner = new Scanner(System.in);
    }

    // O método que o compilador não estava a encontrar!
    public int mostrarMenuPrincipal() {
        System.out.println("\n=== ÁREA DO DOCENTE ===");
        System.out.println("1 - Ver Dados Pessoais");
        System.out.println("2 - Atualizar Dados");
        System.out.println("3 - Adicionar Avaliação a Aluno");
        System.out.println("4 - Sair / Logout");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpar o buffer
        return opcao;
    }

    // Já adicionado para quando formos fazer a Opção 2 do Docente!
    public int mostrarMenuAtualizarDados() {
        System.out.println("\n--- ATUALIZAR DADOS PESSOAIS ---");
        System.out.println("1 - Alterar Nome");
        System.out.println("2 - Alterar NIF");
        System.out.println("3 - Alterar Morada");
        System.out.println("4 - Alterar Password");
        System.out.println("5 - Recuar");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpar o buffer
        return opcao;
    }

    // Ferramentas essenciais para a View comunicar com o Controller
    public String pedirInputString(String mensagem) {
        System.out.print(mensagem + ": ");
        return scanner.nextLine();
    }

    public void mostrarMensagem(String mensagem) {
        System.out.println(">> " + mensagem);
    }
}
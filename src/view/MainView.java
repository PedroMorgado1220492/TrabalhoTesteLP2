// Ficheiro: view/MainView.java
package view;

import java.util.Scanner;

public class MainView {
    private Scanner scanner;

    public MainView() {
        this.scanner = new Scanner(System.in);
    }

    // O seu menu principal perfeitamente integrado na View
    public int mostrarMenu() {
        System.out.println();
        System.out.println("===== SISTEMA ISSMF =====");
        System.out.println("1 - Login");
        System.out.println("2 - Criar Estudante");
        System.out.println("3 - Avançar Ano");
        System.out.println("4 - Importar CSV");
        System.out.println("5 - Sair");
        System.out.print("Opção: ");

        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer do scanner (o Enter que o utilizador carrega)
        return opcao;
    }

    // Outros métodos utilitários que a MainView possa precisar
    public String pedirInputString(String mensagem) {
        System.out.print(mensagem + ": ");
        return scanner.nextLine();
    }

    public void mostrarMensagem(String mensagem) {
        System.out.println(">> " + mensagem);
    }
}
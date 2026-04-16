package utils;

import java.util.Scanner;

/**
 * Classe utilitária centralizada para leitura de dados da consola.
 * Interceta o valor "/" para cancelar operações em curso.
 */
public class Consola {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Lê uma String. Se o utilizador introduzir "/", aborta a operação.
     * @param mensagem O texto a perguntar ao utilizador.
     * @return A string validada.
     */
    public static String lerString(String mensagem) {
        System.out.print(mensagem);
        String input = scanner.nextLine().trim();

        if (input.equals("/")) {
            throw new CancelamentoException();
        }

        return input;
    }

    /**
     * Lê um valor numérico decimal. Se introduzir "/", cancela.
     * Agora permite a introdução perfeita da nota "0" ou "0.0".
     */
    public static double lerDouble(String mensagem) {
        while (true) {
            String input = lerString(mensagem); // A interceção do "/" já acontece lá dentro!
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                // Mensagem de erro atualizada para refletir o "/"
                System.out.println(">> Erro: Formato numérico inválido. Introduza um valor válido ou '/' para cancelar.");
            }
        }
    }

    /**
     * Lê um valor numérico inteiro (útil para NIFs em formato numérico, números mecanográficos, etc).
     */
    public static int lerInt(String mensagem) {
        while (true) {
            String input = lerString(mensagem);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(">> Erro: Formato inteiro inválido. Introduza um número válido ou '/' para cancelar.");
            }
        }
    }

    /**
     * Método EXCLUSIVO para menus.
     * Aqui não usamos a CancelamentoException porque nos menus o "0" continua
     * a ser a opção normal para "Sair" e se o utilizador digitar "/" por engano,
     * simplesmente devolve -1 (o que aciona o 'default: Opção Inválida').
     */
    public static int lerOpcaoMenu() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
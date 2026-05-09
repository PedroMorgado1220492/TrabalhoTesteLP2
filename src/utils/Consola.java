package utils;

import java.io.Console;
import java.util.Scanner;

/**
 * Classe utilitária centralizada para toda a interação com a consola.
 *
 * Regras implementadas:
 *  1. Usa Console quando disponível (CMD, Git Bash) e Scanner como fallback (IDE).
 *  2. Inputs inválidos re-apresentam o prompt (loop).
 *  3. "/" em inputs de dados → lança CancelamentoException.
 *  4. Em menus, "0" é opção legítima de saída — usa lerOpcaoMenu().
 *  5. lerPassword() usa System.console() para ocultar a digitação (quando disponível).
 *  6. lerDouble() aceita vírgula ou ponto como separador decimal.
 *  7. O prompt só é impresso se não for vazio (evita duplicação do ":").
 */
public final class Consola {

    private static final Console CONSOLE = System.console();
    private static final Scanner FALLBACK_SCANNER = new Scanner(System.in);

    private Consola() {}

    // =========================================================================
    // LEITURA DE DADOS
    // =========================================================================

    /**
     * Lê uma String não vazia. "/" → CancelamentoException.
     * O prompt só é impresso se não for vazio.
     *
     * @param prompt Texto a mostrar ao utilizador (pode ser vazio)
     * @return String lida (trimmed)
     * @throws CancelamentoException se o utilizador digitar "/"
     */
    public static String lerString(String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }
        String input;
        if (CONSOLE != null) {
            input = CONSOLE.readLine();
        } else {
            input = FALLBACK_SCANNER.nextLine();
        }
        if (input == null) throw new CancelamentoException();
        input = input.trim();
        if (input.equals("/")) throw new CancelamentoException();
        return input;
    }

    /**
     * Lê um inteiro. "/" → CancelamentoException.
     *
     * @param prompt Texto a mostrar ao utilizador (pode ser vazio)
     * @return Inteiro lido
     * @throws CancelamentoException se o utilizador digitar "/"
     */
    public static int lerInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(lerString(prompt));
            } catch (NumberFormatException e) {
                System.out.println(">> Número inválido. Tente novamente.");
            }
        }
    }

    /**
     * Lê um double. "/" → CancelamentoException.
     * Aceita vírgula ou ponto como separador decimal.
     *
     * @param prompt Texto a mostrar ao utilizador (pode ser vazio)
     * @return Double lido
     * @throws CancelamentoException se o utilizador digitar "/"
     */
    public static double lerDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(lerString(prompt).replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println(">> Número inválido. Tente novamente.");
            }
        }
    }

    /**
     * Método EXCLUSIVO para leitura de opções de menus.
     * "0" é devolvido normalmente — não lança CancelamentoException.
     * Não imprime prompt (a View já o fez).
     *
     * @return Opção escolhida (0-9), ou -1 se inválida.
     */
    public static int lerOpcaoMenu() {
        try {
            String linha;
            if (CONSOLE != null) {
                linha = CONSOLE.readLine();
            } else {
                linha = FALLBACK_SCANNER.nextLine();
            }
            if (linha == null) return -1;
            return Integer.parseInt(linha.trim());
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Lê uma password com mascaramento via System.console().
     * Fallback para Scanner se não houver consola (a password será visível).
     *
     * @param prompt Texto a mostrar ao utilizador
     * @return Password lida
     * @throws CancelamentoException se o utilizador digitar "/"
     */
    public static String lerPassword(String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt + ": ");
        }
        String input;
        if (CONSOLE != null) {
            char[] chars = CONSOLE.readPassword();
            if (chars == null) throw new CancelamentoException();
            input = new String(chars);
            System.out.println(); // quebra de linha após a password
        } else {
            input = FALLBACK_SCANNER.nextLine();
        }
        input = input.trim();
        if (input.equals("/")) throw new CancelamentoException();
        return input;
    }
}
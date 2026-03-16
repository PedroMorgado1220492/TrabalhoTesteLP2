package view;

import java.util.Scanner;

public class GestorView {

    // ---------- ATRIBUTOS ----------
    private Scanner scanner;

    // ---------- CONSTRUTOR ----------
    public GestorView() {
        this.scanner = new Scanner(System.in);
    }

    // ---------- MÉTODOS DE APRESENTAÇÃO E LEITURA ----------

    /**
     * Apresenta o menu principal do painel de administração (Backoffice).
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuPrincipalGestor() {
        System.out.println("\n=== BACKOFFICE - GESTOR ===");
        System.out.println("1 - Gerir Departamentos");
        System.out.println("2 - Gerir Cursos");
        System.out.println("3 - Gerir Unidades Curriculares");
        System.out.println("4 - Gerir Estudantes");
        System.out.println("5 - Gerir Docentes");
        System.out.println("6 - Avançar Ano Letivo");
        System.out.println("7 - Listagens e Relatórios");
        System.out.println("8 - Voltar / Logout");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Apresenta o submenu de gestão de Departamentos.
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuDepartamentos() {
        System.out.println("\n--- GERIR DEPARTAMENTOS ---");
        System.out.println("1 - Adicionar Departamento");
        System.out.println("2 - Alterar Departamento");
        System.out.println("3 - Listar Departamentos");
        System.out.println("4 - Recuar");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Apresenta o submenu de gestão de Cursos.
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuCursos() {
        System.out.println("\n--- GERIR CURSOS ---");
        System.out.println("1 - Adicionar Curso");
        System.out.println("2 - Alterar Curso");
        System.out.println("3 - Listar Cursos");
        System.out.println("4 - Recuar");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Apresenta o submenu de gestão de Unidades Curriculares.
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuUCs() {
        System.out.println("\n--- GERIR UNIDADES CURRICULARES ---");
        System.out.println("1 - Criar Nova Unidade Curricular");
        System.out.println("2 - Associar UC Existente a outro Curso");
        System.out.println("3 - Alterar Unidade Curricular");
        System.out.println("4 - Listar Unidades Curriculares");
        System.out.println("5 - Recuar");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Apresenta o submenu de gestão de Estudantes.
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuEstudantes() {
        System.out.println("\n--- GERIR ESTUDANTES ---");
        System.out.println("1 - Adicionar Estudante");
        System.out.println("2 - Alterar Estudante");
        System.out.println("3 - Listar Estudantes");
        System.out.println("4 - Recuar");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Apresenta o submenu de visualização de Relatórios e Estatísticas.
     * @return A opção numérica selecionada.
     */

    public int mostrarMenuRelatorios() {
        System.out.println("\n--- RELATÓRIOS E ESTATÍSTICAS ---");
        System.out.println("1 - Alunos agrupados por Curso");
        System.out.println("2 - Alunos agrupados por UC");
        System.out.println("3 - UCs agrupadas por Curso");
        System.out.println("4 - Cursos agrupados por Departamento");
        System.out.println("5 - Ver Estatísticas Globais da Faculdade");
        System.out.println("6 - Recuar");
        System.out.print("Opção: ");

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }



    /**
     * Pede ao gestor que insira texto através da consola.
     * @param mensagem Texto de pedido de informação.
     * @return A string inserida.
     */
    public String pedirInputString(String mensagem) {
        System.out.print(mensagem + ": ");
        return scanner.nextLine();
    }

    /**
     * Apresenta uma mensagem de sistema/feedback ao gestor.
     * @param mensagem Texto a apresentar.
     */
    public void mostrarMensagem(String mensagem) {
        System.out.println(">> " + mensagem);
    }
}
// Ficheiro: controller/EstudanteController.java
package controller;

import view.EstudanteView;
import model.Estudante;
import model.RepositorioDados;

public class EstudanteController {
    private EstudanteView view;
    private Estudante estudanteLogado;
    private RepositorioDados repositorio;

    public EstudanteController(Estudante estudanteLogado, RepositorioDados repositorio) {
        this.view = new EstudanteView();
        this.estudanteLogado = estudanteLogado;
        this.repositorio = repositorio;
    }

    public void iniciarMenu() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuPrincipal();
            switch (opcao) {
                case 1:
                    verDadosEstudante();
                    break;
                case 2:
                    view.mostrarMensagem("Funcionalidade de atualizar dados a ser desenvolvida.");
                    break;
                case 3:
                    view.mostrarMensagem("Funcionalidade de Percurso Académico a ser desenvolvida.");
                    break;
                case 4:
                    view.mostrarMensagem("A sair da conta de Estudante...");
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    private void verDadosEstudante() {
        view.mostrarMensagem("\n--- FICHA DE ESTUDANTE ---");
        view.mostrarMensagem("Nº Mecanográfico: " + estudanteLogado.getNumeroMecanografico());
        view.mostrarMensagem("Nome: " + estudanteLogado.getNome());
        view.mostrarMensagem("Email: " + estudanteLogado.getEmail());
        view.mostrarMensagem("NIF: " + estudanteLogado.getNif());
        view.mostrarMensagem("Morada: " + estudanteLogado.getMorada());
        view.mostrarMensagem("Data de Nascimento: " + estudanteLogado.getDataNascimento());
        // Mais tarde adicionaremos o Curso aqui, quando o Gestor inscrever o aluno!
    }
}
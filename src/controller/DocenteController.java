// Ficheiro: controller/DocenteController.java
package controller;

import view.DocenteView;
import model.Docente;
import model.RepositorioDados;

public class DocenteController {
    private DocenteView view;
    private Docente docenteLogado;
    private RepositorioDados repositorio;

    public DocenteController(Docente docenteLogado, RepositorioDados repositorio) {
        this.view = new DocenteView();
        this.docenteLogado = docenteLogado;
        this.repositorio = repositorio;
    }

    public void iniciarMenu() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuPrincipal();
            switch (opcao) {
                case 1:
                    verDadosDocente();
                    break;
                case 2:
                    view.mostrarMensagem("Funcionalidade de atualizar dados a ser desenvolvida.");
                    break;
                case 3:
                    view.mostrarMensagem("Funcionalidade de lançar notas a ser desenvolvida.");
                    break;
                case 4:
                    view.mostrarMensagem("A sair da conta de Docente...");
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    private void verDadosDocente() {
        view.mostrarMensagem("\n--- FICHA DE DOCENTE ---");
        view.mostrarMensagem("Sigla: " + docenteLogado.getSigla());
        view.mostrarMensagem("Nome: " + docenteLogado.getNome());
        view.mostrarMensagem("Email: " + docenteLogado.getEmail());
        view.mostrarMensagem("NIF: " + docenteLogado.getNif());
        view.mostrarMensagem("Morada: " + docenteLogado.getMorada());
        view.mostrarMensagem("Data de Nascimento: " + docenteLogado.getDataNascimento());
    }
}
// Ficheiro: controller/DocenteController.java
package controller;

import view.DocenteView;
import model.Docente;
import model.RepositorioDados;
import utils.Validador; // Importante para as validações de dados!

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
                    atualizarDadosDocente(); // Chamada para a funcionalidade completa
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

    // --- NOVA LÓGICA DE ATUALIZAÇÃO DE DADOS ---
    private void atualizarDadosDocente() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuAtualizarDados();
            switch (opcao) {
                case 1: // Alterar Nome
                    String novoNome = "";
                    while (true) {
                        novoNome = view.pedirInputString("Introduza o novo Nome (Nome e Sobrenome)");
                        if (Validador.isNomeValido(novoNome)) {
                            break; // Validação passou, sai do ciclo
                        }
                        view.mostrarMensagem("Erro: O nome deve conter pelo menos nome e sobrenome, utilizando apenas letras.");
                    }
                    docenteLogado.setNome(novoNome);
                    view.mostrarMensagem("Nome atualizado com sucesso para: " + novoNome);
                    break;

                case 2: // Alterar NIF
                    String novoNif = "";
                    while (true) {
                        novoNif = view.pedirInputString("Introduza o novo NIF (9 dígitos)");
                        if (Validador.isNifValido(novoNif)) {
                            break; // Validação passou, sai do ciclo
                        }
                        view.mostrarMensagem("Erro: O NIF deve conter exatamente 9 dígitos numéricos.");
                    }
                    docenteLogado.setNif(novoNif);
                    view.mostrarMensagem("NIF atualizado com sucesso!");
                    break;

                case 3: // Alterar Morada
                    // A morada é livre, logo não precisa de validação complexa
                    String novaMorada = view.pedirInputString("Introduza a nova Morada");
                    docenteLogado.setMorada(novaMorada);
                    view.mostrarMensagem("Morada atualizada com sucesso!");
                    break;

                case 4: // Alterar Password (Com verificação de segurança)
                    String passwordAntiga = view.pedirInputString("Introduza a sua Password Atual");

                    // 1º Passo: Validar se a password antiga está correta
                    if (passwordAntiga.equals(docenteLogado.getPassword())) {

                        String passwordNova = view.pedirInputString("Introduza a NOVA Password");
                        String confirmarPassword = view.pedirInputString("Confirme a NOVA Password");

                        // 2º Passo: Validar se as duas novas coincidem
                        if (passwordNova.equals(confirmarPassword)) {
                            docenteLogado.setPassword(passwordNova);
                            view.mostrarMensagem("Sucesso! A sua password foi alterada.");
                        } else {
                            view.mostrarMensagem("Erro: As passwords introduzidas não coincidem. Operação cancelada.");
                        }

                    } else {
                        view.mostrarMensagem("Erro: A password atual está incorreta. Operação cancelada.");
                    }
                    break;

                case 5: // Recuar
                    aExecutar = false;
                    break;

                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }
}
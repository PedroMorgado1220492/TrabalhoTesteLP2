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
                    atualizarDadosEstudante();
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
        view.mostrarMensagem("Ano da 1ª Inscrição: " + estudanteLogado.getAnoPrimeiraInscricao());

        // Verificação de segurança para o Curso
        if (estudanteLogado.getCurso() != null) {
            view.mostrarMensagem("Curso: " + estudanteLogado.getCurso().getNome() + " (" + estudanteLogado.getCurso().getSigla() + ")");
        } else {
            view.mostrarMensagem("Curso: Aluno ainda não inscrito em nenhum curso.");
        }
    }
    private void atualizarDadosEstudante() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuAtualizarDados();
            switch (opcao) {
                case 1: // Alterar Nome
                    String novoNome = "";
                    while (true) {
                        novoNome = view.pedirInputString("Introduza o novo Nome (Nome e Sobrenome)");
                        // Utilizamos a nossa classe utilitária Validador
                        if (utils.Validador.isNomeValido(novoNome)) {
                            break; // Sai do ciclo se estiver válido
                        }
                        view.mostrarMensagem("Erro: O nome deve conter pelo menos nome e sobrenome, utilizando apenas letras.");
                    }
                    estudanteLogado.setNome(novoNome);
                    view.mostrarMensagem("Nome atualizado com sucesso para: " + novoNome);
                    break;

                case 2: // Alterar NIF
                    String novoNif = "";
                    while (true) {
                        novoNif = view.pedirInputString("Introduza o novo NIF (9 dígitos)");
                        if (utils.Validador.isNifValido(novoNif)) {
                            break;
                        }
                        view.mostrarMensagem("Erro: O NIF deve conter exatamente 9 dígitos numéricos.");
                    }
                    estudanteLogado.setNif(novoNif);
                    view.mostrarMensagem("NIF atualizado com sucesso!");
                    break;

                case 3: // Alterar Morada
                    String novaMorada = view.pedirInputString("Introduza a nova Morada");
                    estudanteLogado.setMorada(novaMorada);
                    view.mostrarMensagem("Morada atualizada com sucesso!");
                    break;

                case 4: // Alterar Password (Com a sua regra de segurança!)
                    String passwordAntiga = view.pedirInputString("Introduza a sua Password Atual");

                    // 1º Passo: Validar se a password antiga está correta
                    if (passwordAntiga.equals(estudanteLogado.getPassword())) {

                        String passwordNova = view.pedirInputString("Introduza a NOVA Password");
                        String confirmarPassword = view.pedirInputString("Confirme a NOVA Password");

                        // 2º Passo: Validar se as duas novas coincidem
                        if (passwordNova.equals(confirmarPassword)) {
                            estudanteLogado.setPassword(passwordNova);
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
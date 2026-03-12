package controller;

import model.Avaliacao;
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
                    verPercursoAcademico();
                    break;
                case 4:
                    verHistoricoCompleto();
                    break;
                case 5:
                    view.mostrarMensagem("A sair da conta de Estudante...");
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    // --- MÉTODOS DE AÇÃO (FORA DO SWITCH E DO INICIARMENU) ---

    private void verPercursoAcademico() {
        view.mostrarMensagem("\n--- PERCURSO ACADÉMICO ---");

        if (estudanteLogado.getTotalAvaliacoes() == 0) {
            view.mostrarMensagem("Ainda não tens notas lançadas em nenhuma Unidade Curricular.");
            return;
        }

        Avaliacao[] notas = estudanteLogado.getAvaliacoes();

        // Cabeçalho da tabela
        view.mostrarMensagem(String.format("%-15s | %-15s | %-10s", "UC", "MÉDIA", "ESTADO"));
        view.mostrarMensagem("---------------------------------------------------------");

        for (int i = 0; i < estudanteLogado.getTotalAvaliacoes(); i++) {
            Avaliacao av = notas[i];
            if (av != null) {
                double media = av.calcularMedia();
                String estado = (media >= 9.5) ? "APROVADO" : "EM FREQUÊNCIA";

                view.mostrarMensagem(String.format("%-15s | %-15.2f | %-10s",
                        av.getUnidadeCurricular().getSigla(),
                        media,
                        estado));
            }
        }
    }

    private void verHistoricoCompleto() {
        view.mostrarMensagem("\n--- HISTÓRICO COMPLETO DE AVALIAÇÕES (ANOS ANTERIORES) ---");

        if (estudanteLogado.getTotalHistorico() == 0) {
            view.mostrarMensagem("Ainda não tens avaliações no teu histórico.");
            return;
        }

        Avaliacao[] historico = estudanteLogado.getHistoricoAvaliacoes();

        // Cabeçalho da tabela (Adicionámos o ANO)
        view.mostrarMensagem(String.format("%-15s | %-10s | %-15s | %-10s", "UC", "ANO LETIVO", "MÉDIA", "ESTADO"));
        view.mostrarMensagem("-----------------------------------------------------------------");

        for (int i = 0; i < estudanteLogado.getTotalHistorico(); i++) {
            Avaliacao av = historico[i];
            if (av != null) {
                double media = av.calcularMedia();
                // No histórico não há "Em frequência", ou passou ou chumbou.
                String estado = (media >= 9.5) ? "APROVADO" : "REPROVADO";

                view.mostrarMensagem(String.format("%-15s | %-10d | %-15.2f | %-10s",
                        av.getUnidadeCurricular().getSigla(),
                        av.getAnoAvaliacao(),
                        media,
                        estado));
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
                case 1:
                    String novoNome = "";
                    while (true) {
                        novoNome = view.pedirInputString("Introduza o novo Nome (Nome e Sobrenome)");
                        if (utils.Validador.isNomeValido(novoNome)) break;
                        view.mostrarMensagem("Erro: Nome inválido.");
                    }
                    estudanteLogado.setNome(novoNome);
                    view.mostrarMensagem("Nome atualizado!");
                    break;
                case 2:
                    String novoNif = "";
                    while (true) {
                        novoNif = view.pedirInputString("Introduza o novo NIF (9 dígitos)");
                        if (utils.Validador.isNifValido(novoNif)) break;
                        view.mostrarMensagem("Erro: NIF inválido.");
                    }
                    estudanteLogado.setNif(novoNif);
                    view.mostrarMensagem("NIF atualizado!");
                    break;
                case 3:
                    estudanteLogado.setMorada(view.pedirInputString("Introduza a nova Morada"));
                    view.mostrarMensagem("Morada atualizada!");
                    break;
                case 4:
                    String ant = view.pedirInputString("Password Atual");
                    if (ant.equals(estudanteLogado.getPassword())) {
                        String nova = view.pedirInputString("Nova Password");
                        if (nova.equals(view.pedirInputString("Confirme Nova Password"))) {
                            estudanteLogado.setPassword(nova);
                            view.mostrarMensagem("Password alterada!");
                        } else view.mostrarMensagem("Passwords não coincidem.");
                    } else view.mostrarMensagem("Password incorreta.");
                    break;
                case 5:
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }
}
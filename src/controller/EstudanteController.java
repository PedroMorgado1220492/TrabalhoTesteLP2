package controller;

import model.bll.Avaliacao;
import model.bll.Curso;
import model.bll.UnidadeCurricular;
import view.EstudanteView;
import model.bll.Estudante;
import model.dal.RepositorioDados;

public class EstudanteController {

    // ---------- ATRIBUTOS ----------
    private EstudanteView view;
    private Estudante estudanteLogado;
    private RepositorioDados repositorio;

    // ---------- CONSTRUTOR ----------
    public EstudanteController(Estudante estudanteLogado, RepositorioDados repositorio) {
        this.view = new EstudanteView();
        this.estudanteLogado = estudanteLogado;
        this.repositorio = repositorio;
    }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Inicia o ciclo principal do Estudante autenticado, dando acesso
     * à consulta do percurso, histórico e atualização de dados.
     */
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
                    gerirPropinas();
                    break;
                case 0:
                    view.mostrarMensagem("A sair da conta de Estudante...");
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    /**
     * Mostra o plano de estudos completo do aluno cruzando as UCs do curso,
     * as inscrições atuais e o histórico de aprovações, agrupado por Ano Curricular.
     */
    private void verPercursoAcademico() {
        Curso curso = estudanteLogado.getCurso();
        if (curso == null) {
            view.mostrarMensagem("Não tem nenhum curso associado.");
            return;
        }

        view.mostrarMensagem("\n--- PERCURSO ACADÉMICO ---");

        // O curso tem a duração de 3 anos
        for (int ano = 1; ano <= 3; ano++) {
            view.mostrarMensagem("\n--- || " + ano + "º ano ||---");

            boolean temUcNoAno = false;

            // Para cada ano, procuramos no curso inteiro as UCs correspondentes
            for (int i = 0; i < curso.getTotalUCs(); i++) {
                UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];

                // Só processa a UC se ela pertencer ao bloco do ano atual
                if (uc.getAnoCurricular() == ano) {
                    temUcNoAno = true;

                    String sigla = uc.getSigla();
                    String nome = uc.getNome();
                    String status = "Não Inscrito";

                    // 1. Verifica se está INSCRITO neste ano letivo (inclui cadeiras em atraso)
                    if (estudanteLogado.estaInscrito(sigla)) {
                        boolean temNota = false;
                        double notaAtual = 0.0;

                        // Procura se o professor já lhe lançou alguma nota ESTE ano
                        for (int j = 0; j < estudanteLogado.getTotalAvaliacoes(); j++) {
                            if (estudanteLogado.getAvaliacoes()[j].getUnidadeCurricular().getSigla().equalsIgnoreCase(sigla)) {
                                temNota = true;
                                notaAtual = estudanteLogado.getAvaliacoes()[j].calcularMedia();
                                break;
                            }
                        }

                        if (temNota) {
                            double notaFinal = Math.round(notaAtual * 100.0) / 100.0;
                            status = "Inscrito -> " + notaFinal;
                        } else {
                            status = "Inscrito -> Ainda Sem Avaliação";
                        }

                    } else {
                        // 2. Se NÃO está inscrito hoje, verifica se já fez a cadeira no passado (Histórico)
                        boolean feitoNoPassado = false;
                        double notaHistorico = 0.0;

                        for (int j = 0; j < estudanteLogado.getTotalHistorico(); j++) {
                            Avaliacao avHist = estudanteLogado.getHistoricoAvaliacoes()[j];
                            if (avHist.getUnidadeCurricular().getSigla().equalsIgnoreCase(sigla)) {
                                double mediaAntiga = avHist.calcularMedia();
                                if (mediaAntiga >= 9.5) {
                                    feitoNoPassado = true;
                                    notaHistorico = mediaAntiga;
                                    break;
                                }
                            }
                        }

                        if (feitoNoPassado) {
                            double notaFinalHist = Math.round(notaHistorico * 100.0) / 100.0;
                            status = "Concluído -> " + notaFinalHist;
                        }
                    }

                    // 3. Imprime a linha formatada
                    view.mostrarMensagem(">> >> - [" + sigla + "] " + nome + " (Ano: " + ano + "º) -> " + status);
                }
            }

            if (!temUcNoAno) {
                view.mostrarMensagem(">> >> (Nenhuma UC registada para este ano)");
            }
        }
    }

    /**
     * Mostra em formato de tabela o histórico de todas as UCs concluídas
     * (ou reprovadas) em anos letivos anteriores.
     */
    private void verHistoricoCompleto() {
        view.mostrarMensagem("\n--- HISTÓRICO COMPLETO DE AVALIAÇÕES (ANOS ANTERIORES) ---");

        if (estudanteLogado.getTotalHistorico() == 0) {
            view.mostrarMensagem("Ainda não tens avaliações no teu histórico.");
            return;
        }

        Avaliacao[] historico = estudanteLogado.getHistoricoAvaliacoes();

        view.mostrarMensagem(String.format("%-15s | %-10s | %-15s | %-10s", "UC", "ANO LETIVO", "MÉDIA", "ESTADO"));
        view.mostrarMensagem("-----------------------------------------------------------------");

        for (int i = 0; i < estudanteLogado.getTotalHistorico(); i++) {
            Avaliacao av = historico[i];
            if (av != null) {
                double media = av.calcularMedia();

                String estado;
                if (media >= 9.5) {
                    estado = "APROVADO";
                } else {
                    estado = "REPROVADO";
                }

                view.mostrarMensagem(String.format("%-15s | %-10d | %-15.2f | %-10s",
                        av.getUnidadeCurricular().getSigla(),
                        av.getAnoAvaliacao(),
                        media,
                        estado));
            }
        }
    }

    /**
     * Apresenta no ecrã todos os dados pessoais e académicos do estudante.
     */
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
            view.mostrarMensagem("Ano Frequência: " + estudanteLogado.getAnoFrequencia() + "º Ano");
        } else {
            view.mostrarMensagem("Curso: Aluno ainda não inscrito em nenhum curso.");
        }
    }

    /**
     * Permite a alteração iterativa dos dados pessoais do estudante autenticado.
     */
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
                case 0:
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    private void gerirPropinas() {
        model.bll.Propina propina = estudanteLogado.getPropinaDoAno(repositorio.getAnoAtual());

        if (propina == null) {
            view.mostrarMensagem("Erro: Nenhuma propina gerada para o ano letivo atual.");
            return;
        }

        // 1. Mandar a View desenhar o extrato da propina
        view.mostrarDetalhesPropina(
                propina.getValorTotal(), propina.getValorPago(), propina.getValorEmDivida(),
                propina.getHistoricoPagamentos(), propina.getTotalPagamentos(), propina.isPagaTotalmente()
        );

        if (propina.isPagaTotalmente()) return;

        // 2. Mandar a View desenhar o menu e recolher a opção
        double prestacaoFixa = propina.getValorTotal() / 10;
        int opcao = view.mostrarOpcoesPagamento(propina.getValorEmDivida(), prestacaoFixa);

        double valorAPagar = 0;

        // 3. Processar a opção do utilizador
        switch (opcao) {
            case 1:
                valorAPagar = propina.getValorEmDivida();
                break;
            case 2:
                valorAPagar = Math.min(prestacaoFixa, propina.getValorEmDivida());
                break;
            case 3:
                valorAPagar = view.pedirValorPagamento();
                if (valorAPagar <= 0) {
                    view.mostrarMensagem("Erro: Valor inválido.");
                    return;
                }
                break;
            case 0:
                return;
            default:
                view.mostrarMensagem("Opção inválida.");
                return;
        }

        // 4. Executar a ação no Model e Guardar
        if (propina.registarPagamento(valorAPagar)) {
            view.mostrarMensagem("Sucesso! Pagamento de " + valorAPagar + "€ registado.");
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.mostrarMensagem("Erro: Valor superior à dívida atual ou inválido.");
        }
    }
}
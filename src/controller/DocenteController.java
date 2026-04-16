package controller;

import model.bll.Docente;
import model.bll.Estudante;
import model.bll.UnidadeCurricular;
import model.dal.RepositorioDados;
import view.DocenteView;
import utils.Validador;
import utils.Seguranca;

/**
 * Controlador responsável pela gestão das operações e interações do Docente.
 * Estabelece a ponte entre a interface do utilizador (DocenteView) e a camada de dados (RepositorioDados).
 */
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
        boolean running = true;
        while (running) {
            int opcao = view.mostrarMenuPrincipal();
            try {
                switch (opcao) {
                    case 1: view.mostrarFichaDocente(docenteLogado); break;
                    case 2: menuAtualizar(); break;
                    case 3: menuAvaliacoes(); break;
                    case 4: verEstatisticas(); break;
                    case 0:
                        view.msgSaida();
                        running = false;
                        break;
                    default:
                        view.msgOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu do Docente...");
            }
        }
    }


    // ---------- MÉTODOS DE ATUALIZAÇÃO DE PERFIL ----------

    private void menuAtualizar() {
        boolean sub = true;
        while (sub) {
            int op = view.mostrarMenuAtualizarDados();
            try {
                switch (op) {
                    case 1: atualizarNome(); break;
                    case 2: atualizarNif(); break;
                    case 3: atualizarMorada(); break;
                    case 4: atualizarPassword(); break;
                    case 0: sub = false; break;
                    default: view.msgOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu de Atualização...");
            }
        }
    }

    private void atualizarNome() {
        String n = view.pedirNovoNome();
        if (Validador.isNomeValido(n)) {
            docenteLogado.setNome(n);
            view.msgSucesso();
        } else {
            view.msgOpcaoInvalida();
        }
    }

    private void atualizarNif() {
        String nif = view.pedirNovoNif();
        if (Validador.isNifValido(nif)) {
            docenteLogado.setNif(nif);
            view.msgSucesso();
        } else {
            view.msgOpcaoInvalida();
        }
    }

    private void atualizarMorada() {
        docenteLogado.setMorada(view.pedirNovaMorada());
        view.msgSucesso();
    }

    private void atualizarPassword() {
        String passAtualRaw = view.pedirPassAtual();
        String passAtualEnc = Seguranca.encriptar(passAtualRaw);

        if (passAtualEnc.equals(docenteLogado.getPassword())) {
            String novaPassRaw = view.pedirNovaPass();
            String confirmacaoRaw = view.pedirConfirmacaoPass();

            if (!novaPassRaw.isEmpty() && novaPassRaw.equals(confirmacaoRaw)) {
                docenteLogado.setPassword(Seguranca.encriptar(novaPassRaw));
                view.msgSucesso();
            } else {
                view.msgErroPassNaoCoincidem();
            }
        } else {
            view.msgErroPassIncorreta();
        }
    }

    // ---------- AVALIAÇÕES E ESTATISTICAS ----------

    private void menuAvaliacoes() {
        boolean sub = true;
        while (sub) {
            int op = view.mostrarMenuAvaliacoes();
            try {
                switch (op) {
                    case 1: lancarNotaUnica(); break;
                    case 2: lancarNotasEmLote(); break;
                    case 3: listarAvaliacoesUc(); break;
                    case 4: alterarNumAvaliacoes(); break;
                    case 0: sub = false; break;
                    default: view.msgOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu de Avaliações...");
            }
        }
    }

    private void listarAvaliacoesUc() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.msgAvisoSemUCs();
            return;
        }

        int idxUC = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idxUC < 0 || idxUC >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idxUC];
        view.mostrarCabecalhoPauta(uc.getNome());
        boolean encontrou = false;

        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];

            if (e != null && e.isAtivo() && e.estaInscrito(uc.getSigla())) {
                encontrou = true;
                model.bll.Avaliacao av = e.getAvaliacaoAtual(uc.getSigla());
                String notasStr = model.bll.Pauta.formatarNotasAluno(av);
                view.mostrarAlunoNaPauta(e.getNumeroMecanografico(), e.getNome(), e.getAnoFrequencia(), notasStr);
            }
        }

        if (!encontrou) {
            view.msgAvisoSemAlunosInscritos();
        }
    }

    // ---------- MÉTODOS DE LANÇAMENTO DE NOTAS ----------

    private void lancarNotaUnica() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.msgAvisoSemUCs();
            return;
        }

        int idxUC = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idxUC < 0 || idxUC >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idxUC];
        Estudante[] alunos = repositorio.obterEstudantesPorUC(uc.getSigla());

        if (alunos.length == 0) {
            view.msgAvisoTurmaVazia();
            return;
        }

        int idxAlu = view.pedirAluno(alunos, uc.getNome());
        if (idxAlu < 0 || idxAlu >= alunos.length) return;

        Estudante alu = alunos[idxAlu];

        if (!alu.isAtivo()) {
            view.msgErroAlunoInativo();
            return;
        }

        int numNota = alu.obterNumeroProximaAvaliacao(uc.getSigla());
        if (numNota > uc.getNumAvaliacoes()) {
            view.msgErroLimiteNotas(uc.getNumAvaliacoes());
            return;
        }

        try {
            double nota = Double.parseDouble(view.pedirNotaIndividual(alu.getNome(), numNota, uc.getNumAvaliacoes()));
            if (nota < 0 || nota > 20) {
                view.msgErroNotaInvalida();
            } else if (alu.adicionarNota(uc, nota, repositorio.getAnoAtual())) {
                view.msgSucesso();

                model.bll.Avaliacao avAtual = alu.getAvaliacaoAtual(uc.getSigla());
                if (avAtual != null && alu.getEmailPessoal() != null && !alu.getEmailPessoal().isEmpty()) {
                    boolean emailEnviado = utils.ServicoEmail.enviarEmailAvaliacao(alu.getEmailPessoal(), alu.getNome(), uc.getNome(), avAtual);

                    if (emailEnviado) {
                        view.msgNotificacaoEnviada();
                    }
                }

                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            }
        } catch (NumberFormatException e) {
            view.msgErroFormato();
        }
    }

    private void lancarNotasEmLote() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) return;

        int idx = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idx < 0 || idx >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idx];
        Estudante[] alunos = repositorio.obterEstudantesPorUC(uc.getSigla());

        if (alunos.length == 0) return;

        view.cabecalhoLote(uc.getNome());
        int count = 0;

        for (int i = 0; i < alunos.length; i++) {
            Estudante alu = alunos[i];

            if (!alu.isAtivo()) continue;

            int num = alu.obterNumeroProximaAvaliacao(uc.getSigla());
            if (num > uc.getNumAvaliacoes()) continue;

            String input = view.inputNotaLote(i+1, alunos.length, alu.getNome(), num, uc.getNumAvaliacoes());
            if (input.isEmpty()) continue;

            try {
                double nota = Double.parseDouble(input);
                if (nota >= 0 && nota <= 20 && alu.adicionarNota(uc, nota, repositorio.getAnoAtual())) {
                    count++;
                }
            } catch (Exception e) {
                // Silencioso em lote
            }
        }

        view.resumoLote(count);

        if (count > 0) {
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);

            String caminhoTxt = model.bll.Pauta.gerarFicheiroPauta(uc, alunos);
            if (caminhoTxt != null) {
                view.msgPautaGeradaSucesso(caminhoTxt);
            } else {
                view.msgErroPauta();
            }
        }
    }

    private void alterarNumAvaliacoes() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.msgAvisoSemUCs();
            return;
        }
        int idxUC = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idxUC < 0 || idxUC >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idxUC];

        int novoNum = view.pedirNovoNumAvaliacoes();
        if (novoNum >= 1 && novoNum <= 3) {
            uc.setNumAvaliacoes(novoNum);
            view.msgSucesso();
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.msgErroNumAvaliacoes();
        }
    }

    // --- MÉTODO PARA AS ESTATISTICAS ---

    private void verEstatisticas() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.msgAvisoSemUCs();
            return;
        }

        int idxUC = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idxUC < 0 || idxUC >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idxUC];
        double[] stats = utils.Estatisticas.calcularEstatisticasUC(uc, repositorio);
        view.mostrarEstatisticas(uc.getSigla(), stats);
    }
}
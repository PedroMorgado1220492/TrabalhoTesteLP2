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
 * No padrão MVC, esta classe atua como intermediária (Controlador), encaminhando
 * as intenções do utilizador (capturadas na DocenteView) para as respetivas lógicas de negócio
 * presentes nos modelos (Docente, Estudante, UnidadeCurricular, etc.) e na base de dados (RepositorioDados).
 */
public class DocenteController {

    // ---------- ATRIBUTOS ----------
    private DocenteView view;
    private Docente docenteLogado;
    private RepositorioDados repositorio;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor do DocenteController.
     * Inicializa a interface visual (View) e injeta as dependências do modelo e repositório.
     *
     * @param docenteLogado A instância do docente que efetuou o login com sucesso no sistema.
     * @param repositorio   A referência para a base de dados central em memória.
     */
    public DocenteController(Docente docenteLogado, RepositorioDados repositorio) {
        this.view = new DocenteView();
        this.docenteLogado = docenteLogado;
        this.repositorio = repositorio;
    }


    // =========================================================
    // 1. FLUXO PRINCIPAL
    // =========================================================

    /**
     * Inicia o ciclo principal de execução do menu do Docente.
     * Mantém o utilizador neste ecrã até que seja escolhida a opção de saída.
     */
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
                // Captura o cancelamento do utilizador (introduzir '/' a meio de uma operação)
                view.mostrarCancelamento("do Docente");
            }
        }
    }


    // =========================================================
    // 2. MÉTODOS DE ATUALIZAÇÃO DE PERFIL
    // =========================================================

    /**
     * Gere o sub-menu dedicado à atualização dos dados pessoais do docente logado.
     */
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
                view.mostrarCancelamento("de Atualização");
            }
        }
    }

    /**
     * Coordena o fluxo de atualização do Nome, validando o input antes de delegar a alteração ao Modelo.
     */
    private void atualizarNome() {
        String novoNome = view.pedirNovoNome();

        // Delega a verificação da formatação à classe Utilitária Validador
        if (Validador.isNomeValido(novoNome)) {
            docenteLogado.setNome(novoNome); // Atualiza no Modelo
            view.msgSucesso();               // Feedback na View
        } else {
            view.msgOpcaoInvalida();
        }
    }

    /**
     * Coordena o fluxo de atualização do NIF.
     */
    private void atualizarNif() {
        String novoNif = view.pedirNovoNif();
        if (Validador.isNifValido(novoNif)) {
            docenteLogado.setNif(novoNif);
            view.msgSucesso();
        } else {
            view.msgOpcaoInvalida();
        }
    }

    /**
     * Coordena o fluxo de atualização da Morada. A morada tem um formato livre, não exigindo validação estrita.
     */
    private void atualizarMorada() {
        String novaMorada = view.pedirNovaMorada();
        docenteLogado.setMorada(novaMorada);
        view.msgSucesso();
    }

    /**
     * Coordena o fluxo de alteração de palavra-passe, garantindo a segurança através da validação da password atual.
     */
    private void atualizarPassword() {
        String passAtualRaw = view.pedirPassAtual();
        String passAtualEnc = Seguranca.encriptar(passAtualRaw);

        // Delega a validação de segurança ao Modelo (Docente/Utilizador)
        if (docenteLogado.verificarPassword(passAtualEnc)) {
            String novaPassRaw = view.pedirNovaPass();
            String confirmacaoRaw = view.pedirConfirmacaoPass();

            if (!novaPassRaw.isEmpty() && novaPassRaw.equals(confirmacaoRaw)) {
                // Guarda a nova password de forma segura no modelo
                docenteLogado.setPassword(Seguranca.encriptar(novaPassRaw));
                view.msgSucesso();
            } else {
                view.msgErroPassNaoCoincidem();
            }
        } else {
            view.msgErroPassIncorreta();
        }
    }


    // =========================================================
    // 3. GESTÃO DE AVALIAÇÕES E PAUTAS
    // =========================================================

    /**
     * Gere o sub-menu dedicado a operações académicas sobre avaliações e pautas das UCs lecionadas.
     */
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
                view.mostrarCancelamento("de Avaliações");
            }
        }
    }

    /**
     * Processa o fluxo para apresentar a pauta global de notas de todos os alunos de uma específica UC.
     */
    private void listarAvaliacoesUc() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.msgAvisoSemUCs();
            return;
        }

        int idxUC = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idxUC < 0 || idxUC >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idxUC];
        view.mostrarCabecalhoPauta(uc.getNome());

        // Delegação de pesquisa ao Repositório
        Estudante[] alunosDaUc = repositorio.obterEstudantesPorUC(uc.getSigla());
        boolean encontrou = false;

        // Processa os dados dos alunos e delega à View a formatação
        for (int i = 0; i < alunosDaUc.length; i++) {
            Estudante e = alunosDaUc[i];

            if (e != null && e.isAtivo()) {
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

    /**
     * Controla o processo de lançar uma nota para um único aluno selecionado pelo docente.
     */
    private void lancarNotaUnica() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.msgAvisoSemUCs();
            return;
        }

        // 1. Selecionar a UC
        int idxUC = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idxUC < 0 || idxUC >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idxUC];
        Estudante[] alunos = repositorio.obterEstudantesPorUC(uc.getSigla());

        if (alunos.length == 0) {
            view.msgAvisoTurmaVazia();
            return;
        }

        // 2. Selecionar o Aluno
        int idxAlu = view.pedirAluno(alunos, uc.getNome());
        if (idxAlu < 0 || idxAlu >= alunos.length) return;

        Estudante alu = alunos[idxAlu];

        if (!alu.isAtivo()) {
            view.msgErroAlunoInativo();
            return;
        }

        // 3. Validar se o aluno ainda pode receber avaliações
        int numNota = alu.obterNumeroProximaAvaliacao(uc.getSigla());
        if (numNota > uc.getNumAvaliacoes()) {
            view.msgErroLimiteNotas(uc.getNumAvaliacoes());
            return;
        }

        // 4. Pedir a nota e delegar ao modelo para guardar
        try {
            double nota = Double.parseDouble(view.pedirNotaIndividual(alu.getNome(), numNota, uc.getNumAvaliacoes()));

            if (nota < 0 || nota > 20) {
                view.msgErroNotaInvalida();
            } else if (alu.adicionarNota(uc, nota, repositorio.getAnoAtual())) {
                view.msgSucesso();

                // Disparo de notificação por email para o estudante
                model.bll.Avaliacao avAtual = alu.getAvaliacaoAtual(uc.getSigla());
                if (avAtual != null && alu.getEmailPessoal() != null && !alu.getEmailPessoal().isEmpty()) {
                    boolean emailEnviado = utils.ServicoEmail.enviarEmailAvaliacao(alu.getEmailPessoal(), alu.getNome(), uc.getNome(), avAtual);

                    if (emailEnviado) {
                        view.msgNotificacaoEnviada();
                    }
                }

                // Gravar alterações no CSV
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            }
        } catch (NumberFormatException e) {
            view.msgErroFormato();
        }
    }

    /**
     * Processa a inserção rápida de notas passando sucessivamente por todos os alunos da turma.
     * Ideal para o preenchimento no final do semestre.
     */
    private void lancarNotasEmLote() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) return;

        int idx = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idx < 0 || idx >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idx];
        Estudante[] alunos = repositorio.obterEstudantesPorUC(uc.getSigla());

        if (alunos.length == 0) return;

        view.cabecalhoLote(uc.getNome());
        int count = 0;

        // O Controller limita-se a iterar, pedir dados e delegar a gravação
        for (int i = 0; i < alunos.length; i++) {
            Estudante alu = alunos[i];

            if (!alu.isAtivo()) continue;

            int num = alu.obterNumeroProximaAvaliacao(uc.getSigla());
            if (num > uc.getNumAvaliacoes()) continue;

            String input = view.inputNotaLote(i + 1, alunos.length, alu.getNome(), num, uc.getNumAvaliacoes());
            if (input.isEmpty()) continue; // Enter vazio para saltar aluno

            try {
                double nota = Double.parseDouble(input);
                if (nota >= 0 && nota <= 20 && alu.adicionarNota(uc, nota, repositorio.getAnoAtual())) {
                    count++;
                }
            } catch (Exception e) {
                // Silencioso em lote, ignora entradas mal formatadas e passa ao seguinte
            }
        }

        view.resumoLote(count);

        if (count > 0) {
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);

            // Gera e guarda o ficheiro TXT da pauta
            String caminhoTxt = model.bll.Pauta.gerarFicheiroPauta(uc, alunos);
            if (caminhoTxt != null) {
                view.msgPautaGeradaSucesso(caminhoTxt);
            } else {
                view.msgErroPauta();
            }
        }
    }

    /**
     * Permite alterar o número máximo de avaliações de uma Unidade Curricular.
     */
    private void alterarNumAvaliacoes() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.msgAvisoSemUCs();
            return;
        }
        int idxUC = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idxUC < 0 || idxUC >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idxUC];
        int novoNum = view.pedirNovoNumAvaliacoes();

        // Delegação da regra de negócio (validar entre 1 e 3 avaliações) ao Model
        if (uc.alterarNumeroAvaliacoes(novoNum)) {
            view.msgSucesso();
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.msgErroNumAvaliacoes();
        }
    }


    // =========================================================
    // 4. ESTATÍSTICAS
    // =========================================================

    /**
     * Agrupa e exibe as estatísticas de desempenho (Médias, Taxas de aprovação, etc.)
     * relativas a uma UC lecionada pelo docente.
     */
    private void verEstatisticas() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.msgAvisoSemUCs();
            return;
        }

        int idxUC = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idxUC < 0 || idxUC >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idxUC];

        // Delega o cálculo matemático pesado à classe Utilitária
        double[] stats = utils.Estatisticas.calcularEstatisticasUC(uc, repositorio);

        view.mostrarEstatisticas(uc.getSigla(), stats);
    }
}
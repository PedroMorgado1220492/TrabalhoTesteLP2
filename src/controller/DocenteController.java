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

    /**
     * Construtor do controlador do docente.
     *
     * @param docenteLogado A instância do docente que iniciou a sessão.
     * @param repositorio   O repositório central com os dados do sistema.
     */
    public DocenteController(Docente docenteLogado, RepositorioDados repositorio) {
        this.view = new DocenteView();
        this.docenteLogado = docenteLogado;
        this.repositorio = repositorio;
    }

    /**
     * Inicia o ciclo principal do menu do docente.
     * Apresenta as opções disponíveis e encaminha a execução para os métodos correspondentes.
     */
    /**
     * Inicia o ciclo principal do menu do docente.
     */
    public void iniciarMenu() {
        boolean running = true;
        while (running) {
            int opcao = view.mostrarMenuPrincipal();
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
        }
    }

    /**
     * Apresenta o submenu dedicado ao agrupamento das funcionalidades de Avaliação.
     */
    private void menuAvaliacoes() {
        boolean sub = true;
        while (sub) {
            int op = view.mostrarMenuAvaliacoes();
            switch (op) {
                case 1: lancarNotaUnica(); break;
                case 2: lancarNotasEmLote(); break;
                case 3: listarAvaliacoesUc(); break;
                case 0: sub = false; break;
                default: view.msgOpcaoInvalida();
            }
        }
    }

    // ---------- MÉTODOS DE LANÇAMENTO DE NOTAS ----------

    /**
     * Processa o lançamento de uma nota individual a um estudante específico.
     * Valida se o docente tem UCs atribuídas, se o estudante está ativo e se ainda possui avaliações disponíveis.
     */
    private void lancarNotaUnica() {
        // Verifica se o docente leciona alguma Unidade Curricular
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.msgAvisoSemUCs();
            return;
        }

        // Solicita a seleção de uma Unidade Curricular e valida o índice escolhido
        int idxUC = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idxUC < 0 || idxUC >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idxUC];
        Estudante[] alunos = repositorio.obterEstudantesPorUC(uc.getSigla());

        // Verifica se existem estudantes inscritos na UC selecionada
        if (alunos.length == 0) {
            view.msgAvisoTurmaVazia();
            return;
        }

        // Solicita a seleção de um estudante e valida o índice escolhido
        int idxAlu = view.pedirAluno(alunos, uc.getNome());
        if (idxAlu < 0 || idxAlu >= alunos.length) return;

        Estudante alu = alunos[idxAlu];

        // Impede o lançamento de notas a estudantes que se encontrem inativos
        if (!alu.isAtivo()) {
            view.msgErroAlunoInativo();
            return;
        }

        // Verifica qual o número da próxima avaliação (N1, N2 ou N3) e se o limite já foi atingido
        int numNota = alu.obterNumeroProximaAvaliacao(uc.getSigla());
        if (numNota > 3) {
            view.msgErroLimiteNotas();
            return;
        }

        // Solicita o valor da nota, valida o intervalo (0-20) e regista a avaliação
        try {
            double nota = Double.parseDouble(view.pedirNotaIndividual(alu.getNome(), numNota));
            if (nota < 0 || nota > 20) {
                view.msgErroNotaInvalida();
            } else if (alu.adicionarNota(uc, nota, repositorio.getAnoAtual())) {
                view.msgSucesso();

                // Envio de e-mail com o estado atual das avaliações do Estudante
                model.bll.Avaliacao avAtual = alu.getAvaliacaoAtual(uc.getSigla());
                if (avAtual != null && alu.getEmailPessoal() != null && !alu.getEmailPessoal().isEmpty()) {
                    boolean emailEnviado = utils.ServicoEmail.enviarEmailAvaliacao(alu.getEmailPessoal(), alu.getNome(), uc.getNome(), avAtual);

                    if (emailEnviado) {
                        view.msgNotificacaoEnviada();
                    }
                }

                // Gravar as alterações no ficheiro CSV
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            }
        } catch (NumberFormatException e) {
            view.msgErroFormato();
        }
    }

    /**
     * Processa o lançamento sequencial de notas para uma turma inteira (lote).
     * Itera por todos os estudantes inscritos numa UC, ignorando os inativos ou os que já têm as notas todas lançadas.
     */
    private void lancarNotasEmLote() {
        // Validação inicial das UCs do docente
        if (docenteLogado.getTotalUcsLecionadas() == 0) return;

        // Seleção e validação da UC pretendida
        int idx = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idx < 0 || idx >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idx];
        Estudante[] alunos = repositorio.obterEstudantesPorUC(uc.getSigla());

        if (alunos.length == 0) return;

        view.cabecalhoLote(uc.getNome());
        int count = 0; // Contador para registar o número de notas lançadas com sucesso

        // Iteração sobre a lista de estudantes da turma
        for (int i = 0; i < alunos.length; i++) {
            Estudante alu = alunos[i];

            // Ignora estudantes inativos na listagem de lote
            if (!alu.isAtivo()) continue;

            // Ignora estudantes que já atingiram o limite máximo de avaliações nesta UC
            int num = alu.obterNumeroProximaAvaliacao(uc.getSigla());
            if (num > 3) continue;

            // Solicita o input da nota (permite avançar sem preencher através de um input vazio)
            String input = view.inputNotaLote(i+1, alunos.length, alu.getNome(), num);
            if (input.isEmpty()) continue;

            // Tenta converter o valor inserido e, se válido, regista a nota
            try {
                double nota = Double.parseDouble(input);
                if (nota >= 0 && nota <= 20 && alu.adicionarNota(uc, nota, repositorio.getAnoAtual())) {
                    count++;
                }
            } catch (Exception e) {
                // Exceção ignorada silenciosamente em lote; simplesmente não regista a nota
            }
        }

        // Apresenta o resumo final com o número de avaliações submetidas
        view.resumoLote(count);

        // Se lançou pelo menos uma nota, guarda na base de dados e gera a pauta TXT
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

    // ---------- MÉTODOS DE ATUALIZAÇÃO DE PERFIL ----------

    /**
     * Apresenta o submenu dedicado à atualização dos dados pessoais do docente logado.
     */
    private void menuAtualizar() {
        boolean sub = true;
        while (sub) {
            int op = view.mostrarMenuAtualizarDados();
            switch (op) {
                case 1: atualizarNome(); break;
                case 2: atualizarNif(); break;
                case 3: atualizarMorada(); break;
                case 4: atualizarPassword(); break;
                case 0: sub = false; break;
                default: view.msgOpcaoInvalida();
            }
        }
    }

    /**
     * Processa a atualização do nome do docente após validação.
     */
    private void atualizarNome() {
        String n = view.pedirNovoNome();
        if (Validador.isNomeValido(n)) {
            docenteLogado.setNome(n);
            view.msgSucesso();
        } else {
            view.msgOpcaoInvalida();
        }
    }

    /**
     * Processa a atualização do NIF do docente após validação.
     */
    private void atualizarNif() {
        String nif = view.pedirNovoNif();
        if (Validador.isNifValido(nif)) {
            docenteLogado.setNif(nif);
            view.msgSucesso();
        } else {
            view.msgOpcaoInvalida();
        }
    }

    /**
     * Processa a atualização da morada do docente.
     */
    private void atualizarMorada() {
        docenteLogado.setMorada(view.pedirNovaMorada());
        view.msgSucesso();
    }

    /**
     * Processa a atualização da password do docente.
     * Requer a confirmação da password atual e a repetição da nova password para evitar erros de dactilografia.
     */
    private void atualizarPassword() {
        String passAtualRaw = view.pedirPassAtual();
        // Encriptar o input para comparar com a Hash guardada
        String passAtualEnc = Seguranca.encriptar(passAtualRaw);

        if (passAtualEnc.equals(docenteLogado.getPassword())) {
            String novaPassRaw = view.pedirNovaPass();
            String confirmacaoRaw = view.pedirConfirmacaoPass();

            if (!novaPassRaw.isEmpty() && novaPassRaw.equals(confirmacaoRaw)) {
                // Encriptar a nova password antes de guardar
                docenteLogado.setPassword(Seguranca.encriptar(novaPassRaw));
                view.msgSucesso();
            } else {
                view.msgErroPassNaoCoincidem();
            }
        } else {
            view.msgErroPassIncorreta();
        }
    }

    // ---------- OUTROS ----------

    /**
     * Lista as avaliações do ano letivo corrente dos estudantes inscritos numa UC.
     */
    /**
     * Lista as avaliações do ano letivo corrente dos estudantes inscritos numa UC.
     * Imprime o Número Mecanográfico, o Nome, o Ano de Frequência e as respetivas avaliações.
     */
    private void listarAvaliacoesUc() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.msgAvisoSemUCs();
            return;
        }

        // 1. O Docente seleciona a UC da sua lista
        int idxUC = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idxUC < 0 || idxUC >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idxUC];

        view.mostrarCabecalhoPauta(uc.getNome());
        boolean encontrou = false;

        // 2. Itera por todos os estudantes do sistema
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];

            // Filtra apenas os alunos ativos e que estão inscritos na UC
            if (e != null && e.isAtivo() && e.estaInscrito(uc.getSigla())) {
                encontrou = true;

                // Vai buscar apenas as avaliações a decorrer no ano letivo atual
                model.bll.Avaliacao av = e.getAvaliacaoAtual(uc.getSigla());

                // Formata as notas (usando a classe Pauta)
                String notasStr = model.bll.Pauta.formatarNotasAluno(av);

                // Imprime Número, Nome, Ano do Estudante e as Notas da UC
                view.mostrarAlunoNaPauta(e.getNumeroMecanografico(), e.getNome(), e.getAnoFrequencia(), notasStr);
            }
        }

        if (!encontrou) {
            view.msgAvisoSemAlunosInscritos();
        }
    }

    /**
     * Calcula e exibe as estatísticas de desempenho para uma UC selecionada.
     */
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
package controller;

import model.bll.Curso;
import model.bll.UnidadeCurricular;
import model.bll.Propina;
import view.EstudanteView;
import model.bll.Estudante;
import model.dal.RepositorioDados;
import utils.Validador;
import utils.Seguranca;
import model.dal.PrecoCursoDAL;

/**
 * Controlador responsável pela gestão das operações e interações do Estudante.
 * No padrão MVC, esta classe atua como intermediária (Controlador), encaminhando
 * as intenções do utilizador (capturadas na EstudanteView) para as respetivas lógicas de negócio
 * presentes nos modelos (Estudante, Propina, Curso, etc.) e na base de dados (RepositorioDados).
 */
public class EstudanteController {

    // ---------- ATRIBUTOS ----------
    private EstudanteView view;
    private Estudante estudanteLogado;
    private RepositorioDados repositorio;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor do EstudanteController.
     * Inicializa a interface visual (View) e injeta as dependências do modelo e repositório.
     *
     * @param estudanteLogado A instância do estudante que efetuou o login com sucesso no sistema.
     * @param repositorio     A referência para a base de dados central em memória.
     */
    public EstudanteController(Estudante estudanteLogado, RepositorioDados repositorio) {
        this.view = new EstudanteView();
        this.estudanteLogado = estudanteLogado;
        this.repositorio = repositorio;
    }


    // =========================================================
    // 1. FLUXO PRINCIPAL
    // =========================================================

    /**
     * Inicia o ciclo principal de execução do menu do Estudante.
     * Mantém o utilizador neste ecrã até que seja escolhida a opção de saída ou a conta seja desativada.
     */
    public void iniciarMenu() {
        boolean running = true;
        while (running) {
            int opcao = view.mostrarMenuPrincipal();
            try {
                switch (opcao) {
                    case 1:
                        view.mostrarDadosFicha(estudanteLogado);
                        break;
                    case 2:
                        menuAtualizar();
                        break;
                    case 3:
                        verPercurso();
                        break;
                    case 4:
                        gerirPropinas();
                        break;
                    case 5:
                        if (desativarConta()) running = false;
                        break;
                    case 6:
                        if (pedirCertificado()) {
                            running = false;
                        }
                        break;
                    case 0:
                        view.msgSaida();
                        running = false;
                        break;
                    default:
                        view.msgErroOpcao();
                }
            } catch (utils.CancelamentoException e) {
                // Captura o cancelamento do utilizador (ex: introduzir '0' a meio de uma operação)
                view.mostrarCancelamento("do Estudante");
            }
        }
    }


    // =========================================================
    // 2. CONSULTA ACADÉMICA
    // =========================================================

    /**
     * Processa a visualização do percurso académico do estudante.
     * O Controller itera sobre o plano de estudos do curso e interroga o Model (Estudante)
     * sobre o estado e as notas em cada disciplina, delegando a formatação visual à View.
     */
    private void verPercurso() {
        Curso curso = estudanteLogado.getCurso();
        if (curso == null) {
            view.msgErroSemCurso();
            return;
        }

        view.mostrarCabecalhoPercurso();
        view.mostrarAnoFrequencia(estudanteLogado.getAnoFrequencia());

        // Itera sobre os 3 anos estruturais da Licenciatura
        for (int ano = 1; ano <= 3; ano++) {
            view.mostrarAnoPercurso(ano);

            for (int i = 0; i < curso.getTotalUCs(); i++) {
                UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];

                if (uc != null && uc.getAnoCurricular() == ano) {
                    // Delegação de regras ao Model Estudante (saber se está aprovado, inscrito, etc.)
                    int estado = estudanteLogado.obterCodigoEstadoUc(uc.getSigla());
                    double nota = estudanteLogado.obterNotaUc(uc.getSigla());

                    // A View formata a string consoante o estado e a nota devolvidos pelo Model
                    if (estado == 2 && nota == 0.0) {
                        view.mostrarLinhaUC(uc.getSigla(), uc.getNome(), ano, "Inscrito -> Sem Avaliações");
                    } else {
                        String statusStr = view.formatarStatusUC(estado, nota);
                        view.mostrarLinhaUC(uc.getSigla(), uc.getNome(), ano, statusStr);
                    }
                }
            }
        }

        double mediaAtual = estudanteLogado.calcularMediaFinal();
        if (mediaAtual > 0) {
            view.mostrarMediaGlobal(mediaAtual);
        }
    }


    // =========================================================
    // 3. ATUALIZAÇÃO DE PERFIL
    // =========================================================

    /**
     * Gere o sub-menu dedicado à atualização dos dados pessoais do estudante logado.
     */
    private void menuAtualizar() {
        boolean sub = true;
        while (sub) {
            int op = view.mostrarMenuAtualizarDados();
            try {
                switch (op) {
                    case 1:
                        atualizarNome();
                        break;
                    case 2:
                        atualizarNif();
                        break;
                    case 3:
                        atualizarMorada();
                        break;
                    case 4:
                        atualizarPassword();
                        break;
                    case 5:
                        atualizarEmailPessoal();
                        break;
                    case 0:
                        sub = false;
                        break;
                    default:
                        view.msgErroOpcao();
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
        String n = view.pedirNovoNome();
        if (Validador.isNomeValido(n)) {
            estudanteLogado.setNome(n);
            view.msgSucesso();
        } else {
            view.msgErroDados();
        }
    }

    /**
     * Coordena o fluxo de atualização do NIF.
     */
    private void atualizarNif() {
        String nif = view.pedirNovoNif();
        if (Validador.isNifValido(nif)) {
            estudanteLogado.setNif(nif);
            view.msgSucesso();
        } else {
            view.msgErroDados();
        }
    }

    /**
     * Coordena o fluxo de atualização da Morada.
     */
    private void atualizarMorada() {
        estudanteLogado.setMorada(view.pedirNovaMorada());
        view.msgSucesso();
    }

    /**
     * Coordena o fluxo de alteração de palavra-passe, garantindo a segurança através da validação da password atual.
     */
    private void atualizarPassword() {
        String passAtualRaw = view.pedirPassAtual();
        String passAtualEnc = Seguranca.encriptar(passAtualRaw);

        if (estudanteLogado.verificarPassword(passAtualEnc)) {
            String novaPassRaw = view.pedirNovaPass();
            String confirmacaoRaw = view.pedirConfirmacaoPass();

            if (!novaPassRaw.isEmpty() && novaPassRaw.equals(confirmacaoRaw)) {
                String novaPassEnc = Seguranca.encriptar(novaPassRaw);
                estudanteLogado.setPassword(novaPassEnc);

                // ATUALIZAR TAMBÉM NO FICHEIRO DE LOGINS
                model.dal.LoginDAL.atualizarPassword(estudanteLogado.getEmail(), novaPassEnc);

                view.msgSucesso();
            } else {
                view.msgErroPassNaoCoincidem();
            }
        } else {
            view.msgErroPassIncorreta();
        }
    }

    /**
     * Coordena o fluxo de atualização do Email Pessoal.
     * Como não requer validação, aceita qualquer string não vazia.
     */
    private void atualizarEmailPessoal() {
        // 1. Pedir o novo email através da View
        String novoEmail = view.pedirNovoEmailPessoal(estudanteLogado.getEmailPessoal());

        // 2. Verificar se o utilizador escreveu algo (não carregou apenas em Enter)
        if (!novoEmail.trim().isEmpty()) {
            estudanteLogado.setEmailPessoal(novoEmail);

            view.msgSucesso();
        } else {
            // Se estiver vazio, assumimos que o utilizador desistiu da alteração
            view.mostrarCancelamento("de Atualização");
        }
    }
    // =========================================================
    // 4. GESTÃO FINANCEIRA (PROPINAS)
    // =========================================================

    /**
     * Coordena o fluxo de consulta e pagamento de propinas referentes ao ano letivo em curso.
     * Trata da interação com a View, processa o pagamento e emite o respetivo recibo (PDF e Email).
     */
    private void gerirPropinas() {
        int anoAtual = repositorio.getAnoAtual();
        int numMec = estudanteLogado.getNumeroMecanografico();

        // Mostrar histórico de pagamentos
        model.bll.Propina.Pagamento[] pagamentos = model.bll.Propina.getPagamentos(numMec);
        view.mostrarHistoricoPagamentos(pagamentos);

        double valorAnualAtual = PrecoCursoDAL.obterPrecoCurso(estudanteLogado.getCurso().getSigla(), anoAtual);
        double pagoAnoAtual = model.bll.Propina.getTotalPago(numMec, anoAtual);
        double dividaTotal = model.bll.Propina.calcularDividaTotal(estudanteLogado, anoAtual);
        double totalPago = 0.0;
        for (int ano = estudanteLogado.getAnoPrimeiraInscricao(); ano <= anoAtual; ano++) {
            totalPago += model.bll.Propina.getTotalPago(numMec, ano);
        }

        view.mostrarExtratoPropinas(anoAtual, valorAnualAtual, dividaTotal, totalPago);

        if (dividaTotal <= 0.01) return;

        double valor = calcularValorPagamento(dividaTotal, valorAnualAtual);
        if (valor <= 0) return;

        // Guardar estado da dívida de anos anteriores antes do pagamento
        boolean tinhaDividasAnteriores = model.bll.Propina.temDividasAteAno(estudanteLogado, anoAtual - 1, anoAtual);

        // Registar pagamento
        String dataAtual = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        model.bll.Propina.registarPagamento(numMec, anoAtual, valor, dataAtual);

        // Recalcular valores após pagamento
        double novaDividaTotal = model.bll.Propina.calcularDividaTotal(estudanteLogado, anoAtual);
        double novoPagoAno = pagoAnoAtual + valor;
        double novaDividaAno = valorAnualAtual - novoPagoAno;

        // Calcular total devido para recibo (usando PrecoCursoDAL)
        double totalDevido = 0.0;
        for (int ano = estudanteLogado.getAnoPrimeiraInscricao(); ano <= anoAtual; ano++) {
            totalDevido += PrecoCursoDAL.obterPrecoCurso(estudanteLogado.getCurso().getSigla(), ano);
        }


        // Gerar recibo e enviar email (código mantém-se igual)
        String caminhoRecibo = model.bll.Recibo.gerarRecibo(estudanteLogado, valor, totalDevido, novaDividaTotal);

        // Anular Email Recibo
      //  if (caminhoRecibo != null && estudanteLogado.getEmailPessoal() != null && !estudanteLogado.getEmailPessoal().isEmpty()) {
          //  boolean emailEnviado = utils.ServicoEmail.enviarEmailRecibo(estudanteLogado.getEmailPessoal(),
                 //   estudanteLogado.getNome(),
                 //   caminhoRecibo);
       //     if (emailEnviado) {
          //      view.msgNotificacaoEnviada();
       //     } else {
                view.msgFalhaEnvioEmail();
      //      }
    //    } else {
    //        view.msgReciboNaoEnviado();
    //    }
        // Até aqui.

        view.msgSucesso();

        // Verificar estado da dívida de anos anteriores após pagamento
        boolean agoraTemDividasAnteriores = model.bll.Propina.temDividasAteAno(estudanteLogado, anoAtual - 1, anoAtual);

        if (tinhaDividasAnteriores && !agoraTemDividasAnteriores) {
            if (estudanteLogado.reinscrever(anoAtual)) {
                estudanteLogado.reconstruirPercurso();
                repositorio.atualizarEstudante(estudanteLogado);
                view.msgPercursoAtualizado();
                if (estudanteLogado.getAnoFrequencia() > 1) {
                    view.msgEstudanteProgrediu(estudanteLogado.getAnoFrequencia());
                }
            } else {
                view.msgReinscricaoNaoPossivel();
            }
        }
    }

    /**
     * Calcula e valida o valor a ser pago pelo estudante, mediante as regras da instituição.
     * * @param propina A propina alvo de pagamento.
     *
     * @return O valor final aprovado para pagamento, ou 0.0 em caso de cancelamento/erro.
     */
    private double calcularValorPagamento(double dividaTotal, double valorAnualAtual) {
        // Prestação mínima = 10% do valor anual original (e não da dívida)
        double prestacaoBase = valorAnualAtual * 0.10;
        double valorMinimoPrestacao = Math.min(prestacaoBase, dividaTotal);

        int op = view.mostrarOpcoesPagamento(dividaTotal, valorMinimoPrestacao);
        double valorEscolhido = 0.0;

        switch (op) {
            case 1: // Pagamento Integral
                valorEscolhido = dividaTotal;
                break;
            case 2: // Prestação Mínima
                valorEscolhido = valorMinimoPrestacao;
                break;
            case 3: // Valor Personalizado
                valorEscolhido = view.pedirValorLivre();
                break;
            default: // Cancelar
                return 0.0;
        }

        // Validações finais
        if (valorEscolhido <= 0) {
            return 0.0;
        }
        if (valorEscolhido > dividaTotal) {
            view.msgErroValorSuperiorDivida();   // mensagem adicional (ver abaixo)
            return 0.0;
        }
        if (valorEscolhido < valorMinimoPrestacao) {
            view.msgErroValorMinimo(valorMinimoPrestacao);
            return 0.0;
        }
        return valorEscolhido;
    }


    // =========================================================
    // 5. GESTÃO DE CONTA
    // =========================================================

    /**
     * Permite que o estudante desative voluntariamente a sua conta no sistema.
     * * @return true se a conta foi efetivamente desativada, false se a operação foi cancelada.
     */
    private boolean desativarConta() {
        if (view.pedirConfirmacaoDesativacao()) {
            estudanteLogado.setAtivo(false);

            // PERSISTIR A ALTERAÇÃO NO CSV
            repositorio.atualizarEstudante(estudanteLogado);

            view.msgContaDesativada();
            return true;
        }
        return false;
    }

    // =========================================================
    // 6. CERTIFICADO
    // =========================================================


    private boolean pedirCertificado() {
        view.msgCertificadoApenasAnoSeguinte();

        int anoAtual = repositorio.getAnoAtual();
        int anoConclusao = anoAtual - 1;
        String email = estudanteLogado.getEmailPessoal();

        if (email == null || email.isEmpty()) {
            view.msgErroSemEmailPessoal();
            return false;
        }

        // 1. Verificar dívidas (até ao ano de conclusão)
        if (Propina.temDividasAteAno(estudanteLogado, anoConclusao, anoAtual)) {
            view.msgTemDividasParaCertificado();
            return false;
        }

        // 2. Verificar se todas as UCs estão concluídas (histórico)
        if (!estudanteLogado.concluiuCurso()) {
            view.msgNaoConcluiuCurso();
            return false;
        }

        // 3. Gerar certificado (método do model, sem validações)
        boolean sucesso = estudanteLogado.gerarCertificado(anoConclusao, email);
        if (!sucesso) {
            view.msgErroGerarCertificado();
            return false;
        }

        repositorio.atualizarEstudante(estudanteLogado);
        view.msgCertificadoEmitido();
        return true; // indica logout
    }
}

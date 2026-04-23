package controller;

import model.bll.Curso;
import model.bll.UnidadeCurricular;
import model.bll.Propina;
import view.EstudanteView;
import model.bll.Estudante;
import model.dal.RepositorioDados;
import utils.Validador;
import utils.Seguranca;

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
                    case 1: view.mostrarDadosFicha(estudanteLogado); break;
                    case 2: menuAtualizar(); break;
                    case 3: verPercurso(); break;
                    case 4: gerirPropinas(); break;
                    case 5:
                        if (desativarConta()) running = false;
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
                    case 1: atualizarNome(); break;
                    case 2: atualizarNif(); break;
                    case 3: atualizarMorada(); break;
                    case 4: atualizarPassword(); break;
                    case 5: atualizarEmailPessoal(); break;
                    case 0: sub = false; break;
                    default: view.msgErroOpcao();
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

        // Validação de segurança delegada ao modelo (herança de Utilizador)
        if (estudanteLogado.verificarPassword(passAtualEnc)) {
            String novaPassRaw = view.pedirNovaPass();
            String confirmacaoRaw = view.pedirConfirmacaoPass();

            if (!novaPassRaw.isEmpty() && novaPassRaw.equals(confirmacaoRaw)) {
                estudanteLogado.setPassword(Seguranca.encriptar(novaPassRaw));
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

            // 3. Gravar imediatamente no CSV para persistir a alteração
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);

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
        Propina propina = estudanteLogado.getPropinaDoAno(repositorio.getAnoAtual());

        if (propina == null) {
            view.msgErroSemPropina();
            return;
        }

        view.mostrarDetalhesPropina(propina.getValorTotal(), propina.getValorPago(),
                propina.getValorEmDivida(), propina.getHistoricoPagamentos(),
                propina.getTotalPagamentos(), propina.isPagaTotalmente());

        // Se a propina já estiver liquidada, não prossegue para o pagamento
        if (propina.isPagaTotalmente()) return;

        double valor = calcularValorPagamento(propina);
        if (valor <= 0) return;

        // Efetua o registo do pagamento no Modelo
        if (propina.registarPagamento(valor)) {
            view.msgSucesso();

            // Despoleta a geração do recibo em ficheiro texto/PDF
            String caminhoRecibo = model.bll.Recibo.gerarRecibo(estudanteLogado, valor, propina.getValorTotal(), propina.getValorEmDivida());

            // Dispara o envio do recibo por email
            if (caminhoRecibo != null && estudanteLogado.getEmailPessoal() != null) {
                utils.ServicoEmail.enviarEmailRecibo(estudanteLogado.getEmailPessoal(), estudanteLogado.getNome(), caminhoRecibo);
            }

            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.msgErroDados();
        }
    }

    /**
     * Calcula e valida o valor a ser pago pelo estudante, mediante as regras da instituição.
     * * @param propina A propina alvo de pagamento.
     * @return O valor final aprovado para pagamento, ou 0.0 em caso de cancelamento/erro.
     */
    private double calcularValorPagamento(Propina propina) {
        // Regra de negócio (valor mínimo de prestação) delegada ao model Propina
        double valorMinimoPrestacao = propina.calcularValorMinimoPrestacao();

        int op = view.mostrarOpcoesPagamento(propina.getValorEmDivida(), valorMinimoPrestacao);

        double valorEscolhido = switch (op) {
            case 1 -> propina.getValorEmDivida(); // Pagamento Integral
            case 2 -> Math.min(valorMinimoPrestacao, propina.getValorEmDivida()); // Prestação Mínima
            case 3 -> view.pedirValorLivre(); // Outro Valor
            default -> 0.0;
        };

        if (valorEscolhido <= 0) {
            return 0.0;
        }

        double limiteMinimoAceitavel = Math.min(valorMinimoPrestacao, propina.getValorEmDivida());

        // Validação: O valor introduzido não pode ser inferior ao limite mínimo estipulado
        if (valorEscolhido < limiteMinimoAceitavel) {
            view.msgErroValorMinimo(limiteMinimoAceitavel);
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
            estudanteLogado.setAtivo(false); // Altera o estado no Modelo
            model.dal.ExportadorCSV.exportarDados("bd", repositorio); // Grava a desativação
            view.msgContaDesativada();
            return true;
        }
        return false;
    }
}
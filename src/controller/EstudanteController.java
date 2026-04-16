package controller;

import model.bll.Avaliacao;
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
 * Atua como intermediário entre a interface gráfica do estudante (EstudanteView)
 * e a camada de dados (RepositorioDados), garantindo a integridade do percurso académico.
 */
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
                System.out.println("\n>> Operação cancelada. A regressar ao menu do Estudante...");
            }
        }
    }

    // ---------- MÉTODOS DE CONSULTA ACADÉMICA ----------

    private void verPercurso() {
        Curso curso = estudanteLogado.getCurso();
        if (curso == null) {
            view.msgErroSemCurso();
            return;
        }

        view.mostrarCabecalhoPercurso();
        for (int ano = 1; ano <= 3; ano++) {
            view.mostrarAnoPercurso(ano);
            for (int i = 0; i < curso.getTotalUCs(); i++) {
                UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];
                if (uc.getAnoCurricular() == ano) {
                    String statusStr = processarStatusParaView(uc.getSigla());
                    view.mostrarLinhaUC(uc.getSigla(), uc.getNome(), ano, statusStr);
                }
            }
        }

        double mediaAtual = estudanteLogado.calcularMediaFinal();
        if (mediaAtual > 0) {
            view.mostrarMediaGlobal(mediaAtual);
        }
    }

    private String processarStatusParaView(String sigla) {
        int estado = 0;
        double nota = 0.0;

        if (estudanteLogado.estaInscrito(sigla)) {
            Avaliacao av = estudanteLogado.getAvaliacaoAtual(sigla);
            if (av != null) {
                estado = 1;
                nota = av.calcularMedia();
            } else {
                estado = 2;
            }
        } else {
            Avaliacao hist = estudanteLogado.getAvaliacaoHistorico(sigla);
            if (hist != null && hist.calcularMedia() >= 9.5) {
                estado = 3;
                nota = hist.calcularMedia();
            }
        }

        return view.formatarStatusUC(estado, nota);
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
                    default: view.msgErroOpcao();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu de Atualização...");
            }
        }
    }

    private void atualizarNome() {
        String n = view.pedirNovoNome();
        if (Validador.isNomeValido(n)) {
            estudanteLogado.setNome(n);
            view.msgSucesso();
        } else {
            view.msgErroDados();
        }
    }

    private void atualizarNif() {
        String nif = view.pedirNovoNif();
        if (Validador.isNifValido(nif)) {
            estudanteLogado.setNif(nif);
            view.msgSucesso();
        } else {
            view.msgErroDados();
        }
    }

    private void atualizarMorada() {
        estudanteLogado.setMorada(view.pedirNovaMorada());
        view.msgSucesso();
    }

    private void atualizarPassword() {
        String passAtualRaw = view.pedirPassAtual();
        String passAtualEnc = Seguranca.encriptar(passAtualRaw);

        if (passAtualEnc.equals(estudanteLogado.getPassword())) {
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

    // ---------- GESTÃO FINANCEIRA (PROPINAS) ----------

    private void gerirPropinas() {
        Propina propina = estudanteLogado.getPropinaDoAno(repositorio.getAnoAtual());
        if (propina == null) {
            view.msgErroSemPropina();
            return;
        }

        view.mostrarDetalhesPropina(propina.getValorTotal(), propina.getValorPago(),
                propina.getValorEmDivida(), propina.getHistoricoPagamentos(),
                propina.getTotalPagamentos(), propina.isPagaTotalmente());

        if (propina.isPagaTotalmente()) return;

        double valor = calcularValorPagamento(propina);
        if (valor <= 0) return;

        if (propina.registarPagamento(valor)) {
            view.msgSucesso();

            String caminhoRecibo = model.bll.Recibo.gerarRecibo(estudanteLogado, valor, propina.getValorTotal(), propina.getValorEmDivida());

            if (caminhoRecibo != null && estudanteLogado.getEmailPessoal() != null) {
                utils.ServicoEmail.enviarEmailRecibo(estudanteLogado.getEmailPessoal(), estudanteLogado.getNome(), caminhoRecibo);
            }

            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.msgErroDados();
        }
    }

    private double calcularValorPagamento(Propina propina) {
        double valorMinimoPrestacao = propina.getValorTotal() / 10;
        int op = view.mostrarOpcoesPagamento(propina.getValorEmDivida(), valorMinimoPrestacao);

        double valorEscolhido = switch (op) {
            case 1 -> propina.getValorEmDivida();
            case 2 -> Math.min(valorMinimoPrestacao, propina.getValorEmDivida());
            case 3 -> view.pedirValorLivre();
            default -> 0.0;
        };

        if (valorEscolhido <= 0) {
            return 0.0;
        }

        double limiteMinimoAceitavel = Math.min(valorMinimoPrestacao, propina.getValorEmDivida());

        if (valorEscolhido < limiteMinimoAceitavel) {
            view.msgErroValorMinimo(limiteMinimoAceitavel);
            return 0.0;
        }

        return valorEscolhido;
    }

    // ---------- DESATIVAÇÃO DE CONTA ----------

    private boolean desativarConta() {
        if (view.pedirConfirmacaoDesativacao()) {
            estudanteLogado.setAtivo(false);
            view.msgContaDesativada();
            return true;
        }
        return false;
    }
}
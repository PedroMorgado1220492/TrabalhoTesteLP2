package controller;

import model.bll.Avaliacao;
import model.bll.Curso;
import model.bll.UnidadeCurricular;
import model.bll.Propina;
import view.EstudanteView;
import model.bll.Estudante;
import model.dal.RepositorioDados;
import utils.Validador;

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
            switch (opcao) {
                case 1: view.mostrarDadosFicha(estudanteLogado); break;
                case 2: menuAtualizar(); break;
                case 3: verPercurso(); break;
                case 4: gerirPropinas(); break;
                case 0: view.msgSaida(); running = false; break;
                default: view.msgErroOpcao();
            }
        }
    }

    // ---------- MÉTODOS DE CONSULTA ----------

    private void verPercurso() {
        Curso curso = estudanteLogado.getCurso();
        if (curso == null) { view.msgErroSemCurso(); return; }

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
    }

    private String processarStatusParaView(String sigla) {
        int estado = 0; // Default: Não inscrito
        double nota = 0.0;

        if (estudanteLogado.estaInscrito(sigla)) {
            Avaliacao av = estudanteLogado.getAvaliacaoAtual(sigla);
            if (av != null) {
                estado = 1; // Inscrito com nota
                nota = av.calcularMedia();
            } else {
                estado = 2; // Inscrito sem nota
            }
        } else {
            Avaliacao hist = estudanteLogado.getAvaliacaoHistorico(sigla);
            if (hist != null && hist.calcularMedia() >= 9.5) {
                estado = 3; // Concluído
                nota = hist.calcularMedia();
            }
        }
        return view.formatarStatusUC(estado, nota);
    }

    // ---------- MÉTODOS DE ATUALIZAÇÃO (PERFIL) ----------

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
                default: view.msgErroOpcao();
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
        if (view.pedirPassAtual().equals(estudanteLogado.getPassword())) {
            String p = view.pedirNovaPass();
            if (p.equals(view.pedirConfirmacaoPass())) {
                estudanteLogado.setPassword(p);
                view.msgSucesso();
            } else {
                view.msgErroPassNaoCoincidem();
            }
        } else {
            view.msgErroPassIncorreta();
        }
    }

    // ---------- GESTÃO FINANCEIRA ----------

    private void gerirPropinas() {
        Propina propina = estudanteLogado.getPropinaDoAno(repositorio.getAnoAtual());
        if (propina == null) { view.msgErroSemPropina(); return; }

        view.mostrarDetalhesPropina(propina.getValorTotal(), propina.getValorPago(),
                propina.getValorEmDivida(), propina.getHistoricoPagamentos(),
                propina.getTotalPagamentos(), propina.isPagaTotalmente());

        if (propina.isPagaTotalmente()) return;

        double valor = calcularValorPagamento(propina);
        if (valor == 0) return; // Operação cancelada ou inválida

        if (valor > 0 && propina.registarPagamento(valor)) {
            view.msgSucesso();
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.msgErroDados();
        }
    }

    private double calcularValorPagamento(Propina propina) {
        int op = view.mostrarOpcoesPagamento(propina.getValorEmDivida(), propina.getValorTotal() / 10);

        return switch (op) {
            case 1 -> propina.getValorEmDivida();
            case 2 -> Math.min(propina.getValorTotal() / 10, propina.getValorEmDivida());
            case 3 -> view.pedirValorLivre();
            default -> 0;
        };
    }
}
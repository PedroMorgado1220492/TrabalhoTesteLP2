package controller;

import model.bll.Docente;
import model.bll.Estudante;
import model.bll.UnidadeCurricular;
import model.dal.RepositorioDados;
import view.DocenteView;
import utils.Validador;

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
            switch (opcao) {
                case 1: view.mostrarFichaDocente(docenteLogado); break;
                case 2: menuAtualizar(); break;
                case 3: lancarNotaUnica(); break;
                case 4: lancarNotasEmLote(); break;
                case 5: verStats(); break;
                case 0: view.msgSaida(); running = false; break;
                default: view.msgOpcaoInvalida();
            }
        }
    }

    // ---------- MÉTODOS DE AÇÃO (LANÇAMENTO DE NOTAS) ----------

    private void lancarNotaUnica() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) { view.msgAvisoSemUCs(); return; }

        int idxUC = view.pedirUC(docenteLogado.getUcsLecionadas(), docenteLogado.getTotalUcsLecionadas());
        if (idxUC < 0 || idxUC >= docenteLogado.getTotalUcsLecionadas()) return;

        UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[idxUC];
        Estudante[] alunos = repositorio.obterEstudantesPorUC(uc.getSigla());
        if (alunos.length == 0) { view.msgAvisoTurmaVazia(); return; }

        int idxAlu = view.pedirAluno(alunos, uc.getNome());
        if (idxAlu < 0 || idxAlu >= alunos.length) return;

        Estudante alu = alunos[idxAlu];

        if (!alu.isAtivo()) {
            view.msgErroAlunoInativo();
            return;
        }

        int numNota = alu.obterNumeroProximaAvaliacao(uc.getSigla());
        if (numNota > 3) { view.msgErroLimiteNotas(); return; }

        try {
            double nota = Double.parseDouble(view.pedirNotaIndividual(alu.getNome(), numNota));
            if (nota < 0 || nota > 20) view.msgErroNotaInvalida();
            else if (alu.adicionarNota(uc, nota, repositorio.getAnoAtual())) view.msgSucesso();
        } catch (NumberFormatException e) { view.msgErroFormato(); }
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
            if (num > 3) continue;

            String input = view.inputNotaLote(i+1, alunos.length, alu.getNome(), num);
            if (input.isEmpty()) continue;
            try {
                double nota = Double.parseDouble(input);
                if (nota >= 0 && nota <= 20 && alu.adicionarNota(uc, nota, repositorio.getAnoAtual())) count++;
            } catch (Exception e) {}
        }
        view.resumoLote(count);
    }

    // ---------- MÉTODOS DE ATUALIZAÇÃO DE PERFIL ----------

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
        if (view.pedirPassAtual().equals(docenteLogado.getPassword())) {
            String p = view.pedirNovaPass();
            if (p.equals(view.pedirConfirmacaoPass())) {
                docenteLogado.setPassword(p);
                view.msgSucesso();
            } else {
                view.msgErroPassNaoCoincidem();
            }
        } else {
            view.msgErroPassIncorreta();
        }
    }

    // ---------- OUTROS ----------

    private void verStats() {
        double m = utils.Estatisticas.calcularMediaUCsDocente(docenteLogado, repositorio);
        int t = utils.Estatisticas.contarAlunosAvaliadosDoDocente(docenteLogado, repositorio);
        view.mostrarEstatisticas(m, t);
    }
}
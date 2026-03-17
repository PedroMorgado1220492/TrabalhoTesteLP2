package controller;

import model.*;
import view.DocenteView;
import utils.Validador;

public class DocenteController {

    // ---------- ATRIBUTOS ----------
    private DocenteView view;
    private Docente docenteLogado;
    private RepositorioDados repositorio;

    // ---------- CONSTRUTOR ----------
    public DocenteController(Docente docenteLogado, RepositorioDados repositorio) {
        this.view = new DocenteView();
        this.docenteLogado = docenteLogado;
        this.repositorio = repositorio;
    }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Inicia o ciclo principal do Docente autenticado, permitindo a consulta
     * de dados pessoais e o lançamento de avaliações.
     */
    public void iniciarMenu() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuPrincipal();
            switch (opcao) {
                case 1:
                    verDadosDocente();
                    break;
                case 2:
                    atualizarDadosDocente();
                    break;
                case 3:
                    gerirAvaliacoes();
                    break;
                case 4:
                    lancarNotasEmLote();
                    break;
                case 5:
                    mostrarEstatisticasDocente();
                    break;
                case 0:
                    view.mostrarMensagem("A sair da conta de Docente...");
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    /**
     * Apresenta a ficha com as informações de registo do Docente no sistema.
     */
    private void verDadosDocente() {
        view.mostrarMensagem("\n--- FICHA DE DOCENTE ---");
        view.mostrarMensagem("Sigla: " + docenteLogado.getSigla());
        view.mostrarMensagem("Nome: " + docenteLogado.getNome());
        view.mostrarMensagem("Email: " + docenteLogado.getEmail());
        view.mostrarMensagem("NIF: " + docenteLogado.getNif());
        view.mostrarMensagem("Morada: " + docenteLogado.getMorada());
        view.mostrarMensagem("Data de Nascimento: " + docenteLogado.getDataNascimento());
    }

    /**
     * Gere o processo de seleção de Unidades Curriculares lecionadas pelo Docente,
     * seleção de alunos nelas inscritos e posterior lançamento da nota.
     */
    private void gerirAvaliacoes() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.mostrarMensagem("Ainda não tem Unidades Curriculares atribuídas.");
            return;
        }

        // 1. ESCOLHER A UC
        view.mostrarMensagem("\n--- AS MINHAS UNIDADES CURRICULARES ---");
        for (int i = 0; i < docenteLogado.getTotalUcsLecionadas(); i++) {
            model.UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[i];
            view.mostrarMensagem((i + 1) + " - " + uc.getNome() + " [" + uc.getSigla() + "]");
        }

        int escolhaUC;
        try {
            escolhaUC = Integer.parseInt(view.pedirInputString("Escolha a UC (Número)")) - 1;
        } catch (NumberFormatException e) {
            view.mostrarMensagem("Entrada inválida. Digite um número.");
            return;
        }

        if (escolhaUC < 0 || escolhaUC >= docenteLogado.getTotalUcsLecionadas()) {
            view.mostrarMensagem("UC inválida.");
            return;
        }

        model.UnidadeCurricular ucSelecionada = docenteLogado.getUcsLecionadas()[escolhaUC];

        // 2. OBTER APENAS OS ALUNOS INSCRITOS NESTA CADEIRA
        model.Estudante[] alunosDaUC = repositorio.obterEstudantesPorUC(ucSelecionada.getSigla());

        if (alunosDaUC.length == 0) {
            view.mostrarMensagem("Não existem alunos inscritos a esta Unidade Curricular neste momento.");
            return;
        }

        // 3. ESCOLHER O ALUNO
        view.mostrarMensagem("\n--- ALUNOS INSCRITOS EM " + ucSelecionada.getNome() + " ---");
        for (int i = 0; i < alunosDaUC.length; i++) {
            view.mostrarMensagem((i + 1) + " - " + alunosDaUC[i].getNome() + " (" + alunosDaUC[i].getNumeroMecanografico() + ")");
        }

        int escolhaAluno;
        try {
            escolhaAluno = Integer.parseInt(view.pedirInputString("Escolha o Aluno (Número)")) - 1;
        } catch (NumberFormatException e) {
            view.mostrarMensagem("Entrada inválida.");
            return;
        }

        if (escolhaAluno < 0 || escolhaAluno >= alunosDaUC.length) {
            view.mostrarMensagem("Aluno inválido.");
            return;
        }

        model.Estudante alunoSelecionado = alunosDaUC[escolhaAluno];

        // 4. LANÇAR A NOTA

        int proximaNota = alunoSelecionado.obterNumeroProximaAvaliacao(ucSelecionada.getSigla());

        if (proximaNota > 3) {
            view.mostrarMensagem(">> Erro: O aluno " + alunoSelecionado.getNome() + " já tem as 3 avaliações máximas lançadas para esta UC.");
            return;
        }

        double nota;
        try {
            String mensagemInput = "Introduza a avaliação (0.0 a 20.0) para " + alunoSelecionado.getNome() + " (" + proximaNota + "/3)";
            nota = Double.parseDouble(view.pedirInputString(mensagemInput));
        } catch (NumberFormatException e) {
            view.mostrarMensagem("Formato de nota inválido (use . para decimais, ex: 14.5).");
            return;
        }

        if (nota < 0.0 || nota > 20.0) {
            view.mostrarMensagem("Erro: A nota deve estar compreendida entre 0.0 e 20.0.");
            return;
        }

        boolean sucesso = alunoSelecionado.adicionarNota(ucSelecionada, nota, repositorio.getAnoAtual());

        if (sucesso) {
            view.mostrarMensagem(">> Sucesso! Nota de " + nota + " lançada ao aluno " + alunoSelecionado.getNome() + " na UC de " + ucSelecionada.getSigla() + ".");
        } else {
            view.mostrarMensagem(">> Erro ao guardar a nota. A operação falhou.");
        }
    }

    /**
     * Consulta a quantidade de notas que já foram registadas para um aluno
     * numa específica Unidade Curricular.
     * * @param est O Estudante a analisar.
     * @param uc A Unidade Curricular a procurar.
     * @return O número de notas já lançadas.
     */
    private int obterQuantidadeNotas(Estudante est, UnidadeCurricular uc) {
        if (est.getAvaliacoes() == null) return 0;

        for (Avaliacao a : est.getAvaliacoes()) {
            if (a != null && a.getUnidadeCurricular().getSigla().equals(uc.getSigla())) {
                return a.getTotalAvaliacoesLancadas();
            }
        }
        return 0;
    }

    /**
     * Permite a alteração iterativa dos dados pessoais do Docente autenticado.
     */
    private void atualizarDadosDocente() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuAtualizarDados();
            switch (opcao) {
                case 1:
                    String novoNome = "";
                    while (true) {
                        novoNome = view.pedirInputString("Introduza o novo Nome (Nome e Sobrenome)");
                        if (Validador.isNomeValido(novoNome)) break;
                        view.mostrarMensagem("Erro: O nome deve conter nome e sobrenome.");
                    }
                    docenteLogado.setNome(novoNome);
                    view.mostrarMensagem("Nome atualizado!");
                    break;
                case 2:
                    String novoNif = "";
                    while (true) {
                        novoNif = view.pedirInputString("Introduza o novo NIF (9 dígitos)");
                        if (Validador.isNifValido(novoNif)) break;
                        view.mostrarMensagem("Erro: NIF inválido.");
                    }
                    docenteLogado.setNif(novoNif);
                    view.mostrarMensagem("NIF atualizado!");
                    break;
                case 3:
                    docenteLogado.setMorada(view.pedirInputString("Introduza a nova Morada"));
                    view.mostrarMensagem("Morada atualizada!");
                    break;
                case 4:
                    String antiga = view.pedirInputString("Password Atual");
                    if (antiga.equals(docenteLogado.getPassword())) {
                        String nova = view.pedirInputString("Nova Password");
                        if (nova.equals(view.pedirInputString("Confirme Nova Password"))) {
                            docenteLogado.setPassword(nova);
                            view.mostrarMensagem("Password alterada!");
                        } else view.mostrarMensagem("As passwords não coincidem.");
                    } else view.mostrarMensagem("Password atual incorreta.");
                    break;
                case 0:
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }
    /**
     * Utiliza a classe utilitária de Estatísticas enviando como filtro
     * o Docente logado, garantindo que ele só vê os seus próprios dados.
     */
    private void mostrarEstatisticasDocente() {
        view.mostrarMensagem("\n--- AS MINHAS ESTATÍSTICAS ---");

        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.mostrarMensagem("Ainda não tem Unidades Curriculares atribuídas para gerar estatísticas.");
            return;
        }

        double mediaUcs = utils.Estatisticas.calcularMediaUCsDocente(docenteLogado, repositorio);
        int totalAlunos = utils.Estatisticas.contarAlunosAvaliadosDoDocente(docenteLogado, repositorio);

        view.mostrarMensagem("Média de notas nas suas UCs: " + mediaUcs + " valores.");
        view.mostrarMensagem("Total de alunos avaliados por si: " + totalAlunos + " alunos.");
    }
    /**
     * Permite ao Docente lançar notas a todos os alunos de uma UC de forma contínua.
     */
    private void lancarNotasEmLote() {
        if (docenteLogado.getTotalUcsLecionadas() == 0) {
            view.mostrarMensagem("Ainda não tem Unidades Curriculares atribuídas.");
            return;
        }

        // 1. ESCOLHER A UC
        view.mostrarMensagem("\n--- LANÇAMENTO CONTÍNUO DE NOTAS ---");
        for (int i = 0; i < docenteLogado.getTotalUcsLecionadas(); i++) {
            model.UnidadeCurricular uc = docenteLogado.getUcsLecionadas()[i];
            view.mostrarMensagem((i + 1) + " - " + uc.getNome() + " [" + uc.getSigla() + "]");
        }

        int escolhaUC;
        try {
            escolhaUC = Integer.parseInt(view.pedirInputString("Escolha a UC (Número)")) - 1;
        } catch (NumberFormatException e) {
            view.mostrarMensagem("Entrada inválida. Operação cancelada.");
            return;
        }

        if (escolhaUC < 0 || escolhaUC >= docenteLogado.getTotalUcsLecionadas()) {
            view.mostrarMensagem("UC inválida.");
            return;
        }

        model.UnidadeCurricular ucSelecionada = docenteLogado.getUcsLecionadas()[escolhaUC];

        // 2. OBTER A TURMA (ALUNOS INSCRITOS)
        model.Estudante[] alunosDaUC = repositorio.obterEstudantesPorUC(ucSelecionada.getSigla());

        if (alunosDaUC.length == 0) {
            view.mostrarMensagem("Não existem alunos inscritos a esta Unidade Curricular neste momento.");
            return;
        }

        view.mostrarMensagem("\n>> A iniciar lançamento de notas para " + ucSelecionada.getNome() + "...");
        view.mostrarMensagem(">> Dica: Pressione ENTER sem digitar nada para saltar um aluno.");

        int notasLancadasComSucesso = 0;

        // 3. CICLO CONTÍNUO ALUNO A ALUNO
        for (int i = 0; i < alunosDaUC.length; i++) {
            model.Estudante aluno = alunosDaUC[i];

            // Verifica logo se este aluno já tem as 3 notas máximas
            int proximaNota = aluno.obterNumeroProximaAvaliacao(ucSelecionada.getSigla());
            if (proximaNota > 3) {
                view.mostrarMensagem("\n- " + aluno.getNome() + " (" + aluno.getNumeroMecanografico() + ") -> Já tem as 3 avaliações máximas. A saltar...");
                continue;
            }


            String prompt = "\n[" + (i + 1) + "/" + alunosDaUC.length + "] Avaliação (" + proximaNota + "/3) para " + aluno.getNome() + " (ou ENTER para saltar)";

            boolean notaInseridaComSucesso = false;

            while (!notaInseridaComSucesso) {
                String input = view.pedirInputString(prompt);

                if (input.trim().isEmpty()) {
                    view.mostrarMensagem(">> Aluno saltado.");
                    break;
                }

                try {
                    double nota = Double.parseDouble(input);
                    if (nota < 0.0 || nota > 20.0) {
                        view.mostrarMensagem(">> Erro: A nota deve estar entre 0.0 e 20.0. Tente novamente.");
                    } else {
                        // Grava a nota no aluno
                        boolean sucesso = aluno.adicionarNota(ucSelecionada, nota, repositorio.getAnoAtual());
                        if (sucesso) {
                            view.mostrarMensagem(">> Sucesso! Nota de " + nota + " guardada.");
                            notasLancadasComSucesso++;
                        } else {
                            view.mostrarMensagem(">> Erro inesperado ao guardar a nota.");
                        }
                        notaInseridaComSucesso = true;
                    }

                } catch (NumberFormatException e) {
                    view.mostrarMensagem(">> Erro: Formato inválido (use . para decimais, ex: 14.5). Tente novamente.");
                }
            }
        }

        // 4. RESUMO FINAL
        view.mostrarMensagem("\n>> Pauta concluída! " + notasLancadasComSucesso + " nota(s) lançada(s) com sucesso na turma.");
    }
}
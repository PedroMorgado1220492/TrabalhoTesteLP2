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
                    mostrarEstatisticasDocente();
                    break;
                case 5:
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
            view.mostrarMensagem("Atenção: Você não tem Unidades Curriculares atribuídas.");
            return;
        }

        view.mostrarMensagem("\n--- AS MINHAS UNIDADES CURRICULARES ---");
        UnidadeCurricular[] minhasUcs = docenteLogado.getUcsLecionadas();

        for (int i = 0; i < docenteLogado.getTotalUcsLecionadas(); i++) {
            view.mostrarMensagem((i + 1) + " - " + minhasUcs[i].getNome() + " [" + minhasUcs[i].getSigla() + "]");
        }

        try {
            int escolhaUc = Integer.parseInt(view.pedirInputString("Escolha a UC (Número)")) - 1;

            if (escolhaUc < 0 || escolhaUc >= docenteLogado.getTotalUcsLecionadas()) {
                view.mostrarMensagem("Opção inválida.");
                return;
            }

            UnidadeCurricular ucSelecionada = minhasUcs[escolhaUc];
            Curso cursoDaUc = ucSelecionada.getCursos()[0];

            view.mostrarMensagem("\n--- ALUNOS INSCRITOS EM " + cursoDaUc.getNome() + " ---");

            Estudante[] todosEstudantes = repositorio.getEstudantes();
            Estudante[] alunosDoCurso = new Estudante[100];
            int totalAlunosEncontrados = 0;

            for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
                if (todosEstudantes[i] != null && todosEstudantes[i].getCurso() != null &&
                        todosEstudantes[i].getCurso().getSigla().equals(cursoDaUc.getSigla())) {
                    alunosDoCurso[totalAlunosEncontrados] = todosEstudantes[i];
                    totalAlunosEncontrados++;
                    view.mostrarMensagem(totalAlunosEncontrados + " - " + todosEstudantes[i].getNome());
                }
            }

            if (totalAlunosEncontrados == 0) {
                view.mostrarMensagem("Não existem alunos inscritos neste curso.");
                return;
            }

            int escolhaAluno = Integer.parseInt(view.pedirInputString("Escolha o Aluno (Número)")) - 1;
            if (escolhaAluno < 0 || escolhaAluno >= totalAlunosEncontrados) {
                view.mostrarMensagem("Aluno inválido.");
                return;
            }

            Estudante alunoSelecionado = alunosDoCurso[escolhaAluno];
            view.mostrarMensagem("\nLançar nota para: " + alunoSelecionado.getNome());
            view.mostrarMensagem("Notas já lançadas: " + this.obterQuantidadeNotas(alunoSelecionado, ucSelecionada) + "/3");

            double nota = Double.parseDouble(view.pedirInputString("Introduza a nota (0-20)"));

            if (nota < 0 || nota > 20) {
                view.mostrarMensagem("Erro: A nota deve ser entre 0 e 20.");
            } else {
                alunoSelecionado.adicionarNota(ucSelecionada, nota, repositorio.getAnoAtual());
                view.mostrarMensagem("Sucesso! Nota registada no sistema.");
            }
        } catch (Exception e) {
            view.mostrarMensagem("Erro na introdução de dados. Operação cancelada.");
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
                case 5:
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
}
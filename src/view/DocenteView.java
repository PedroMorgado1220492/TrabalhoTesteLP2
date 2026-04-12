package view;

import model.bll.Docente;
import model.bll.Estudante;
import model.bll.UnidadeCurricular;
import java.util.Scanner;

/**
 * Interface de utilizador (View) dedicada ao perfil de Docente.
 * Gere toda a interação relacionada com a gestão de pautas, lançamento de notas,
 * consulta de dados profissionais e estatísticas de lecionação.
 */
public class DocenteView {

    private Scanner scanner;

    public DocenteView() {
        this.scanner = new Scanner(System.in);
    }

    // ---------- MENUS DE NAVEGAÇÃO ----------

    /**
     * Apresenta o menu principal da área docente.
     * @return A opção inteira selecionada.
     */
    public int mostrarMenuPrincipal() {
        System.out.println("\n=== ÁREA DO DOCENTE ===");
        System.out.println("1 - Ver Dados Pessoais e Profissionais");
        System.out.println("2 - Atualizar Dados de Perfil");
        System.out.println("3 - Lançar Nota (Aluno Específico)");
        System.out.println("4 - Lançar Notas em Lote (Turma Completa)");
        System.out.println("5 - Consultar as Minhas Estatísticas");
        System.out.println("0 - Sair / Logout");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    /**
     * Menu para edição de dados demográficos do docente.
     * @return A opção selecionada.
     */
    public int mostrarMenuAtualizarDados() {
        System.out.println("\n--- ATUALIZAR DADOS PESSOAIS ---");
        System.out.println("1 - Alterar Nome");
        System.out.println("2 - Alterar NIF");
        System.out.println("3 - Alterar Morada");
        System.out.println("4 - Alterar Password");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    // ---------- INPUTS DE DADOS ----------

    public String pedirNovoNome() { System.out.print("Introduza o novo Nome: "); return scanner.nextLine().trim(); }

    public String pedirNovoNif() { System.out.print("Introduza o novo NIF: "); return scanner.nextLine().trim(); }

    public String pedirNovaMorada() { System.out.print("Introduza a nova Morada: "); return scanner.nextLine().trim(); }

    public String pedirPassAtual() { System.out.print("Password Atual: "); return scanner.nextLine().trim(); }

    public String pedirNovaPass() { System.out.print("Nova Password: "); return scanner.nextLine().trim(); }

    public String pedirConfirmacaoPass() { System.out.print("Confirme Nova Password: "); return scanner.nextLine().trim(); }

    /**
     * Método auxiliar para garantir a leitura segura de inteiros.
     */
    private int lerOpcaoInteira() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ---------- EXIBIÇÃO E SELEÇÃO DE DADOS ----------

    /**
     * Imprime a ficha biográfica do docente.
     */
    public void mostrarFichaDocente(Docente d) {
        System.out.println("\n--- FICHA DE DOCENTE ---");
        System.out.println("Sigla Institucional : " + d.getSigla());
        System.out.println("Nome Completo       : " + d.getNome());
        System.out.println("Email Institucional : " + d.getEmail());
        System.out.println("NIF                 : " + d.getNif());
        System.out.println("Morada de Residência: " + d.getMorada());
        System.out.println("Data de Nascimento  : " + d.getDataNascimento());
        System.out.println("------------------------");
    }

    /**
     * Lista as UCs atribuídas ao docente para seleção.
     */
    public int pedirUC(UnidadeCurricular[] ucs, int total) {
        System.out.println("\n--- AS MINHAS UNIDADES CURRICULARES ---");
        for (int i = 0; i < total; i++) {
            System.out.println((i + 1) + " - " + ucs[i].getNome() + " [" + ucs[i].getSigla() + "]");
        }
        System.out.print("Escolha o número da UC: ");
        return lerOpcaoInteira() - 1;
    }

    /**
     * Lista os alunos inscritos numa UC para seleção individual.
     */
    public int pedirAluno(Estudante[] alunos, String nomeUC) {
        System.out.println("\n--- ALUNOS INSCRITOS EM " + nomeUC + " ---");
        for (int i = 0; i < alunos.length; i++) {
            System.out.println((i + 1) + " - " + alunos[i].getNome() + " (" + alunos[i].getNumeroMecanografico() + ")");
        }
        System.out.print("Escolha o número do Aluno: ");
        return lerOpcaoInteira() - 1;
    }

    /**
     * Pede a nota para um aluno específico, indicando qual a avaliação (N1, N2 ou N3).
     */
    public String pedirNotaIndividual(String nome, int numAvaliacao) {
        System.out.print("Introduza a Nota (" + numAvaliacao + "/3) para " + nome + " [0.0 - 20.0]: ");
        return scanner.nextLine().trim();
    }

    /**
     * Apresenta métricas de desempenho relativas à carga letiva do docente.
     */
    public void mostrarEstatisticas(double media, int totalAvaliados) {
        System.out.println("\n--- AS MINHAS ESTATÍSTICAS ---");
        System.out.println("Média Global das Notas Lançadas: " + media + " valores.");
        System.out.println("Total de Estudantes Avaliados  : " + totalAvaliados);
        System.out.println("------------------------------");
    }

    // ---------- FEEDBACK E MENSAGENS DE PAUTA ----------

    public void msgSaida() { System.out.println(">> A terminar sessão de Docente..."); }

    public void msgOpcaoInvalida() { System.out.println(">> Erro: Opção inválida."); }

    public void msgSucesso() { System.out.println(">> SUCESSO: Operação concluída com êxito."); }

    public void msgErroFormato() { System.out.println(">> Erro: O formato do dado introduzido é inválido."); }

    public void msgErroNotaInvalida() { System.out.println(">> Erro: A classificação deve situar-se entre 0.0 e 20.0."); }

    public void msgErroLimiteNotas() { System.out.println(">> Erro: O estudante já atingiu o limite máximo de 3 avaliações nesta UC."); }

    public void msgErroPassIncorreta() { System.out.println(">> Erro: A password atual não coincide com os nossos registos."); }

    public void msgErroPassNaoCoincidem() { System.out.println(">> Erro: A nova password e a confirmação não são idênticas."); }

    public void msgAvisoSemUCs() { System.out.println(">> AVISO: Atualmente não possui Unidades Curriculares atribuídas."); }

    public void msgAvisoTurmaVazia() { System.out.println(">> AVISO: Não existem alunos inscritos nesta turma."); }

    public void msgErroAlunoInativo() { System.out.println(">> Erro: O estudante está inativo. Não é possível lançar avaliações."); }

    // ---------- MÉTODOS PARA LANÇAMENTO EM LOTE ----------

    public void cabecalhoLote(String uc) {
        System.out.println("\n--- LANÇAMENTO DE PAUTA EM LOTE: " + uc + " ---");
        System.out.println("(Pressione ENTER sem digitar nada para saltar um aluno)");
    }

    /**
     * Input específico para a pauta rápida (lote).
     */
    public String inputNotaLote(int atual, int total, String nome, int numAvaliacao) {
        System.out.print("[" + atual + "/" + total + "] Nota para " + nome + " (" + numAvaliacao + "/3): ");
        return scanner.nextLine().trim();
    }

    public void resumoLote(int totalLancadas) {
        System.out.println(">> Processamento concluído: " + totalLancadas + " notas registadas no sistema.");
    }
}
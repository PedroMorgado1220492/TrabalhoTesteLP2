package view;

import model.bll.Docente;
import model.bll.Estudante;
import model.bll.UnidadeCurricular;
import java.util.Scanner;

public class DocenteView {

    private Scanner scanner;

    public DocenteView() {
        this.scanner = new Scanner(System.in);
    }

    // ---------- MENUS ----------
    public int mostrarMenuPrincipal() {
        System.out.println("\n=== ÁREA DO DOCENTE ===");
        System.out.println("1 - Ver Dados Pessoais");
        System.out.println("2 - Atualizar Dados");
        System.out.println("3 - Adicionar Avaliação a Aluno Específico");
        System.out.println("4 - Adicionar Avaliação à Turma");
        System.out.println("5 - Ver as Minhas Estatísticas");
        System.out.println("0 - Sair / Logout");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

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

    // ---------- INPUTS ESPECÍFICOS ----------
    public String pedirNovoNome() { System.out.print("Introduza o novo Nome: "); return scanner.nextLine().trim(); }
    public String pedirNovoNif() { System.out.print("Introduza o novo NIF: "); return scanner.nextLine().trim(); }
    public String pedirNovaMorada() { System.out.print("Introduza a nova Morada: "); return scanner.nextLine().trim(); }
    public String pedirPassAtual() { System.out.print("Password Atual: "); return scanner.nextLine().trim(); }
    public String pedirNovaPass() { System.out.print("Nova Password: "); return scanner.nextLine().trim(); }
    public String pedirConfirmacaoPass() { System.out.print("Confirme Nova Password: "); return scanner.nextLine().trim(); }

    private int lerOpcaoInteira() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ---------- EXIBIÇÃO DE DADOS ----------
    public void mostrarFichaDocente(Docente d) {
        System.out.println("\n--- FICHA DE DOCENTE ---");
        System.out.println("Sigla: " + d.getSigla());
        System.out.println("Nome: " + d.getNome());
        System.out.println("Email: " + d.getEmail());
        System.out.println("NIF: " + d.getNif());
        System.out.println("Morada: " + d.getMorada());
        System.out.println("Data: " + d.getDataNascimento());
    }

    public int pedirUC(UnidadeCurricular[] ucs, int total) {
        System.out.println("\n--- AS MINHAS UNIDADES CURRICULARES ---");
        for (int i = 0; i < total; i++) System.out.println((i + 1) + " - " + ucs[i].getNome());
        System.out.print("Escolha o número da UC: ");
        return lerOpcaoInteira() - 1;
    }

    public int pedirAluno(Estudante[] alunos, String nomeUC) {
        System.out.println("\n--- ALUNOS INSCRITOS EM " + nomeUC + " ---");
        for (int i = 0; i < alunos.length; i++) System.out.println((i + 1) + " - " + alunos[i].getNome());
        System.out.print("Escolha o número do Aluno: ");
        return lerOpcaoInteira() - 1;
    }

    public String pedirNotaIndividual(String nome, int num) {
        System.out.print("Nota (" + num + "/3) para " + nome + " (0.0 a 20.0): ");
        return scanner.nextLine().trim();
    }

    public void mostrarEstatisticas(double media, int total) {
        System.out.println("\n--- AS MINHAS ESTATÍSTICAS ---");
        System.out.println("Média das notas: " + media + " valores.");
        System.out.println("Total de alunos avaliados: " + total);
    }

    // ---------- FEEDBACK (SEM TEXTO NO CONTROLLER) ----------
    public void msgSaida() { System.out.println(">> A sair da conta de Docente..."); }
    public void msgOpcaoInvalida() { System.out.println(">> Erro: Opção inválida."); }
    public void msgSucesso() { System.out.println(">> Sucesso: Operação concluída."); }
    public void msgErroFormato() { System.out.println(">> Erro: Formato inválido."); }
    public void msgErroNotaInvalida() { System.out.println(">> Erro: A nota deve estar entre 0.0 e 20.0."); }
    public void msgErroLimiteNotas() { System.out.println(">> Erro: Aluno já atingiu as 3 notas máximas."); }
    public void msgErroPassIncorreta() { System.out.println(">> Erro: Password atual incorreta."); }
    public void msgErroPassNaoCoincidem() { System.out.println(">> Erro: As passwords não coincidem."); }
    public void msgAvisoSemUCs() { System.out.println(">> Aviso: Não tem UCs atribuídas."); }
    public void msgAvisoTurmaVazia() { System.out.println(">> Aviso: Não há alunos nesta turma."); }

    // ---------- LOTE ----------
    public void cabecalhoLote(String uc) { System.out.println("\n--- LANÇAMENTO EM LOTE: " + uc + " ---"); }
    public String inputNotaLote(int i, int total, String nome, int num) {
        System.out.print("[" + i + "/" + total + "] Nota para " + nome + " (" + num + "/3) [ENTER p/ saltar]: ");
        return scanner.nextLine().trim();
    }
    public void resumoLote(int n) { System.out.println(">> Fim da pauta. " + n + " notas lançadas."); }
}
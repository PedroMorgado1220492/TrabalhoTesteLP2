package view;

import model.bll.Avaliacao;
import model.bll.Estudante;
import java.util.Scanner;

public class EstudanteView {

    private Scanner scanner;

    public EstudanteView() {
        this.scanner = new Scanner(System.in);
    }

    // ---------- MENUS ----------

    public int mostrarMenuPrincipal() {
        System.out.println("\n=== ÁREA DO ESTUDANTE ===");
        System.out.println("1 - Ver Dados Pessoais e Académicos");
        System.out.println("2 - Atualizar Dados Pessoais");
        System.out.println("3 - Consultar Percurso Académico");
        System.out.println("4 - Tesouraria e Propinas");
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

    // ---------- INPUTS DE DADOS ----------

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

    public void mostrarDadosFicha(Estudante e) {
        System.out.println("\n--- FICHA DE ESTUDANTE ---");
        System.out.println("Nº Mecanográfico: " + e.getNumeroMecanografico());
        System.out.println("Nome: " + e.getNome());
        System.out.println("Email: " + e.getEmail());
        System.out.println("NIF: " + e.getNif());
        System.out.println("Morada: " + e.getMorada());
        System.out.println("Nascimento: " + e.getDataNascimento());
        System.out.println("1ª Inscrição: " + e.getAnoPrimeiraInscricao());
        if (e.getCurso() != null) {
            System.out.println("Curso: " + e.getCurso().getNome() + " (" + e.getCurso().getSigla() + ")");
            System.out.println("Ano de Frequência: " + e.getAnoFrequencia() + "º Ano");
        }
    }

    public void mostrarCabecalhoPercurso() { System.out.println("\n--- PERCURSO ACADÉMICO ---"); }
    public void mostrarAnoPercurso(int ano) { System.out.println("\n--- || " + ano + "º ano ||---"); }
    public void mostrarLinhaUC(String sigla, String nome, int ano, String status) {
        System.out.println(">> [" + sigla + "] " + nome + " (Ano: " + ano + "º) -> " + status);
    }

    /**
     * Centraliza a formatação dos estados das UCs (Apenas a View sabe falar)
     */
    public String formatarStatusUC(int estado, double nota) {
        double notaArredondada = Math.round(nota * 100.0) / 100.0;
        switch (estado) {
            case 1: return "Inscrito -> " + notaArredondada;
            case 2: return "Inscrito -> Sem Avaliação";
            case 3: return "Concluído -> " + notaArredondada;
            default: return "Não Inscrito";
        }
    }

    // ---------- PROPINAS ----------

    public void mostrarDetalhesPropina(double total, double pago, double divida, double[] historico, int nPagamentos, boolean estaPaga) {
        System.out.println("\n--- EXTRATO DE PROPINAS ---");
        System.out.println("Valor Total do Ano: " + total + "€");
        System.out.println("Valor Já Pago: " + pago + "€");
        System.out.println("Valor em Dívida: " + divida + "€");
        if (estaPaga) System.out.println(">> SITUAÇÃO: REGULARIZADA.");
        else System.out.println(">> SITUAÇÃO: PAGAMENTO EM FALTA.");
    }

    public int mostrarOpcoesPagamento(double divida, double prestacao) {
        System.out.println("\n--- REALIZAR PAGAMENTO ---");
        System.out.println("1 - Pagamento Integral (" + divida + "€)");
        System.out.println("2 - Pagar 1 Prestação (" + prestacao + "€)");
        System.out.println("3 - Outro valor");
        System.out.println("0 - Cancelar");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    public double pedirValorLivre() {
        System.out.print("Introduza o valor a pagar (€): ");
        try { return Double.parseDouble(scanner.nextLine()); }
        catch (Exception e) { return -1; }
    }

    // ---------- FEEDBACK ----------

    public void msgSaida() { System.out.println(">> A sair da conta de Estudante..."); }
    public void msgSucesso() { System.out.println(">> SUCESSO: Operação concluída."); }
    public void msgErroOpcao() { System.out.println(">> ERRO: Opção inválida."); }
    public void msgErroDados() { System.out.println(">> ERRO: Dados inválidos ou formato incorreto."); }
    public void msgErroPassIncorreta() { System.out.println(">> ERRO: Password atual incorreta."); }
    public void msgErroPassNaoCoincidem() { System.out.println(">> ERRO: As passwords não coincidem."); }
    public void msgErroSemCurso() { System.out.println(">> ERRO: Não tem curso associado."); }
    public void msgErroSemPropina() { System.out.println(">> ERRO: Nenhuma propina gerada para este ano."); }
}
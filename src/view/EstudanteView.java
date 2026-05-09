package view;

import model.bll.Estudante;
import model.bll.Propina;

/**
 * Interface de utilizador (View) dedicada ao perfil de Estudante.
 */
public class EstudanteView {

    public EstudanteView() { }

    // =========================================================
    // 1. MENUS DE NAVEGAÇÃO
    // =========================================================

    public int mostrarMenuPrincipal() {
        System.out.println("\n========= ÁREA DO ESTUDANTE =========");
        System.out.println("1 - Ver Ficha de Estudante");
        System.out.println("2 - Atualizar Dados Pessoais");
        System.out.println("3 - Consultar Percurso Académico");
        System.out.println("4 - Gestão de Propinas e Pagamentos");
        System.out.println("5 - Desativar Conta de Utilizador");
        System.out.println("0 - Sair (Logout)");
        System.out.print("Escolha uma opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    public int mostrarMenuAtualizarDados() {
        System.out.println("\n--- ATUALIZAR DADOS PESSOAIS ---");
        System.out.println("1 - Alterar Nome Completo");
        System.out.println("2 - Alterar NIF");
        System.out.println("3 - Alterar Morada");
        System.out.println("4 - Alterar Palavra-passe");
        System.out.println("5 - Alterar Email Pessoal");
        System.out.println("0 - Recuar");
        System.out.print("Escolha uma opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Exibe as modalidades de pagamento disponíveis.
     * @param divida Valor total em dívida.
     * @param prestacao Valor da prestação mínima.
     * @return Opção escolhida (1, 2, 3 ou 0).
     */
    public int mostrarOpcoesPagamento(double divida, double prestacao) {
        System.out.println("\n--- LIQUIDAÇÃO DE VALORES ---");
        System.out.printf("1 - Pagamento Integral (%.2f€)\n", divida);
        System.out.printf("2 - Pagar Prestação Mínima (%.2f€)\n", prestacao);
        System.out.println("3 - Introduzir Valor Personalizado");
        System.out.println("0 - Cancelar Operação");
        System.out.print("Escolha uma opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    // =========================================================
    // 2. INPUTS DE DADOS (FORMULÁRIOS)
    // =========================================================

    public String pedirNovoNome() { return utils.Consola.lerString("Novo Nome Completo: "); }
    public String pedirNovoNif() { return utils.Consola.lerString("Novo NIF: "); }
    public String pedirNovaMorada() { return utils.Consola.lerString("Nova Morada: "); }
    public String pedirPassAtual() { return utils.Consola.lerString("Palavra-passe Atual: "); }
    public String pedirNovaPass() { return utils.Consola.lerString("Nova Palavra-passe: "); }
    public String pedirConfirmacaoPass() { return utils.Consola.lerString("Confirme a Nova Palavra-passe: "); }
    public double pedirValorLivre() { return utils.Consola.lerDouble("Montante a liquidar (€): "); }
    public String pedirNovoEmailPessoal(String atual) { return utils.Consola.lerString("Novo Email Pessoal (Atual: " + atual + ") [Enter p/ manter]: "); }
    public boolean pedirConfirmacaoDesativacao() {
        System.out.println("\n[AVISO] A desativação é imediata. Perderá o acesso a todos os serviços.");
        return utils.Consola.lerString("Deseja mesmo desativar a sua conta? (S/N): ").equalsIgnoreCase("S");
    }

    // =========================================================
    // 3. EXIBIÇÃO DE DADOS ACADÉMICOS E RELATÓRIOS
    // =========================================================

    public void mostrarDadosFicha(Estudante e) {
        System.out.println("\n---------- FICHA DE ESTUDANTE ----------");
        System.out.println("Nº Mecanográfico : " + e.getNumeroMecanografico());
        System.out.println("Nome Completo    : " + e.getNome());
        System.out.println("Email Instituc.  : " + e.getEmail());
        System.out.println("NIF              : " + e.getNif());
        System.out.println("Morada           : " + e.getMorada());
        System.out.println("Data Nascimento  : " + e.getDataNascimento());
        System.out.println("Ano de Ingresso  : " + e.getAnoPrimeiraInscricao());
        if (e.getCurso() != null) {
            System.out.println("Curso            : " + e.getCurso().getNome() + " (" + e.getCurso().getSigla() + ")");
            System.out.println("Ano de Frequência: " + e.getAnoFrequencia() + "º Ano");
        }
        System.out.println("----------------------------------------");
    }
    public void mostrarCabecalhoPercurso() { System.out.println("\n============= REGISTO ACADÉMICO GLOBAL ============="); }
    public void mostrarAnoPercurso(int ano) { System.out.println("\n--- [ " + ano + "º Ano Curricular ] ---"); }
    public void mostrarLinhaUC(String sigla, String nome, int ano, String status) { System.out.printf(">> [%-6s] %-30s | Status: %s\n", sigla, nome, status); }
    public String formatarStatusUC(int estado, double nota) {
        return switch (estado) {
            case 1 -> String.format("Em Curso (Inscrito) -> Média Atual: %.2f", nota);
            case 2 -> "Inscrito -> Aguarda Avaliação";
            case 3 -> String.format("Concluído -> Nota Final: %.2f", nota);
            default -> "Não Inscrito / Pendente";
        };
    }
    public void mostrarMediaGlobal(double media) {
        System.out.println("\n-----------------------------------------------------");
        System.out.printf(">> MÉDIA GLOBAL ESTIMADA: %.2f Valores\n", media);
        System.out.println("-----------------------------------------------------");
    }
    public void mostrarAnoFrequencia(int ano) {
        System.out.println(">> Ano de Frequência Atual: " + ano + "º Ano");
        System.out.println("=================================================");
    }

    // =========================================================
    // 4. EXIBIÇÃO FINANCEIRA (EXTRATO E HISTÓRICO)
    // =========================================================

    public void mostrarExtratoPropinas(int anoAtual, double valorAnual, double dividaTotal) {
        double dividaAnualExibicao = valorAnual;
        double dividaAnteriorExibicao = dividaTotal - dividaAnualExibicao;
        double valorPago = valorAnual + dividaAnteriorExibicao - dividaTotal;
        if (dividaAnteriorExibicao < 0) dividaAnteriorExibicao = 0;
        System.out.print("\n");
        System.out.println("\n--------- EXTRATO FINANCEIRO ----------");
        System.out.printf("Propina do Ano %d : %.2f€\n", anoAtual, valorAnual);
        System.out.printf("Dívida Anos Anteriores: %.2f€\n", dividaAnteriorExibicao);
        System.out.printf("Total Pago: %.2f€\n", valorPago);
        System.out.printf("Total por Pagar: %.2f€\n", dividaTotal);
        System.out.println("---------------------------------------");
    }
    public void mostrarHistoricoPagamentos(Propina.Pagamento[] pagamentos) {
        if (pagamentos == null || pagamentos.length == 0) {
            System.out.println("\n------- HISTÓRICO DE PAGAMENTOS -------");
            System.out.println(">> Nenhum pagamento registado.");
            return;
        }
        System.out.println("\n------- HISTÓRICO DE PAGAMENTOS -------");
        System.out.printf("%-10s %-12s %-15s\n", "Ano Letivo", "Valor (€)", "Data");
        System.out.println("---------------------------------------");
        for (Propina.Pagamento p : pagamentos) System.out.printf("%-10d %-12.2f %-15s\n", p.getAnoLetivo(), p.getValor(), p.getData());
        System.out.println("----------------------------------------");
    }

    // =========================================================
    // 5. MENSAGENS DE FEEDBACK E SISTEMA
    // =========================================================

    public void msgSucesso() { System.out.println(">> Sucesso: Alteração registada no sistema."); }
    public void msgPercursoAtualizado() { System.out.println(">> Conta reativada com sucesso! Agora pode progredir no próximo ano letivo."); }
    public void msgNotificacaoEnviada() { System.out.println(">> Recibo enviado para o seu email pessoal."); }
    public void msgErroOpcao() { System.out.println(">> Erro: Opção inválida."); }
    public void msgErroDados() { System.out.println(">> Erro: Formato de dados incorreto ou inválido."); }
    public void msgErroPassIncorreta() { System.out.println(">> Erro: A palavra-passe atual não coincide."); }
    public void msgErroPassNaoCoincidem() { System.out.println(">> Erro: A confirmação da palavra-passe falhou."); }
    public void msgErroSemCurso() { System.out.println(">> Erro: Não possui curso associado (Processo pendente)."); }
    public void msgErroValorSuperiorDivida() { System.out.println(">> Erro: O valor do pagamento não pode ser superior ao montante em dívida."); }
    public void msgErroValorMinimo(double valorMinimo) { System.out.printf("\nErro: Montante insuficiente. O pagamento mínimo aceite é de %.2f€.\n", valorMinimo); }
    public void msgEstudanteProgrediu(int novoAno) { System.out.println(">> Estudante progrediu para o " + novoAno + "º ano."); }
    public void msgReinscricaoNaoPossivel() { System.out.println(">> Dívidas de anos anteriores pagas, mas não foi possível reinscrever (verifique se o aluno está ativo)."); }
    public void mostrarCancelamento(String menuDestino) { System.out.println("\n>> Ação cancelada. A regressar ao menu " + menuDestino + "..."); }
    public void msgSaida() { System.out.println(">> Sessão terminada. Até à próxima!"); }
    public void msgContaDesativada() { System.out.println(">> Conta desativada com sucesso. A encerrar aplicação..."); }
    public void msgFalhaEnvioEmail() { System.out.println(">> Falha no envio do recibo por email. Contacte o suporte."); }
    public void msgReciboNaoEnviado() { System.out.println(">> Recibo gerado mas não foi possível enviar por email (endereço inválido)."); }
}
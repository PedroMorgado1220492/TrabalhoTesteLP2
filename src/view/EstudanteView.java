package view;

import model.bll.Estudante;
import model.bll.Propina;

/**
 * Interface de utilizador (View) dedicada ao perfil de Estudante.
 * <p>
 * No padrão MVC, esta classe é responsável pela apresentação de dados académicos
 * e financeiros ao aluno, bem como pela captura de inputs para atualizações de perfil
 * e processamento de pagamentos.
 * </p>
 */
public class EstudanteView {

    /**
     * Construtor por defeito da EstudanteView.
     */
    public EstudanteView() { }

    // =========================================================
    // 1. MENUS DE NAVEGAÇÃO
    // =========================================================

    /**
     * Apresenta o menu principal da área reservada ao aluno.
     * @return A opção selecionada.
     */
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

    /**
     * Apresenta o submenu dedicado à edição de dados biográficos.
     * @return A opção selecionada.
     */
    public int mostrarMenuAtualizarDados() {
        System.out.println("\n--- ATUALIZAR DADOS PESSOAIS ---");
        System.out.println("1 - Alterar Nome Completo");
        System.out.println("2 - Alterar NIF");
        System.out.println("3 - Alterar Morada");
        System.out.println("4 - Alterar Palavra-passe");
        System.out.println("5 - Alterar Email Pessoal");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Exibe as modalidades de pagamento disponíveis para a propina corrente.
     * @param divida Valor total remanescente em dívida.
     * @param prestacao Valor da prestação mínima calculada pelo sistema.
     * @return A opção de pagamento selecionada.
     */
    public int mostrarOpcoesPagamento(double divida, double prestacao) {
        System.out.println("\n--- LIQUIDAÇÃO DE VALORES ---");
        System.out.printf("1 - Pagamento Integral (%.2f€)\n", divida);
        System.out.printf("2 - Pagar Prestação Mínima (%.2f€)\n", prestacao);
        System.out.println("3 - Introduzir Valor Personalizado");
        System.out.println("0 - Cancelar Operação");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    // =========================================================
    // 2. INPUTS DE DADOS (FORMULÁRIOS)
    // =========================================================

    /** Solicita o novo nome completo. */
    public String pedirNovoNome() {
        return utils.Consola.lerString("Novo Nome Completo: ");
    }

    /** Solicita o novo NIF (9 dígitos). */
    public String pedirNovoNif() {
        return utils.Consola.lerString("Novo NIF: ");
    }

    /** Solicita a nova morada. */
    public String pedirNovaMorada() {
        return utils.Consola.lerString("Nova Morada: ");
    }

    /** Solicita a palavra‑passe atual. */
    public String pedirPassAtual() {
        return utils.Consola.lerString("Palavra-passe Atual: ");
    }

    /** Solicita a nova palavra‑passe. */
    public String pedirNovaPass() {
        return utils.Consola.lerString("Nova Palavra-passe: ");
    }

    /** Solicita a confirmação da nova palavra‑passe. */
    public String pedirConfirmacaoPass() {
        return utils.Consola.lerString("Confirme a Nova Palavra-passe: ");
    }

    /** Solicita um valor de pagamento personalizado. */
    public double pedirValorLivre() {
        return utils.Consola.lerDouble("Montante a liquidar (€): ");
    }

    /**
     * Solicita o novo email pessoal, mostrando o email atual.
     * @param atual Email pessoal atual.
     * @return Novo email (ou vazio se o utilizador só premir Enter).
     */
    public String pedirNovoEmailPessoal(String atual) {
        return utils.Consola.lerString("Novo Email Pessoal (Atual: " + atual + ") [Enter p/ manter]: ");
    }

    /**
     * Solicita confirmação para desativação da conta.
     * @return true se o utilizador confirmar com 'S'/'s', false caso contrário.
     */
    public boolean pedirConfirmacaoDesativacao() {
        System.out.println("\n[AVISO] A desativação é imediata. Perderá o acesso a todos os serviços.");
        String input = utils.Consola.lerString("Deseja mesmo desativar a sua conta? (S/N): ");
        return input.equalsIgnoreCase("S");
    }

    // =========================================================
    // 3. EXIBIÇÃO DE DADOS ACADÉMICOS E RELATÓRIOS
    // =========================================================

    /**
     * Imprime a ficha detalhada com os dados biográficos e escolares do aluno.
     * @param e Instância do Estudante logado.
     */
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

    /** Cabeçalho do percurso académico global. */
    public void mostrarCabecalhoPercurso() {
        System.out.println("\n============= REGISTO ACADÉMICO GLOBAL =============");
    }

    /** Mostra o título de um ano curricular. */
    public void mostrarAnoPercurso(int ano) {
        System.out.println("\n--- [ " + ano + "º Ano Curricular ] ---");
    }

    /**
     * Imprime o estado de aproveitamento numa disciplina específica.
     * @param sigla Sigla da UC.
     * @param nome Nome da UC.
     * @param ano Ano curricular.
     * @param status Estado formatado.
     */
    public void mostrarLinhaUC(String sigla, String nome, int ano, String status) {
        System.out.printf(">> [%-6s] %-30s | Status: %s\n", sigla, nome, status);
    }

    /**
     * Converte códigos lógicos do Model em descrições legíveis e formatadas.
     * @param estado Código (1‑Inscrito c/ nota, 2‑Inscrito s/ nota, 3‑Concluído).
     * @param nota Classificação numérica.
     * @return String formatada para o ecrã.
     */
    public String formatarStatusUC(int estado, double nota) {
        return switch (estado) {
            case 1 -> String.format("Em Curso (Inscrito) -> Média Atual: %.2f", nota);
            case 2 -> "Inscrito -> Aguarda Avaliação";
            case 3 -> String.format("Concluído -> Nota Final: %.2f", nota);
            default -> "Não Inscrito / Pendente";
        };
    }

    /**
     * Apresenta a média final do curso calculada pelo Modelo.
     * @param media Média global do estudante.
     */
    public void mostrarMediaGlobal(double media) {
        System.out.println("\n-----------------------------------------------------");
        System.out.printf(">> MÉDIA GLOBAL ESTIMADA: %.2f Valores\n", media);
        System.out.println("-----------------------------------------------------");
    }

    /** Mostra o ano de frequência atual do estudante. */
    public void mostrarAnoFrequencia(int ano) {
        System.out.println(">> Ano de Frequência Atual: " + ano + "º Ano");
        System.out.println("=================================================");
    }

    // =========================================================
    // 4. EXIBIÇÃO FINANCEIRA (EXTRATO E HISTÓRICO)
    // =========================================================

    /**
     * Exibe o estado financeiro atual do aluno (versão antiga – manter para compatibilidade).
     * @deprecated Usar {@link #mostrarExtratoPropinas} para informação mais detalhada.
     */
    public void mostrarDetalhesPropina(double total, double pago, double divida, double[] historico, int nPagamentos, boolean estaPaga) {
        System.out.println("\n----------- EXTRATO FINANCEIRO -----------");
        System.out.printf("Valor Total Anual : %.2f€\n", total);
        System.out.printf("Montante Liquidado: %.2f€\n", pago);
        System.out.printf("Montante em Dívida: %.2f€\n", divida);
        if (estaPaga) {
            System.out.println(">> ESTADO: REGULARIZADO. Obrigado.");
        } else {
            System.out.println(">> ESTADO: PAGAMENTO PENDENTE.");
        }
        System.out.println("------------------------------------------");
    }

    /**
     * Exibe o extrato financeiro detalhado (por ano corrente e total acumulado).
     * @param anoAtual Ano letivo corrente.
     * @param valorAnual Valor total da propina para o ano atual.
     * @param pagoAnual Montante pago no ano corrente.
     * @param dividaAnual Dívida referente apenas ao ano corrente.
     * @param dividaTotal Dívida acumulada de todos os anos.
     */
    public void mostrarExtratoPropinas(int anoAtual, double valorAnual, double pagoAnual, double dividaAnual, double dividaTotal) {
        double dividaAnterior = dividaTotal - dividaAnual;
        System.out.println("\n----------- EXTRATO FINANCEIRO -----------");
        System.out.printf("Valor Total do Ano %d : %.2f€\n", anoAtual, valorAnual);
        System.out.printf("Montante Liquidado Ano Atual: %.2f€\n", pagoAnual);
        System.out.printf("Dívida do Ano Atual: %.2f€\n", dividaAnual);
        System.out.printf("Dívida Anos Anteriores: %.2f€\n", dividaAnterior);
        System.out.printf("Dívida Total: %.2f€\n", dividaTotal);
        if (dividaTotal <= 0.01) {
            System.out.println(">> ESTADO: REGULARIZADO.");
        } else {
            System.out.println(">> ESTADO: PAGAMENTO PENDENTE.");
        }
        System.out.println("------------------------------------------");
    }

    /**
     * Exibe o histórico de pagamentos (lista de Propina.Pagamento).
     * @param pagamentos Array de pagamentos ordenados cronologicamente.
     */
    public void mostrarHistoricoPagamentos(Propina.Pagamento[] pagamentos) {
        if (pagamentos == null || pagamentos.length == 0) {
            System.out.println("\n--- Histórico de Pagamentos ---");
            System.out.println(">> Nenhum pagamento registado.");
            return;
        }
        System.out.println("\n--- HISTÓRICO DE PAGAMENTOS ---");
        System.out.printf("%-10s %-12s %-15s\n", "Ano Letivo", "Valor (€)", "Data");
        System.out.println("----------------------------------------");
        for (Propina.Pagamento p : pagamentos) {
            System.out.printf("%-10d %-12.2f %-15s\n", p.getAnoLetivo(), p.getValor(), p.getData());
        }
        System.out.println("----------------------------------------");
    }

    // =========================================================
    // 5. MENSAGENS DE FEEDBACK E SISTEMA
    // =========================================================

    // --- Mensagens de sucesso ---
    /** Mensagem de sucesso genérica. */
    public void msgSucesso() {
        System.out.println(">> Sucesso: Alteração registada no sistema.");
    }

    /** Mensagem indicando que a conta foi reativada. */
    public void msgContaReativada() {
        System.out.println(">> Conta reativada com sucesso! Agora pode progredir no próximo ano letivo.");
    }

    /** Mensagem informando que o percurso foi atualizado. */
    public void msgPercursoAtualizado() {
        System.out.println(">> Percurso académico atualizado com base nas disciplinas já concluídas.");
    }

    /** Mensagem informando que o recibo foi enviado por email. */
    public void msgNotificacaoEnviada() {
        System.out.println(">> Recibo enviado para o seu email pessoal.");
    }

    // --- Mensagens de erro ---
    /** Erro: opção de menu inválida. */
    public void msgErroOpcao() {
        System.out.println(">> Erro: Opção inválida.");
    }

    /** Erro: formato de dados inválido. */
    public void msgErroDados() {
        System.out.println(">> Erro: Formato de dados incorreto ou inválido.");
    }

    /** Erro: palavra‑passe atual incorreta. */
    public void msgErroPassIncorreta() {
        System.out.println(">> Erro: A palavra-passe atual não coincide.");
    }

    /** Erro: confirmação da nova palavra‑passe falhou. */
    public void msgErroPassNaoCoincidem() {
        System.out.println(">> Erro: A confirmação da palavra-passe falhou.");
    }

    /** Erro: estudante não tem curso associado. */
    public void msgErroSemCurso() {
        System.out.println(">> Erro: Não possui curso associado (Processo pendente).");
    }

    /** Erro: valor de pagamento superior à dívida total. */
    public void msgErroValorSuperiorDivida() {
        System.out.println(">> Erro: O valor do pagamento não pode ser superior ao montante em dívida.");
    }

    /**
     * Erro: valor de pagamento inferior ao mínimo aceite.
     * @param valorMinimo Valor mínimo exigido.
     */
    public void msgErroValorMinimo(double valorMinimo) {
        System.out.printf("\nErro: Montante insuficiente. O pagamento mínimo aceite é de %.2f€.\n", valorMinimo);
    }

    // --- Mensagens de aviso / cancelamento ---
    /**
     * Aviso de cancelamento de operação.
     * @param menuDestino Nome do menu para onde se regressa.
     */
    public void mostrarCancelamento(String menuDestino) {
        System.out.println("\n>> Ação cancelada. A regressar ao menu " + menuDestino + "...");
    }

    // --- Mensagens de saída / encerramento ---
    /** Mensagem de fim de sessão. */
    public void msgSaida() {
        System.out.println(">> Sessão terminada. Até à próxima!");
    }

    /** Mensagem informando que a conta foi desativada. */
    public void msgContaDesativada() {
        System.out.println(">> Conta desativada com sucesso. A encerrar aplicação...");
    }

    // --- Mensagens de email ---
    /** Erro no envio do recibo por email. */
    public void msgFalhaEnvioEmail() {
        System.out.println(">> Falha no envio do recibo por email. Contacte o suporte.");
    }

    /** Aviso de que o recibo foi gerado mas o email não foi enviado (endereço inválido). */
    public void msgReciboNaoEnviado() {
        System.out.println(">> Recibo gerado mas não foi possível enviar por email (endereço inválido).");
    }
}
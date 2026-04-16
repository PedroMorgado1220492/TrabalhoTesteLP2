package model.bll;

/**
 * Representa a obrigação financeira (propina) anual de um estudante.
 * No padrão MVC, atua como um Model de domínio financeiro.
 * Regista o valor total devido, o montante já liquidado e mantém um histórico
 * detalhado das prestações ou pagamentos efetuados durante um específico ano letivo.
 */
public class Propina {

    // ---------- ATRIBUTOS ----------
    private int anoLetivo;
    private double valorTotal;
    private double valorPago;

    // Matriz de arquivo para o histórico de transações (prestações)
    private double[] historicoPagamentos;
    private int totalPagamentos;


    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Propina.
     * Inicializa o registo financeiro de um determinado ano letivo com o valor base a cobrar.
     * Prepara uma matriz em memória para suportar até 20 registos de pagamento (prestações) ao longo do ano.
     *
     * @param anoLetivo  O ano civil/letivo a que a propina diz respeito.
     * @param valorTotal O valor total a pagar pelo estudante nesse ano.
     */
    public Propina(int anoLetivo, double valorTotal) {
        this.anoLetivo = anoLetivo;
        this.valorTotal = valorTotal;
        this.valorPago = 0.0; // Inicia o ano letivo sem qualquer pagamento efetuado

        // Limite estrutural flexível para permitir pagamentos em múltiplas prestações
        this.historicoPagamentos = new double[20];
        this.totalPagamentos = 0;
    }


    // ---------- GETTERS SIMPLES ----------

    public int getAnoLetivo() { return anoLetivo; }
    public double getValorTotal() { return valorTotal; }
    public double getValorPago() { return valorPago; }
    public double[] getHistoricoPagamentos() { return historicoPagamentos; }
    public int getTotalPagamentos() { return totalPagamentos; }


    // ---------- SETTERS SIMPLES ----------

    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }


    // =========================================================
    // LÓGICA DE NEGÓCIO: GESTÃO FINANCEIRA E PAGAMENTOS
    // =========================================================

    /**
     * Calcula dinamicamente o montante que ainda falta liquidar à instituição.
     *
     * @return A diferença exata entre o valor total da propina e o valor que já foi pago.
     */
    public double getValorEmDivida() {
        return valorTotal - valorPago;
    }

    /**
     * Calcula a fasquia mínima financeira exigida para aceitar um pagamento em prestações.
     * Regra de Negócio: O valor mínimo de prestação aceite é 10% do valor total da propina anual.
     * * @return O valor correspondente a 10% da propina anual.
     */
    public double calcularValorMinimoPrestacao() {
        return this.valorTotal / 10.0;
    }

    /**
     * Verifica o estado global de regularização desta propina específica.
     *
     * @return true se o montante pago for igual ou superior ao montante total cobrado; false se houver dívida.
     */
    public boolean isPagaTotalmente() {
        return valorPago >= valorTotal;
    }

    /**
     * Processa e regista uma transação (pagamento total ou parcial em prestação) associada a esta propina.
     * Efetua as validações críticas para impedir fraudes, pagamentos nulos/negativos
     * ou pagamentos que excedam de forma anómala o valor em dívida.
     *
     * @param valor O montante monetário a liquidar na transação atual.
     * @return true se o pagamento for validado e registado com sucesso; false se as regras financeiras forem quebradas.
     */
    public boolean registarPagamento(double valor) {
        /*
         * Regras de Negócio e Prevenção de Inconsistências:
         * 1. O pagamento não pode ser menor ou igual a zero.
         * 2. O pagamento não pode exceder o valor total da propina.
         * * Nota Técnica: A adição de uma margem de segurança técnica (+0.01) na validação do teto
         * previne a rejeição de pagamentos legítimos causada pela imprecisão natural da linguagem Java
         * na soma flutuante de números decimais (ex: 99.9999999).
         */
        if (valor <= 0 || (valorPago + valor) > (valorTotal + 0.01)) {
            return false;
        }

        // Consolida o saldo liquidado com o novo valor
        this.valorPago += valor;

        // Arquiva o montante individual na matriz de histórico se o limite de prestações (20) não tiver sido atingido
        if (totalPagamentos < historicoPagamentos.length) {
            historicoPagamentos[totalPagamentos++] = valor;
        }

        return true;
    }
}
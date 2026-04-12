package model.bll;

/**
 * Representa a obrigação financeira (propina) anual de um estudante.
 * Regista o valor total devido, o montante já liquidado e mantém um histórico
 * detalhado das prestações ou pagamentos efetuados durante o ano letivo.
 */
public class Propina {

    // ---------- ATRIBUTOS ----------
    private int anoLetivo;
    private double valorTotal;
    private double valorPago;
    private double[] historicoPagamentos;
    private int totalPagamentos;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Propina.
     * Inicializa o registo financeiro de um determinado ano letivo com o valor base a cobrar.
     * Prepara uma matriz para suportar até 20 registos de pagamento (prestações).
     *
     * @param anoLetivo  O ano civil/letivo a que a propina diz respeito.
     * @param valorTotal O valor total a pagar pelo estudante nesse ano.
     */
    public Propina(int anoLetivo, double valorTotal) {
        this.anoLetivo = anoLetivo;
        this.valorTotal = valorTotal;
        this.valorPago = 0.0; // Inicia o ano letivo sem qualquer pagamento efetuado
        this.historicoPagamentos = new double[20]; // Limite máximo de 20 prestações por ano
        this.totalPagamentos = 0;
    }

    // ---------- GETTERS ----------

    public int getAnoLetivo() { return anoLetivo; }

    public double getValorTotal() { return valorTotal; }

    public double getValorPago() { return valorPago; }

    /**
     * Calcula dinamicamente o montante que ainda falta liquidar.
     *
     * @return A diferença entre o valor total da propina e o valor já pago.
     */
    public double getValorEmDivida() { return valorTotal - valorPago; }

    public double[] getHistoricoPagamentos() { return historicoPagamentos; }

    public int getTotalPagamentos() { return totalPagamentos; }

    // ---------- SETTERS ----------

    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Verifica o estado de regularização desta propina específica.
     *
     * @return true se o montante pago for igual ou superior ao montante total cobrado; false caso contrário.
     */
    public boolean isPagaTotalmente() {
        return valorPago >= valorTotal;
    }

    /**
     * Processa e regista uma transação (pagamento total ou parcial) associada a esta propina.
     * Efetua validações para impedir pagamentos nulos/negativos ou pagamentos que excedam o valor em dívida.
     *
     * @param valor O montante monetário liquidado na transação.
     * @return true se o pagamento for validado e registado com sucesso; false se os valores forem inválidos.
     */
    public boolean registarPagamento(double valor) {
        /*
         * Regra de Negócio e Prevenção de Erros:
         * O pagamento não pode ser menor ou igual a zero.
         * A adição de uma margem de segurança técnica (+0.01) previne rejeições de pagamento
         * legítimas causadas pela imprecisão natural do Java na soma de números decimais (dízimas).
         */
        if (valor <= 0 || (valorPago + valor) > (valorTotal + 0.01)) {
            return false;
        }

        // Atualiza o saldo liquidado
        this.valorPago += valor;

        // Regista o montante individual na matriz de histórico se existir espaço
        if (totalPagamentos < historicoPagamentos.length) {
            historicoPagamentos[totalPagamentos++] = valor;
        }
        return true;
    }
}
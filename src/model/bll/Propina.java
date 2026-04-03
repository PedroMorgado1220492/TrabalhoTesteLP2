package model.bll;

public class Propina {
    private int anoLetivo;
    private double valorTotal;
    private double valorPago;
    private double[] historicoPagamentos;
    private int totalPagamentos;

    public Propina(int anoLetivo, double valorTotal) {
        this.anoLetivo = anoLetivo;
        this.valorTotal = valorTotal;
        this.valorPago = 0.0;
        this.historicoPagamentos = new double[20]; // Máximo de 20 prestações/pagamentos
        this.totalPagamentos = 0;
    }

    public int getAnoLetivo() { return anoLetivo; }
    public double getValorTotal() { return valorTotal; }
    public double getValorPago() { return valorPago; }
    public double getValorEmDivida() { return valorTotal - valorPago; }
    public double[] getHistoricoPagamentos() { return historicoPagamentos; }
    public int getTotalPagamentos() { return totalPagamentos; }

    public void setValorTotal(double valorTotal) {this.valorTotal = valorTotal; }

    public boolean isPagaTotalmente() {
        return valorPago >= valorTotal;
    }

    /**
     * Regista um pagamento (total ou parcial).
     * Retorna false se o valor for inválido ou se ultrapassar a dívida.
     */
    public boolean registarPagamento(double valor) {
        // A margem de +0.01 evita bugs estranhos de dízimas do Java
        if (valor <= 0 || (valorPago + valor) > (valorTotal + 0.01)) {
            return false;
        }

        this.valorPago += valor;
        if (totalPagamentos < historicoPagamentos.length) {
            historicoPagamentos[totalPagamentos++] = valor;
        }
        return true;
    }
}
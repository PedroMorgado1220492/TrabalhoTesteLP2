package model.bll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        this.valorPago = 0.0;
        this.historicoPagamentos = new double[20];
        this.totalPagamentos = 0;
    }

    // ---------- GETTERS SIMPLES ----------
    public int getAnoLetivo() {
        return anoLetivo;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public double getValorPago() {
        return valorPago;
    }

    public double[] getHistoricoPagamentos() {
        return historicoPagamentos;
    }

    public int getTotalPagamentos() {
        return totalPagamentos;
    }

    // ---------- SETTERS SIMPLES ----------
    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

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
     *
     * @return O valor correspondente a 10% da propina anual.
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
        return valorPago >= valorTotal - 0.01;
    }

    /**
     * Processa e regista uma transação (pagamento total ou parcial em prestação) associada a esta propina.
     *
     * @param valor O montante monetário a liquidar na transação atual.
     * @return true se o pagamento for validado e registado com sucesso; false caso contrário.
     */
    public boolean registarPagamento(double valor) {
        if (valor <= 0 || (valorPago + valor) > (valorTotal + 0.01)) {
            return false;
        }
        this.valorPago += valor;
        if (totalPagamentos < historicoPagamentos.length) {
            historicoPagamentos[totalPagamentos++] = valor;
        }
        return true;
    }

    // =========================================================
    // MÉTODOS ESTÁTICOS PARA GESTÃO DE PAGAMENTOS (CSV)
    // =========================================================
    private static final String PAGAMENTOS_FILE = "bd/pagamentos_propinas.csv";

    private static void garantirFicheiroPagamentos() {
        File f = new File(PAGAMENTOS_FILE);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("NUM_MEC;ANO_LETIVO;VALOR_PAGO;DATA_PAGAMENTO");
            } catch (IOException e) {
            }
        }
    }

    /**
     * Regista um pagamento (adiciona uma linha ao CSV).
     */
    public static void registarPagamento(int numMec, int anoLetivo, double valor, String data) {
        garantirFicheiroPagamentos();
        try (PrintWriter pw = new PrintWriter(new FileWriter(PAGAMENTOS_FILE, true))) {
            pw.println(numMec + ";" + anoLetivo + ";" + valor + ";" + data);
        } catch (IOException e) {
        }
    }

    /**
     * Calcula o total pago por um estudante num determinado ano letivo.
     */
    public static double getTotalPago(int numMec, int ano) {
        garantirFicheiroPagamentos();
        double total = 0.0;
        try (BufferedReader br = new BufferedReader(new FileReader(PAGAMENTOS_FILE))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length >= 3 && Integer.parseInt(p[0]) == numMec && Integer.parseInt(p[1]) == ano) {
                    total += Double.parseDouble(p[2]);
                }
            }
        } catch (IOException | NumberFormatException e) {
        }
        return total;
    }

    /**
     * Calcula a dívida total de um estudante até ao ano atual (inclusive).
     * O total devido é a soma dos preços do curso ano a ano desde o ano de ingresso.
     */
    public static double calcularDividaTotal(Estudante estudante, int anoAtual) {
        double totalDevido = 0.0;
        for (int ano = estudante.getAnoPrimeiraInscricao(); ano <= anoAtual; ano++) {
            totalDevido += Curso.obterPrecoCurso(estudante.getCurso().getSigla(), ano);
        }
        double totalPago = 0.0;
        for (int ano = estudante.getAnoPrimeiraInscricao(); ano <= anoAtual; ano++) {
            totalPago += getTotalPago(estudante.getNumeroMecanografico(), ano);
        }
        return totalDevido - totalPago;
    }

    public static boolean temDividas(Estudante estudante, int anoAtual) {
        return calcularDividaTotal(estudante, anoAtual) > 0.01;
    }

    // ---------- PAGAMENTO (CLASSE INTERNA PARA HISTÓRICO) ----------
    public static class Pagamento {
        private int anoLetivo;
        private double valor;
        private String data;

        public Pagamento(int anoLetivo, double valor, String data) {
            this.anoLetivo = anoLetivo;
            this.valor = valor;
            this.data = data;
        }

        public int getAnoLetivo() {
            return anoLetivo;
        }

        public double getValor() {
            return valor;
        }

        public String getData() {
            return data;
        }
    }

    /**
     * Obtém a lista de todos os pagamentos registados para um estudante, ordenados por ano e data.
     *
     * @param numMec Número mecanográfico do estudante.
     * @return Lista de pagamentos.
     */
    public static Pagamento[] getPagamentos(int numMec) {
        garantirFicheiroPagamentos();
        // Primeira passagem: contar quantos pagamentos existem
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(PAGAMENTOS_FILE))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length >= 4 && Integer.parseInt(p[0]) == numMec) {
                    count++;
                }
            }
        } catch (IOException | NumberFormatException e) {
        }

        // Alocar array do tamanho certo
        Pagamento[] pagamentos = new Pagamento[count];

        // Segunda passagem: preencher o array
        int idx = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(PAGAMENTOS_FILE))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length >= 4 && Integer.parseInt(p[0]) == numMec) {
                    int ano = Integer.parseInt(p[1]);
                    double valor = Double.parseDouble(p[2]);
                    String data = p[3];
                    pagamentos[idx++] = new Pagamento(ano, valor, data);
                }
            }
        } catch (IOException | NumberFormatException e) {
        }
        return pagamentos;
    }
}
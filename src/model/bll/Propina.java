package model.bll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe utilitária para gestão de pagamentos e cálculo de dívidas de propinas.
 * Toda a persistência é feita através do ficheiro CSV "pagamentos_propinas.csv".
 *
 * @see model.dal.ImportadorCSV
 * @see model.dal.ExportadorCSV
 */
public class Propina {

    // =========================================================
    // CONSTANTES E MÉTODOS DE ACESSO AO FICHEIRO
    // =========================================================
    private static final String PAGAMENTOS_FILE = "bd/pagamentos_propinas.csv";

    private static void garantirFicheiroPagamentos() {
        File f = new File(PAGAMENTOS_FILE);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("NUM_MEC;ANO_LETIVO;VALOR_PAGO;DATA_PAGAMENTO");
            } catch (IOException e) { }
        }
    }



    // =========================================================
    // OPERAÇÕES DE REGISTO E CONSULTA DE PAGAMENTOS
    // =========================================================

    /**
     * Regista um pagamento (adiciona uma linha ao CSV).
     */
    public static void registarPagamento(int numMec, int anoLetivo, double valor, String data) {
        garantirFicheiroPagamentos();
        try (PrintWriter pw = new PrintWriter(new FileWriter(PAGAMENTOS_FILE, true))) {
            // Garantir que o registo começa numa nova linha
            pw.println();
            pw.print(numMec + ";" + anoLetivo + ";" + valor + ";" + data);
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (IOException | NumberFormatException e) { }
        return total;
    }

    /**
     * Obtém todos os pagamentos registados para um estudante, ordenados cronologicamente.
     * @return Array de {@link Pagamento} (vazio se não existirem)
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
                if (p.length >= 4 && Integer.parseInt(p[0]) == numMec) count++;
            }
        } catch (IOException | NumberFormatException e) { }

        Pagamento[] pagamentos = new Pagamento[count];
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
        } catch (IOException | NumberFormatException e) { }
        return pagamentos;
    }

    /**
     * Adiciona uma multa (pagamento negativo) para aumentar a dívida.
     */
    public static void adicionarMulta(int numMec, int anoLetivo, double valorMulta, String data) {
        if (valorMulta <= 0) return;
        registarPagamento(numMec, anoLetivo, -valorMulta, data + " [MULTA]");
    }

    // =========================================================
    // CÁLCULO DE DÍVIDAS
    // =========================================================

    /**
     * Calcula a dívida total do estudante até ao ano atual (inclusive).
     * Considera todos os preços dos cursos desde o ano de ingresso e todos os pagamentos registados.
     */
    public static double calcularDividaTotal(Estudante estudante, int anoAtual) {
        double totalDevido = 0.0;
        for (int ano = estudante.getAnoPrimeiraInscricao(); ano <= anoAtual; ano++) {
            totalDevido += model.dal.ImportadorCSV.obterPrecoCurso(estudante.getCurso().getSigla(), ano);
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

    /**
     * Calcula a dívida acumulada apenas até um determinado ano limite,
     * usando todos os pagamentos já efectuados (mesmo de anos posteriores) para abater as dívidas mais antigas (FIFO).
     * @param anoLimite   Último ano a considerar no valor devido (ex: 2026)
     * @param anoCorrente Ano letivo actual (ex: 2027) – usado para buscar pagamentos até essa data
     */
    public static double getDividaAteAno(Estudante estudante, int anoLimite, int anoCorrente) {
        double totalDevido = 0.0;
        for (int ano = estudante.getAnoPrimeiraInscricao(); ano <= anoLimite; ano++) {
            totalDevido += model.dal.ImportadorCSV.obterPrecoCurso(estudante.getCurso().getSigla(), ano);
        }
        double totalPagoGeral = 0.0;
        for (int ano = estudante.getAnoPrimeiraInscricao(); ano <= anoCorrente; ano++) {
            totalPagoGeral += getTotalPago(estudante.getNumeroMecanografico(), ano);
        }
        double divida = totalDevido - totalPagoGeral;
        return divida > 0 ? divida : 0.0;
    }

    public static boolean temDividasAteAno(Estudante estudante, int anoLimite, int anoCorrente) {
        return getDividaAteAno(estudante, anoLimite, anoCorrente) > 0.01;
    }

    // =========================================================
    // CLASSE AUXILIAR PARA REPRESENTAR UM PAGAMENTO
    // =========================================================
    public static class Pagamento {
        private final int anoLetivo;
        private final double valor;
        private final String data;

        public Pagamento(int anoLetivo, double valor, String data) {
            this.anoLetivo = anoLetivo;
            this.valor = valor;
            this.data = data;
        }

        public int getAnoLetivo() { return anoLetivo; }
        public double getValor() { return valor; }
        public String getData() { return data; }
    }
}
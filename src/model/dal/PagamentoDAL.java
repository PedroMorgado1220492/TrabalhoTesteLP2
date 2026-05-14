package model.dal;

import java.io.*;
import java.util.*;

/**
 * Classe DAL para gestão de Pagamentos.
 * Ficheiro: bd/pagamentos_propinas.csv
 */
public class PagamentoDAL {

    private static final String FILE_PATH = "bd/pagamentos_propinas.csv";

    /**
     * Classe interna para representar um pagamento.
     */
    public static class Pagamento {
        private int numMec;
        private int anoLetivo;
        private double valor;
        private String data;

        public Pagamento(int numMec, int anoLetivo, double valor, String data) {
            this.numMec = numMec;
            this.anoLetivo = anoLetivo;
            this.valor = valor;
            this.data = data;
        }

        public int getNumMec() { return numMec; }
        public int getAnoLetivo() { return anoLetivo; }
        public double getValor() { return valor; }
        public String getData() { return data; }
    }

    /**
     * Obtém todos os pagamentos de um estudante (lê diretamente do ficheiro).
     * @param numMec Número mecanográfico do estudante
     * @return Lista de pagamentos
     */
    public static List<Pagamento> buscarPorEstudante(int numMec) {
        List<Pagamento> lista = new ArrayList<>();
        File f = new File(FILE_PATH);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length >= 4 && Integer.parseInt(p[0]) == numMec) {
                    lista.add(new Pagamento(
                            Integer.parseInt(p[0]),
                            Integer.parseInt(p[1]),
                            Double.parseDouble(p[2]),
                            p[3]
                    ));
                }
            }
        } catch (IOException e) { }
        return lista;
    }

    /**
     * Calcula o total pago por um estudante num determinado ano letivo.
     * @param numMec Número mecanográfico do estudante
     * @param ano Ano letivo
     * @return Total pago
     */
    public static double getTotalPago(int numMec, int ano) {
        double total = 0.0;
        for (Pagamento p : buscarPorEstudante(numMec)) {
            if (p.getAnoLetivo() == ano) {
                total += p.getValor();
            }
        }
        return total;
    }

    /**
     * Adiciona um pagamento ao ficheiro.
     * @param numMec Número mecanográfico do estudante
     * @param ano Ano letivo
     * @param valor Valor pago
     * @param data Data do pagamento
     */
    public static void adicionarPagamento(int numMec, int ano, double valor, String data) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            pw.printf("%d;%d;%.2f;%s\n", numMec, ano, valor, data);
        } catch (IOException e) { }
    }
}
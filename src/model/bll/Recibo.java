package model.bll;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitária responsável pela geração de recibos de pagamento de propinas.
 * A gestão da numeração sequencial é delegada às classes DAL.
 */
public class Recibo {

    /**
     * Gera um recibo em formato .txt e regista a sua numeração no CSV.
     *
     * @param e               Estudante que efetuou o pagamento.
     * @param valorPago       Montante pago nesta transação.
     * @param totalDevido     Valor total em dívida antes deste pagamento (soma de todas as propinas desde o ingresso).
     * @param novoSaldo       Saldo devedor após o pagamento.
     * @return Caminho do ficheiro .txt gerado, ou null em caso de erro.
     */
    public static String gerarRecibo(Estudante e, double valorPago, double totalDevido, double novoSaldo) {
        // 1. Criar a pasta de recibos se não existir
        File diretorio = new File("recibos");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        // 2. Obter data atual formatada
        String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // 3. Obter próximo número sequencial e registar recibo através das classes DAL
        int numRecibo = model.dal.ImportadorCSV.obterProximoNumeroRecibo();
        String numReciboFormatado = String.format("%08d", numRecibo);
        model.dal.ExportadorCSV.registarRecibo(numReciboFormatado, e.getNumeroMecanografico());

        // 4. Caminho do ficheiro .txt
        String caminhoTxt = "recibos/recibo_" + numReciboFormatado + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(caminhoTxt))) {
            pw.println("=====================================================");
            pw.println("                 RECIBO DE PAGAMENTO                 ");
            pw.println("=====================================================");

            pw.println("O aluno " + e.getNome() + ",");
            pw.println("numero mecanografico " + e.getNumeroMecanografico() + ",");
            pw.println("morada " + e.getMorada() + ",");
            pw.println("na data " + dataAtual + ",");

            String valorPagoStr = String.format("%.2f", valorPago);
            String totalDevidoStr = String.format("%.2f", totalDevido);
            String novoSaldoStr = String.format("%.2f", novoSaldo);
            String nomeCurso = (e.getCurso() != null) ? e.getCurso().getNome() : "Desconhecido";

            pw.println("pagou " + valorPagoStr + " euros de um total de " + totalDevidoStr + " euros,");
            pw.println("do curso de " + nomeCurso + ".");
            pw.println("");
            if (novoSaldo <= 0) {
                pw.println("Situação Financeira: REGULARIZADA (0.00 euros em falta).");
            } else {
                pw.println("Falta pagar: " + novoSaldoStr + " euros.");
            }
            pw.println("=====================================================");
            pw.println("Recibo Nº " + numReciboFormatado);

            return caminhoTxt;

        } catch (IOException ex) {
            return null;
        }
    }
}
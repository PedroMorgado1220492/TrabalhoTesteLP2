package model.bll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitária responsável pela geração de recibos de pagamento de propinas.
 */
public class Recibo {

    public static String gerarRecibo(Estudante e, double valorPago, double valorTotalCurso, double valorEmFalta) {

        // 1. Criar a pasta dedicada para recibos se ela não existir
        File diretorio = new File("recibos");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        // 2. Definir a data de hoje formatada
        String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // 3. Obter o próximo número sequencial e registar no CSV
        String csvCaminho = "bd/recibos.csv";
        int numRecibo = obterProximoNumero(csvCaminho);

        // O CSV guardará: ID_Recibo ; Num_Mecanografico ; Valor_Pago ; Data
        String detalhesCSV = String.format("%.2f", valorPago).replace(",", ".") + ";" + dataAtual;
        registarNoCSV(csvCaminho, numRecibo, e.getNumeroMecanografico(), detalhesCSV, "ID_RECIBO;NUM_MECANOGRAFICO;VALOR_PAGO;DATA");

        // 4. Define o caminho do documento de texto
        String caminhoTxt = "recibos/recibo_" + numRecibo + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(caminhoTxt))) {
            pw.println("=====================================================");
            pw.println("                 RECIBO DE PAGAMENTO                 ");
            pw.println("=====================================================");

            pw.println("O aluno " + e.getNome() + ",");
            pw.println("numero mecanografico " + e.getNumeroMecanografico() + ",");
            pw.println("morada " + e.getMorada() + ",");
            pw.println("na data " + dataAtual + ",");

            // Formatamos para duas casas decimais
            String valorPagoStr = String.format("%.2f", valorPago);
            String valorTotalStr = String.format("%.2f", valorTotalCurso);
            String valorEmFaltaStr = String.format("%.2f", valorEmFalta);
            String nomeCurso = (e.getCurso() != null) ? e.getCurso().getNome() : "Desconhecido";

            pw.println("pagou " + valorPagoStr + " euros de um total de " + valorTotalStr + " euros,");
            pw.println("do curso de " + nomeCurso + ".");
            pw.println("");
            if (valorEmFalta <= 0) {
                pw.println("Situação Financeira: REGULARIZADA (0.00 euros em falta).");
            } else {
                pw.println("Falta pagar: " + valorEmFaltaStr + " euros.");
            }
            pw.println("=====================================================");
            pw.println("Recibo Nº " + numRecibo);

            return caminhoTxt;

        } catch (IOException ex) {
            return null; // Falha silenciosa
        }
    }

    // --- MÉTODOS DE CONTROLO SEQUENCIAL ---

    private static int obterProximoNumero(String caminhoArquivo) {
        int ultimoNumero = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha = br.readLine(); // Ignora o cabeçalho

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] partes = linha.split(";");
                if (partes.length > 0) {
                    try {
                        int numeroAAtual = Integer.parseInt(partes[0]);
                        if (numeroAAtual > ultimoNumero) ultimoNumero = numeroAAtual;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) { }
        return ultimoNumero + 1;
    }

    private static void registarNoCSV(String caminho, int id, int numMec, String extra, String cabecalho) {
        java.io.File ficheiro = new java.io.File(caminho);
        boolean ficheiroJaExiste = ficheiro.exists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(ficheiro, true))) {
            if (!ficheiroJaExiste) {
                pw.println(cabecalho);
            }
            pw.println(id + ";" + numMec + (extra != null ? ";" + extra : ""));
        } catch(IOException e) {}
    }
}
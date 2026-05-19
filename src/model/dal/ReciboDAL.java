package model.dal;

import java.io.*;
import java.util.*;

/**
 * Classe DAL para gestão de recibos.
 * Ficheiro: bd/recibos.csv
 */
public class ReciboDAL {

    private static final String FILE_PATH = "bd/recibos.csv";

    /**
     * Obtém o próximo número de recibo disponível.
     * @return Próximo número sequencial
     */
    public static int obterProximoNumeroRecibo() {
        int maxNumero = 0;
        File f = new File(FILE_PATH);
        if (!f.exists()) return 1;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            boolean primeiraLinha = true;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                if (primeiraLinha) {
                    primeiraLinha = false;
                    // Ignorar qualquer linha que não comece com dígito (cabeçalho)
                    if (!linha.matches("^\\d.*")) continue;
                }
                String[] p = linha.split(";");
                if (p.length >= 1) {
                    try {
                        int num = Integer.parseInt(p[0].trim());
                        if (num > maxNumero) maxNumero = num;
                    } catch (NumberFormatException e) { }
                }
            }
        } catch (IOException e) { }
        return maxNumero + 1;
    }

    /**
     * Regista um novo recibo no ficheiro CSV.
     * @param numRecibo Número do recibo (formatado)
     * @param numMec Número mecanográfico do estudante
     * @return true se o registo foi bem-sucedido
     */
    public static boolean registarRecibo(String numRecibo, int numMec) {
        File f = new File(FILE_PATH);
        File parentDir = f.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        boolean ficheiroExiste = f.exists() && f.length() > 0;

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            // Escrever cabeçalho apenas se o ficheiro é novo
            if (!ficheiroExiste) {
                pw.println("ID_RECIBO;NUM_MECANOGRAFICO;DATA_EMISSAO");
            }
            String dataAtual = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            pw.println(numRecibo + ";" + numMec + ";" + dataAtual);
            pw.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao registar recibo: " + e.getMessage());
            return false;
        }
    }
}
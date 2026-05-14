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

        // Se o ficheiro não existe, começar do 1
        if (!f.exists()) {
            return 1;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linha;
            boolean primeiraLinha = true;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                // Ignorar cabeçalho
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                String[] p = linha.split(";");
                if (p.length >= 1) {
                    try {
                        // O número do recibo está na primeira coluna
                        int num = Integer.parseInt(p[0].trim());
                        if (num > maxNumero) {
                            maxNumero = num;
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar linhas com formato inválido
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler recibos: " + e.getMessage());
            return 1;
        }

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

        boolean ficheiroExiste = f.exists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            // Se o ficheiro não existe ou está vazio, adicionar cabeçalho
            if (!ficheiroExiste || f.length() == 0) {
                pw.println("NUM_RECIBO;NUM_MEC;DATA_EMISSAO");
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
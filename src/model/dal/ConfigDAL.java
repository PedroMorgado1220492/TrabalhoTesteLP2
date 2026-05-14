package model.dal;

import java.io.*;

/**
 * Classe DAL para gestão da configuração do ano letivo.
 * Ficheiro: bd/ano.csv
 * Formato: ANO;INICIADO
 * Exemplo: 2026;false
 */
public class ConfigDAL {

    private static final String FILE_PATH = "bd/ano.csv";

    /**
     * Carrega o ano letivo atual a partir do ficheiro.
     * @return ano atual, ou 2026 se o ficheiro não existir
     */
    public int carregarAno() {
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            criarFicheiroPadrao();
            return 2026;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // ignorar cabeçalho
            String linha = br.readLine();
            if (linha != null && linha.contains(";")) {
                return Integer.parseInt(linha.split(";")[0].trim());
            }
        } catch (IOException e) {
            criarFicheiroPadrao();
        } catch (NumberFormatException e) {
            criarFicheiroPadrao();
        }
        return 2026;
    }

    /**
     * Carrega o estado do ano letivo (iniciado ou não).
     * @return true se iniciado, false caso contrário
     */
    public boolean carregarAnoIniciado() {
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            criarFicheiroPadrao();
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // ignorar cabeçalho
            String linha = br.readLine();
            if (linha != null && linha.contains(";")) {
                return Boolean.parseBoolean(linha.split(";")[1].trim());
            }
        } catch (IOException e) {
            criarFicheiroPadrao();
        }
        return false;
    }

    /**
     * Salva o ano e o estado no ficheiro.
     * @param ano Ano letivo
     * @param iniciado Estado (true se iniciado)
     */
    public void salvarAno(int ano, boolean iniciado) {
        File f = new File(FILE_PATH);
        File parentDir = f.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("ANO;INICIADO");
            pw.println(ano + ";" + iniciado);
            pw.flush();
        } catch (IOException e) {
            System.err.println("Erro ao salvar ano: " + e.getMessage());
        }
    }

    /**
     * Cria o ficheiro com valores padrão.
     */
    private void criarFicheiroPadrao() {
        File f = new File(FILE_PATH);
        File parentDir = f.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("ANO;INICIADO");
            pw.println("2026;false");
            pw.flush();
        } catch (IOException e) {
            System.err.println("Erro ao criar ficheiro ano.csv: " + e.getMessage());
        }
    }
}
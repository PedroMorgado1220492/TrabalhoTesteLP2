package model.dal;

import java.io.*;
import java.util.*;

/**
 * Classe DAL para gestão centralizada de logins (emails e passwords).
 * Ficheiro: bd/logins.csv
 * Formato: TIPO;EMAIL;PASSWORD
 */
public class LoginDAL {

    private static final String FILE_PATH = "bd/logins.csv";

    /**
     * Regista um novo utilizador no ficheiro de logins.
     * @param tipo Tipo de utilizador (ESTUDANTE, DOCENTE, GESTOR)
     * @param email Email do utilizador
     * @param passwordEncriptada Password já encriptada
     * @return true se registado com sucesso
     */
    public static boolean registarLogin(String tipo, String email, String passwordEncriptada) {
        File f = new File(FILE_PATH);
        File parentDir = f.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        boolean ficheiroExiste = f.exists() && f.length() > 0;

        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {

            // Se o ficheiro não existe ou está vazio, adicionar cabeçalho
            if (!ficheiroExiste) {
                pw.println("TIPO;EMAIL;PASSWORD");  // println adiciona quebra de linha automática
            }

            // Adicionar o novo registo
            pw.println(tipo.toUpperCase() + ";" + email + ";" + passwordEncriptada);
            pw.flush();
            return true;

        } catch (IOException e) {
            System.err.println("Erro ao registar login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtém a password encriptada para um determinado email.
     * @param email Email do utilizador
     * @return Password encriptada ou string vazia se não encontrado
     */
    public static String obterPassword(String email) {
        File f = new File(FILE_PATH);
        if (!f.exists()) return "";

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] p = linha.split(";");
                if (p.length < 3) continue;

                // Ignorar cabeçalho
                if (p[0].equalsIgnoreCase("TIPO")) continue;

                String emailFicheiro = p[1].trim();
                if (emailFicheiro.equalsIgnoreCase(email.trim())) {
                    return p[2].trim();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler login: " + e.getMessage());
        }
        return "";
    }

    /**
     * Remove um login do ficheiro (útil para testes ou remoção de utilizadores).
     * @param email Email do utilizador a remover
     * @return true se removido com sucesso
     */
    public static boolean removerLogin(String email) {
        File f = new File(FILE_PATH);
        if (!f.exists()) return false;

        List<String> linhas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            return false;
        }

        // Escrever todas as linhas exceto a do email a remover
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String linha : linhas) {
                if (linha.trim().isEmpty()) continue;
                String[] p = linha.split(";");
                if (p.length >= 2 && p[1].trim().equalsIgnoreCase(email)) {
                    continue; // Saltar esta linha
                }
                pw.println(linha);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Atualiza a password de um utilizador no ficheiro de logins.
     * @param email Email do utilizador
     * @param novaPasswordEncriptada Nova password já encriptada
     * @return true se atualizado com sucesso
     */
    public static boolean atualizarPassword(String email, String novaPasswordEncriptada) {
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            // Se o ficheiro não existe, criar com o novo registo
            return registarLogin("UTILIZADOR", email, novaPasswordEncriptada);
        }

        List<String> linhas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler logins.csv: " + e.getMessage());
            return false;
        }

        // Atualizar a linha correspondente
        boolean atualizado = false;
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String linha : linhas) {
                if (linha.trim().isEmpty()) {
                    pw.println(linha);
                    continue;
                }
                String[] p = linha.split(";");

                // Ignorar cabeçalho
                if (p[0].equalsIgnoreCase("TIPO")) {
                    pw.println(linha);
                    continue;
                }

                if (p.length >= 2 && p[1].trim().equalsIgnoreCase(email)) {
                    // Atualizar a password mantendo o tipo e email
                    String tipo = (p.length >= 1) ? p[0] : "UTILIZADOR";
                    pw.println(tipo + ";" + email + ";" + novaPasswordEncriptada);
                    atualizado = true;
                } else {
                    pw.println(linha);
                }
            }

            // Se não encontrou o email, adicionar como novo registo
            if (!atualizado) {
                pw.println("UTILIZADOR;" + email + ";" + novaPasswordEncriptada);
                atualizado = true;
            }

            return atualizado;
        } catch (IOException e) {
            System.err.println("Erro ao escrever logins.csv: " + e.getMessage());
            return false;
        }
    }
}
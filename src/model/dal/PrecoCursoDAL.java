package model.dal;

import java.io.*;
import java.util.*;

public class PrecoCursoDAL {

    private static final String FILE_PATH = "bd/cursos_precos.csv";

    /**
     * Obtém o preço de um curso para um determinado ano.
     * @param siglaCurso Sigla do curso
     * @param ano Ano letivo
     * @return Preço do curso (default 1000.0 se não encontrado)
     */
    public static double obterPrecoCurso(String siglaCurso, int ano) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length >= 3 && p[1].equalsIgnoreCase(siglaCurso) && Integer.parseInt(p[0]) == ano) {
                    return Double.parseDouble(p[2]);
                }
            }
        } catch (IOException | NumberFormatException e) { }
        return 1000.0;
    }

    /**
     * Obtém o histórico completo de preços de um curso.
     * @param siglaCurso Sigla do curso
     * @return Matriz double[ano][preco]
     */
    public static double[][] obterHistoricoPrecos(String siglaCurso) {
        // Contar quantos registos existem
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length >= 3 && p[1].equalsIgnoreCase(siglaCurso)) count++;
            }
        } catch (IOException e) { }

        double[][] historico = new double[count][2];
        int idx = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length >= 3 && p[1].equalsIgnoreCase(siglaCurso)) {
                    historico[idx][0] = Integer.parseInt(p[0]);
                    historico[idx][1] = Double.parseDouble(p[2]);
                    idx++;
                }
            }
        } catch (IOException e) { }
        return historico;
    }

    /**
     * Lê todas as linhas do ficheiro de preços (sem cabeçalho).
     * @return Array com todas as linhas
     */
    public static String[] lerTodasLinhasPrecos() {
        List<String> linhasList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            br.readLine(); // ignorar cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                linhasList.add(linha);
            }
        } catch (IOException e) {
            return new String[0];
        }
        return linhasList.toArray(new String[0]);
    }

    /**
     * NOVO MÉTODO: Escreve todas as linhas no ficheiro de preços.
     * @param linhas Array de strings com as linhas a escrever (cada linha no formato "ANO;SIGLA;PRECO")
     */
    public static void escreverFicheiroPrecos(String[] linhas) {
        File f = new File(FILE_PATH);
        // Criar diretório se não existir
        File parentDir = f.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            // Escrever cabeçalho
            pw.println("ANO;SIGLA_CURSO;PRECO");
            // Escrever as linhas de dados
            for (String linha : linhas) {
                if (linha != null && !linha.trim().isEmpty()) {
                    pw.println(linha);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever ficheiro de preços: " + e.getMessage());
        }
    }

    /**
     * NOVO MÉTODO: Adiciona um novo preço para um curso num determinado ano.
     * Se já existir um preço para esse ano, substitui-o.
     * @param siglaCurso Sigla do curso
     * @param ano Ano letivo
     * @param preco Novo preço
     * @return true se a operação foi bem-sucedida
     */
    public static boolean adicionarOuAtualizarPreco(String siglaCurso, int ano, double preco) {
        File f = new File(FILE_PATH);
        File parentDir = f.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        List<String> linhas = new ArrayList<>();
        boolean ficheiroExiste = f.exists();

        // Se o ficheiro existir, ler todas as linhas
        if (ficheiroExiste) {
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    linhas.add(linha);
                }
            } catch (IOException e) {
                return false;
            }
        }

        // Verificar se o cabeçalho existe, se não, adicionar
        boolean hasHeader = !linhas.isEmpty() && linhas.get(0).startsWith("ANO");
        if (!hasHeader && !linhas.isEmpty()) {
            // Se não tem cabeçalho mas tem dados, inserir cabeçalho no início
            linhas.add(0, "ANO;SIGLA_CURSO;PRECO");
        } else if (linhas.isEmpty()) {
            linhas.add("ANO;SIGLA_CURSO;PRECO");
        }

        // Procurar e atualizar ou adicionar
        boolean atualizado = false;
        for (int i = 1; i < linhas.size(); i++) {
            String[] p = linhas.get(i).split(";");
            if (p.length >= 2 && p[1].equalsIgnoreCase(siglaCurso) && Integer.parseInt(p[0]) == ano) {
                linhas.set(i, ano + ";" + siglaCurso.toUpperCase() + ";" + preco);
                atualizado = true;
                break;
            }
        }

        if (!atualizado) {
            linhas.add(ano + ";" + siglaCurso.toUpperCase() + ";" + preco);
        }

        // Escrever de volta para o ficheiro
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String linha : linhas) {
                pw.println(linha);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * NOVO MÉTODO: Remove todos os preços de um curso.
     * @param siglaCurso Sigla do curso
     * @return true se a operação foi bem-sucedida
     */
    public static boolean removerPrecosCurso(String siglaCurso) {
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

        // Remover linhas do curso (mantendo o cabeçalho)
        List<String> novasLinhas = new ArrayList<>();
        for (int i = 0; i < linhas.size(); i++) {
            if (i == 0) {
                novasLinhas.add(linhas.get(i)); // manter cabeçalho
            } else {
                String[] p = linhas.get(i).split(";");
                if (p.length >= 2 && !p[1].equalsIgnoreCase(siglaCurso)) {
                    novasLinhas.add(linhas.get(i));
                }
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String linha : novasLinhas) {
                pw.println(linha);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
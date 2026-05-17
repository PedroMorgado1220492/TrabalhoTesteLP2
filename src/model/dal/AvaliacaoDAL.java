package model.dal;

import model.bll.Avaliacao;
import model.bll.Estudante;
import model.bll.UnidadeCurricular;
import java.io.*;
import java.util.*;

/**
 * Classe DAL para gestão de Avaliações.
 * Ficheiro: bd/avaliacoes.csv
 */
public class AvaliacaoDAL {

    private static final String FILE_PATH = "bd/avaliacoes.csv";

    /**
     * Guarda uma nota no ficheiro CSV
     */
    public static void guardarNota(int numMec, String siglaUC, int ano, double nota) {
        File dir = new File("bd");
        if (!dir.exists()) dir.mkdirs();

        File f = new File(FILE_PATH);
        List<String> linhas = new ArrayList<>();
        boolean ficheiroExiste = f.exists() && f.length() > 0;

        // Ler linhas existentes
        if (ficheiroExiste) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    if (!linha.trim().isEmpty()) linhas.add(linha);
                }
            } catch (IOException e) { }
        }

        // Cabeçalho correto
        String cabecalho = "TIPO;NUM_MEC;SIGLA_UC;ANO;NOTA1;NOTA2;NOTA3";
        if (linhas.isEmpty()) {
            linhas.add(cabecalho);
        } else if (!linhas.get(0).equals(cabecalho)) {
            linhas.set(0, cabecalho);
        }

        // Procurar linha existente para (numMec, siglaUC, ano)
        int index = -1;
        for (int i = 1; i < linhas.size(); i++) {
            String[] p = linhas.get(i).split(";");
            if (p.length >= 4 && p[0].equals("NOTA")) {
                try {
                    if (Integer.parseInt(p[1]) == numMec && p[2].equalsIgnoreCase(siglaUC) && Integer.parseInt(p[3]) == ano) {
                        index = i;
                        break;
                    }
                } catch (NumberFormatException e) {}
            }
        }

        if (index != -1) {
            // Atualizar linha existente
            String[] p = linhas.get(index).split(";");
            // Garantir 7 colunas
            if (p.length < 7) {
                String[] novo = new String[7];
                System.arraycopy(p, 0, novo, 0, p.length);
                for (int i = p.length; i < 7; i++) novo[i] = "";
                p = novo;
            }
            // Colocar na primeira nota vazia
            for (int i = 4; i <= 6; i++) {
                if (p[i].isEmpty()) {
                    p[i] = String.format("%.1f", nota);
                    break;
                }
            }
            // Reconstruir linha
            linhas.set(index, String.join(";", p));
        } else {
            // Nova linha
            linhas.add(String.format("NOTA;%d;%s;%d;%.1f;;", numMec, siglaUC.toUpperCase(), ano, nota));
        }

        // Escrever ficheiro com quebras de linha
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (int i = 0; i < linhas.size(); i++) {
                pw.println(linhas.get(i));
            }
        } catch (IOException e) { }
    }

    /**
     * Carrega todas as avaliações e adiciona aos estudantes fornecidos
     */
    public static void carregarAvaliacoes(List<Estudante> estudantes, List<UnidadeCurricular> ucs, int anoAtual) {
        File f = new File(FILE_PATH);
        System.out.println("DEBUG: A carregar avaliações de " + f.getAbsolutePath());
        if (!f.exists()) {
            System.out.println("DEBUG: Ficheiro não existe!");
            return;
        }

        Map<Integer, Estudante> mapEst = new HashMap<>();
        for (Estudante e : estudantes) mapEst.put(e.getNumeroMecanografico(), e);

        Map<String, UnidadeCurricular> mapUc = new HashMap<>();
        for (UnidadeCurricular uc : ucs) mapUc.put(uc.getSigla().toUpperCase(), uc);

        System.out.println("DEBUG: Mapa estudantes tem " + mapEst.size() + " entradas");
        System.out.println("DEBUG: Mapa UCs tem " + mapUc.size() + " entradas");

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            boolean primeiro = true;
            int linhasLidas = 0;
            int notasAdicionadas = 0;
            while ((linha = br.readLine()) != null) {
                if (primeiro) {
                    System.out.println("DEBUG: Cabeçalho: " + linha);
                    primeiro = false;
                    continue;
                }
                if (linha.trim().isEmpty()) continue;
                linhasLidas++;
                String[] p = linha.split(";");
                if (p.length < 5 || !p[0].equals("NOTA")) {
                    System.out.println("DEBUG: Linha ignorada (não NOTA ou formato inválido): " + linha);
                    continue;
                }
                try {
                    int num = Integer.parseInt(p[1]);
                    String sigla = p[2].toUpperCase();
                    int ano = Integer.parseInt(p[3]);
                    System.out.println("DEBUG: Processando linha: num=" + num + " sigla=" + sigla + " ano=" + ano);
                    Estudante e = mapEst.get(num);
                    UnidadeCurricular uc = mapUc.get(sigla);
                    if (e == null) {
                        System.out.println("DEBUG: Estudante " + num + " não encontrado no mapa!");
                    }
                    if (uc == null) {
                        System.out.println("DEBUG: UC " + sigla + " não encontrada no mapa!");
                    }
                    if (e == null || uc == null) continue;

                    for (int i = 4; i <= 6 && i < p.length; i++) {
                        String notaStr = p[i].trim();
                        if (!notaStr.isEmpty()) {
                            notaStr = notaStr.replace(",", ".");
                            double nota = Double.parseDouble(notaStr);
                            System.out.println("DEBUG: Adicionando nota " + nota + " a " + e.getNome() + " na UC " + sigla);
                            e.adicionarNota(uc, nota, ano);
                            notasAdicionadas++;
                        }
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("DEBUG: Erro de formato na linha: " + linha + " - " + ex.getMessage());
                }
            }
            System.out.println("DEBUG: Total de linhas lidas (excluindo cabeçalho): " + linhasLidas);
            System.out.println("DEBUG: Total de notas adicionadas: " + notasAdicionadas);
        } catch (IOException e) {
            System.out.println("DEBUG: Erro de I/O: " + e.getMessage());
        }
    }
}
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
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            File f = new File(FILE_PATH);
            if (f.length() == 0) {
                pw.println("TIPO;NUM_MEC;SIGLA_UC;ANO;NOTA1;NOTA2;NOTA3");
            }
            pw.println("NOTA;" + numMec + ";" + siglaUC + ";" + ano + ";" + nota + ";;");
            pw.flush();
        } catch (IOException e) {
            System.err.println("Erro ao guardar nota: " + e.getMessage());
        }
    }

    /**
     * Carrega todas as avaliações e adiciona aos estudantes fornecidos
     */
    public static void carregarAvaliacoes(List<Estudante> estudantes, List<UnidadeCurricular> ucs, int anoAtual) {
        File f = new File(FILE_PATH);
        if (!f.exists()) return;

        // Criar mapa de estudantes por número mecanográfico para busca rápida
        Map<Integer, Estudante> mapaEstudantes = new HashMap<>();
        for (Estudante e : estudantes) {
            mapaEstudantes.put(e.getNumeroMecanografico(), e);
        }

        // Criar mapa de UCs por sigla para busca rápida
        Map<String, UnidadeCurricular> mapaUcs = new HashMap<>();
        for (UnidadeCurricular uc : ucs) {
            mapaUcs.put(uc.getSigla().toUpperCase(), uc);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            boolean primeiraLinha = true;
            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }
                if (linha.trim().isEmpty()) continue;

                String[] p = linha.split(";");
                if (p.length < 4) continue;

                String tipo = p[0];
                int numMec;
                try {
                    numMec = Integer.parseInt(p[1]);
                } catch (NumberFormatException e) {
                    continue;
                }
                String siglaUC = p[2].toUpperCase();
                int ano;
                try {
                    ano = Integer.parseInt(p[3]);
                } catch (NumberFormatException e) {
                    continue;
                }

                Estudante est = mapaEstudantes.get(numMec);
                UnidadeCurricular uc = mapaUcs.get(siglaUC);

                if (est == null || uc == null) continue;

                if (tipo.equalsIgnoreCase("NOTA")) {
                    // Adicionar cada nota individual
                    for (int i = 4; i < p.length && i <= 6; i++) {
                        if (p[i] != null && !p[i].isEmpty()) {
                            try {
                                double nota = Double.parseDouble(p[i]);
                                if (nota >= 0) {
                                    est.adicionarNota(uc, nota, ano);
                                }
                            } catch (NumberFormatException e) {}
                        }
                    }
                } else if (tipo.equalsIgnoreCase("HISTORICO")) {
                    if (p.length < 5) continue;
                    Avaliacao hist = new Avaliacao(est, uc, ano);
                    for (int i = 4; i < p.length && i <= 6; i++) {
                        if (p[i] != null && !p[i].isEmpty()) {
                            try {
                                double nota = Double.parseDouble(p[i]);
                                if (nota >= 0) hist.adicionarResultado(nota);
                            } catch (NumberFormatException e) {}
                        }
                    }
                    est.adicionarAoHistorico(hist);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler avaliações: " + e.getMessage());
        }
    }
}
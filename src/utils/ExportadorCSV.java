package utils;

import model.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ExportadorCSV {

    // ---------- CONSTRUTOR ----------
    /**
     * Construtor privado para evitar instanciação.
     * Esta é uma classe utilitária estática.
     */
    private ExportadorCSV() {}

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Guarda permanentemente os dados em memória num ficheiro físico de extensão CSV.
     * Percorre iterativamente todas as estruturas do RepositorioDados.
     * * @param caminho O caminho ou nome do ficheiro (ex: "dados.csv").
     * @param repo A instância central do RepositorioDados a ser exportada.
     */
    public static void exportarDados(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("Tipo;Coluna1;Coluna2;Coluna3;Coluna4;Coluna5;Coluna6;Coluna7;Coluna8;Coluna9");

            for (int i = 0; i < repo.getTotalGestores(); i++) {
                Gestor g = repo.getGestores()[i];
                if (g != null) {
                    pw.println("GESTOR;" + g.getEmail() + ";" + g.getPassword() + ";" + g.getNome() + ";" +
                            g.getNif() + ";" + g.getMorada() + ";" + g.getDataNascimento());
                }
            }

            for (int i = 0; i < repo.getTotalDepartamentos(); i++) {
                Departamento d = repo.getDepartamentos()[i];
                pw.println("DEPARTAMENTO;" + d.getSigla() + ";" + d.getNome());
            }

            for (int i = 0; i < repo.getTotalDocentes(); i++) {
                Docente d = repo.getDocentes()[i];
                pw.println("DOCENTE;" + d.getSigla() + ";" + d.getEmail() + ";" + d.getPassword() + ";" +
                        d.getNome() + ";" + d.getNif() + ";" + d.getMorada() + ";" + d.getDataNascimento());
            }

            for (int i = 0; i < repo.getTotalCursos(); i++) {
                Curso c = repo.getCursos()[i];
                pw.println("CURSO;" + c.getSigla() + ";" + c.getNome() + ";" + c.getDepartamento().getSigla());
            }

            for (int i = 0; i < repo.getTotalUcs(); i++) {
                UnidadeCurricular uc = repo.getUcs()[i];
                pw.println("UC;" + uc.getSigla() + ";" + uc.getNome() + ";" + uc.getAnoCurricular() + ";" +
                        uc.getDocenteResponsavel().getSigla() + ";" + uc.getCursos()[0].getSigla());
            }

            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                Estudante e = repo.getEstudantes()[i];

                String siglaCurso;
                if (e.getCurso() != null) {
                    siglaCurso = e.getCurso().getSigla();
                } else {
                    siglaCurso = "N/A";
                }

                pw.println("ESTUDANTE;" + e.getNumeroMecanografico() + ";" + e.getEmail() + ";" + e.getPassword() + ";" +
                        e.getNome() + ";" + e.getNif() + ";" + e.getMorada() + ";" + e.getDataNascimento() + ";" +
                        e.getAnoPrimeiraInscricao() + ";" + siglaCurso);
            }

            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                Estudante e = repo.getEstudantes()[i];
                for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                    Avaliacao av = e.getAvaliacoes()[j];
                    if (av != null) {
                        double[] notas = av.getResultadosAvaliacoes();
                        pw.println("NOTA;" + e.getNumeroMecanografico() + ";" + av.getUnidadeCurricular().getSigla() + ";" +
                                notas[0] + ";" + notas[1] + ";" + notas[2]);
                    }
                }
            }

            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                Estudante e = repo.getEstudantes()[i];
                for (int j = 0; j < e.getTotalHistorico(); j++) {
                    Avaliacao av = e.getHistoricoAvaliacoes()[j];
                    if (av != null) {
                        double[] notas = av.getResultadosAvaliacoes();
                        pw.println("HISTORICO;" + e.getNumeroMecanografico() + ";" + av.getUnidadeCurricular().getSigla() + ";" +
                                av.getAnoAvaliacao() + ";" + notas[0] + ";" + notas[1] + ";" + notas[2]);
                    }
                }
            }

            System.out.println(">> Dados exportados com sucesso para: " + caminho);
        } catch (IOException e) {
            System.err.println("Erro ao exportar dados: " + e.getMessage());
        }
    }
}
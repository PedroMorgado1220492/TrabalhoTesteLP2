package utils;

import model.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ExportadorCSV {

    public static void exportarDados(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            // 1. Cabeçalho
            pw.println("Tipo;Coluna1;Coluna2;Coluna3;Coluna4;Coluna5;Coluna6;Coluna7;Coluna8;Coluna9");

            // 2. Exportar Gestores
            for (int i = 0; i < repo.getTotalGestores(); i++) {
                Gestor g = repo.getGestores()[i];
                if (g != null) { // Proteção extra
                    pw.println("GESTOR;" + g.getEmail() + ";" + g.getPassword() + ";" + g.getNome() + ";" +
                            g.getNif() + ";" + g.getMorada() + ";" + g.getDataNascimento());
                }
            }

            // 3. Exportar Departamentos
            for (int i = 0; i < repo.getTotalDepartamentos(); i++) {
                Departamento d = repo.getDepartamentos()[i];
                pw.println("DEPARTAMENTO;" + d.getSigla() + ";" + d.getNome());
            }

            // 4. Exportar Docentes
            for (int i = 0; i < repo.getTotalDocentes(); i++) {
                Docente d = repo.getDocentes()[i];
                pw.println("DOCENTE;" + d.getSigla() + ";" + d.getEmail() + ";" + d.getPassword() + ";" +
                        d.getNome() + ";" + d.getNif() + ";" + d.getMorada() + ";" + d.getDataNascimento());
            }

            // 5. Exportar Cursos
            for (int i = 0; i < repo.getTotalCursos(); i++) {
                Curso c = repo.getCursos()[i];
                pw.println("CURSO;" + c.getSigla() + ";" + c.getNome() + ";" + c.getDepartamento().getSigla());
            }

            // 6. Exportar UCs
            for (int i = 0; i < repo.getTotalUcs(); i++) {
                UnidadeCurricular uc = repo.getUcs()[i];
                pw.println("UC;" + uc.getSigla() + ";" + uc.getNome() + ";" + uc.getAnoCurricular() + ";" +
                        uc.getDocenteResponsavel().getSigla() + ";" + uc.getCursos()[0].getSigla());
            }

            // 7. Exportar Estudantes (Incluindo a sigla do curso)
            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                Estudante e = repo.getEstudantes()[i];
                String siglaCurso = (e.getCurso() != null) ? e.getCurso().getSigla() : "N/A";
                pw.println("ESTUDANTE;" + e.getNumeroMecanografico() + ";" + e.getEmail() + ";" + e.getPassword() + ";" +
                        e.getNome() + ";" + e.getNif() + ";" + e.getMorada() + ";" + e.getDataNascimento() + ";" +
                        e.getAnoPrimeiraInscricao() + ";" + siglaCurso);
            }

            // 8. Exportar Notas (AVALIAÇÃO)
            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                Estudante e = repo.getEstudantes()[i];
                for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                    Avaliacao av = e.getAvaliacoes()[j];
                    if (av != null) {
                        // Formato: NOTA;numMecanografico;siglaUC;nota1;nota2;nota3
                        double[] notas = av.getResultadosAvaliacoes();
                        pw.println("NOTA;" + e.getNumeroMecanografico() + ";" + av.getUnidadeCurricular().getSigla() + ";" +
                                notas[0] + ";" + notas[1] + ";" + notas[2]);
                    }
                }
            }

            // 9. Exportar Histórico de Notas (HISTORICO)
            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                Estudante e = repo.getEstudantes()[i];
                for (int j = 0; j < e.getTotalHistorico(); j++) {
                    Avaliacao av = e.getHistoricoAvaliacoes()[j];
                    if (av != null) {
                        double[] notas = av.getResultadosAvaliacoes();
                        // Adicionamos o anoAvaliacao para sabermos de quando é a nota
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
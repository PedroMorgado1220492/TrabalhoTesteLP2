package model.dal;

import model.bll.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ExportadorCSV {

    private ExportadorCSV() {}

    /**
     * Exporta todos os dados do Repositório para os respetivos ficheiros separados.
     * @param pastaBD A diretoria onde os ficheiros vão ser guardados (ex: "bd/")
     * @param repo O RepositorioDados com os dados em memória.
     */
    public static void exportarDados(String pastaBD, RepositorioDados repo) {
        // Garantir que a pasta tem a barra no final
        if (!pastaBD.endsWith("/")) pastaBD += "/";

        java.io.File diretorio = new java.io.File(pastaBD);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        exportarLogins(pastaBD + "logins.csv", repo);
        exportarGestores(pastaBD + "gestores.csv", repo);
        exportarDepartamentos(pastaBD + "departamentos.csv", repo);
        exportarCursos(pastaBD + "cursos.csv", repo);
        exportarDocentes(pastaBD + "docentes.csv", repo);
        exportarUCs(pastaBD + "ucs.csv", repo);
        exportarEstudantes(pastaBD + "estudantes.csv", repo);
        exportarAvaliacoes(pastaBD + "avaliacoes.csv", repo);
    }

    // =========================================================
    // MÉTODOS DE EXPORTAÇÃO SEPARADOS (Try-With-Resources)
    // =========================================================

    private static void exportarLogins(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;EMAIL;PASSWORD");

            for (int i = 0; i < repo.getTotalGestores(); i++) {
                Gestor g = repo.getGestores()[i];
                if (g != null) pw.println("GESTOR;" + g.getEmail() + ";" + g.getPassword());
            }
            for (int i = 0; i < repo.getTotalDocentes(); i++) {
                Docente d = repo.getDocentes()[i];
                if (d != null) pw.println("DOCENTE;" + d.getEmail() + ";" + d.getPassword());
            }
            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                Estudante e = repo.getEstudantes()[i];
                if (e != null) pw.println("ESTUDANTE;" + e.getEmail() + ";" + e.getPassword());
            }
        } catch (IOException e) { System.out.println("Erro ao exportar logins: " + e.getMessage()); }
    }

    private static void exportarGestores(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;EMAIL;NOME;NIF;MORADA;DATANASCIMENTO");
            for (int i = 0; i < repo.getTotalGestores(); i++) {
                Gestor g = repo.getGestores()[i];
                if (g != null) {
                    pw.println("GESTOR;" + g.getEmail() + ";" + g.getNome() + ";" +
                            g.getNif() + ";" + g.getMorada() + ";" + g.getDataNascimento());
                }
            }
        } catch (IOException e) { /* Ignorar avisos silenciosos na exportação */ }
    }

    private static void exportarDepartamentos(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;SIGLA;NOME");
            for (int i = 0; i < repo.getTotalDepartamentos(); i++) {
                Departamento d = repo.getDepartamentos()[i];
                if (d != null) pw.println("DEPARTAMENTO;" + d.getSigla() + ";" + d.getNome());
            }
        } catch (IOException e) { }
    }

    private static void exportarCursos(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;SIGLA;NOME;DEPARTAMENTO");
            for (int i = 0; i < repo.getTotalCursos(); i++) {
                Curso c = repo.getCursos()[i];
                if (c != null && c.getDepartamento() != null) {
                    pw.println("CURSO;" + c.getSigla() + ";" + c.getNome() + ";" + c.getDepartamento().getSigla());
                }
            }
        } catch (IOException e) { }
    }

    private static void exportarDocentes(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;SIGLA;EMAIL;NOME;NIF;MORADA;DATANASCIMENTO");
            for (int i = 0; i < repo.getTotalDocentes(); i++) {
                Docente d = repo.getDocentes()[i];
                if (d != null) {
                    pw.println("DOCENTE;" + d.getSigla() + ";" + d.getEmail() + ";" +
                            d.getNome() + ";" + d.getNif() + ";" + d.getMorada() + ";" + d.getDataNascimento());
                }
            }
        } catch (IOException e) { }
    }

    private static void exportarUCs(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;SIGLA;NOME;ANO;DOCENTE_RESPONSAVEL;CURSO");
            for (int i = 0; i < repo.getTotalUcs(); i++) {
                UnidadeCurricular uc = repo.getUcs()[i];
                if (uc != null) {
                    String siglaDocente = (uc.getDocenteResponsavel() != null) ? uc.getDocenteResponsavel().getSigla() : "";
                    String siglaCurso = (uc.getCursos()[0] != null) ? uc.getCursos()[0].getSigla() : "";

                    pw.println("UC;" + uc.getSigla() + ";" + uc.getNome() + ";" + uc.getAnoCurricular() + ";" +
                            siglaDocente + ";" + siglaCurso);
                }
            }
        } catch (IOException e) { }
    }

    private static void exportarEstudantes(String caminho, RepositorioDados repo) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(caminho))) {
            pw.println("TIPO;NUM_MEC;EMAIL;NOME;NIF;MORADA;DATANASCIMENTO;ANO_MATRICULA;CURSO;VALOR_PROPINA_BASE;VALOR_PAGO;TOTAL_PRESTACOES;HISTORICO_PAGAMENTOS...");
            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                model.bll.Estudante e = repo.getEstudantes()[i];
                if (e != null) {
                    String siglaCurso = (e.getCurso() != null) ? e.getCurso().getSigla() : "";

                    // 1. Escrever os dados básicos e o Valor Base da Propina
                    pw.print("ESTUDANTE;" + e.getNumeroMecanografico() + ";" + e.getEmail() + ";" +
                            e.getNome() + ";" + e.getNif() + ";" + e.getMorada() + ";" + e.getDataNascimento() + ";" +
                            e.getAnoPrimeiraInscricao() + ";" + siglaCurso + ";" + e.getValorPropinaBase());

                    // 2. Escrever a Informação Financeira
                    model.bll.Propina propina = e.getPropinaDoAno(e.getAnoPrimeiraInscricao());
                    if (propina != null) {
                        pw.print(";" + propina.getValorPago() + ";" + propina.getTotalPagamentos());

                        // 3. Escrever o histórico de prestações dinamicamente
                        for (int j = 0; j < propina.getTotalPagamentos(); j++) {
                            pw.print(";" + propina.getHistoricoPagamentos()[j]);
                        }
                    } else {
                        // Se por algum motivo o aluno não tiver propina gerada, fica tudo a zero
                        pw.print(";0.0;0");
                    }
                    pw.println();
                }
            }
        } catch (java.io.IOException e) { System.out.println("Erro ao exportar estudantes: " + e.getMessage()); }
    }
    private static void exportarAvaliacoes(String caminho, RepositorioDados repo) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(caminho))) {
            pw.println("TIPO;NUM_MEC;UC;ANO_LETIVO;NOTA_NORMAL;NOTA_RECURSO;NOTA_ESPECIAL");

            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                Estudante e = repo.getEstudantes()[i];

                // Como as avaliações estão diretamente no Estudante, chamamos e.getTotalAvaliacoes()
                if (e != null) {

                    // 1. Exportar Notas do Ano Atual (NOTA)
                    for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                        Avaliacao aval = e.getAvaliacoes()[j];
                        if (aval != null) {
                            // Extrair as 3 notas do array usando getTotalAvaliacoesLancadas() e getResultadosAvaliacoes()
                            double nNormal = (aval.getTotalAvaliacoesLancadas() > 0) ? aval.getResultadosAvaliacoes()[0] : -1.0;
                            double nRecurso = (aval.getTotalAvaliacoesLancadas() > 1) ? aval.getResultadosAvaliacoes()[1] : -1.0;
                            double nEspecial = (aval.getTotalAvaliacoesLancadas() > 2) ? aval.getResultadosAvaliacoes()[2] : -1.0;

                            // A NOTA não guarda o ano no CSV (o Importador não o lê), por isso não incluímos o ano
                            pw.println("NOTA;" + e.getNumeroMecanografico() + ";" + aval.getUc().getSigla() + ";" +
                                    nNormal + ";" + nRecurso + ";" + nEspecial);
                        }
                    }

                    // 2. Exportar Histórico Antigo (HISTORICO)
                    for (int j = 0; j < e.getTotalHistorico(); j++) {
                        Avaliacao aval = e.getHistoricoAvaliacoes()[j];
                        if (aval != null) {
                            double nNormal = (aval.getTotalAvaliacoesLancadas() > 0) ? aval.getResultadosAvaliacoes()[0] : -1.0;
                            double nRecurso = (aval.getTotalAvaliacoesLancadas() > 1) ? aval.getResultadosAvaliacoes()[1] : -1.0;
                            double nEspecial = (aval.getTotalAvaliacoesLancadas() > 2) ? aval.getResultadosAvaliacoes()[2] : -1.0;

                            // O HISTÓRICO guarda o Ano da Avaliação, por isso adicionamos aval.getAnoAvaliacao()
                            pw.println("HISTORICO;" + e.getNumeroMecanografico() + ";" + aval.getUc().getSigla() + ";" +
                                    aval.getAnoAvaliacao() + ";" + nNormal + ";" + nRecurso + ";" + nEspecial);
                        }
                    }
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Erro ao exportar avaliações: " + e.getMessage());
        }
    }
}
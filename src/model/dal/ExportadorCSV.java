package model.dal;

import model.bll.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

/**
 * Classe utilitária responsável pela persistência de dados em formato CSV.
 * Implementa a camada DAL (Data Access Layer) de escrita.
 * Esta classe é responsável por serializar a árvore de objetos presente na memória
 * para ficheiros físicos no disco, garantindo a integridade referencial entre as entidades.
 */
public class ExportadorCSV {

    /**
     * Construtor privado para impedir a instanciação da classe.
     * Segue o padrão Utility Class, onde todos os métodos são estáticos.
     */
    private ExportadorCSV() {}

    /**
     * Coordena o processo global de gravação da base de dados.
     * Normaliza os caminhos de ficheiro e invoca sequencialmente os exportadores individuais.
     *
     * @param pastaBD O caminho da diretoria de destino (ex: "bd").
     * @param repo    A instância do RepositorioDados contendo as informações a salvar.
     */
    public static void exportarDados(String pastaBD, RepositorioDados repo) {
        // Normalização do delimitador de pastas
        if (!pastaBD.endsWith("/")) {
            pastaBD += "/";
        }

        // Garante a existência física da diretoria antes de tentar escrever
        File diretorio = new File(pastaBD);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        // Sequência lógica de exportação (do topo para a base)
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
    // MÉTODOS DE EXPORTAÇÃO DE INFRAESTRUTURA
    // =========================================================

    /**
     * Exporta as credenciais de acesso rápido para validação de login sem carga total.
     * Formato: TIPO;EMAIL;PASSWORD (encriptada).
     */
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
        } catch (IOException e) {
            // Falha de I/O ignorada para manter a resiliência do sistema
        }
    }

    /**
     * Exporta a estrutura administrativa dos Departamentos.
     */
    private static void exportarDepartamentos(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;SIGLA;NOME");
            for (int i = 0; i < repo.getTotalDepartamentos(); i++) {
                Departamento d = repo.getDepartamentos()[i];
                if (d != null) pw.println("DEPARTAMENTO;" + d.getSigla() + ";" + d.getNome());
            }
        } catch (IOException e) { }
    }


    // =========================================================
    // MÉTODOS DE EXPORTAÇÃO DE UTILIZADORES E CARGOS
    // =========================================================

    /**
     * Exporta os dados detalhados da equipa de Backoffice.
     */
    private static void exportarGestores(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;EMAIL;NOME;MORADA;ATIVO");
            for (int i = 0; i < repo.getTotalGestores(); i++) {
                Gestor g = repo.getGestores()[i];
                if (g != null) {
                    pw.println("GESTOR;" + g.getEmail() + ";" + g.getNome() + ";" + g.getMorada() + ";" + g.isAtivo());
                }
            }
        } catch (IOException e) { }
    }

    /**
     * Exporta a ficha profissional dos Docentes.
     */
    private static void exportarDocentes(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;SIGLA;EMAIL;NOME;NIF;MORADA;DATANASCIMENTO;EMAIL_PESSOAL;ATIVO");
            for (int i = 0; i < repo.getTotalDocentes(); i++) {
                Docente d = repo.getDocentes()[i];
                if (d != null) {
                    pw.println("DOCENTE;" + d.getSigla() + ";" + d.getEmail() + ";" +
                            d.getNome() + ";" + d.getNif() + ";" + d.getMorada() + ";" +
                            d.getDataNascimento() + ";" + d.getEmailPessoal() + ";" + d.isAtivo());
                }
            }
        } catch (IOException e) { }
    }


    // =========================================================
    // MÉTODOS DE EXPORTAÇÃO ACADÉMICA
    // =========================================================

    /**
     * Exporta a configuração dos Cursos e a sua vinculação a Departamentos.
     */
    private static void exportarCursos(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;SIGLA;NOME;DEPARTAMENTO;ATIVO");
            for (int i = 0; i < repo.getTotalCursos(); i++) {
                Curso c = repo.getCursos()[i];
                if (c != null && c.getDepartamento() != null) {
                    pw.println("CURSO;" + c.getSigla() + ";" + c.getNome() + ";" +
                            c.getDepartamento().getSigla() + ";" + c.isAtivo());
                }
            }
        } catch (IOException e) { }
    }

    /**
     * Exporta as Unidades Curriculares, incluindo a lista de cursos que as partilham.
     */
    private static void exportarUCs(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;SIGLA;NOME;ANO;DOCENTE_RESPONSAVEL;CURSOS;ATIVO;NUM_AVALIACOES");
            for (int i = 0; i < repo.getTotalUcs(); i++) {
                UnidadeCurricular uc = repo.getUcs()[i];
                if (uc != null) {
                    String siglaDoc = (uc.getDocenteResponsavel() != null) ? uc.getDocenteResponsavel().getSigla() : "";

                    // Serialização da lista de cursos vinculados (formato sigla1,sigla2,...)
                    StringBuilder listaCursos = new StringBuilder();
                    for (int j = 0; j < uc.getCursos().length; j++) {
                        if (uc.getCursos()[j] != null) {
                            if (listaCursos.length() > 0) listaCursos.append(",");
                            listaCursos.append(uc.getCursos()[j].getSigla());
                        }
                    }

                    pw.println("UC;" + uc.getSigla() + ";" + uc.getNome() + ";" + uc.getAnoCurricular() + ";" +
                            siglaDoc + ";" + listaCursos.toString() + ";" + uc.isAtivo() + ";" + uc.getNumAvaliacoes());
                }
            }
        } catch (IOException e) { }
    }

    /**
     * Exporta a ficha do Estudante e o seu estado financeiro (propinas do ano de ingresso).
     */
    private static void exportarEstudantes(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;NUM_MEC;EMAIL;NOME;NIF;MORADA;DATANASCIMENTO;ANO_MATRICULA;CURSO;EMAIL_PESSOAL;ATIVO;VALOR_PROPINA_BASE;VALOR_PAGO;TOTAL_PRESTACOES;HISTORICO_PAGAMENTOS...");

            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                try {
                    Estudante e = repo.getEstudantes()[i];
                    if (e != null) {
                        String siglaCurso = (e.getCurso() != null) ? e.getCurso().getSigla() : "";

                        pw.print("ESTUDANTE;" + e.getNumeroMecanografico() + ";" + e.getEmail() + ";" +
                                e.getNome() + ";" + e.getNif() + ";" + e.getMorada() + ";" + e.getDataNascimento() + ";" +
                                e.getAnoPrimeiraInscricao() + ";" + siglaCurso + ";" + e.getEmailPessoal() + ";" +
                                e.isAtivo() + ";" + e.getValorPropinaBase());

                        // Persistência financeira associada ao registo do aluno
                        Propina p = e.getPropinaDoAno(e.getAnoPrimeiraInscricao());
                        if (p != null) {
                            pw.print(";" + p.getValorPago() + ";" + p.getTotalPagamentos());
                            if (p.getHistoricoPagamentos() != null && p.getTotalPagamentos() > 0) {
                                for (int j = 0; j < p.getTotalPagamentos(); j++) {
                                    pw.print(";" + p.getHistoricoPagamentos()[j]);
                                }
                            }
                        } else {
                            pw.print(";0.0;0");
                        }
                        pw.println();
                    }
                } catch (Exception ex) { /* Salta aluno corrompido para proteger o ficheiro */ }
            }
        } catch (IOException e) { }
    }

    /**
     * Consolida e exporta todas as classificações atuais e históricas dos alunos.
     */
    private static void exportarAvaliacoes(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;NUM_MEC;SIGLA_UC;ANO_OU_NOTA1;NOTA2_OU_NOTA1;NOTA3_OU_NOTA2;NOTA3");

            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                Estudante e = repo.getEstudantes()[i];
                if (e == null) continue;

                // 1. Exportação do Buffer Anual
                for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                    Avaliacao av = e.getAvaliacoes()[j];
                    if (av != null && av.getUc() != null) {
                        double n1 = (av.getTotalAvaliacoesLancadas() > 0) ? av.getResultadosAvaliacoes()[0] : -1.0;
                        double n2 = (av.getTotalAvaliacoesLancadas() > 1) ? av.getResultadosAvaliacoes()[1] : -1.0;
                        double n3 = (av.getTotalAvaliacoesLancadas() > 2) ? av.getResultadosAvaliacoes()[2] : -1.0;
                        pw.println("NOTA;" + e.getNumeroMecanografico() + ";" + av.getUc().getSigla() + ";" + n1 + ";" + n2 + ";" + n3);
                    }
                }

                // 2. Exportação do Arquivo Histórico
                for (int j = 0; j < e.getTotalHistorico(); j++) {
                    Avaliacao avH = e.getHistoricoAvaliacoes()[j];
                    if (avH != null && avH.getUc() != null) {
                        double n1 = (avH.getTotalAvaliacoesLancadas() > 0) ? avH.getResultadosAvaliacoes()[0] : -1.0;
                        double n2 = (avH.getTotalAvaliacoesLancadas() > 1) ? avH.getResultadosAvaliacoes()[1] : -1.0;
                        double n3 = (avH.getTotalAvaliacoesLancadas() > 2) ? avH.getResultadosAvaliacoes()[2] : -1.0;
                        pw.println("HISTORICO;" + e.getNumeroMecanografico() + ";" + avH.getUc().getSigla() + ";" +
                                avH.getAnoAvaliacao() + ";" + n1 + ";" + n2 + ";" + n3);
                    }
                }
            }
        } catch (IOException e) { }
    }

    /**
     * Persiste o ano letivo corrente num ficheiro CSV (ano.csv).
     * O ficheiro é guardado na diretoria especificada com um cabeçalho "ANO"
     * e uma única linha com o valor do ano.
     *
     * @param pastaBD  Diretoria onde o ficheiro será guardado (ex: "bd").
     * @param anoAtual O ano letivo a ser persistido.
     */
    public static void exportarAno(String pastaBD, int anoAtual) {
        if (!pastaBD.endsWith("/")) pastaBD += "/";
        try (PrintWriter pw = new PrintWriter(new FileWriter(pastaBD + "ano.csv"))) {
            pw.println("ANO");
            pw.println(anoAtual);
        } catch (IOException e) {
            System.err.println("Erro ao guardar ano: " + e.getMessage());
        }
    }
}
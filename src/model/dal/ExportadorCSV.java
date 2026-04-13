package model.dal;

import model.bll.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe utilitária responsável pela exportação e persistência de dados em ficheiros CSV.
 * Implementa a camada de acesso a dados (DAL) para escrita, garantindo que o estado
 * dos objetos em memória é guardado de forma estruturada no disco.
 */
public class ExportadorCSV {

    /**
     * Construtor privado para impedir a instanciação da classe.
     * Sendo uma classe de utilidade, todos os métodos são estáticos.
     */
    private ExportadorCSV() {}

    /**
     * Ponto de entrada central para o processo de gravação global.
     * Coordena a exportação sequencial de todas as entidades do sistema para a diretoria especificada.
     *
     * @param pastaBD O caminho da diretoria onde os ficheiros serão armazenados (ex: "bd/").
     * @param repo    O repositório de dados contendo as instâncias a exportar.
     */
    public static void exportarDados(String pastaBD, RepositorioDados repo) {
        // Normalização do caminho da diretoria
        if (!pastaBD.endsWith("/")) {
            pastaBD += "/";
        }

        // Verificação e criação automática da diretoria caso esta não exista
        java.io.File diretorio = new java.io.File(pastaBD);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        // Execução da exportação individual por tipo de entidade
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
    // MÉTODOS DE EXPORTAÇÃO INDIVIDUAL
    // =========================================================

    /**
     * Exporta as credenciais de acesso (Email e Password Hash) de todos os perfis.
     * Este ficheiro serve como base para o mecanismo de "Login Rápido" do sistema.
     *
     * @param caminho O destino final do ficheiro logins.csv.
     * @param repo    O repositório de dados.
     */
    private static void exportarLogins(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;EMAIL;PASSWORD");

            // Processamento dos Gestores
            for (int i = 0; i < repo.getTotalGestores(); i++) {
                Gestor g = repo.getGestores()[i];
                if (g != null) pw.println("GESTOR;" + g.getEmail() + ";" + g.getPassword());
            }
            // Processamento dos Docentes
            for (int i = 0; i < repo.getTotalDocentes(); i++) {
                Docente d = repo.getDocentes()[i];
                if (d != null) pw.println("DOCENTE;" + d.getEmail() + ";" + d.getPassword());
            }
            // Processamento dos Estudantes
            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                Estudante e = repo.getEstudantes()[i];
                if (e != null) pw.println("ESTUDANTE;" + e.getEmail() + ";" + e.getPassword());
            }
        } catch (IOException e) {
            // Em caso de erro de I/O, a falha é ignorada silenciosamente para não interromper o fluxo principal
        }
    }

    /**
     * Exporta os dados administrativos e demográficos dos Gestores.
     */
    private static void exportarGestores(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;EMAIL;NOME;MORADA");
            for (int i = 0; i < repo.getTotalGestores(); i++) {
                Gestor g = repo.getGestores()[i];
                if (g != null) {
                    pw.println("GESTOR;" + g.getEmail() + ";" + g.getNome() + ";" + g.getMorada());
                }
            }
        } catch (IOException e) { }
    }

    /**
     * Exporta a lista de Departamentos registados no sistema.
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

    /**
     * Serializa a informação dos Cursos e a sua respetiva ligação aos Departamentos.
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
     * Exporta os dados pessoais e profissionais de cada Docente.
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

    /**
     * Exporta o plano curricular (UCs), mapeando os regentes e os cursos associados.
     */
    private static void exportarUCs(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;SIGLA;NOME;ANO;DOCENTE_RESPONSAVEL;CURSO;ATIVO");
            for (int i = 0; i < repo.getTotalUcs(); i++) {
                UnidadeCurricular uc = repo.getUcs()[i];
                if (uc != null) {
                    String siglaDocente = (uc.getDocenteResponsavel() != null) ? uc.getDocenteResponsavel().getSigla() : "";
                    // Assume-se a primeira posição do array de cursos para exportação simples
                    String siglaCurso = (uc.getCursos()[0] != null) ? uc.getCursos()[0].getSigla() : "";

                    pw.println("UC;" + uc.getSigla() + ";" + uc.getNome() + ";" + uc.getAnoCurricular() + ";" +
                            siglaDocente + ";" + siglaCurso + ";" + uc.isAtivo());
                }
            }
        } catch (IOException e) { }
    }


    /**
     * Exporta os dados demográficos, académicos e o estado financeiro (propinas) dos Estudantes.
     */
    private static void exportarEstudantes(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;NUM_MEC;EMAIL;NOME;NIF;MORADA;DATANASCIMENTO;ANO_MATRICULA;CURSO;EMAIL_PESSOAL;ATIVO;VALOR_PROPINA_BASE;VALOR_PAGO;TOTAL_PRESTACOES;HISTORICO_PAGAMENTOS...");

            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                // Tenta exportar o aluno. Se der erro, salta só este aluno e não apaga o ficheiro!
                try {
                    model.bll.Estudante e = repo.getEstudantes()[i];
                    if (e != null) {
                        String siglaCurso = (e.getCurso() != null) ? e.getCurso().getSigla() : "";

                        // Escrita dos dados base do estudante
                        pw.print("ESTUDANTE;" + e.getNumeroMecanografico() + ";" + e.getEmail() + ";" +
                                e.getNome() + ";" + e.getNif() + ";" + e.getMorada() + ";" + e.getDataNascimento() + ";" +
                                e.getAnoPrimeiraInscricao() + ";" + siglaCurso + ";" + e.getEmailPessoal() + ";" +
                                e.isAtivo() + ";" + e.getValorPropinaBase());

                        // Exportação dos dados da propina do ano de ingresso
                        model.bll.Propina propina = e.getPropinaDoAno(e.getAnoPrimeiraInscricao());
                        if (propina != null) {
                            pw.print(";" + propina.getValorPago() + ";" + propina.getTotalPagamentos());

                            // Verifica se o histórico não é nulo antes de imprimir
                            if (propina.getHistoricoPagamentos() != null) {
                                for (int j = 0; j < propina.getTotalPagamentos(); j++) {
                                    pw.print(";" + propina.getHistoricoPagamentos()[j]);
                                }
                            }
                        } else {
                            pw.print(";0.0;0");
                        }
                        pw.println();
                    }
                } catch (Exception ex) {
                    // Ignora silenciosamente dados corrompidos num estudante específico
                }
            }
        } catch (IOException e) { }
    }

    /**
     * Consolida todas as classificações (atuais e históricas) extraídas da árvore de objetos Estudante.
     */
    private static void exportarAvaliacoes(String caminho, RepositorioDados repo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("TIPO;NUM_MEC;SIGLA_UC;ANO_OU_NOTA1;NOTA2_OU_NOTA1;NOTA3_OU_NOTA2;NOTA3");

            for (int i = 0; i < repo.getTotalEstudantes(); i++) {
                try {
                    Estudante e = repo.getEstudantes()[i];

                    if (e != null) {
                        // 1. Exportação das Notas do Ano Corrente
                        for (int j = 0; j < e.getTotalAvaliacoes(); j++) {
                            Avaliacao aval = e.getAvaliacoes()[j];

                            // Garante que a avaliação tem uma UC real associada antes de pedir a Sigla
                            if (aval != null && aval.getUc() != null) {
                                double nota1 = (aval.getTotalAvaliacoesLancadas() > 0) ? aval.getResultadosAvaliacoes()[0] : -1.0;
                                double nota2 = (aval.getTotalAvaliacoesLancadas() > 1) ? aval.getResultadosAvaliacoes()[1] : -1.0;
                                double nota3 = (aval.getTotalAvaliacoesLancadas() > 2) ? aval.getResultadosAvaliacoes()[2] : -1.0;

                                pw.println("NOTA;" + e.getNumeroMecanografico() + ";" + aval.getUc().getSigla() + ";" +
                                        nota1 + ";" + nota2 + ";" + nota3);
                            }
                        }

                        // 2. Exportação do Histórico Permanente
                        for (int j = 0; j < e.getTotalHistorico(); j++) {
                            Avaliacao aval = e.getHistoricoAvaliacoes()[j];

                            // A mesma verificação para o Histórico!
                            if (aval != null && aval.getUc() != null) {
                                double nota1 = (aval.getTotalAvaliacoesLancadas() > 0) ? aval.getResultadosAvaliacoes()[0] : -1.0;
                                double nota2 = (aval.getTotalAvaliacoesLancadas() > 1) ? aval.getResultadosAvaliacoes()[1] : -1.0;
                                double nota3 = (aval.getTotalAvaliacoesLancadas() > 2) ? aval.getResultadosAvaliacoes()[2] : -1.0;

                                pw.println("HISTORICO;" + e.getNumeroMecanografico() + ";" + aval.getUc().getSigla() + ";" +
                                        aval.getAnoAvaliacao() + ";" + nota1 + ";" + nota2 + ";" + nota3);
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Previne que uma falha de conversão apague o ficheiro
                }
            }
        } catch (IOException e) { }
    }
}
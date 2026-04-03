package model.dal;

import model.bll.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ImportadorCSV {

    private ImportadorCSV() {}

    // =========================================================
    // 1. CARREGAMENTO AUTOMÁTICO (LOGINS)
    // =========================================================
    public static String verificarLoginRapido(String caminho, String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine(); // Saltar o cabeçalho
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                // dados[0] = TIPO, dados[1] = EMAIL, dados[2] = PASSWORD
                if (dados[1].equalsIgnoreCase(email) && dados[2].equals(password)) {
                    return dados[0].toUpperCase(); // Devolve GESTOR, DOCENTE ou ESTUDANTE
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao aceder ao ficheiro de logins: " + e.getMessage());
        }
        return null;
    }

    // =========================================================
    // 2. CARREGAMENTO MODULAR (A PEDIDO)
    // =========================================================

    public static void importarGestores(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha; br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                repositorio.adicionarGestor(new Gestor(dados[1], dados[2], dados[3], dados[4], dados[5], dados[6]));
            }
        } catch (IOException e) { System.out.println("Aviso: " + e.getMessage()); }
    }

    public static void importarDepartamentos(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha; br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                repositorio.adicionarDepartamento(new Departamento(dados[1], dados[2]));
            }
        } catch (IOException e) { System.out.println("Aviso: " + e.getMessage()); }
    }

    public static void importarDocentes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha; br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                repositorio.adicionarDocente(new Docente(dados[1], dados[2], dados[3], dados[4], dados[5], dados[6], dados[7]));
            }
        } catch (IOException e) { System.out.println("Aviso: " + e.getMessage()); }
    }

    public static void importarCursos(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha; br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                Departamento dep = procurarDepartamento(dados[3], repositorio);
                if (dep != null) {
                    Curso novoCurso = new Curso(dados[1], dados[2], dep);
                    if (repositorio.adicionarCurso(novoCurso)) {
                        dep.adicionarCurso(novoCurso);
                    }
                }
            }
        } catch (IOException e) { System.out.println("Aviso: " + e.getMessage()); }
    }

    public static void importarUCs(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha; br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                Docente doc = procurarDocente(dados[4], repositorio);
                Curso cursoUC = procurarCurso(dados[5], repositorio);
                if (doc != null && cursoUC != null) {
                    UnidadeCurricular novaUc = new UnidadeCurricular(dados[1], dados[2], Integer.parseInt(dados[3]), doc);
                    if (repositorio.adicionarUnidadeCurricular(novaUc)) {
                        cursoUC.adicionarUnidadeCurricular(novaUc);
                        novaUc.adicionarCurso(cursoUC);
                        doc.adicionarUcResponsavel(novaUc);
                        doc.adicionarUcLecionada(novaUc);
                    }
                }
            }
        } catch (IOException e) { System.out.println("Aviso: " + e.getMessage()); }
    }

    public static void importarEstudantes(String caminho, RepositorioDados repositorio) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(caminho))) {
            String linha; br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");

                model.bll.Curso cursoEst = (dados.length > 9 && !dados[9].isEmpty()) ? procurarCurso(dados[9], repositorio) : null;

                model.bll.Estudante est = new model.bll.Estudante(
                        Integer.parseInt(dados[1]), dados[2], dados[3], dados[4],
                        dados[5], dados[6], dados[7], cursoEst, Integer.parseInt(dados[8])
                );

                // --- PROPINA E PRESTAÇÕES ---
                if (dados.length > 10 && !dados[10].isEmpty()) {
                    double precoAntigo = Double.parseDouble(dados[10]);
                    est.setValorPropinaBase(precoAntigo);

                    model.bll.Propina propinaGerada = est.getPropinaDoAno(est.getAnoPrimeiraInscricao());
                    if (propinaGerada != null) {
                        propinaGerada.setValorTotal(precoAntigo);

                        // Verificar se há histórico de pagamentos na linha
                        if (dados.length > 12) {
                            int totalPrestacoes = Integer.parseInt(dados[12]);

                            // Ler cada prestação
                            for (int i = 0; i < totalPrestacoes; i++) {
                                int indexDaPrestacao = 13 + i;
                                if (indexDaPrestacao < dados.length) {
                                    double valorPrestacao = Double.parseDouble(dados[indexDaPrestacao]);

                                    // Ao registar, a propina automaticamente soma o valor pago!
                                    propinaGerada.registarPagamento(valorPrestacao);
                                }
                            }
                        }
                    }
                }

                // --- Auto-Matrícula do 1º Ano ---
                if (cursoEst != null && est.getPercursoAcademico() != null) {
                    for (int i = 0; i < cursoEst.getTotalUCs(); i++) {
                        model.bll.UnidadeCurricular uc = cursoEst.getUnidadesCurriculares()[i];
                        if (uc.getAnoCurricular() == est.getAnoFrequencia()) {
                            est.getPercursoAcademico().inscreverEmUc(uc);
                        }
                    }
                }
                repositorio.adicionarEstudante(est);
            }
        } catch (java.io.IOException e) {
            System.out.println("Aviso ao ler estudantes: " + e.getMessage());
        }
    }

    public static void importarAvaliacoes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha; br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                String tipo = dados[0].toUpperCase();

                if (tipo.equals("NOTA") || tipo.equals("HISTORICO")) {
                    int numMec = Integer.parseInt(dados[1]);
                    String siglaUC = dados[2];
                    Estudante est = procurarEstudante(numMec, repositorio);
                    UnidadeCurricular uc = procurarUC(siglaUC, repositorio);

                    if (est != null && uc != null) {
                        if (tipo.equals("NOTA")) {
                            for (int i = 3; i <= 5; i++) {
                                double valor = Double.parseDouble(dados[i]);
                                if (valor > 0) est.adicionarNota(uc, valor, repositorio.getAnoAtual());
                            }
                        } else {
                            int ano = Integer.parseInt(dados[3]);
                            Avaliacao avalAntiga = new Avaliacao(est, uc, ano);
                            for (int i = 4; i <= 6 && i < dados.length; i++) {
                                double valor = Double.parseDouble(dados[i]);
                                if (valor > 0) avalAntiga.adicionarResultado(valor);
                            }
                            est.adicionarAoHistorico(avalAntiga);
                        }
                    }
                }
            }
        } catch (IOException e) { System.out.println("Aviso: " + e.getMessage()); }
    }

    // ---------- MÉTODOS AUXILIARES PRIVADOS ----------
    private static Departamento procurarDepartamento(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalDepartamentos(); i++) {
            if (repo.getDepartamentos()[i] != null && repo.getDepartamentos()[i].getSigla().equalsIgnoreCase(sigla)) return repo.getDepartamentos()[i];
        } return null;
    }
    private static Curso procurarCurso(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalCursos(); i++) {
            if (repo.getCursos()[i] != null && repo.getCursos()[i].getSigla().equalsIgnoreCase(sigla)) return repo.getCursos()[i];
        } return null;
    }
    private static Docente procurarDocente(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalDocentes(); i++) {
            if (repo.getDocentes()[i] != null && repo.getDocentes()[i].getSigla().equalsIgnoreCase(sigla)) return repo.getDocentes()[i];
        } return null;
    }
    private static Estudante procurarEstudante(int numMec, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            if (repo.getEstudantes()[i] != null && repo.getEstudantes()[i].getNumeroMecanografico() == numMec) return repo.getEstudantes()[i];
        } return null;
    }
    private static UnidadeCurricular procurarUC(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalUcs(); i++) {
            if (repo.getUcs()[i] != null && repo.getUcs()[i].getSigla().equalsIgnoreCase(sigla)) return repo.getUcs()[i];
        } return null;
    }
}
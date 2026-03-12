package utils;

import model.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ImportadorCSV {

    public static void importarDados(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine(); // Saltar o cabeçalho

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");
                String tipo = dados[0].toUpperCase();

                switch (tipo) {
                    case "GESTOR":
                        repositorio.adicionarGestor(new Gestor(dados[1], dados[2], dados[3], dados[4], dados[5], dados[6]));
                        break;

                    case "DEPARTAMENTO":
                        // DEPARTAMENTO;sigla;nome
                        repositorio.adicionarDepartamento(new Departamento(dados[1], dados[2]));
                        break;

                    case "DOCENTE":
                        // DOCENTE;sigla;email;pass;nome;nif;morada;dataNasc
                        repositorio.adicionarDocente(new Docente(dados[1], dados[2], dados[3], dados[4], dados[5], dados[6], dados[7]));
                        break;

                    case "CURSO":
                        // CURSO;sigla;nome;siglaDep
                        Departamento dep = procurarDepartamento(dados[3], repositorio);
                        if (dep != null) {
                            repositorio.adicionarCurso(new Curso(dados[1], dados[2], dep));
                        }
                        break;

                    case "UC":
                        // UC;sigla;nome;ano;siglaDocente;siglaCurso
                        Docente doc = procurarDocente(dados[4], repositorio);
                        Curso cursoUC = procurarCurso(dados[5], repositorio);
                        if (doc != null && cursoUC != null) {
                            UnidadeCurricular novaUc = new UnidadeCurricular(dados[1], dados[2], Integer.parseInt(dados[3]), doc);
                            if (repositorio.adicionarUnidadeCurricular(novaUc)) {
                                cursoUC.adicionarUnidadeCurricular(novaUc);
                            }
                        }
                        break;

                    case "ESTUDANTE":
                        // ESTUDANTE;numMec;email;pass;nome;nif;morada;dataNasc;anoInsc;siglaCurso
                        Curso cursoEst = (dados.length > 9) ? procurarCurso(dados[9], repositorio) : null;

                        Estudante est = new Estudante(
                                Integer.parseInt(dados[1]), dados[2], dados[3], dados[4],
                                dados[5], dados[6], dados[7], cursoEst, Integer.parseInt(dados[8])
                        );
                        repositorio.adicionarEstudante(est);
                        break;

                    case "NOTA":
                        // Formato: NOTA;numMecanografico;siglaUC;nota1;nota2;nota3
                        int numMec = Integer.parseInt(dados[1]);
                        String siglaUC = dados[2];

                        // 1. Procurar o estudante no repositório
                        Estudante estNota = procurarEstudante(numMec, repositorio);
                        // 2. Procurar a UC no repositório
                        UnidadeCurricular ucNota = procurarUC(siglaUC, repositorio);

                        if (estNota != null && ucNota != null) {
                            // Criamos ou obtemos a avaliação para este aluno e UC
                            // Passamos o ano atual do repositório ou um valor padrão
                            for (int i = 3; i <= 5; i++) {
                                double valorNota = Double.parseDouble(dados[i]);
                                // Só adicionamos se a nota for maior que 0 (ou seja, se foi lançada)
                                if (valorNota > 0) {
                                    estNota.adicionarNota(ucNota, valorNota, repositorio.getAnoAtual());
                                }
                            }
                        }
                        break;
                    case "HISTORICO":
                        // Formato: HISTORICO;numMecanografico;siglaUC;anoAvaliacao;nota1;nota2;nota3
                        int numMecHist = Integer.parseInt(dados[1]);
                        String siglaUCHist = dados[2];
                        int anoAvaliacao = Integer.parseInt(dados[3]);

                        Estudante estHist = procurarEstudante(numMecHist, repositorio);
                        UnidadeCurricular ucHist = procurarUC(siglaUCHist, repositorio);

                        if (estHist != null && ucHist != null) {
                            // Cria a avaliação com o ano passado e injeta as notas
                            Avaliacao avaliacaoAntiga = new Avaliacao(estHist, ucHist, anoAvaliacao);
                            for (int i = 4; i <= 6; i++) {
                                double valorNota = Double.parseDouble(dados[i]);
                                if (valorNota > 0) {
                                    avaliacaoAntiga.adicionarResultado(valorNota);
                                }
                            }

                            // Adiciona diretamente ao array de histórico (precisas de criar este método no Estudante.java)
                            estHist.adicionarAoHistorico(avaliacaoAntiga);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o ficheiro: " + e.getMessage());
        }
    }

    // Métodos Auxiliares de Procura (Essenciais para ligar os dados)
    private static Departamento procurarDepartamento(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalDepartamentos(); i++) {
            if (repo.getDepartamentos()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getDepartamentos()[i];
            }
        }
        return null;
    }

    private static Curso procurarCurso(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalCursos(); i++) {
            if (repo.getCursos()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getCursos()[i];
            }
        }
        return null;
    }

    private static Docente procurarDocente(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalDocentes(); i++) {
            if (repo.getDocentes()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getDocentes()[i];
            }
        }
        return null;
    }

    private static Estudante procurarEstudante(int numMec, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            if (repo.getEstudantes()[i].getNumeroMecanografico() == numMec) {
                return repo.getEstudantes()[i];
            }
        }
        return null;
    }

    private static UnidadeCurricular procurarUC(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalUcs(); i++) {
            if (repo.getUcs()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getUcs()[i];
            }
        }
        return null;
    }
}
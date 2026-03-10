package util;

import model.*;
import java.io.*;

public class CSVService {

    // -----------------------------
    // EXPORTAR TODO O SISTEMA
    // -----------------------------
    public static void exportarSistema(SistemaAcademico sistema, String caminho) {
        try (FileWriter writer = new FileWriter(caminho)) {

            // Header
            writer.append("TIPO,ID,Nome,Sigla,Email,Password,Curso,Ano\n");

            // ----------------------------- ESTUDANTES -----------------------------
            for (int i = 0; i < sistema.getTotalEstudantes(); i++) {
                Estudante e = sistema.getEstudantes()[i];
                writer.append("ESTUDANTE,");
                writer.append(e.getNumeroMecanografico() + ",");
                writer.append(e.getNome() + ",,"); // campos não usados
                writer.append(e.getEmail() + ",");
                writer.append(e.getPassword() + ",");
                writer.append((e.getCurso() != null ? e.getCurso().getSigla() : "") + ",");
                writer.append(e.getAnoPrimeiraInscricao() + "\n");
            }

            // ----------------------------- DOCENTES -----------------------------
            for (int i = 0; i < sistema.getTotalDocentes(); i++) {
                Docente d = sistema.getDocentes()[i];
                writer.append("DOCENTE,");
                writer.append(d.getSigla() + ",");
                writer.append(d.getNome() + ",");
                writer.append(d.getEmail() + ",");
                writer.append(d.getPassword() + ",,,\n");
            }

            // ----------------------------- CURSOS -----------------------------
            for (int i = 0; i < sistema.getTotalCursos(); i++) {
                Curso c = sistema.getCursos()[i];
                writer.append("CURSO,");
                writer.append(c.getSigla() + ",");
                writer.append(c.getNome() + ",");
                writer.append((c.getDepartamento() != null ? c.getDepartamento().getSigla() : "") + ",,,\n");
            }

            // ----------------------------- UNIDADES CURRICULARES -----------------------------
            for (int i = 0; i < sistema.getTotalUCs(); i++) {
                UnidadeCurricular uc = sistema.getUcs()[i];
                writer.append("UC,");
                writer.append(uc.getSigla() + ",");
                writer.append(uc.getNome() + ",");
                writer.append(uc.getAnoCurricular() + ",");
                writer.append((uc.getDocenteResponsavel() != null ? uc.getDocenteResponsavel().getSigla() : "") + ",\n");
            }

            System.out.println("Sistema exportado com sucesso para " + caminho);

        } catch (IOException e) {
            System.out.println("Erro ao exportar CSV: " + e.getMessage());
        }
    }

    // -----------------------------
    // IMPORTAR TODO O SISTEMA
    // -----------------------------
    public static void importarCSV(SistemaAcademico sistema, String caminho) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            boolean headerLido = false;

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty() || linha.startsWith("#")) continue;

                if (!headerLido) { // Ignora header
                    headerLido = true;
                    continue;
                }

                String[] dados = linha.split(",");

                switch (dados[0].toUpperCase()) {

                    case "DEPARTAMENTO":
                        Departamento d = new Departamento(dados[1], dados[2]);
                        sistema.adicionarDepartamento(d);
                        break;

                    case "CURSO":
                        Curso c = new Curso(dados[1], dados[2]);
                        Departamento dep = sistema.procurarDepartamento(dados[3]);
                        if (dep != null) c.setDepartamento(dep);
                        sistema.adicionarCurso(c);
                        break;

                    case "UC":
                        int anoUC = Integer.parseInt(dados[3]);
                        UnidadeCurricular uc = new UnidadeCurricular(dados[1], dados[2], anoUC);
                        Curso cursoUC = sistema.procurarCurso(dados[4]);
                        if (cursoUC != null) cursoUC.adicionarUC(uc);
                        Docente docenteUC = sistema.procurarDocenteSigla(dados[5]);
                        if (docenteUC != null) uc.setDocenteResponsavel(docenteUC);
                        sistema.adicionarUC(uc);
                        break;

                    case "DOCENTE":
                        String sigla = dados[1];
                        String nomeDoc = dados[2];
                        String emailDoc = dados[3];
                        String passwordDoc = dados.length > 4 ? dados[4] : "1234";
                        Docente doc = new Docente(sigla, emailDoc, passwordDoc, nomeDoc, "", "", "");
                        sistema.adicionarDocente(doc);
                        break;

                    case "ESTUDANTE":
                        int numero = Integer.parseInt(dados[1]);
                        String nomeEst = dados[2];
                        String emailEst = dados[3];
                        String passwordEst = dados[4];
                        Curso cursoEst = sistema.procurarCurso(dados[5]);
                        int ano = dados.length > 6 && !dados[6].isEmpty() ? Integer.parseInt(dados[6]) : sistema.getAnoAtual();
                        Estudante e = new Estudante(numero, emailEst, passwordEst, nomeEst, "", "", "", cursoEst, ano);
                        sistema.adicionarEstudante(e);
                        break;

                    default:
                        System.out.println("Linha ignorada: " + linha);
                        break;
                }
            }

            System.out.println("Sistema importado com sucesso de " + caminho);

        } catch (FileNotFoundException e) {
            System.out.println("CSV não encontrado em " + caminho + ". Criando novo sistema.");
        } catch (IOException e) {
            System.out.println("Erro ao ler CSV: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Erro ao converter número: " + e.getMessage());
        }
    }
}
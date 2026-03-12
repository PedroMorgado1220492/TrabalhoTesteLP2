// Ficheiro: utils/ImportadorCSV.java
package utils;

import model.*; // Importa todas as nossas classes do modelo
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ImportadorCSV {

    // Método estático para podermos chamar sem instanciar a classe
    public static void importarDados(String caminhoFicheiro, RepositorioDados repositorio) {
        BufferedReader leitor = null;
        String linha = "";
        String separador = ";"; // O ponto e vírgula é o separador padrão de CSVs em português

        try {
            leitor = new BufferedReader(new FileReader(caminhoFicheiro));

            // Lê a primeira linha e avança (útil se o seu CSV tiver um cabeçalho como "Tipo;Nome;NIF...")
            // Se não tiver cabeçalho, pode apagar/comentar a linha abaixo.
            linha = leitor.readLine();

            // Lê o ficheiro linha a linha até chegar ao fim (null)
            while ((linha = leitor.readLine()) != null) {

                // Divide a linha pelas colunas
                String[] dados = linha.split(separador);

                // A primeira coluna diz-nos o que estamos a importar
                String tipoRegisto = dados[0].trim().toUpperCase();

                // Lógica para cada tipo de entidade
                if (tipoRegisto.equals("GESTOR")) {
                    importarGestor(dados, repositorio);
                }
                else if (tipoRegisto.equals("DOCENTE")) {
                    importarDocente(dados, repositorio);
                }
                else if (tipoRegisto.equals("ESTUDANTE")) {
                    importarEstudante(dados, repositorio);
                }
                // Poderíamos adicionar os "else if" para DEPARTAMENTO, CURSO, etc.
            }

            System.out.println(">> Importação do ficheiro CSV concluída com sucesso!");

        } catch (IOException e) {
            System.out.println(">> ERRO ao ler o ficheiro CSV: " + e.getMessage());
        } finally {
            // Garante que fechamos o ficheiro no final, mesmo que dê erro
            if (leitor != null) {
                try {
                    leitor.close();
                } catch (IOException e) {
                    System.out.println(">> ERRO ao fechar o leitor.");
                }
            }
        }
    }

    // --- MÉTODOS AUXILIARES PARA LIMPAR O CÓDIGO ---

    private static void importarGestor(String[] dados, RepositorioDados repositorio) {
        if (dados.length < 7) {
            System.out.println(">> AVISO: Linha de Gestor incompleta. A ignorar: " + String.join(";", dados));
            return;
        }
        String email = dados[1];
        String password = dados[2];
        String nome = dados[3];
        String nif = dados[4];
        String morada = dados[5];
        String dataNascimento = dados[6];

        Gestor novoGestor = new Gestor(email, password, nome, nif, morada, dataNascimento);
        repositorio.adicionarGestor(novoGestor);
    }

    private static void importarDocente(String[] dados, RepositorioDados repositorio) {
        // VALIDAÇÃO DE SEGURANÇA: Verifica se a linha tem pelo menos as 8 colunas esperadas
        if (dados.length < 8) {
            System.out.println(">> AVISO: Linha de Docente incompleta ou mal formatada. A ignorar esta linha: " + String.join(";", dados));
            return; // Sai do método sem tentar ler os índices que faltam
        }

        // Exemplo CSV: DOCENTE;sigla;email;password;nome;nif;morada;dataNascimento
        String sigla = dados[1];
        String email = dados[2];
        String password = dados[3];
        String nome = dados[4];
        String nif = dados[5];
        String morada = dados[6];
        String dataNascimento = dados[7];

        Docente novoDocente = new Docente(sigla, email, password, nome, nif, morada, dataNascimento);
        repositorio.adicionarDocente(novoDocente);
    }

    private static void importarEstudante(String[] dados, RepositorioDados repositorio) {
        if (dados.length < 9) {
            System.out.println(">> AVISO: Linha de Estudante incompleta. A ignorar: " + String.join(";", dados));
            return;
        }
        int numero = Integer.parseInt(dados[1]);
        String email = dados[2];
        String password = dados[3];
        String nome = dados[4];
        String nif = dados[5];
        String morada = dados[6];
        String dataNascimento = dados[7];
        int anoInscricao = Integer.parseInt(dados[8]);

        // Nota: Por agora passamos o Curso como null. Mais tarde poderemos procurar o curso pelo nome.
        Estudante novoEstudante = new Estudante(numero, email, password, nome, nif, morada, dataNascimento, null, anoInscricao);
        repositorio.adicionarEstudante(novoEstudante);
    }
}
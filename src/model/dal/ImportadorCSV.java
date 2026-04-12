package model.dal;

import model.bll.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe utilitária responsável pela leitura de ficheiros CSV.
 * Ocupa a camada de acesso a dados (DAL) e atua como um desserializador, convertendo os
 * registos persistidos em disco (ficheiros de texto) nos respetivos Objetos Java (POJOs),
 * reconstruindo as suas relações de dependência no RepositorioDados.
 */
public class ImportadorCSV {

    /**
     * Construtor privado para evitar a instanciação acidental da classe,
     * garantindo o padrão Utility Class, uma vez que todos os seus métodos são estáticos.
     */
    private ImportadorCSV() {}

    // =========================================================
    // 1. CARREGAMENTO AUTOMÁTICO (LOGINS E CREDENCIAIS)
    // =========================================================

    /**
     * Valida as credenciais de um utilizador executando uma leitura otimizada
     * exclusivamente no ficheiro mestre de logins, evitando o carregamento prematuro
     * da base de dados completa para a memória RAM (Fast-Fail Login).
     *
     * @param caminho  O caminho relativo para o ficheiro de logins.
     * @param email    O email submetido na tentativa de login.
     * @param password A palavra-passe (já em hash) calculada a partir do input do utilizador.
     * @return Uma String contendo o tipo de utilizador (ex: "GESTOR", "ESTUDANTE") se a validação for bem-sucedida; null caso contrário.
     */
    public static String verificarLoginRapido(String caminho, String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine(); // Ignora a linha de cabeçalho

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");

                // Prevenção de exceções de indexação (garante que a linha está formatada com TIPO;EMAIL;PASS)
                if (dados.length >= 3) {
                    String fileEmail = dados[1].trim();
                    String filePass = dados[2].trim();

                    if (fileEmail.equalsIgnoreCase(email.trim()) && filePass.equalsIgnoreCase(password.trim())) {
                        return dados[0].trim().toUpperCase();
                    }
                }
            }
        } catch (IOException e) {
            // Retorna null silenciosamente se o ficheiro não existir ou falhar a leitura
            return null;
        }
        return null;
    }

    /**
     * Efetua uma pesquisa sequencial no ficheiro mestre de logins para extrair
     * a hash da password associada a um determinado endereço de email.
     *
     * @param caminhoLogins  O caminho do ficheiro.
     * @param emailProcurado O email de referência.
     * @return A hash da password, ou uma String vazia se o email não for encontrado.
     */
    public static String procurarPasswordNoLogins(String caminhoLogins, String emailProcurado) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoLogins))) {
            String linha;
            br.readLine();

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");
                if (dados.length >= 3) {
                    if (dados[1].trim().equalsIgnoreCase(emailProcurado.trim())) {
                        return dados[2].trim();
                    }
                }
            }
        } catch (IOException e) {
            // Ignorado silenciosamente
        }
        return "";
    }

    // =========================================================
    // 2. CARREGAMENTO MODULAR (ENTIDADES)
    // =========================================================

    /**
     * Importa a listagem de administradores/gestores para o sistema.
     *
     * @param caminho     O destino do ficheiro gestores.csv.
     * @param repositorio O repositório central.
     */
    public static void importarGestores(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");
                // Mapeamento inter-ficheiros: vai buscar a password ao logins.csv
                String pass = procurarPasswordNoLogins("bd/logins.csv", dados[1]);

                repositorio.adicionarGestor(new Gestor(dados[1], pass, dados[2], dados[3]));
            }
        } catch (IOException e) { }
    }

    /**
     * Importa e instancia os departamentos institucionais.
     */
    public static void importarDepartamentos(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");
                repositorio.adicionarDepartamento(new Departamento(dados[1], dados[2]));
            }
        } catch (IOException e) { }
    }

    /**
     * Importa o corpo docente, restaurando o seu estado funcional (Ativo/Inativo).
     */
    public static void importarDocentes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");
                String pass = procurarPasswordNoLogins("bd/logins.csv", dados[2]);
                String emailPessoal = dados.length > 7 ? dados[7] : "";

                Docente d = new Docente(dados[1], dados[2], pass, dados[3], dados[4], dados[5], dados[6], emailPessoal);

                // Restauro de estado de ativação
                if (dados.length > 8) d.setAtivo(Boolean.parseBoolean(dados[8]));

                repositorio.adicionarDocente(d);
            }
        } catch (IOException e) { }
    }

    /**
     * Importa e instancia os Cursos.
     * Executa o mapeamento relacional 1:N ao associar de imediato o Curso ao seu respetivo Departamento em memória.
     */
    public static void importarCursos(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");
                // Pesquisa a instância de departamento já carregada
                Departamento dep = procurarDepartamento(dados[3], repositorio);

                if (dep != null) {
                    Curso novoCurso = new Curso(dados[1], dados[2], dep);
                    if (dados.length > 4) novoCurso.setAtivo(Boolean.parseBoolean(dados[4]));

                    if (repositorio.adicionarCurso(novoCurso)) {
                        dep.adicionarCurso(novoCurso); // Vínculo bidirecional
                    }
                }
            }
        } catch (IOException e) { }
    }

    /**
     * Importa as Unidades Curriculares e restabelece a complexa teia de dependências
     * entre a UC, o Docente Responsável e o(s) Curso(s) a que pertence.
     */
    public static void importarUCs(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");
                Docente doc = procurarDocente(dados[4], repositorio);
                Curso cursoUC = procurarCurso(dados[5], repositorio);

                if (doc != null && cursoUC != null) {
                    UnidadeCurricular novaUc = new UnidadeCurricular(dados[1], dados[2], Integer.parseInt(dados[3]), doc);
                    if (dados.length > 6) novaUc.setAtivo(Boolean.parseBoolean(dados[6]));

                    if (repositorio.adicionarUnidadeCurricular(novaUc)) {
                        // Estabelecimento de vínculos cruzados na hierarquia
                        cursoUC.adicionarUnidadeCurricular(novaUc);
                        novaUc.adicionarCurso(cursoUC);
                        doc.adicionarUcResponsavel(novaUc);
                        doc.adicionarUcLecionada(novaUc);
                    }
                }
            }
        } catch (IOException e) { }
    }

    /**
     * Importa o perfil completo de cada estudante.
     * Esta rotina é de elevada complexidade pois tem a responsabilidade de desserializar
     * quer o histórico de pagamentos parcelares (Propinas), quer o estado do plano de estudos atual.
     */
    public static void importarEstudantes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");

                String pass = procurarPasswordNoLogins("bd/logins.csv", dados[2]);
                Curso cursoEst = (dados.length > 8 && !dados[8].isEmpty()) ? procurarCurso(dados[8], repositorio) : null;
                String emailPessoal = dados.length > 9 ? dados[9] : "";

                Estudante est = new Estudante(
                        Integer.parseInt(dados[1]), dados[2], pass, dados[3],
                        dados[4], dados[5], dados[6], cursoEst, Integer.parseInt(dados[7]), emailPessoal
                );

                if (dados.length > 10 && !dados[10].isEmpty()) est.setAtivo(Boolean.parseBoolean(dados[10]));

                // Reconstrução do Objeto Financeiro (Propina e Prestações)
                if (dados.length > 11 && !dados[11].isEmpty()) {
                    double precoAntigo = Double.parseDouble(dados[11]);
                    est.setValorPropinaBase(precoAntigo);

                    Propina propinaGerada = est.getPropinaDoAno(est.getAnoPrimeiraInscricao());
                    if (propinaGerada != null) {
                        propinaGerada.setValorTotal(precoAntigo);

                        if (dados.length > 13) {
                            int totalPrestacoes = Integer.parseInt(dados[13]);
                            // Itera pelas colunas dinâmicas para recuperar os pagamentos efetuados
                            for (int i = 0; i < totalPrestacoes; i++) {
                                int indexDaPrestacao = 14 + i;
                                if (indexDaPrestacao < dados.length) {
                                    propinaGerada.registarPagamento(Double.parseDouble(dados[indexDaPrestacao]));
                                }
                            }
                        }
                    }
                }

                // Reconstrução do Percurso Académico Base (auto-inscrição em UCs se aplicável)
                if (cursoEst != null && est.getPercursoAcademico() != null) {
                    for (int i = 0; i < cursoEst.getTotalUCs(); i++) {
                        UnidadeCurricular uc = cursoEst.getUnidadesCurriculares()[i];
                        if (uc.getAnoCurricular() == est.getAnoFrequencia()) {
                            est.getPercursoAcademico().inscreverEmUc(uc);
                        }
                    }
                }
                repositorio.adicionarEstudante(est);
            }
        } catch (IOException e) { }
    }

    /**
     * Interpreta o ficheiro de pautas unificado e reconstrói a matriz de classificações,
     * distinguindo entre avaliações ativas no presente ano ("NOTA") e anos anteriores ("HISTORICO").
     */
    public static void importarAvaliacoes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();

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
                            // Carrega as 3 slots de notas anuais (caso tenham valores preenchidos)
                            for (int i = 3; i <= 5; i++) {
                                double valor = Double.parseDouble(dados[i]);
                                if (valor > 0) est.adicionarNota(uc, valor, repositorio.getAnoAtual());
                            }
                        } else {
                            // Empacota e envia as avaliações passadas diretamente para o arquivo histórico
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
        } catch (IOException e) { }
    }

    // =========================================================
    // 3. MÉTODOS AUXILIARES DE PESQUISA INTERNA
    // =========================================================

    private static Departamento procurarDepartamento(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalDepartamentos(); i++) {
            if (repo.getDepartamentos()[i] != null && repo.getDepartamentos()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getDepartamentos()[i];
            }
        }
        return null;
    }

    private static Curso procurarCurso(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalCursos(); i++) {
            if (repo.getCursos()[i] != null && repo.getCursos()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getCursos()[i];
            }
        }
        return null;
    }

    private static Docente procurarDocente(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalDocentes(); i++) {
            if (repo.getDocentes()[i] != null && repo.getDocentes()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getDocentes()[i];
            }
        }
        return null;
    }

    private static Estudante procurarEstudante(int numMec, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            if (repo.getEstudantes()[i] != null && repo.getEstudantes()[i].getNumeroMecanografico() == numMec) {
                return repo.getEstudantes()[i];
            }
        }
        return null;
    }

    private static UnidadeCurricular procurarUC(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalUcs(); i++) {
            if (repo.getUcs()[i] != null && repo.getUcs()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getUcs()[i];
            }
        }
        return null;
    }
}
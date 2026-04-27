package model.dal;

import model.bll.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe utilitária responsável pela desserialização de dados a partir de ficheiros CSV.
 * Ocupa a camada DAL (Data Access Layer) e atua como o motor de reconstrução do sistema,
 * convertendo registos textuais em Objetos Java (POJOs) e restabelecendo as suas
 * interdependências e referências cruzadas dentro do RepositorioDados.
 */
public class ImportadorCSV {

    /**
     * Construtor privado para garantir o padrão Utility Class.
     */
    private ImportadorCSV() {}


    // =========================================================
    // 1. MECANISMOS DE AUTENTICAÇÃO RÁPIDA
    // =========================================================

    /**
     * Valida credenciais através de uma leitura otimizada no ficheiro mestre de logins.
     * Este método implementa a estratégia de "Fast-Fail", permitindo validar um utilizador
     * sem a necessidade de carregar toda a base de dados para a memória RAM.
     *
     * @param caminho  O caminho para o ficheiro logins.csv.
     * @param email    O email de login submetido.
     * @param password A hash da password submetida.
     * @return O tipo de utilizador (ex: "GESTOR") se válido; null caso contrário.
     */
    public static String verificarLoginRapido(String caminho, String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine(); // Ignora cabeçalho

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");

                if (dados.length >= 3) {
                    if (dados[1].trim().equalsIgnoreCase(email.trim()) &&
                            dados[2].trim().equals(password.trim())) {
                        return dados[0].trim().toUpperCase();
                    }
                }
            }
        } catch (IOException e) { return null; }
        return null;
    }

    /**
     * Recupera a password encriptada de um utilizador a partir do ficheiro de logins.
     * Utilizado durante o carregamento de entidades para recompor os objetos Utilizador.
     */
    public static String procurarPasswordNoLogins(String caminhoLogins, String emailProcurado) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoLogins))) {
            String linha;
            br.readLine();
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length >= 3 && dados[1].trim().equalsIgnoreCase(emailProcurado.trim())) {
                    return dados[2].trim();
                }
            }
        } catch (IOException e) { }
        return "";
    }


    // =========================================================
    // 2. CARREGAMENTO DE INFRAESTRUTURA E DOCÊNCIA
    // =========================================================

    /**
     * Importa os administradores do sistema.
     */
    public static void importarGestores(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                String pass = procurarPasswordNoLogins("bd/logins.csv", dados[1]);
                Gestor g = new Gestor(dados[1], pass, dados[2], dados[3]);
                if (dados.length > 4) g.setAtivo(Boolean.parseBoolean(dados[4]));
                repositorio.adicionarGestor(g);
            }
        } catch (IOException e) { }
    }

    /**
     * Importa os Departamentos, que servem de base para a criação de Cursos.
     */
    public static void importarDepartamentos(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                repositorio.adicionarDepartamento(new Departamento(dados[1], dados[2]));
            }
        } catch (IOException e) { }
    }

    /**
     * Importa os Docentes e restabelece os seus dados pessoais e estado de conta.
     */
    public static void importarDocentes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                String pass = procurarPasswordNoLogins("bd/logins.csv", dados[2]);
                Docente d = new Docente(dados[1], dados[2], pass, dados[3], dados[4], dados[5], dados[6], (dados.length > 7 ? dados[7] : ""));
                if (dados.length > 8) d.setAtivo(Boolean.parseBoolean(dados[8]));
                repositorio.adicionarDocente(d);
            }
        } catch (IOException e) { }
    }


    // =========================================================
    // 3. CARREGAMENTO ACADÉMICO (CURSOS E UCS)
    // =========================================================

    /**
     * Importa Cursos e estabelece o vínculo bidirecional com o respetivo Departamento.
     */
    public static void importarCursos(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                Departamento dep = procurarDepartamento(dados[3], repositorio);
                if (dep != null) {
                    Curso c = new Curso(dados[1], dados[2], dep);
                    if (dados.length > 4) c.setAtivo(Boolean.parseBoolean(dados[4]));
                    if (repositorio.adicionarCurso(c)) dep.adicionarCurso(c);
                }
            }
        } catch (IOException e) { }
    }

    /**
     * Importa Unidades Curriculares e restabelece a partilha entre cursos.
     * Versão corrigida com verificação de nulos para evitar crash no login.
     */
    public static void importarUCs(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine(); // Cabeçalho

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");

                Docente doc = procurarDocente(dados[4], repositorio);
                if (doc == null) continue;

                int numAv = (dados.length > 7) ? Integer.parseInt(dados[7]) : 3;
                UnidadeCurricular uc = new UnidadeCurricular(dados[1], dados[2], Integer.parseInt(dados[3]), doc, numAv);

                if (dados.length > 6) uc.setAtivo(Boolean.parseBoolean(dados[6]));

                if (repositorio.adicionarUnidadeCurricular(uc)) {
                    // Verificamos se existem cursos listados na coluna 5
                    if (dados.length > 5 && !dados[5].trim().isEmpty()) {
                        String[] siglas = dados[5].split(",");

                        // O primeiro curso da lista é o "Principal" para os vínculos iniciais
                        Curso cursoPrincipal = procurarCurso(siglas[0].trim(), repositorio);
                        if (cursoPrincipal != null) {
                            uc.estabelecerVinculosIniciais(cursoPrincipal);
                        }

                        // Se houver mais cursos (partilha), adicionamos os restantes
                        for (int i = 1; i < siglas.length; i++) {
                            Curso cExtra = procurarCurso(siglas[i].trim(), repositorio);
                            if (cExtra != null) {
                                cExtra.adicionarUnidadeCurricular(uc);
                                uc.adicionarCurso(cExtra);
                            }
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println(">> Erro crítico na leitura das UCs: " + e.getMessage());
        }
    }


    // =========================================================
    // 4. CARREGAMENTO DE ESTUDANTES E AVALIAÇÕES
    // =========================================================

    /**
     * Importa Estudantes, reconstruindo o seu Percurso Académico e o histórico financeiro (Propinas).
     */
    /**
     * Importa Estudantes, reconstruindo o seu Percurso Académico e o histórico financeiro (Propinas).
     */
    /**
     * Importa Estudantes, reconstruindo o seu Percurso Académico e o histórico financeiro (Propinas).
     */
    public static void importarEstudantes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine(); // cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length < 12) continue;
                String pass = procurarPasswordNoLogins("bd/logins.csv", dados[2]);
                Curso curso = procurarCurso(dados[8], repositorio);
                Estudante est = new Estudante(
                        Integer.parseInt(dados[1]), dados[2], pass, dados[3],
                        dados[4], dados[5], dados[6], curso, Integer.parseInt(dados[7]), dados[9]
                );
                est.setAtivo(Boolean.parseBoolean(dados[10]));
                est.setAnoFrequencia(Integer.parseInt(dados[11]));
                est.setAnoCurricular(est.getAnoFrequencia());
                repositorio.adicionarEstudante(est);
                if (est.isAtivo()) est.reconstruirPercurso();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao importar estudantes: " + e.getMessage());
        }
    }

    /**
     * Importa e distribui as classificações pelos boletins dos estudantes.
     * Versão com debug para identificar falhas no carregamento.
     */
    /**
     * Importa e distribui as classificações pelos boletins dos estudantes.
     */
    public static void importarAvaliacoes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine(); // cabeçalho
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                if (dados.length < 4) continue;

                int numMec = Integer.parseInt(dados[1]);
                String siglaUC = dados[2];
                String tipo = dados[0];

                Estudante est = procurarEstudante(numMec, repositorio);
                if (est == null) continue;

                UnidadeCurricular uc = procurarUC(siglaUC, repositorio);
                if (uc == null) continue;

                if (tipo.equalsIgnoreCase("NOTA")) {
                    for (int i = 3; i <= 5 && i < dados.length; i++) {
                        double v = Double.parseDouble(dados[i]);
                        if (v >= 0) est.adicionarNota(uc, v, repositorio.getAnoAtual());
                    }
                } else if (tipo.equalsIgnoreCase("HISTORICO")) {
                    if (dados.length < 5) continue;
                    int ano = Integer.parseInt(dados[3]);
                    Avaliacao hist = new Avaliacao(est, uc, ano);
                    for (int i = 4; i <= 6 && i < dados.length; i++) {
                        double v = Double.parseDouble(dados[i]);
                        if (v >= 0) hist.adicionarResultado(v);
                    }
                    est.adicionarAoHistorico(hist);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao importar avaliações: " + e.getMessage());
        }
    }
    /**
     * Lê o ano letivo corrente a partir do ficheiro CSV "ano.csv".
     * Se o ficheiro não existir ou ocorrer um erro de leitura, retorna o valor
     * padrão 2026.
     *
     * @param caminho Caminho completo do ficheiro ano.csv (ex: "bd/ano.csv").
     * @return O ano letivo armazenado no ficheiro, ou 2026 se o ficheiro
     *         não estiver disponível.
     */
    public static int importarAno(String caminho) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            br.readLine(); // cabeçalho
            String linha = br.readLine();
            if (linha != null) {
                return Integer.parseInt(linha.trim());
            }
        } catch (IOException | NumberFormatException e) { }
        return 2026; // valor padrão se ficheiro não existir
    }


    // =========================================================
    // 5. MÉTODOS DE PESQUISA INTERNA (DESSERIALIZAÇÃO)
    // =========================================================

    private static Departamento procurarDepartamento(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalDepartamentos(); i++) {
            if (repo.getDepartamentos()[i].getSigla().equalsIgnoreCase(sigla)) return repo.getDepartamentos()[i];
        }
        return null;
    }

    private static Curso procurarCurso(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalCursos(); i++) {
            if (repo.getCursos()[i].getSigla().equalsIgnoreCase(sigla)) return repo.getCursos()[i];
        }
        return null;
    }

    private static Docente procurarDocente(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalDocentes(); i++) {
            if (repo.getDocentes()[i].getSigla().equalsIgnoreCase(sigla)) return repo.getDocentes()[i];
        }
        return null;
    }

    private static Estudante procurarEstudante(int numMec, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            if (repo.getEstudantes()[i].getNumeroMecanografico() == numMec) return repo.getEstudantes()[i];
        }
        return null;
    }

    private static UnidadeCurricular procurarUC(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalUcs(); i++) {
            if (repo.getUcs()[i].getSigla().equalsIgnoreCase(sigla)) return repo.getUcs()[i];
        }
        return null;
    }

    // =========================================================
    // 6. CARREGAMENTO DE PREÇOS CURSOS
    // =========================================================

    // Dentro de ImportadorCSV
    public static double obterPrecoCurso(String siglaCurso, int ano) {
        try (BufferedReader br = new BufferedReader(new FileReader("bd/cursos_precos.csv"))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length >= 3 && p[1].equalsIgnoreCase(siglaCurso) && Integer.parseInt(p[0]) == ano) {
                    return Double.parseDouble(p[2]);
                }
            }
        } catch (IOException | NumberFormatException e) { }
        return 1000.0; // valor padrão
    }

    public static double[][] obterHistoricoPrecos(String siglaCurso) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("bd/cursos_precos.csv"))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.contains(";" + siglaCurso + ";")) count++;
            }
        } catch (IOException e) { }

        double[][] historico = new double[count][2];
        int idx = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("bd/cursos_precos.csv"))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length >= 3 && p[1].equalsIgnoreCase(siglaCurso)) {
                    historico[idx][0] = Integer.parseInt(p[0]);
                    historico[idx][1] = Double.parseDouble(p[2]);
                    idx++;
                }
            }
        } catch (IOException e) { }
        // ordenar por ano
        for (int i = 0; i < historico.length - 1; i++) {
            for (int j = 0; j < historico.length - i - 1; j++) {
                if (historico[j][0] > historico[j+1][0]) {
                    double[] temp = historico[j];
                    historico[j] = historico[j+1];
                    historico[j+1] = temp;
                }
            }
        }
        return historico;
    }

    public static String[] lerTodasLinhasPrecos() {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("bd/cursos_precos.csv"))) {
            br.readLine(); // cabeçalho
            while (br.readLine() != null) count++;
        } catch (IOException e) { return new String[0]; }
        String[] linhas = new String[count];
        try (BufferedReader br = new BufferedReader(new FileReader("bd/cursos_precos.csv"))) {
            br.readLine();
            for (int i = 0; i < count; i++) {
                linhas[i] = br.readLine();
            }
        } catch (IOException e) { return new String[0]; }
        return linhas;
    }
}
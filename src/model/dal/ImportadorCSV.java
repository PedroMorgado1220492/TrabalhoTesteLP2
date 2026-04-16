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
    public static void importarEstudantes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                String pass = procurarPasswordNoLogins("bd/logins.csv", dados[2]);
                Curso curso = (dados.length > 8) ? procurarCurso(dados[8], repositorio) : null;

                Estudante est = new Estudante(Integer.parseInt(dados[1]), dados[2], pass, dados[3],
                        dados[4], dados[5], dados[6], curso, Integer.parseInt(dados[7]),
                        (dados.length > 9 ? dados[9] : ""));

                if (dados.length > 10) est.setAtivo(Boolean.parseBoolean(dados[10]));

                // Reconstrução financeira (Propina e Prestações)
                if (dados.length > 11) {
                    double preco = Double.parseDouble(dados[11]);
                    est.setValorPropinaBase(preco);
                    Propina p = est.getPropinaDoAno(est.getAnoPrimeiraInscricao());
                    if (p != null) {
                        p.setValorTotal(preco);
                        if (dados.length > 13) {
                            int numPags = Integer.parseInt(dados[13]);
                            for (int i = 0; i < numPags; i++) {
                                if (14 + i < dados.length) p.registarPagamento(Double.parseDouble(dados[14 + i]));
                            }
                        }
                    }
                }
                repositorio.adicionarEstudante(est);
                if (est.isAtivo()) est.matricularNasUcsIniciais();
            }
        } catch (IOException | NumberFormatException e) { }
    }

    /**
     * Importa e distribui as classificações pelos boletins dos estudantes.
     */
    public static void importarAvaliacoes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                Estudante est = procurarEstudante(Integer.parseInt(dados[1]), repositorio);
                UnidadeCurricular uc = procurarUC(dados[2], repositorio);

                if (est != null && uc != null) {
                    if (dados[0].equalsIgnoreCase("NOTA")) {
                        for (int i = 3; i <= 5; i++) {
                            double v = Double.parseDouble(dados[i]);
                            if (v >= 0) est.adicionarNota(uc, v, repositorio.getAnoAtual());
                        }
                    } else {
                        Avaliacao hist = new Avaliacao(est, uc, Integer.parseInt(dados[3]));
                        for (int i = 4; i <= 6 && i < dados.length; i++) {
                            double v = Double.parseDouble(dados[i]);
                            if (v >= 0) hist.adicionarResultado(v);
                        }
                        est.adicionarAoHistorico(hist);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) { }
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
}
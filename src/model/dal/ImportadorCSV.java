package model.dal;

import model.bll.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe utilitária responsável pela leitura de ficheiros CSV.
 * Converte os dados persistidos em memória secundária (ficheiros de texto)
 * em Objetos Java (POJOs) e popula o RepositorioDados.
 */
public class ImportadorCSV {

    /**
     * Construtor privado para evitar a instanciação acidental da classe,
     * uma vez que todos os seus métodos são estáticos.
     */
    private ImportadorCSV() {}

    // =========================================================
    // 1. CARREGAMENTO AUTOMÁTICO (LOGINS E CREDENCIAIS)
    // =========================================================

    /**
     * Valida rapidamente as credenciais de um utilizador, lendo apenas
     * o ficheiro mestre de logins, sem carregar o resto da base de dados.
     * @param caminho O caminho relativo para o ficheiro de logins.
     * @param email O email inserido pelo utilizador.
     * @param password A palavra-passe (já encriptada) inserida pelo utilizador.
     * @return O tipo de utilizador em maiúsculas (ex: "GESTOR", "ESTUDANTE") ou null se falhar.
     */
    public static String verificarLoginRapido(String caminho, String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");

                // Prevenção extra: garantir que a linha tem pelo menos 3 colunas
                if (dados.length >= 3) {
                    String fileEmail = dados[1].trim();
                    String filePass = dados[2].trim();

                    if (fileEmail.equalsIgnoreCase(email.trim()) && filePass.equalsIgnoreCase(password.trim())) {
                        return dados[0].trim().toUpperCase();
                    }
                }
            }
        } catch (IOException e) {

            return null;
        }
        return null;
    }

    /**
     * Pesquisa sequencial no ficheiro mestre de logins para recuperar a hash
     * da password de um utilizador específico através do seu email.
     * @param caminhoLogins O caminho relativo para o ficheiro de logins.
     * @param emailProcurado O email do utilizador cuja password queremos recuperar.
     * @return A password encriptada (hash) ou uma string vazia se não encontrar.
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
            // Ignorar silenciosamente
        }
        return "";
    }

    // =========================================================
    // 2. CARREGAMENTO MODULAR (ENTIDADES)
    // =========================================================

    /**
     * Importa todos os administradores/gestores do sistema.
     * @param caminho O caminho do ficheiro gestores.csv.
     * @param repositorio O repositório central onde a informação será guardada em memória.
     */
    public static void importarGestores(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha; br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                String pass = procurarPasswordNoLogins("bd/logins.csv", dados[1]);
                repositorio.adicionarGestor(new Gestor(dados[1], pass, dados[2], dados[3], dados[4], dados[5]));
            }
        } catch (IOException e) {
            // Ignorar silenciosamente: o sistema arranca sem dados caso o ficheiro não exista.
        }
    }

    /**
     * Importa todos os departamentos institucionais.
     * @param caminho O caminho do ficheiro departamentos.csv.
     * @param repositorio O repositório central onde a informação será guardada em memória.
     */
    public static void importarDepartamentos(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha; br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                repositorio.adicionarDepartamento(new Departamento(dados[1], dados[2]));
            }
        } catch (IOException e) {
            // Ignorar silenciosamente
        }
    }

    /**
     * Importa o corpo docente da instituição. Associa as credenciais do ficheiro mestre.
     * @param caminho O caminho do ficheiro docentes.csv.
     * @param repositorio O repositório central onde a informação será guardada em memória.
     */
    public static void importarDocentes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha; br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                String pass = procurarPasswordNoLogins("bd/logins.csv", dados[2]);

                String emailPessoal = dados.length > 7 ? dados[7] : "";
                Docente d = new Docente(dados[1], dados[2], pass, dados[3], dados[4], dados[5], dados[6], emailPessoal);
                if (dados.length > 8) d.setAtivo(Boolean.parseBoolean(dados[8]));

                repositorio.adicionarDocente(d);
            }
        } catch (IOException e) { }
    }

    /**
     * Importa os Cursos. Requer que os Departamentos já tenham sido carregados
     * para estabelecer a relação de dependência.
     * @param caminho O caminho do ficheiro cursos.csv.
     * @param repositorio O repositório central onde a informação será guardada em memória.
     */
    public static void importarCursos(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha; br.readLine();
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");
                Departamento dep = procurarDepartamento(dados[3], repositorio);
                if (dep != null) {
                    Curso novoCurso = new Curso(dados[1], dados[2], dep);
                    if (dados.length > 4) novoCurso.setAtivo(Boolean.parseBoolean(dados[4]));

                    if (repositorio.adicionarCurso(novoCurso)) {
                        dep.adicionarCurso(novoCurso);
                    }
                }
            }
        } catch (IOException e) { }
    }

    /**
     * Importa as Unidades Curriculares e efetua o mapeamento complexo com Docentes e Cursos.
     * @param caminho O caminho do ficheiro ucs.csv.
     * @param repositorio O repositório central onde a informação será guardada em memória.
     */
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
                    if (dados.length > 6) novaUc.setAtivo(Boolean.parseBoolean(dados[6]));

                    if (repositorio.adicionarUnidadeCurricular(novaUc)) {
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
     * Importa o corpo estudantil.
     * Esta função é altamente complexa pois reconstrói o estado financeiro (Propinas)
     * e o estado académico do aluno a partir da sua matrícula.
     * @param caminho O caminho do ficheiro estudantes.csv.
     * @param repositorio O repositório central onde a informação será guardada em memória.
     */
    public static void importarEstudantes(String caminho, RepositorioDados repositorio) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha; br.readLine();
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

                if (dados.length > 11 && !dados[11].isEmpty()) {
                    double precoAntigo = Double.parseDouble(dados[11]);
                    est.setValorPropinaBase(precoAntigo);

                    Propina propinaGerada = est.getPropinaDoAno(est.getAnoPrimeiraInscricao());
                    if (propinaGerada != null) {
                        propinaGerada.setValorTotal(precoAntigo);

                        if (dados.length > 13) {
                            int totalPrestacoes = Integer.parseInt(dados[13]);
                            for (int i = 0; i < totalPrestacoes; i++) {
                                int indexDaPrestacao = 14 + i;
                                if (indexDaPrestacao < dados.length) {
                                    propinaGerada.registarPagamento(Double.parseDouble(dados[indexDaPrestacao]));
                                }
                            }
                        }
                    }
                }

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
     * Importa o histórico de avaliações (notas ativas e cadeiras concluídas/reprovadas).
     * @param caminho O caminho do ficheiro avaliacoes.csv.
     * @param repositorio O repositório central onde a informação será guardada em memória.
     */
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
        } catch (IOException e) {
            // Ignorar silenciosamente
        }
    }

    // =========================================================
    // 3. MÉTODOS AUXILIARES DE PESQUISA INTERNA
    // =========================================================

    /**
     * Procura um Departamento em memória pela sua sigla.
     */
    private static Departamento procurarDepartamento(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalDepartamentos(); i++) {
            if (repo.getDepartamentos()[i] != null && repo.getDepartamentos()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getDepartamentos()[i];
            }
        }
        return null;
    }

    /**
     * Procura um Curso em memória pela sua sigla.
     */
    private static Curso procurarCurso(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalCursos(); i++) {
            if (repo.getCursos()[i] != null && repo.getCursos()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getCursos()[i];
            }
        }
        return null;
    }

    /**
     * Procura um Docente em memória pela sua sigla (ex: ABC).
     */
    private static Docente procurarDocente(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalDocentes(); i++) {
            if (repo.getDocentes()[i] != null && repo.getDocentes()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getDocentes()[i];
            }
        }
        return null;
    }

    /**
     * Procura um Estudante em memória pelo seu Número Mecanográfico.
     */
    private static Estudante procurarEstudante(int numMec, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalEstudantes(); i++) {
            if (repo.getEstudantes()[i] != null && repo.getEstudantes()[i].getNumeroMecanografico() == numMec) {
                return repo.getEstudantes()[i];
            }
        }
        return null;
    }

    /**
     * Procura uma Unidade Curricular em memória pela sua sigla.
     */
    private static UnidadeCurricular procurarUC(String sigla, RepositorioDados repo) {
        for (int i = 0; i < repo.getTotalUcs(); i++) {
            if (repo.getUcs()[i] != null && repo.getUcs()[i].getSigla().equalsIgnoreCase(sigla)) {
                return repo.getUcs()[i];
            }
        }
        return null;
    }
}
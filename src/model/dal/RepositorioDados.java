package model.dal;

import model.bll.*;
import java.util.*;

/**
 * Repositório central de dados em memória (cache).
 * Utiliza as classes DAL para persistência em CSV.
 *
 * @author ISSMF
 * @version 1.0
 */
public class RepositorioDados {

    // ---------- ATRIBUTOS ----------
    private int anoAtual;
    private boolean anoIniciado;

    // Listas em memória (cache)
    private List<Estudante> estudantes;
    private List<Gestor> gestores;
    private List<Docente> docentes;
    private List<Departamento> departamentos;
    private List<Curso> cursos;
    private List<UnidadeCurricular> ucs;

    // DALs
    private ConfigDAL configDAL;
    private EstudanteDAL estudanteDAL;
    private GestorDAL gestorDAL;
    private DocenteDAL docenteDAL;
    private DepartamentoDAL departamentoDAL;
    private CursoDAL cursoDAL;
    private UnidadeCurricularDAL ucDAL;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor do RepositorioDados.
     * Inicializa as DALs e carrega todos os dados dos ficheiros CSV.
     */
    public RepositorioDados() {
        configDAL = new ConfigDAL();
        estudanteDAL = new EstudanteDAL();
        gestorDAL = new GestorDAL();
        docenteDAL = new DocenteDAL();
        departamentoDAL = new DepartamentoDAL();
        cursoDAL = new CursoDAL();
        ucDAL = new UnidadeCurricularDAL();

        carregarTudo();
    }

    /**
     * Carrega todos os dados dos ficheiros CSV para as listas em memória.
     */
    private void carregarTudo() {
        this.anoAtual = configDAL.carregarAno();
        this.anoIniciado = configDAL.carregarAnoIniciado();
        this.departamentos = departamentoDAL.buscarTodos();
        this.cursos = cursoDAL.buscarTodos();
        this.docentes = docenteDAL.buscarTodos();
        this.estudantes = estudanteDAL.buscarTodos();
        this.gestores = gestorDAL.buscarTodos();
        this.ucs = ucDAL.buscarTodos(this.cursos);

        // REMOVA O IF - carregar sempre as avaliações
        AvaliacaoDAL.carregarAvaliacoes(this.estudantes, this.ucs, this.anoAtual);

        reconstruirAssociacoes();
    }

    // =========================================================
    // GETTERS
    // =========================================================

    /** @return Ano letivo atual */
    public int getAnoAtual() { return anoAtual; }

    /** @return true se o ano letivo já foi iniciado, false caso contrário */
    public boolean isAnoIniciado() { return anoIniciado; }

    /** @return Array de estudantes */
    public Estudante[] getEstudantes() { return estudantes.toArray(new Estudante[0]); }

    /** @return Número total de estudantes */
    public int getTotalEstudantes() { return estudantes.size(); }

    /** @return Array de gestores */
    public Gestor[] getGestores() { return gestores.toArray(new Gestor[0]); }

    /** @return Número total de gestores */
    public int getTotalGestores() { return gestores.size(); }

    /** @return Array de docentes */
    public Docente[] getDocentes() { return docentes.toArray(new Docente[0]); }

    /** @return Número total de docentes */
    public int getTotalDocentes() { return docentes.size(); }

    /** @return Array de departamentos */
    public Departamento[] getDepartamentos() { return departamentos.toArray(new Departamento[0]); }

    /** @return Número total de departamentos */
    public int getTotalDepartamentos() { return departamentos.size(); }

    /** @return Array de cursos */
    public Curso[] getCursos() { return cursos.toArray(new Curso[0]); }

    /** @return Número total de cursos */
    public int getTotalCursos() { return cursos.size(); }

    /** @return Array de unidades curriculares */
    public UnidadeCurricular[] getUcs() { return ucs.toArray(new UnidadeCurricular[0]); }

    /** @return Número total de UCs */
    public int getTotalUcs() { return ucs.size(); }

    // =========================================================
    // MÉTODOS DE ESCRITA (CRUD) - ESTUDANTE
    // =========================================================

    /**
     * Adiciona um novo estudante.
     * @param e Estudante a adicionar
     * @return true se adicionado com sucesso
     */
    public boolean adicionarEstudante(Estudante e) {
        estudanteDAL.inserir(e);
        return estudantes.add(e);
    }

    /**
     * Remove um estudante pelo número mecanográfico.
     * @param numMec Número mecanográfico
     */
    public void removerEstudante(int numMec) {
        estudanteDAL.remover(numMec);
        estudantes.removeIf(est -> est.getNumeroMecanografico() == numMec);
    }

    /**
     * Atualiza os dados de um estudante.
     * @param e Estudante com dados atualizados
     */
    public void atualizarEstudante(Estudante e) {
        estudanteDAL.atualizar(e);
        for (int i = 0; i < estudantes.size(); i++) {
            if (estudantes.get(i).getNumeroMecanografico() == e.getNumeroMecanografico()) {
                estudantes.set(i, e);
                break;
            }
        }
    }

    /**
     * Obtém um estudante pelo número mecanográfico.
     * @param numMec Número mecanográfico
     * @return Estudante encontrado ou null
     */
    public Estudante obterEstudantePorNumMec(int numMec) {
        for (Estudante e : estudantes) {
            if (e.getNumeroMecanografico() == numMec) return e;
        }
        return null;
    }

    /**
     * Verifica se um NIF já existe no sistema.
     * @param nif NIF a verificar
     * @return true se existir, false caso contrário
     */
    public boolean existeNif(String nif) {
        for (Estudante e : estudantes) if (e.getNif().equals(nif)) return true;
        for (Docente d : docentes) if (d.getNif().equals(nif)) return true;
        for (Gestor g : gestores) if (g.getNif().equals(nif)) return true;
        return false;
    }

    /**
     * Conta os estudantes inscritos no primeiro ano de um curso num determinado ano.
     * @param siglaCurso Sigla do curso
     * @param ano Ano de inscrição
     * @return Número de estudantes
     */
    public int contarInscritosPrimeiroAno(String siglaCurso, int ano) {
        int count = 0;
        for (Estudante e : estudantes) {
            if (e.getCurso() != null && e.getCurso().getSigla().equalsIgnoreCase(siglaCurso) &&
                    e.getAnoPrimeiraInscricao() == ano) count++;
        }
        return count;
    }

    /**
     * Conta os alunos ativos no primeiro ano de um curso.
     * @param siglaCurso Sigla do curso
     * @return Número de alunos no primeiro ano
     */
    public int contarAlunosNoPrimeiroAno(String siglaCurso) {
        int count = 0;
        for (Estudante e : estudantes) {
            if (e.isAtivo() && e.getCurso() != null && e.getCurso().getSigla().equalsIgnoreCase(siglaCurso) &&
                    e.getAnoFrequencia() == 1) count++;
        }
        return count;
    }

    /**
     * Remove todos os estudantes do primeiro ano de um curso.
     * @param siglaCurso Sigla do curso
     * @param ano Ano de inscrição
     */
    public void anularMatriculasPrimeiroAno(String siglaCurso, int ano) {
        List<Estudante> paraRemover = new ArrayList<>();
        for (Estudante e : estudantes) {
            if (e.getCurso() != null && e.getCurso().getSigla().equalsIgnoreCase(siglaCurso) &&
                    e.getAnoPrimeiraInscricao() == ano) {
                paraRemover.add(e);
            }
        }
        for (Estudante e : paraRemover) {
            removerEstudante(e.getNumeroMecanografico());
        }
    }

    // =========================================================
    // MÉTODOS DE ESCRITA (CRUD) - GESTOR
    // =========================================================

    /**
     * Adiciona um novo gestor.
     * @param g Gestor a adicionar
     * @return true se adicionado com sucesso
     */
    public boolean adicionarGestor(Gestor g) {
        gestorDAL.inserir(g);
        return gestores.add(g);
    }

    /**
     * Atualiza os dados de um gestor.
     * @param g Gestor com dados atualizados
     */
    public void atualizarGestor(Gestor g) {
        gestorDAL.atualizar(g);
        for (int i = 0; i < gestores.size(); i++) {
            if (gestores.get(i).getEmail().equalsIgnoreCase(g.getEmail())) {
                gestores.set(i, g);
                break;
            }
        }
    }

    // =========================================================
    // MÉTODOS DE ESCRITA (CRUD) - DOCENTE
    // =========================================================

    /**
     * Adiciona um novo docente.
     * @param d Docente a adicionar
     * @return true se adicionado com sucesso
     */
    public boolean adicionarDocente(Docente d) {
        docenteDAL.inserir(d);
        return docentes.add(d);
    }

    /**
     * Atualiza os dados de um docente.
     * @param d Docente com dados atualizados
     */
    public void atualizarDocente(Docente d) {
        docenteDAL.atualizar(d);
        for (int i = 0; i < docentes.size(); i++) {
            if (docentes.get(i).getSigla().equalsIgnoreCase(d.getSigla())) {
                docentes.set(i, d);
                break;
            }
        }
    }

    /**
     * Obtém um docente pela sigla.
     * @param sigla Sigla do docente
     * @return Docente encontrado ou null
     */
    public Docente obterDocentePorSigla(String sigla) {
        for (Docente d : docentes) if (d.getSigla().equalsIgnoreCase(sigla)) return d;
        return null;
    }

    /**
     * Verifica se uma sigla de docente já existe.
     * @param sigla Sigla a verificar
     * @return true se existir, false caso contrário
     */
    public boolean existeSiglaDocente(String sigla) {
        for (Docente d : docentes) if (d.getSigla().equalsIgnoreCase(sigla)) return true;
        return false;
    }

    /**
     * Gera uma sigla única para um docente.
     * @param nome Nome do docente
     * @return Sigla gerada
     */
    public String gerarSiglaDocente(String nome) {
        char prefixo = nome.trim().toUpperCase().charAt(0);
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random r = new Random();
        while (true) {
            String sigla = "" + prefixo + abc.charAt(r.nextInt(26)) + abc.charAt(r.nextInt(26));
            if (!existeSiglaDocente(sigla)) return sigla;
        }
    }

    // =========================================================
// MÉTODOS DE ESCRITA (CRUD) - DEPARTAMENTO
// =========================================================

    /**
     * Adiciona um novo departamento.
     * @param dep Departamento a adicionar
     * @return true se adicionado com sucesso
     */
    public boolean adicionarDepartamento(Departamento dep) {
        departamentoDAL.inserir(dep);
        return departamentos.add(dep);
    }

    /**
     * Atualiza os dados de um departamento.
     * @param dep Departamento com dados atualizados
     */
    public void atualizarDepartamento(Departamento dep) {
        departamentoDAL.atualizar(dep);
        for (int i = 0; i < departamentos.size(); i++) {
            if (departamentos.get(i).getSigla().equalsIgnoreCase(dep.getSigla())) {
                departamentos.set(i, dep);
                break;
            }
        }
    }

    /**
     * Verifica se uma sigla de departamento já existe.
     * @param sigla Sigla a verificar
     * @return true se existir, false caso contrário
     */
    public boolean existeSiglaDepartamento(String sigla) {
        for (Departamento d : departamentos) if (d.getSigla().equalsIgnoreCase(sigla)) return true;
        return false;
    }
    // =========================================================
    // MÉTODOS DE ESCRITA (CRUD) - CURSO
    // =========================================================

    /**
     * Adiciona um novo curso.
     * @param c Curso a adicionar
     * @return true se adicionado com sucesso
     */
    public boolean adicionarCurso(Curso c) {
        cursoDAL.inserir(c);
        return cursos.add(c);
    }

    /**
     * Atualiza os dados de um curso.
     * @param c Curso com dados atualizados
     */
    public void atualizarCurso(Curso c) {
        cursoDAL.atualizar(c);
        for (int i = 0; i < cursos.size(); i++) {
            if (cursos.get(i).getSigla().equalsIgnoreCase(c.getSigla())) {
                cursos.set(i, c);
                break;
            }
        }
    }

    /**
     * Obtém um curso pela sigla.
     * @param sigla Sigla do curso
     * @return Curso encontrado ou null
     */
    public Curso obterCursoPorSigla(String sigla) {
        for (Curso c : cursos) if (c.getSigla().equalsIgnoreCase(sigla)) return c;
        return null;
    }

    /**
     * Verifica se uma sigla de curso já existe.
     * @param sigla Sigla a verificar
     * @return true se existir, false caso contrário
     */
    public boolean existeSiglaCurso(String sigla) {
        for (Curso c : cursos) if (c.getSigla().equalsIgnoreCase(sigla)) return true;
        return false;
    }

    /**
     * Obtém os cursos disponíveis para matrícula (ativos e com estrutura válida).
     * @return Array de cursos disponíveis
     */
    public Curso[] obterCursosDisponiveisParaMatricula() {
        List<Curso> ativos = new ArrayList<>();
        for (Curso c : cursos) {
            if (c.isAtivo() && c.temEstruturaValida()) ativos.add(c);
        }
        return ativos.toArray(new Curso[0]);
    }

    // =========================================================
    // MÉTODOS DE ESCRITA (CRUD) - UNIDADE CURRICULAR
    // =========================================================

    /**
     * Adiciona uma nova unidade curricular.
     * @param uc Unidade curricular a adicionar
     * @return true se adicionada com sucesso
     */
    public boolean adicionarUnidadeCurricular(UnidadeCurricular uc) {

        ucDAL.inserir(uc);
        boolean resultado = ucs.add(uc);

        return resultado;
    }

    /**
     * Atualiza os dados de uma unidade curricular.
     * @param uc Unidade curricular com dados atualizados
     */
    public void atualizarUnidadeCurricular(UnidadeCurricular uc) {
        ucDAL.atualizar(uc);
        for (int i = 0; i < ucs.size(); i++) {
            if (ucs.get(i).getSigla().equalsIgnoreCase(uc.getSigla())) {
                ucs.set(i, uc);
                break;
            }
        }
    }

    /**
     * Obtém uma unidade curricular pela sigla.
     * @param sigla Sigla da UC
     * @return UC encontrada ou null
     */
    public UnidadeCurricular obterUCPorSigla(String sigla) {
        for (UnidadeCurricular uc : ucs) if (uc.getSigla().equalsIgnoreCase(sigla)) return uc;
        return null;
    }

    /**
     * Verifica se uma sigla de UC já existe.
     * @param sigla Sigla a verificar
     * @return true se existir, false caso contrário
     */
    public boolean existeSiglaUC(String sigla) {
        for (UnidadeCurricular uc : ucs) if (uc.getSigla().equalsIgnoreCase(sigla)) return true;
        return false;
    }

    /**
     * Obtém todos os estudantes inscritos numa determinada UC.
     * @param siglaUC Sigla da UC
     * @return Array de estudantes inscritos
     */
    public Estudante[] obterEstudantesPorUC(String siglaUC) {
        List<Estudante> lista = new ArrayList<>();
        for (Estudante e : estudantes) {
            if (e.estaInscrito(siglaUC)) lista.add(e);
        }
        return lista.toArray(new Estudante[0]);
    }

    /**
     * Reconstrói todas as associações entre entidades (Estudantes, UCs, Cursos, Docentes)
     * Este método deve ser chamado após carregar todos os dados do CSV.
     */
    public void reconstruirAssociacoes() {
        // Reatribuir o curso correto aos estudantes
        for (Estudante e : estudantes) {
            if (e.getCurso() != null) {
                Curso cursoCorreto = null;
                for (Curso c : cursos) {
                    if (c.getSigla().equals(e.getCurso().getSigla())) {
                        cursoCorreto = c;
                        break;
                    }
                }
                if (cursoCorreto != null && cursoCorreto != e.getCurso()) {
                    e.setCurso(cursoCorreto);
                }
            }
        }

        // Forçar a inscrição dos estudantes nas UCs, mas apenas se não tiverem dívidas
        int anoAtual = this.anoAtual;
        for (Estudante e : estudantes) {
            if (e.isAtivo() && e.getCurso() != null) {
                // Verificar apenas dívidas de anos anteriores (excluindo o ano atual)
                if (!Propina.temDividasAteAno(e, anoAtual - 1, anoAtual)) {
                    e.reconstruirPercurso();
                } else {
                    e.getPercursoAcademico().limparInscricoesAtivas();
                }
            }
        }

        // Associar docentes às UCs
        for (Docente d : docentes) {
            try {
                java.lang.reflect.Field fieldUcsLecionadas = Docente.class.getDeclaredField("ucsLecionadas");
                fieldUcsLecionadas.setAccessible(true);
                fieldUcsLecionadas.set(d, new UnidadeCurricular[20]);

                java.lang.reflect.Field fieldTotalUcsLecionadas = Docente.class.getDeclaredField("totalUcsLecionadas");
                fieldTotalUcsLecionadas.setAccessible(true);
                fieldTotalUcsLecionadas.setInt(d, 0);
            } catch (Exception ex) {}
        }

        for (UnidadeCurricular uc : ucs) {
            if (uc.getDocenteResponsavel() != null) {
                String siglaDoc = uc.getDocenteResponsavel().getSigla();
                for (Docente d : docentes) {
                    if (d.getSigla().equals(siglaDoc)) {
                        d.adicionarUcLecionada(uc);
                        d.adicionarUcResponsavel(uc);
                        break;
                    }
                }
            }
        }
    }

    // =========================================================
    // AUTENTICAÇÃO E PESQUISA
    // =========================================================

    /**
     * Autentica um utilizador pelo email e password.
     * @param email Email do utilizador
     * @param password Password (encriptada)
     * @return Utilizador autenticado ou null
     */
    public Utilizador autenticar(String email, String password) {
        for (Gestor g : gestores) if (g.getEmail().equalsIgnoreCase(email) && g.getPassword().equals(password)) return g;
        for (Docente d : docentes) if (d.getEmail().equalsIgnoreCase(email) && d.getPassword().equals(password)) return d;
        for (Estudante e : estudantes) if (e.getEmail().equalsIgnoreCase(email) && e.getPassword().equals(password)) return e;
        return null;
    }

    /**
     * Procura um utilizador pelo email.
     * @param email Email do utilizador
     * @return Utilizador encontrado ou null
     */
    public Utilizador procurarUtilizadorPorEmail(String email) {
        for (Gestor g : gestores) if (g.getEmail().equalsIgnoreCase(email)) return g;
        for (Docente d : docentes) if (d.getEmail().equalsIgnoreCase(email)) return d;
        for (Estudante e : estudantes) if (e.getEmail().equalsIgnoreCase(email)) return e;
        return null;
    }

    // =========================================================
    // GERAÇÃO DE IDENTIFICADORES
    // =========================================================

    /**
     * Gera um número mecanográfico único para um estudante.
     * @param anoInscricao Ano de inscrição
     * @return Número mecanográfico gerado
     */
    public int gerarNumeroMecanografico(int anoInscricao) {
        int contador = 0;
        for (Estudante e : estudantes) {
            if (e.getAnoPrimeiraInscricao() == anoInscricao) contador++;
        }
        return (anoInscricao * 10000) + (contador + 1);
    }

    // =========================================================
    // VERIFICAÇÃO DE AVALIAÇÕES EM FALTA
    // =========================================================

    /**
     * Verifica se todos os alunos ativos têm todas as avaliações lançadas
     * para as UCs em que estão inscritos.
     *
     * @return Um array de strings com as ocorrências em falta, ou array vazio se estiver tudo ok.
     *         Cada string tem o formato: "numMec;nome;siglaUC;nomeUC;numExistente;numNecessario"
     */
    public String[] verificarAvaliacoesEmFalta() {
        int countFaltas = 0;
        for (Estudante e : estudantes) {
            if (e != null && e.isAtivo() && e.getPercursoAcademico() != null) {
                for (int i = 0; i < e.getPercursoAcademico().getTotalUcsInscrito(); i++) {
                    UnidadeCurricular uc = e.getPercursoAcademico().getUcsInscrito()[i];
                    if (uc != null && uc.getNumAvaliacoes() != null) {
                        int numNecessario = uc.getNumAvaliacoes();
                        Avaliacao av = e.getAvaliacaoAtual(uc.getSigla());
                        int numExistente = (av != null) ? av.getTotalAvaliacoesLancadas() : 0;
                        if (numExistente < numNecessario) {
                            countFaltas++;
                        }
                    }
                }
            }
        }

        if (countFaltas == 0) return new String[0];

        String[] faltas = new String[countFaltas];
        int idx = 0;
        for (Estudante e : estudantes) {
            if (e != null && e.isAtivo() && e.getPercursoAcademico() != null) {
                for (int i = 0; i < e.getPercursoAcademico().getTotalUcsInscrito(); i++) {
                    UnidadeCurricular uc = e.getPercursoAcademico().getUcsInscrito()[i];
                    if (uc != null && uc.getNumAvaliacoes() != null) {
                        int numNecessario = uc.getNumAvaliacoes();
                        Avaliacao av = e.getAvaliacaoAtual(uc.getSigla());
                        int numExistente = (av != null) ? av.getTotalAvaliacoesLancadas() : 0;
                        if (numExistente < numNecessario) {
                            faltas[idx++] = e.getNumeroMecanografico() + ";" + e.getNome() + ";" +
                                    uc.getSigla() + ";" + uc.getNome() + ";" +
                                    numExistente + ";" + numNecessario;
                        }
                    }
                }
            }
        }
        return faltas;
    }

    // =========================================================
    // AVANÇO DE ANO LETIVO
    // =========================================================

    /**
     * Avança para o próximo ano letivo.
     * Incrementa o ano, reinicia o estado e processa todos os alunos.
     */
    public void avancarAno() {
        int anoAntigo = this.anoAtual;
        this.anoAtual++;
        this.anoIniciado = false;
        configDAL.salvarAno(anoAtual, anoIniciado);

        // Limpar número de avaliações de todas as UCs
        for (UnidadeCurricular uc : ucs) {
            uc.setNumAvaliacoes(null);
            ucDAL.atualizar(uc);  // Persistir a alteração no CSV
        }

        // Lista para remover estudantes que devem ser eliminados
        List<Estudante> paraRemover = new ArrayList<>();

        // Processar alunos ativos
        for (Estudante e : estudantes) {
            if (e.isAtivo()) {
                if (Propina.temDividas(e, anoAntigo)) {
                    e.setAtivo(false);
                    atualizarEstudante(e);
                } else {
                    e.processarFimDeAno(this.anoAtual);
                    atualizarEstudante(e);
                }
            }

            // Verificar estudantes inativos sem avaliações (para remover)
            if (!e.isAtivo()) {
                boolean temAvaliacoes = false;
                for (int i = 0; i < e.getTotalAvaliacoes(); i++) {
                    if (e.getAvaliacoes()[i] != null && e.getAvaliacoes()[i].getTotalAvaliacoesLancadas() > 0) {
                        temAvaliacoes = true;
                        break;
                    }
                }
                if (!temAvaliacoes && e.getTotalHistorico() == 0) {
                    paraRemover.add(e);
                }
            }
        }

        // Remover estudantes marcados
        for (Estudante e : paraRemover) {
            removerEstudante(e.getNumeroMecanografico());
        }
    }

    // =========================================================
    // INÍCIO DE ANO LETIVO
    // =========================================================

    /**
     * Desativa todos os cursos que possuem estrutura curricular inválida,
     * bem como todos os estudantes associados a esses cursos.
     * <p>
     * Um curso é considerado com estrutura inválida se não tiver pelo menos uma
     * Unidade Curricular ativa em cada um dos três anos curriculares (1º, 2º e 3º).
     * </p>
     * <p>
     * Para cada curso inválido:
     * <ul>
     *   <li>O estado do curso é alterado para <code>false</code> (inativo).</li>
     *   <li>Todos os estudantes cujo curso corresponda a essa sigla são também
     *       desativados (<code>setAtivo(false)</code>).</li>
     *   <li>As alterações são persistidas no respetivo ficheiro CSV através do
     *       repositório.</li>
     * </ul>
     * </p>
     * <p>
     * Este método é chamado durante o processo de início do ano letivo,
     * antes da geração do relatório de verificação, garantindo que apenas cursos
     * válidos permaneçam ativos.
     * </p>
     *
     * @see model.bll.Curso#temEstruturaValida()
     * @see model.dal.RepositorioDados#atualizarCurso(Curso)
     * @see model.dal.RepositorioDados#atualizarEstudante(Estudante)
     */
    public void desativarCursoEAlunos(Curso curso) {
        if (curso == null) return;
        curso.setAtivo(false);
        atualizarCurso(curso);
        for (Estudante e : estudantes) {
            if (e.getCurso() != null && e.getCurso().getSigla().equals(curso.getSigla())) {
                e.setAtivo(false);
                atualizarEstudante(e);
            }
        }
    }

    // =========================================================
    // CONFIGURAÇÃO DO ANO (ESTADO)
    // =========================================================

    /**
     * Define o estado do ano letivo (iniciado ou não).
     * @param iniciado true se iniciado, false caso contrário
     */
    public void setAnoIniciado(boolean iniciado) {
        this.anoIniciado = iniciado;
        configDAL.salvarAno(anoAtual, iniciado);
    }

    // =========================================================
    // LIMPAR DADOS (PARA TESTES)
    // =========================================================

    /**
     * Limpa todas as listas em memória.
     * Útil para testes ou recarga de dados.
     */
    public void limpar() {
        estudantes.clear();
        gestores.clear();
        docentes.clear();
        departamentos.clear();
        cursos.clear();
        ucs.clear();
    }
}

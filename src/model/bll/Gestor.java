package model.bll;

/**
 * Representa um Gestor (Administrador do Backoffice) no sistema.
 * No padrão MVC e princípios GRASP, esta classe atua como um Model e assume o
 * papel de "Creator" (Fábrica). O Gestor é a única entidade com privilégios para
 * instanciar estruturas fundacionais da instituição, como Departamentos, Cursos e UCs.
 * Herda as credenciais base e informações de contacto da superclasse Utilizador.
 */
public class Gestor extends Utilizador {

    // ---------- ATRIBUTOS ----------
    private String morada;
    private boolean ativo;


    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Gestor.
     * Inicializa uma conta de administração. Atribui valores padrão ("N/A") aos campos
     * do Utilizador que não se aplicam a um Gestor (como NIF ou Data de Nascimento obrigatória).
     *
     * @param email    O endereço de email institucional (login).
     * @param password A palavra-passe (encriptada) de acesso.
     * @param nome     O nome do gestor.
     * @param morada   A morada de contacto.
     */
    public Gestor(String email, String password, String nome, String morada) {
        // Invoca o construtor da superclasse preenchendo os dados não aplicáveis com "N/A"
        super(email, password, nome, "N/A", morada, "N/A", "N/A");
        this.morada = morada;
        this.ativo = true; // Uma conta de Gestor é criada sempre ativa por defeito
    }


    // ---------- GETTERS E SETTERS ----------

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public boolean isAtivo() {
        return ativo;
    }

    /**
     * Altera o estado da conta do Gestor (Ativo / Inativo).
     * Contas inativas perdem imediatamente o direito de aceder ao sistema.
     * @param ativo true para ativar a conta, false para revogar o acesso.
     */
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }


    // =========================================================
    // LÓGICA DE NEGÓCIO: PADRÃO CREATOR (FÁBRICA)
    // =========================================================

    /**
     * Fabrica uma nova instância de Departamento.
     * Garante arquiteturalmente que apenas um Gestor tem autoridade para instanciar departamentos.
     *
     * @param sigla A sigla do novo departamento.
     * @param nome  O nome por extenso do departamento.
     * @return Uma nova instância de Departamento pronta a ser adicionada ao Repositório.
     */
    public Departamento criarDepartamento(String sigla, String nome) {
        return new Departamento(sigla, nome);
    }

    /**
     * Fabrica uma nova instância de Curso.
     *
     * @param sigla        A sigla identificativa do curso.
     * @param nome         O nome completo do curso.
     * @param departamento O departamento ao qual o curso ficará alocado.
     * @return Uma nova instância de Curso.
     */
    public Curso criarCurso(String sigla, String nome, Departamento departamento) {
        return new Curso(sigla, nome, departamento);
    }

    /**
     * Fabrica uma nova instância de Unidade Curricular (UC).
     *
     * @param sigla              A sigla da disciplina.
     * @param nome               O nome extenso da disciplina.
     * @param anoCurricular      O ano do plano de estudos em que é lecionada (1, 2 ou 3).
     * @param docenteResponsavel O professor regente/responsável da disciplina.
     * @param numAvaliacoes      O número de avaliações planeadas para o ano letivo.
     * @return Uma nova instância de Unidade Curricular.
     */
    public UnidadeCurricular criarUnidadeCurricular(String sigla, String nome, int anoCurricular, Docente docenteResponsavel, int numAvaliacoes) {
        return new UnidadeCurricular(sigla, nome, anoCurricular, docenteResponsavel, numAvaliacoes);
    }
}
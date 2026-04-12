package model.bll;

/**
 * Representa um Docente da instituição de ensino, estendendo a classe base Utilizador.
 * Além dos dados demográficos, esta classe gere a carga letiva do docente,
 * distinguindo entre as unidades curriculares que leciona e aquelas pelas quais é o regente (responsável).
 */
public class Docente extends Utilizador {

    // ---------- ATRIBUTOS ----------
    private String sigla;
    private UnidadeCurricular[] ucsLecionadas;
    private int totalUcsLecionadas;
    private UnidadeCurricular[] ucsResponsavel;
    private int totalUcsResponsavel;
    private boolean ativo;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Docente.
     * Inicializa o docente com os dados de utilizador e prepara as estruturas
     * para armazenar até 20 unidades curriculares lecionadas e 20 sob coordenação.
     *
     * @param sigla           A sigla identificativa do docente (ex: ABC).
     * @param email           O email institucional de acesso ao sistema.
     * @param password        A password (encriptada) de acesso.
     * @param nome            O nome completo do docente.
     * @param nif             O Número de Identificação Fiscal.
     * @param morada          A morada de residência.
     * @param dataNascimento  A data de nascimento do docente.
     * @param emailPessoal    O email pessoal para comunicações externas e recuperação.
     */
    public Docente(String sigla, String email, String password, String nome, String nif, String morada, String dataNascimento, String emailPessoal) {
        // Chamada ao construtor da classe pai (Utilizador)
        super(email, password, nome, nif, morada, dataNascimento, emailPessoal);

        this.sigla = sigla;
        this.ativo = true; // Por defeito, o docente é criado em estado ativo

        // Inicialização das matrizes de Unidades Curriculares com limite fixo de 20
        this.ucsLecionadas = new UnidadeCurricular[20];
        this.totalUcsLecionadas = 0;

        this.ucsResponsavel = new UnidadeCurricular[20];
        this.totalUcsResponsavel = 0;
    }

    // ---------- GETTERS ----------

    public String getSigla() { return sigla; }

    public UnidadeCurricular[] getUcsLecionadas() { return ucsLecionadas; }

    public int getTotalUcsLecionadas() { return totalUcsLecionadas; }

    public UnidadeCurricular[] getUcsResponsavel() { return ucsResponsavel; }

    public int getTotalUcsResponsavel() { return totalUcsResponsavel; }

    public boolean isAtivo() { return ativo; }

    // ---------- SETTERS ----------

    public void setSigla(String sigla) { this.sigla = sigla; }

    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Associa uma nova Unidade Curricular à lista de disciplinas lecionadas pelo docente.
     *
     * @param uc A Unidade Curricular a ser adicionada à carga letiva.
     * @return true se a associação for bem-sucedida; false se o limite de 20 UCs já tiver sido atingido.
     */
    public boolean adicionarUcLecionada(UnidadeCurricular uc) {
        // Valida a disponibilidade de espaço no array de UCs lecionadas
        if (totalUcsLecionadas < ucsLecionadas.length) {
            ucsLecionadas[totalUcsLecionadas] = uc;
            totalUcsLecionadas++;
            return true;
        }
        return false;
    }

    /**
     * Regista o docente como coordenador ou responsável de uma determinada Unidade Curricular.
     *
     * @param uc A Unidade Curricular a ser coordenada pelo docente.
     * @return true se o registo for bem-sucedido; false se o limite de 20 UCs sob responsabilidade já tiver sido atingido.
     */
    public boolean adicionarUcResponsavel(UnidadeCurricular uc) {
        // Valida a disponibilidade de espaço no array de regências
        if (totalUcsResponsavel < ucsResponsavel.length) {
            ucsResponsavel[totalUcsResponsavel] = uc;
            totalUcsResponsavel++;
            return true;
        }
        return false;
    }
}
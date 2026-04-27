package model.bll;

/**
 * Representa um Docente da instituição de ensino, estendendo a classe base Utilizador.
 * No padrão MVC, atua como um Model de domínio puro, encapsulando os dados do professor
 * e garantindo as regras e limites associados à sua carga letiva (as disciplinas que leciona
 * e aquelas que coordena).
 */
public class Docente extends Utilizador {

    // ---------- ATRIBUTOS ----------
    private String sigla;
    private boolean ativo;

    // Estruturas de associação com as Unidades Curriculares (Agregação)
    private UnidadeCurricular[] ucsLecionadas;
    private int totalUcsLecionadas;

    private UnidadeCurricular[] ucsResponsavel;
    private int totalUcsResponsavel;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Docente.
     * Inicializa o docente com os dados demográficos (herdados de Utilizador) e prepara
     * as estruturas internas para gerir a sua alocação às disciplinas.
     *
     * @param sigla          A sigla identificativa única do docente (ex: ABC).
     * @param email          O email institucional de acesso ao sistema.
     * @param password       A password (encriptada) de acesso.
     * @param nome           O nome completo do docente.
     * @param nif            O Número de Identificação Fiscal.
     * @param morada         A morada de residência.
     * @param dataNascimento A data de nascimento do docente.
     * @param emailPessoal   O email pessoal secundário (para contactos e recuperações).
     */
    public Docente(String sigla, String email, String password, String nome, String nif, String morada, String dataNascimento, String emailPessoal) {
        // Chamada ao construtor da classe pai (Utilizador) para inicializar os dados comuns
        super(email, password, nome, nif, morada, dataNascimento, emailPessoal);

        this.sigla = sigla;
        this.ativo = true; // Por defeito, o docente inicia funções no estado ativo

        // O sistema permite alocar um Docente a um máximo estrutural de 20 UCs
        this.ucsLecionadas = new UnidadeCurricular[20];
        this.totalUcsLecionadas = 0;

        this.ucsResponsavel = new UnidadeCurricular[20];
        this.totalUcsResponsavel = 0;
    }

    // ---------- GETTERS ----------

    public String getSigla() {
        return sigla;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public UnidadeCurricular[] getUcsLecionadas() {
        return ucsLecionadas;
    }

    public int getTotalUcsLecionadas() {
        return totalUcsLecionadas;
    }

    public UnidadeCurricular[] getUcsResponsavel() {
        return ucsResponsavel;
    }

    public int getTotalUcsResponsavel() {
        return totalUcsResponsavel;
    }

    // ---------- SETTERS ----------

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }


    // =========================================================
    // MÉTODOS DE LÓGICA DE NEGÓCIO: GESTÃO DE CARGA LETIVA
    // =========================================================

    /**
     * Adiciona uma Unidade Curricular à carga letiva do docente (turmas que leciona ativamente).
     *
     * @param uc A Unidade Curricular a ser atribuída.
     * @return true se associada com sucesso; false se o limite máximo (20 UCs) for atingido.
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
     * Adiciona uma Unidade Curricular à lista de regências (coordenações) do docente.
     *
     * @param uc A Unidade Curricular pela qual o docente passa a ser o responsável principal.
     * @return true se o registo for bem-sucedido; false se exceder o limite de 20 UCs sob responsabilidade.
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

    /**
     * Remove uma Unidade Curricular da lista de regências do docente, reorganizando a matriz de forma contígua.
     * Este método é frequentemente acionado automaticamente pela própria UnidadeCurricular
     * quando um Gestor decide trocar o docente responsável.
     *
     * @param siglaUC A sigla da Unidade Curricular a remover das responsabilidades.
     * @return true se a UC foi encontrada e removida com sucesso, false caso contrário.
     */
    public boolean removerUcResponsavel(String siglaUC) {
        for (int i = 0; i < totalUcsResponsavel; i++) {
            if (ucsResponsavel[i] != null && ucsResponsavel[i].getSigla().equalsIgnoreCase(siglaUC)) {
                // Efetua um Shift-Left para não deixar buracos ('nulls') no meio do array
                for (int j = i; j < totalUcsResponsavel - 1; j++) {
                    ucsResponsavel[j] = ucsResponsavel[j + 1];
                }
                ucsResponsavel[totalUcsResponsavel - 1] = null;
                totalUcsResponsavel--;
                return true;
            }
        }
        return false;
    }


    // =========================================================
    // MÉTODOS DE LÓGICA DE NEGÓCIO: VALIDAÇÕES
    // =========================================================

    /**
     * Valida se é seguro suspender ou desativar a conta do docente no sistema.
     * Regra de Negócio: Um docente não pode ser desativado caso ainda tenha disciplinas
     * atribuídas à sua carga letiva, prevenindo que turmas fiquem sem professor a meio do semestre.
     *
     * @return true se o docente não tiver turmas atribuídas (seguro desativar), false se a regra impedir.
     */
    public boolean podeSerDesativado() {
        for (int i = 0; i < totalUcsResponsavel; i++) {
            UnidadeCurricular uc = ucsResponsavel[i];
            if (uc != null && uc.isAtivo()) {
                return false;
            }
        }
        return true;
    }
}
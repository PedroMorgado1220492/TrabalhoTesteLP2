package controller;

import model.bll.Avaliacao;
import model.bll.Curso;
import model.bll.UnidadeCurricular;
import model.bll.Propina;
import view.EstudanteView;
import model.bll.Estudante;
import model.dal.RepositorioDados;
import utils.Validador;
import utils.Seguranca; // Importação essencial para a gestão de passwords

/**
 * Controlador responsável pela gestão das operações e interações do Estudante.
 * Atua como intermediário entre a interface gráfica do estudante (EstudanteView)
 * e a camada de dados (RepositorioDados), garantindo a integridade do percurso académico.
 */
public class EstudanteController {

    private EstudanteView view;
    private Estudante estudanteLogado;
    private RepositorioDados repositorio;

    /**
     * Construtor do controlador do estudante.
     *
     * @param estudanteLogado A instância do estudante que iniciou a sessão.
     * @param repositorio     O repositório central com os dados do sistema.
     */
    public EstudanteController(Estudante estudanteLogado, RepositorioDados repositorio) {
        this.view = new EstudanteView();
        this.estudanteLogado = estudanteLogado;
        this.repositorio = repositorio;
    }

    /**
     * Inicia o ciclo principal do menu do estudante.
     * Mantém o utilizador na área reservada até que este decida sair ou desativar a conta.
     */
    public void iniciarMenu() {
        boolean running = true;
        while (running) {
            int opcao = view.mostrarMenuPrincipal();
            switch (opcao) {
                case 1: view.mostrarDadosFicha(estudanteLogado); break;
                case 2: menuAtualizar(); break;
                case 3: verPercurso(); break;
                case 4: gerirPropinas(); break;
                case 5:
                    // Se a conta for desativada, o ciclo encerra forçando o logout imediato
                    if (desativarConta()) running = false;
                    break;
                case 0:
                    view.msgSaida();
                    running = false;
                    break;
                default:
                    view.msgErroOpcao();
            }
        }
    }

    // ---------- MÉTODOS DE CONSULTA ACADÉMICA ----------

    /**
     * Reconstitui visualmente o percurso académico do estudante.
     * Organiza as Unidades Curriculares por ano e apresenta o estado de aprovação de cada uma.
     */
    private void verPercurso() {
        Curso curso = estudanteLogado.getCurso();
        if (curso == null) {
            view.msgErroSemCurso();
            return;
        }

        view.mostrarCabecalhoPercurso();
        // Itera pelos 3 anos curriculares padrão (Licenciatura)
        for (int ano = 1; ano <= 3; ano++) {
            view.mostrarAnoPercurso(ano);
            for (int i = 0; i < curso.getTotalUCs(); i++) {
                UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];
                if (uc.getAnoCurricular() == ano) {
                    String statusStr = processarStatusParaView(uc.getSigla());
                    view.mostrarLinhaUC(uc.getSigla(), uc.getNome(), ano, statusStr);
                }
            }
        }
    }

    /**
     * Determina o estado lógico de uma UC para o estudante (Inscrito, Concluído ou Pendente).
     *
     * @param sigla A sigla da Unidade Curricular.
     * @return String formatada com o estado e classificação correspondente.
     */
    private String processarStatusParaView(String sigla) {
        int estado = 0; // Por defeito: Não inscrito
        double nota = 0.0;

        // 1. Verifica se a UC faz parte das inscrições ativas do ano corrente
        if (estudanteLogado.estaInscrito(sigla)) {
            Avaliacao av = estudanteLogado.getAvaliacaoAtual(sigla);
            if (av != null) {
                estado = 1; // Inscrito e já possui notas lançadas
                nota = av.calcularMedia();
            } else {
                estado = 2; // Inscrito mas aguarda lançamento de notas
            }
        } else {
            // 2. Procura no histórico de anos transatos
            Avaliacao hist = estudanteLogado.getAvaliacaoHistorico(sigla);
            if (hist != null && hist.calcularMedia() >= 9.5) {
                estado = 3; // Concluído com sucesso (Aprovado)
                nota = hist.calcularMedia();
            }
        }

        return view.formatarStatusUC(estado, nota);
    }

    // ---------- MÉTODOS DE ATUALIZAÇÃO DE PERFIL ----------

    private void menuAtualizar() {
        boolean sub = true;
        while (sub) {
            int op = view.mostrarMenuAtualizarDados();
            switch (op) {
                case 1: atualizarNome(); break;
                case 2: atualizarNif(); break;
                case 3: atualizarMorada(); break;
                case 4: atualizarPassword(); break;
                case 0: sub = false; break;
                default: view.msgErroOpcao();
            }
        }
    }

    private void atualizarNome() {
        String n = view.pedirNovoNome();
        if (Validador.isNomeValido(n)) {
            estudanteLogado.setNome(n);
            view.msgSucesso();
        } else {
            view.msgErroDados();
        }
    }

    private void atualizarNif() {
        String nif = view.pedirNovoNif();
        if (Validador.isNifValido(nif)) {
            estudanteLogado.setNif(nif);
            view.msgSucesso();
        } else {
            view.msgErroDados();
        }
    }

    private void atualizarMorada() {
        estudanteLogado.setMorada(view.pedirNovaMorada());
        view.msgSucesso();
    }

    /**
     * Atualiza a password do estudante garantindo a integridade via Hash SHA-256.
     */
    private void atualizarPassword() {
        String passAtualRaw = view.pedirPassAtual();
        // Encripta o input para validar contra a Hash guardada no modelo
        String passAtualEnc = Seguranca.encriptar(passAtualRaw);

        if (passAtualEnc.equals(estudanteLogado.getPassword())) {
            String novaPassRaw = view.pedirNovaPass();
            String confirmacaoRaw = view.pedirConfirmacaoPass();

            if (!novaPassRaw.isEmpty() && novaPassRaw.equals(confirmacaoRaw)) {
                // Encriptamos a nova password antes da persistência
                estudanteLogado.setPassword(Seguranca.encriptar(novaPassRaw));
                view.msgSucesso();
            } else {
                view.msgErroPassNaoCoincidem();
            }
        } else {
            view.msgErroPassIncorreta();
        }
    }

    // ---------- GESTÃO FINANCEIRA (PROPINAS) ----------

    /**
     * Gere o fluxo de visualização de dívidas e processamento de pagamentos.
     */
    private void gerirPropinas() {
        Propina propina = estudanteLogado.getPropinaDoAno(repositorio.getAnoAtual());
        if (propina == null) {
            view.msgErroSemPropina();
            return;
        }

        view.mostrarDetalhesPropina(propina.getValorTotal(), propina.getValorPago(),
                propina.getValorEmDivida(), propina.getHistoricoPagamentos(),
                propina.getTotalPagamentos(), propina.isPagaTotalmente());

        if (propina.isPagaTotalmente()) return;

        double valor = calcularValorPagamento(propina);
        if (valor <= 0) return;

        // Regista o pagamento e sincroniza imediatamente com a base de dados CSV
        if (propina.registarPagamento(valor)) {
            view.msgSucesso();

            // 1. Gera o ficheiro e guarda o caminho
            String caminhoRecibo = model.bll.Recibo.gerarRecibo(estudanteLogado, valor, propina.getValorTotal(), propina.getValorEmDivida());

            // 2. Envia por email
            if (caminhoRecibo != null && estudanteLogado.getEmailPessoal() != null) {
                utils.ServicoEmail.enviarEmailComAnexo(
                        estudanteLogado.getEmailPessoal(),
                        "ISSMF - Recibo de Pagamento",
                        "Caro(a) Estudante,\n\nSegue em anexo o seu recibo de pagamento das propinas.\n\nCom os melhores cumprimentos,\nServiços Financeiros - ISSMF.",
                        caminhoRecibo
                );
            }

            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.msgErroDados();
        }
    }

    /**
     * Calcula o valor a pagar com base na opção escolhida, garantindo que
     * respeita o montante mínimo de uma prestação (10%).
     *
     * @param propina A propina atual do estudante.
     * @return O valor validado ou 0.0 se a validação falhar.
     */
    private double calcularValorPagamento(Propina propina) {
        double valorMinimoPrestacao = propina.getValorTotal() / 10;
        int op = view.mostrarOpcoesPagamento(propina.getValorEmDivida(), valorMinimoPrestacao);

        // 1. Switch apenas atribui o valor bruto escolhido
        double valorEscolhido = switch (op) {
            case 1 -> propina.getValorEmDivida(); // Liquidação Total
            case 2 -> Math.min(valorMinimoPrestacao, propina.getValorEmDivida()); // Prestação Mensal
            case 3 -> view.pedirValorLivre(); // Montante Personalizado
            default -> 0.0;
        };

        // 2. Se o utilizador cancelou (0), saí imediatamente
        if (valorEscolhido <= 0) {
            return 0.0;
        }

        // 3. O limite real é o menor entre uma prestação padrão ou o que resta da dívida
        double limiteMinimoAceitavel = Math.min(valorMinimoPrestacao, propina.getValorEmDivida());

        if (valorEscolhido < limiteMinimoAceitavel) {
            view.msgErroValorMinimo(limiteMinimoAceitavel);
            return 0.0;
        }

        return valorEscolhido;
    }

    // ---------- DESATIVAÇÃO DE CONTA ----------

    /**
     * Permite ao estudante suspender a sua conta de forma voluntária.
     * @return true se a desativação foi confirmada e processada.
     */
    private boolean desativarConta() {
        if (view.pedirConfirmacaoDesativacao()) {
            estudanteLogado.setAtivo(false);
            view.msgContaDesativada();
            return true;
        }
        return false;
    }
}
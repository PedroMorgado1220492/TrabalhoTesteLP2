import controller.MainController;
import view.MainView;
import model.dal.RepositorioDados;
import model.dal.ImportadorCSV;

/**
 * Classe principal (Entry Point) do Sistema de Gestão Académica ISSMF.
 * Esta classe é responsável por orquestrar o arranque da aplicação,
 * instanciando os componentes core do padrão MVC e delegando o controlo
 * ao MainController.
 */
public class Main {

    /**
     * Método principal que serve de gatilho para a execução do programa.
     * @param args Argumentos da linha de comandos (não utilizados).
     */
    public static void main(String[] args) {

        // Log de diagnóstico para ajudar na localização dos ficheiros CSV na base de dados
        System.out.println(">>> Diretoria de trabalho: " + System.getProperty("user.dir"));

        // 1. Instanciação da VIEW (Camada de Apresentação)
        MainView view = new MainView();

        // 2. Instanciação do MODEL (Camada de Dados - Repositório em Memória)
        RepositorioDados repositorio = new RepositorioDados();

        // 3. Carregar o ano atual persistido
        int ano = ImportadorCSV.importarAno("bd/ano.csv");
        repositorio.setAnoAtual(ano);

        // 4. Instanciação do CONTROLLER (Liga a View ao Model)
        MainController mc = new MainController(view, repositorio);

        // 5. Início do ciclo de vida do sistema
        mc.iniciarSistema();
    }
}
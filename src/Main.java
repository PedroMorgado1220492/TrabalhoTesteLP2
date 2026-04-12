import controller.MainController;
import view.MainView;
import model.dal.RepositorioDados;

public class Main {
    public static void main(String[] args) {

        System.out.println(">>> O Java está a procurar ficheiros na pasta: " + System.getProperty("user.dir"));

        // Criar o Ecrã (View)
        MainView view = new MainView();

        // Criar a Memória (Model/Repositorio)
        RepositorioDados repositorio = new RepositorioDados();

        // Criar o Controlador
        MainController mc = new MainController(view, repositorio);

        // Arrancar o sistema
        mc.iniciarSistema();
    }
}
package Vista;

import Controlador.ControladorUNO;
import Modelo.Partida;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        Partida partida = new Partida();
        ControladorUNO controlador = new ControladorUNO(partida);

        VistaPrincipal vistaPrincipal = new VistaPrincipal(controlador);
        controlador.registrarVista(vistaPrincipal);

        Scene scene = new Scene(vistaPrincipal);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Juego UNO");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


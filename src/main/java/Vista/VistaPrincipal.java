package Vista;

import Controlador.ControladorUNO;
import Controlador.VistaObserver;
import Modelo.Carta;
import Modelo.Jugador;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VistaPrincipal extends BorderPane implements VistaObserver {
    private final ControladorUNO controlador;
    private final Label lblInformacion;
    private final HBox contenedorCartas;

    public VistaPrincipal(ControladorUNO controlador) {
        this.controlador = controlador;
        this.lblInformacion = new Label("Bienvenido al juego de UNO");
        this.contenedorCartas = new HBox();

        VBox panelLateral = new VBox(lblInformacion);
        setLeft(panelLateral);
        setCenter(contenedorCartas);

        // Registramos la vista en el controlador para recibir actualizaciones remotas
        controlador.registrarVista(this);

        actualizar();
    }

    @Override
    public void actualizar() {
        contenedorCartas.getChildren().clear();

        Jugador jugadorActual = controlador.obtenerJugadorActual();
        if (jugadorActual == null) {
            lblInformacion.setText("Esperando inicio de la partida...");
            return;
        }

        for (int i = 0; i < jugadorActual.getCartas().size(); i++) {
            Carta carta = jugadorActual.getCartas().get(i);
            int indice = i; // para usar en la lambda
            Button btnCarta = new Button(carta.toString());
            btnCarta.setOnAction(e -> {
                try {
                    controlador.jugarCarta(indice);
                } catch (Exception ex) {
                    lblInformacion.setText("Error: " + ex.getMessage());
                }
            });
            contenedorCartas.getChildren().add(btnCarta);
        }

        lblInformacion.setText("Turno de: " + jugadorActual.getNombre());
    }
}

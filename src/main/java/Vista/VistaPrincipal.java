package Vista;


import Controlador.ControladorUNO;
import Controlador.VistaObserver;
import Modelo.Carta;
import Modelo.Color;
import Modelo.Jugador;
import javafx.scene.control.*;
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

        actualizar();
    }

    @Override
    public void actualizar() {
        contenedorCartas.getChildren().clear();
        Jugador jugadorActual = controlador.obtenerJugadorActual();
        for (Carta carta : jugadorActual.getCartas()) {
            Button btnCarta = new Button(carta.toString());
            btnCarta.setOnAction(e -> controlador.jugarCarta(jugadorActual.getCartas().indexOf(carta)));
            contenedorCartas.getChildren().add(btnCarta);
        }
        lblInformacion.setText("Turno de: " + jugadorActual.getNombre());
    }
}


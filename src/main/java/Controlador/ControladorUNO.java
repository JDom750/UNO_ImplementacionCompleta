// Controlador para el juego UNO
package Controlador;

import Modelo.*;
import java.rmi.RemoteException;
import java.util.*;

public class ControladorUNO {
    public final Partida partida;

    public ControladorUNO(Partida partida) {
        this.partida = partida;
    }

    // Inicia una nueva partida
    public void iniciarPartida(List<String> nombresJugadores) {
        partida.iniciarPartida(nombresJugadores);
    }

    // Maneja la acción de jugar una carta
    public void jugarCarta(int indiceCarta) {
        Jugador jugadorActual = partida.getJugadorActual();
        Carta carta = jugadorActual.getCartas().get(indiceCarta);
        partida.jugarCarta(carta);
    }

    // Pide al mazo una carta para el jugador actual
    public void robarCarta() {
        Jugador jugadorActual = partida.getJugadorActual();
        jugadorActual.tomarCarta(partida.robarCartaDelMazo()); // no va al mazo sino a l partida
        partida.pasarTurno(); //cambio para que al robar pase de turno
    }

    // Obtiene información del estado actual del juego
    public String obtenerEstadoJuego() {
        StringBuilder estado = new StringBuilder();
        estado.append("Turno de: ").append(partida.getJugadorActual().getNombre()).append("\n");
        estado.append("Cartas del jugador: ").append(partida.getJugadorActual().getCartas()).append("\n");
        try {
            estado.append("Última carta jugada: ").append(partida.getUltimaCartaJugadas()).append("\n");
        } catch (IllegalStateException e) { // Captura IllegalStateException
            estado.append("Aún no se han jugado cartas.\n"); // Mensaje alternativo
        }
        return estado.toString();
    }


    public boolean isPartidaEnCurso() {
        return partida.isPartidaEnCurso();
    }
}


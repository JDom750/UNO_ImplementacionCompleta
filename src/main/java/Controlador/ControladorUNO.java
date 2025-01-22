package Controlador;

import Modelo.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorUNO {
    private final Partida partida;
    private final List<VistaObserver> vistas; // Lista de observadores (vistas)

    public ControladorUNO(Partida partida) {
        this.partida = partida;
        this.vistas = new ArrayList<>();
    }

    // Registra una vista como observador
    public void registrarVista(VistaObserver vista) {
        vistas.add(vista);
    }

    // Notifica a todas las vistas que hubo un cambio
    private void notificarVistas() {
        for (VistaObserver vista : vistas) {
            vista.actualizar(); // Cada vista implementará cómo se actualiza
        }
    }

    // Inicia una nueva partida
    public void iniciarPartida(List<String> nombresJugadores) {
        partida.iniciarPartida(nombresJugadores);
        notificarVistas(); // Notificar que la partida fue iniciada
    }


    // Maneja la acción de jugar una carta
    public void jugarCarta(int indiceCarta) {
        Jugador jugadorActual = partida.getJugadorActual();
        Carta carta = jugadorActual.getCartas().get(indiceCarta);

        // Jugar la carta
        partida.jugarCarta(carta);

        // Si la carta requiere cambio de color, no pases el turno aún
        if (carta.getColor() == Color.SIN_COLOR) {
            notificarVistas(); // Notificar que la carta fue jugada
            return; // Esperar a que el jugador elija un color
        }

        // Si no requiere cambio de color, pasar el turno normalmente
        partida.pasarTurno();
        notificarVistas(); // Notificar que el turno cambió
    }

    // Pide al mazo una carta para el jugador actual
    public void robarCarta() {
        Jugador jugadorActual = partida.getJugadorActual();
        jugadorActual.tomarCarta(partida.robarCartaDelMazo());
        partida.pasarTurno();
        notificarVistas(); // Notificar que el turno cambió
    }

    // Maneja el cambio de color cuando se juega una carta especial
    public void manejarCambioDeColor(Color nuevoColor) {
        if (nuevoColor == Color.SIN_COLOR) {
            throw new IllegalArgumentException("El color no puede ser SIN_COLOR.");
        }

        // Cambiar el color y pasar el turno
        partida.cambiarColorActual(nuevoColor);
        partida.pasarTurno(); // Ahora sí cambia el turno
        notificarVistas(); // Notificar que el color y el turno cambiaron
    }

    // Métodos para obtener el estado del juego en un formato neutral
    public Jugador obtenerJugadorActual() {
        return partida.getJugadorActual();
    }

    public Carta obtenerUltimaCartaJugadas() {
        return partida.getUltimaCartaJugadas();
    }

    public Color obtenerColorActual() {
        return partida.getColorActual();
    }

    public boolean isPartidaEnCurso() {
        return partida.isPartidaEnCurso();
    }
}


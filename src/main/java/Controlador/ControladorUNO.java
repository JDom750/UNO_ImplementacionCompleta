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
        partida.jugarCarta(carta);
        notificarVistas(); // Notificar que se jugó una carta
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
        partida.cambiarColorActual(nuevoColor);
        notificarVistas(); // Notificar que el color cambió
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


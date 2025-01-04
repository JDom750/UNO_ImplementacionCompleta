// Nuevo modelo refactorizado para el juego UNO

package Modelo;

import java.util.*;

// Representa el estado de la partida y las reglas del juego
public class Partida extends Observable {
    private final Mazo mazo;
    private final List<Jugador> jugadores;
    private int turnoActual;
    private boolean partidaEnCurso;

    public Partida() {
        this.mazo = new Mazo();
        this.jugadores = new ArrayList<>();
        this.turnoActual = 0;
        this.partidaEnCurso = false;
    }

    // Inicializa la partida
    public void iniciarPartida(List<String> nombresJugadores) {
        for (String nombre : nombresJugadores) {
            jugadores.add(new Jugador(nombre));
        }
        mazo.barajar();
        repartirCartasIniciales();
        partidaEnCurso = true;
        setChanged();
        notifyObservers("La partida ha comenzado");
    }

    private void repartirCartasIniciales() {
        for (Jugador jugador : jugadores) {
            for (int i = 0; i < 7; i++) {
                jugador.tomarCarta(mazo.robarCarta());
            }
        }
    }

    // Lógica para jugar una carta
    public void jugarCarta(Carta carta) {
        Jugador jugadorActual = jugadores.get(turnoActual);
        if (!jugadorActual.puedeJugar(carta)) {
            throw new IllegalArgumentException("Carta inválida");
        }

        jugadorActual.jugarCarta(carta);
        mazo.descartar(carta);

        // Notificar a los observadores
        setChanged();
        notifyObservers("El jugador " + jugadorActual.getNombre() + " jugó: " + carta);

        // Verificar si ganó
        if (jugadorActual.getCartas().isEmpty()) {
            partidaEnCurso = false;
            setChanged();
            notifyObservers("El jugador " + jugadorActual.getNombre() + " ha ganado la partida!");
            return;
        }

        // Pasar turno
        pasarTurno();
    }

    public void pasarTurno() {
        turnoActual = (turnoActual + 1) % jugadores.size();
        setChanged();
        notifyObservers("Es el turno de " + jugadores.get(turnoActual).getNombre());
    }

    public boolean isPartidaEnCurso() {
        return partidaEnCurso;
    }

    public List<Jugador> getJugadores() {
        return Collections.unmodifiableList(jugadores);
    }

    public Jugador getJugadorActual() {
        return jugadores.get(turnoActual);
    }

    public Mazo getMazo() {
        return mazo;
    }

    // Métodos delegados al mazo siguiendo el principio de encapsulamiento y delegacion
    public Carta robarCartaDelMazo() {
        return mazo.robarCarta();
    }

    public Carta getUltimaCartaJugadas() {
        return mazo.getUltimaCartaJugadas();
    }
}

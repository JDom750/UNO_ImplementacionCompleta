package Modelo;

import java.util.*;

public class Partida extends Observable {
    private static final int MIN_JUGADORES = 2; // Mínimo permitido
    private static final int MAX_JUGADORES = 10; // Máximo permitido
    private final Mazo mazo;
    private final List<Jugador> jugadores;
    private int turnoActual;
    private boolean partidaEnCurso;
    private boolean direccionNormal; // Para manejar la dirección de los turnos
    private Color colorActual; // Color actual en juego

    public Color getColorActual() {
        return colorActual;
    }

    public Partida() {
        this.mazo = new Mazo();
        this.jugadores = new ArrayList<>();
        this.turnoActual = 0;
        this.partidaEnCurso = false;
        this.direccionNormal = true; // Por defecto, los turnos avanzan en sentido normal
        this.colorActual = Color.SIN_COLOR; // Inicialmente, no hay un color actual definido
    }

    public void iniciarPartida(List<String> nombresJugadores) {
        if (nombresJugadores.size() < MIN_JUGADORES || nombresJugadores.size() > MAX_JUGADORES) {
            throw new IllegalArgumentException("El número de jugadores debe estar entre " + MIN_JUGADORES + " y " + MAX_JUGADORES + ".");
        }

        for (String nombre : nombresJugadores) {
            jugadores.add(new Jugador(nombre));
        }

        mazo.barajar();
        repartirCartasIniciales();
        partidaEnCurso = true;

        Carta primeraCarta = mazo.robarCarta();
        while (primeraCarta.getColor() == Color.SIN_COLOR) {
            mazo.descartar(primeraCarta);
            primeraCarta = mazo.robarCarta();
        }
        mazo.descartar(primeraCarta);
        this.colorActual = primeraCarta.getColor();

        notificarEvento(new Evento("INICIO_PARTIDA", colorActual));
    }

    private void repartirCartasIniciales() {
        for (Jugador jugador : jugadores) {
            for (int i = 0; i < 7; i++) {
                jugador.tomarCarta(mazo.robarCarta());
            }
        }
    }

    public void jugarCarta(Carta carta) {
        Jugador jugadorActual = jugadores.get(turnoActual);
        Carta ultimaCarta = mazo.getUltimaCartaJugadas();

        if (!esCartaValida(carta, ultimaCarta)) {
            throw new IllegalArgumentException("La carta no coincide con el color o número actual.");
        }

        if (carta.getValor().equals(Numero.MASCUATRO) && jugadorActual.tieneCartaDelColor(colorActual)) {
            throw new IllegalArgumentException("No puedes jugar +4 si tienes cartas del color actual.");
        }

        jugadorActual.jugarCarta(carta);
        mazo.descartar(carta);

        // Manejar cartas especiales
        manejarCartaEspecial(carta);

        if (jugadorActual.getCartas().isEmpty()) {
            finalizarPartida(jugadorActual);
            return;
        }

        if (carta.getColor() != Color.SIN_COLOR) {
            colorActual = carta.getColor();
        }

        pasarTurno();
    }

    private boolean esCartaValida(Carta carta, Carta ultimaCarta) {
        return carta.getColor().equals(colorActual) ||
                carta.getValor().equals(ultimaCarta.getValor()) ||
                carta.getValor().equals(Numero.CAMBIOCOLOR) ||
                carta.getValor().equals(Numero.MASCUATRO);
    }

    private void manejarCartaEspecial(Carta carta) {
        switch (carta.getValor()) {
            case CAMBIOSENTIDO:
                direccionNormal = !direccionNormal;
                break;
            case SALTARSE:
                pasarTurno(); // Salta el siguiente turno
                break;
            case MASDOS:
                robarCartasSiguientes(2);
                break;
            case MASCUATRO:
                robarCartasSiguientes(4);
                break;
            default:
                // No es carta especial
                break;
        }
    }

    private void robarCartasSiguientes(int cantidad) {
        int siguienteJugador = (direccionNormal) ?
                (turnoActual + 1) % jugadores.size() :
                (turnoActual - 1 + jugadores.size()) % jugadores.size();

        for (int i = 0; i < cantidad; i++) {
            jugadores.get(siguienteJugador).tomarCarta(mazo.robarCarta());
        }

        //setChanged();
        //notifyObservers("El jugador " + jugadores.get(siguienteJugador).getNombre() + " ha robado " + cantidad + " cartas");
        notificarEvento(new Evento("ROBAR_CARTAS", jugadores.get(siguienteJugador)));
    }

    public void cambiarColorActual(Color nuevoColor) {
        if (nuevoColor == null || nuevoColor == Color.SIN_COLOR) {
            throw new IllegalArgumentException("El color ingresado no es válido.");
        }
        this.colorActual = nuevoColor;

        notificarEvento(new Evento("CAMBIO_COLOR", nuevoColor));
    }

    public void pasarTurno() {
        turnoActual = (direccionNormal) ?
                (turnoActual + 1) % jugadores.size() :
                (turnoActual - 1 + jugadores.size()) % jugadores.size();

        notificarEvento(new Evento("CAMBIO_TURNO", jugadores.get(turnoActual)));
    }

    private void finalizarPartida(Jugador jugadorGanador) {
        partidaEnCurso = false;
        notificarEvento(new Evento("FIN_PARTIDA", jugadorGanador));
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

    public Carta robarCartaDelMazo() {
        return mazo.robarCarta();
    }

    public Carta getUltimaCartaJugadas() {
        return mazo.getUltimaCartaJugadas();
    }

    private void notificarEvento(Evento evento) {
        setChanged();
        notifyObservers(evento);
    }
}

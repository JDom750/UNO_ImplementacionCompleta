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

        // Validar cantidad de jugadores
        if (nombresJugadores.size() < MIN_JUGADORES || nombresJugadores.size() > MAX_JUGADORES) {
            throw new IllegalArgumentException("El número de jugadores debe estar entre " + MIN_JUGADORES + " y " + MAX_JUGADORES + ".");
        }
        for (String nombre : nombresJugadores) {
            jugadores.add(new Jugador(nombre));
        }
        mazo.barajar();
        repartirCartasIniciales();
        partidaEnCurso = true;

        // Establecer el color inicial basado en la primera carta jugada
        Carta primeraCarta = mazo.robarCarta();

        //Verificar si la carta inicial es sin color  --->VER SI ESTA REGAL ES ASI
        if (primeraCarta.getColor() == Color.SIN_COLOR) {
            while (primeraCarta.getColor() == Color.SIN_COLOR) {
                mazo.descartar(primeraCarta);
                primeraCarta = mazo.robarCarta();
            }
        }
        mazo.descartar(primeraCarta);
        this.colorActual = primeraCarta.getColor();

        setChanged();
        notifyObservers("La partida ha comenzado. Color inicial: " + colorActual);
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

        // Actualizar el color actual si es necesario
//        if (carta.getValor().equals(Numero.CAMBIOCOLOR) || carta.getValor().equals(Numero.MASCUATRO)) {
//            cambiarColorActual(Color.ROJO); // Este valor debería venir del jugador (ejemplo aquí)
//        } else {
//            colorActual = carta.getColor();
//        }

        // Notificar acción
//        setChanged();
//        notifyObservers("El jugador " + jugadorActual.getNombre() + " jugó: " + carta);

        // Manejar cartas especiales
        manejarCartaEspecial(carta);

        // Verificar si ganó
        if (jugadorActual.getCartas().isEmpty()) {
            finalizarPartida("El jugador " + jugadorActual.getNombre() + " ha ganado la partida!");
            return;
        }
        // se actualiza el color actual
        if (carta.getColor() != Color.SIN_COLOR) {
            colorActual = carta.getColor();
        }

        // Pasar turno
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

        setChanged();
        notifyObservers("El jugador " + jugadores.get(siguienteJugador).getNombre() + " ha robado " + cantidad + " cartas");
    }

    public void cambiarColorActual(Color nuevoColor) {
        if (nuevoColor == null || nuevoColor == Color.SIN_COLOR) {
            throw new IllegalArgumentException("El color ingresado no es válido.");
        }
        this.colorActual = nuevoColor;

        setChanged();
        notifyObservers("El color actual ha cambiado a: " + nuevoColor);
    }

    public void pasarTurno() {
        turnoActual = (direccionNormal) ?
                (turnoActual + 1) % jugadores.size() :
                (turnoActual - 1 + jugadores.size()) % jugadores.size();

        setChanged();
        notifyObservers("Es el turno de " + jugadores.get(turnoActual).getNombre());
    }

    private void finalizarPartida(String mensaje) {
        partidaEnCurso = false;
        setChanged();
        notifyObservers(mensaje);
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
}

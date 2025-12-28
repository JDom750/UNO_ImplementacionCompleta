package Modelo;

import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

import java.rmi.RemoteException;
import java.io.Serializable;
import java.util.*;

/**
 * Clase que maneja la lógica de una partida de UNO.
 * Implementa IPartidaRemota para permitir el uso vía RMI.
 */
public class Partida extends ObservableRemoto implements IPartidaRemota, Serializable {

    private static final int MIN_JUGADORES = 2;
    private static final int MAX_JUGADORES = 10;

    private final Mazo mazo;
    private final List<Jugador> jugadores;
    private int turnoActual;
    private boolean partidaEnCurso;
    private boolean direccionNormal;
    private Color colorActual;
    private boolean estadoEsperandoColor;
    // Índice del jugador que jugó la última carta (se usa cuando esperamos color)
    // Para saber quién puede haber quedado sin cartas y declarar ganador
    private int indiceJugadorUltimaJugada = -1;

    public Partida() throws RemoteException {
        super();
        this.mazo = new Mazo();
        this.jugadores = new ArrayList<>();
        this.turnoActual = 0;
        this.partidaEnCurso = false;
        this.direccionNormal = true;
        this.colorActual = Color.SIN_COLOR;
        this.estadoEsperandoColor = false;
    }

    @Override
    public Color getColorActual() throws RemoteException {
        return colorActual;
    }

    @Override
    public synchronized void iniciarPartida(List<String> nombresJugadores) throws RemoteException {
        if (nombresJugadores.size() < MIN_JUGADORES || nombresJugadores.size() > MAX_JUGADORES) {
            throw new IllegalArgumentException("El número de jugadores debe estar entre " + MIN_JUGADORES + " y " + MAX_JUGADORES + ".");
        }

        // Reiniciar estado interno (por si se reinicia la partida)
        jugadores.clear();
        turnoActual = 0;
        direccionNormal = true;
        estadoEsperandoColor = false;
        indiceJugadorUltimaJugada = -1;
        partidaEnCurso = false;
        colorActual = Color.SIN_COLOR; // limpiar color previo

        // Reiniciar mazo (muy importante si hay múltiples partidas)
        mazo.reiniciar();
        mazo.barajar();

        for (String nombre : nombresJugadores) {
            jugadores.add(new Jugador(nombre));
        }

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

    private synchronized void repartirCartasIniciales() throws RemoteException {
        for (Jugador jugador : jugadores) {
            jugador.vaciarMano(); // <--- AGREGAR ESTO (limpia las cartas viejas)
            for (int i = 0; i < 7; i++) {
                jugador.tomarCarta(mazo.robarCarta());
            }
        }
    }
    /**
     * Juega una carta del jugador actual. Si la carta es CAMBIOCOLOR o MASCUATRO
     * se marca estadoEsperandoColor = true y se devuelve el control a la vista para que pida el color.
     * Para otras cartas se aplican los efectos inmediatamente.
     */
//    public synchronized void jugarCarta(Carta carta) throws RemoteException {
//        if (!partidaEnCurso) {
//            throw new IllegalStateException("No hay una partida en curso.");
//        }
//
//        Jugador jugadorActual = jugadores.get(turnoActual);
//        Carta ultimaCarta = mazo.getUltimaCartaJugadas();
//
//        if (!esCartaValida(carta, ultimaCarta)) {
//            throw new IllegalArgumentException("La carta no coincide con el color o número actual.");
//        }
//
//        // Validación de +4: solo se puede si no hay cartas del color actual
//        if (carta.getValor().equals(Numero.MASCUATRO) && jugadorTieneDelColor(jugadorActual, colorActual)) {
//            throw new IllegalArgumentException("No puedes jugar +4 si tienes cartas del color actual.");
//        }
//
//        // Remover la carta de la mano y descartarla (se hace siempre)
//        jugadorActual.jugarCarta(carta);
//        mazo.descartar(carta);
//
//        // Guardamos quién fue el jugador que jugó (para finalizar después, si corresponde)
//        indiceJugadorUltimaJugada = turnoActual;
//
//        // Si la carta requiere que el jugador elija color, activamos el flag
//        if (carta.getValor() == Numero.CAMBIOCOLOR || carta.getValor() == Numero.MASCUATRO) {
//            estadoEsperandoColor = true;
//            notificarEvento(new Evento("ESPERANDO_COLOR", jugadores.get(turnoActual).getNombre()));
//            // No aplicamos efectos ahora; se aplicarán cuando se llame a cambiarColorActual(...)
//            return;
//        }
//        // Para cualquier otra carta, aplicamos efectos inmediatamente
//        manejarCartaEspecial(carta);
//
//        // Verificar si ganó (se chequea después de aplicar efectos)
//        if (!jugadorActual.tieneCartas()) {
//            finalizarPartida(jugadorActual);
//            return;
//        }
//
//        // Actualizar color actual cuando la carta tiene color
//        if (carta.getColor() != Color.SIN_COLOR) {
//            colorActual = carta.getColor();
//        }
//
//        notificarEvento(new Evento("JUGAR_CARTA", carta));
//    }
    public synchronized void jugarCarta(int indiceCarta) throws RemoteException {

        if (!partidaEnCurso) {
            throw new IllegalStateException("No hay una partida en curso.");
        }

        if (jugadores.isEmpty()) {
            throw new IllegalStateException("No hay jugadores en la partida.");
        }

        Jugador jugadorActual = jugadores.get(turnoActual);

        if (indiceCarta < 0 || indiceCarta >= jugadorActual.getCartas().size()) {
            throw new IllegalArgumentException("Índice de carta inválido.");
        }

        Carta carta = jugadorActual.getCartas().get(indiceCarta);
        Carta ultima = null;
        try {
            ultima = mazo.getUltimaCartaJugadas();
        } catch (IllegalStateException e) {
            // si no hay ultima carta en descartes, ultima queda null (fallback)
            ultima = null;
        }

        // Validación general
        if (!esCartaValida(carta, ultima)) {
            throw new IllegalArgumentException("La carta no coincide con color/valor.");
        }

        // Validación +4
        if (carta.getValor() == Numero.MASCUATRO &&
                jugadorTieneDelColor(jugadorActual, colorActual)) {
            throw new IllegalArgumentException("No podés jugar +4 si tenés el color actual.");
        }

        // Remover carta del jugador y descartarla
        jugadorActual.jugarCarta(carta);
        mazo.descartar(carta);

        indiceJugadorUltimaJugada = turnoActual;

        // Si requiere elegir color: setear flag y notificar; la vista pedirá color.
        if (carta.getValor() == Numero.CAMBIOCOLOR || carta.getValor() == Numero.MASCUATRO) {
            estadoEsperandoColor = true;
            notificarEvento(new Evento("ESPERANDO_COLOR", jugadorActual.getNombre()));
            return;
        }

        // Manejar efectos especiales. manejarCartaEspecial devuelve true si ya avanzó el turno.
        boolean yaAvanzoTurno = manejarCartaEspecial(carta);

        // Si el jugador quedó sin cartas -> finalizar y notificar (antes de pasar turno)
        if (!jugadorActual.tieneCartas()) {
            finalizarPartida(jugadorActual);
            return;
        }

        // Actualizar color si la carta tiene color
        if (carta.getColor() != Color.SIN_COLOR) {
            colorActual = carta.getColor();
        }

        // Si manejarCartaEspecial NO avanzó el turno, para cartas normales debemos avanzar turno.
        if (!yaAvanzoTurno) {
            pasarTurno();
        }

        notificarEvento(new Evento("JUGAR_CARTA", carta));
    }


    private synchronized boolean esCartaValida(Carta carta, Carta ultimaCarta) {
        // Si alguna de las condiciones es true, la carta es válida.
        // Consideramos comodines válidos siempre.
        if (carta.getValor().equals(Numero.CAMBIOCOLOR) || carta.getValor().equals(Numero.MASCUATRO)) {
            return true;
        }
        if (ultimaCarta == null) {
            return true; // fallback defensivo
        }
        return carta.getColor().equals(colorActual) ||
                carta.getValor().equals(ultimaCarta.getValor());
    }

    /**
     * Ahora esta lógica está acá en lugar de Jugador.
     */
    private synchronized boolean jugadorTieneDelColor(Jugador jugador, Color color) {
        for (Carta carta : jugador.getCartas()) {
            if (carta.getColor() == color) {
                return true;
            }
        }
        return false;
    }

    private synchronized boolean manejarCartaEspecial(Carta carta) throws RemoteException {
        switch (carta.getValor()) {
            case CAMBIOSENTIDO:
                // invertir dirección y NO avanzar turno aquí -> el juego avanzará al siguiente
                // pero según la regla: invertir + luego avanzar al "nuevo siguiente"
                direccionNormal = !direccionNormal;
                // No avanzamos turno aquí (dejamos que jugarCarta() lo haga después)
                return false;

            case SALTARSE:
                // Saltar el siguiente jugador: pasarTurno() lo mueve al siguiente ya
                pasarTurno();
                pasarTurno();
                return true;

            case MASDOS:
                // El siguiente jugador roba 2 y se saltea (pasarTurno ya saltea)
                robarCartasSiguientes(2);
                pasarTurno();
                pasarTurno(); //Mueve el turno al siguiente jugador
                return true;

            // MASCUATRO lo manejamos cuando se elige color (cambiarColorActual)
            default:
                // cartas numéricas u otras => no avanzó el turno aquí
                return false;
        }
    }


    private synchronized void robarCartasSiguientes(int cantidad) throws RemoteException {
        if (jugadores.isEmpty()) return;

        int siguienteJugador = (direccionNormal) ?
                (turnoActual + 1) % jugadores.size() :
                (turnoActual - 1 + jugadores.size()) % jugadores.size();

        Jugador j = jugadores.get(siguienteJugador);

        for (int i = 0; i < cantidad; i++) {
            Carta c = mazo.robarCarta();
            // si no hay cartas en mazo y no se puede reponer, mazo.robarCarta() podría devolver null
            if (c == null) continue;
            j.tomarCarta(c);
        }

        // No enviar el jugador entero a la vista RMI:
        notificarEvento(new Evento("ROBAR_CARTAS", j.getNombre()));
    }

    /**
     * Este método es invocado por la vista a través del controlador cuando el jugador eligió color
     * tras haber jugado un CAMBIOCOLOR o MASCUATRO. Aquí se aplican los efectos correspondientes
     * (en particular, +4 se aplica ahora), se pasa el turno y se limpia el estadoEsperandoColor.
     */
    @Override
    public synchronized void cambiarColorActual(Color nuevoColor) throws RemoteException {
        if (!estadoEsperandoColor) {
            throw new IllegalStateException("No se esperaba elección de color en este momento.");
        }
        if (nuevoColor == null || nuevoColor == Color.SIN_COLOR) {
            throw new IllegalArgumentException("El color ingresado no es válido.");
        }
        this.colorActual = nuevoColor;
        estadoEsperandoColor = false;
        // Revisar si la última carta jugada fue MASCUATRO: en ese caso aplicamos el efecto ahora.
        Carta ultima = mazo.getUltimaCartaJugadas();
        if (ultima != null && ultima.getValor() == Numero.MASCUATRO) {
            // Aplicar +4 al siguiente jugador y salteo de turno
            robarCartasSiguientes(4);
            pasarTurno();
            pasarTurno(); //saltea al afectado
        } else {
            // Si fue CAMBIOCOLOR, simplemente avanzamos al siguiente jugador
            pasarTurno();
        }

        // Si el jugador que jugó quedó sin cartas (caso: jugó su última carta que era comodín/+4),
        // ahora declaramos ganador (se aplica después de efectos).
        if (indiceJugadorUltimaJugada >= 0) {
            Jugador posibleGanador = jugadores.get(indiceJugadorUltimaJugada);
            if (!posibleGanador.tieneCartas()) {
                finalizarPartida(posibleGanador);
                indiceJugadorUltimaJugada = -1;
                return;
            }
            indiceJugadorUltimaJugada = -1;
        }

        notificarEvento(new Evento("CAMBIO_COLOR", nuevoColor));
    }

    @Override
    public synchronized void pasarTurno() throws RemoteException {
        if (jugadores.isEmpty()) return;

        turnoActual = (direccionNormal) ?
                (turnoActual + 1) % jugadores.size() :
                (turnoActual - 1 + jugadores.size()) % jugadores.size();

        notificarEvento(new Evento("CAMBIO_TURNO", jugadores.get(turnoActual).getNombre()));
    }

    private synchronized void finalizarPartida(Jugador jugadorGanador) throws RemoteException {
        partidaEnCurso = false;
        notificarEvento(new Evento("FIN_PARTIDA", jugadorGanador.getNombre()));
    }

    @Override
    public synchronized boolean isPartidaEnCurso() throws RemoteException {
        return partidaEnCurso;
    }

    @Override
    public synchronized List<Jugador> getJugadores() throws RemoteException {
        return Collections.unmodifiableList(jugadores);
    }

    @Override
    public synchronized Jugador getJugadorActual() throws RemoteException {
        return jugadores.get(turnoActual);
    }

    public synchronized Mazo getMazo() throws RemoteException {
        return mazo;
    }

    @Override
    public synchronized Carta robarCartaDelMazo() throws RemoteException {
        Carta carta = mazo.robarCarta();
        jugadores.get(turnoActual).tomarCarta(carta);
        notificarEvento(new Evento("ROBAR_CARTA", carta));
        return carta;
    }

    @Override
    public synchronized Carta getUltimaCartaJugadas() throws RemoteException {
        return mazo.getUltimaCartaJugadas();
    }

    private synchronized void notificarEvento(Evento evento) {
        try {
            notificarObservadores(evento);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: reemplazar con logger
        }
    }

    public synchronized boolean isEstadoEsperandoColor() {
        return estadoEsperandoColor;
    }

    public synchronized void setEstadoEsperandoColor(boolean estadoEsperandoColor) {
        this.estadoEsperandoColor = estadoEsperandoColor;
    }

    //-------------------------------------------------------------------------
    public synchronized void registrarJugador(String nombre) {
        if (partidaEnCurso) return; // si ya arrancó no se puede
        jugadores.add(new Jugador(nombre));
        notificarEvento(new Evento("JUGADOR_REGISTRADO", nombre));

//        if (jugadores.size() >= MIN_JUGADORES) {            //SI LO MANTENGO DE ESTA MANERA SIEMPRE INICIA TAN PRONTO SE REGISTREN 2 JUGADORES Y NO podrai por ejemplo iniciar una partida con 3 jugadores
//            iniciarPartidaInterna();
//        }
    }

    public synchronized void iniciarJuego() throws RemoteException {
        // BLINDAJE: Si ya está en curso, ignoramos la segunda llamada
        if (partidaEnCurso) return;

        if (jugadores.size() < MIN_JUGADORES) {
            throw new IllegalStateException("Faltan jugadores.");
        }
        iniciarPartidaInterna();
    }

    private synchronized void iniciarPartidaInterna() {
        try {
            // Reiniciar estado interno
            turnoActual = 0;
            direccionNormal = true;
            estadoEsperandoColor = false;
            indiceJugadorUltimaJugada = -1;

            // Reiniciar y mezclar mazo
            mazo.reiniciar();
            mazo.barajar();

            // Repartir cartas
            repartirCartasIniciales();

            partidaEnCurso = true;

            // Seleccionar primera carta válida
            Carta primeraCarta = mazo.robarCarta();
            while (primeraCarta.getColor() == Color.SIN_COLOR) {
                mazo.descartar(primeraCarta);
                primeraCarta = mazo.robarCarta();
            }
            mazo.descartar(primeraCarta);
            this.colorActual = primeraCarta.getColor();

            // Notificar inicio
            notificarEvento(new Evento("INICIO_PARTIDA", colorActual));
            notificarEvento(new Evento(
                    "CAMBIO_TURNO",
                    jugadores.get(turnoActual).getNombre()
            ));

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

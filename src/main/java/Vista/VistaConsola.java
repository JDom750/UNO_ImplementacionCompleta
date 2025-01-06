package Vista;

import Controlador.ControladorUNO;
import Modelo.Carta;
import Modelo.Color;
import Modelo.Jugador;
import Modelo.Partida;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VistaConsola {
    private final ControladorUNO controlador;
    private final Scanner scanner;

    public VistaConsola(ControladorUNO controlador) {
        this.controlador = controlador;
        this.scanner = new Scanner(System.in);
    }

    public void iniciarJuego() {
        System.out.println("¡Bienvenido a UNO!");

        int numeroJugadores = obtenerNumeroJugadores();
        List<String> nombresJugadores = obtenerNombresJugadores(numeroJugadores);

        try {
            controlador.iniciarPartida(nombresJugadores);
        } catch (IllegalArgumentException e) {
            System.out.println("Error al iniciar la partida: " + e.getMessage());
            return;
        }

        while (controlador.isPartidaEnCurso()) {
            mostrarEstadoPartida();
            manejarAccionJugador();
        }

        System.out.println("¡La partida ha terminado!");
    }

    private int obtenerNumeroJugadores() {
        while (true) {
            System.out.print("Ingrese el número de jugadores (entre 2 y 10): ");
            try {
                int numeroJugadores = Integer.parseInt(scanner.nextLine());
                if (numeroJugadores >= 2 && numeroJugadores <= 10) {
                    return numeroJugadores;
                } else {
                    System.out.println("El número de jugadores debe estar entre 2 y 10.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debe ingresar un número.");
            }
        }
    }

    private List<String> obtenerNombresJugadores(int numeroJugadores) {
        List<String> nombresJugadores = new ArrayList<>();
        for (int i = 1; i <= numeroJugadores; i++) {
            System.out.print("Ingrese el nombre del jugador " + i + ": ");
            nombresJugadores.add(scanner.nextLine());
        }
        return nombresJugadores;
    }


    private void mostrarEstadoPartida() {
        Jugador jugadorActual = controlador.obtenerJugadorActual();
        Carta ultimaCartaJugadas = controlador.obtenerUltimaCartaJugadas();
        Color colorActual = controlador.obtenerColorActual();
        List<Carta> cartasJugadorActual = jugadorActual.getCartas();

        System.out.println("Estado actual de la partida:");
        System.out.println("Jugador actual: " + jugadorActual.getNombre());
        System.out.println("Cartas del jugador actual: " + cartasJugadorActual);
        System.out.println("Última carta jugada: " + ultimaCartaJugadas);
        System.out.println("Color actual: " + colorActual);
    }



    private void manejarAccionJugador() {
        System.out.print("Seleccione una acción (1: Jugar carta, 2: Robar carta): ");
        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            switch (opcion) {
                case 1 -> jugarCarta();
                case 2 -> controlador.robarCarta();
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Intente nuevamente.");
        }
    }

    private void jugarCarta() {
        List<Carta> cartasJugador = controlador.obtenerJugadorActual().getCartas();
        System.out.println("Tus cartas: " + cartasJugador);

        System.out.print("Ingrese el índice de la carta que desea jugar: ");
        try {
            int indiceCarta = Integer.parseInt(scanner.nextLine());
            Carta cartaSeleccionada = cartasJugador.get(indiceCarta);
            controlador.jugarCarta(indiceCarta);

            if (cartaSeleccionada.getColor() == Color.SIN_COLOR) {
                cambiarColor();
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Debe ingresar un número.");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Índice de carta inválido.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void cambiarColor() {
        System.out.println("Seleccione el nuevo color:");
        System.out.println("1: ROJO");
        System.out.println("2: AZUL");
        System.out.println("3: VERDE");
        System.out.println("4: AMARILLO");

        try {
            int opcionColor = Integer.parseInt(scanner.nextLine());
            Color nuevoColor = switch (opcionColor) {
                case 1 -> Color.ROJO;
                case 2 -> Color.AZUL;
                case 3 -> Color.VERDE;
                case 4 -> Color.AMARILLO;
                default -> {
                    System.out.println("Opción inválida. Eligiendo ROJO por defecto.");
                    yield Color.ROJO;
                }
            };
            controlador.manejarCambioDeColor(nuevoColor);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Eligiendo ROJO por defecto.");
            controlador.manejarCambioDeColor(Color.ROJO);
        }
    }

    public static void main(String[] args) {
        Partida partida = new Partida();
        ControladorUNO controlador = new ControladorUNO(partida);
        VistaConsola vistaConsola = new VistaConsola(controlador);
        vistaConsola.iniciarJuego();
    }
}

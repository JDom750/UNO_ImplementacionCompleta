package Vista;

import Controlador.ControladorUNO;
import Controlador.VistaObserver;
import Modelo.Carta;
import Modelo.Color;
import Modelo.Jugador;

import java.util.List;
import java.util.Scanner;

public class VistaConsola implements VistaObserver {

    private final ControladorUNO controlador;
    private final Scanner scanner;

    public VistaConsola(ControladorUNO controlador) {
        this.controlador = controlador;
        this.scanner = new Scanner(System.in);
        controlador.registrarVista(this);
    }

    public void iniciarJuego() {
        System.out.println("¡Bienvenido a UNO en consola!");

        // La partida ya está iniciada desde el main o el controlador.
        while (controlador.isPartidaEnCurso()) {
            manejarAccionJugador();
        }

        System.out.println("¡La partida ha terminado!");
    }

    private void mostrarEstadoPartida() {
        try {
            Jugador jugadorActual = controlador.obtenerJugadorActual();
            Carta ultimaCarta = controlador.obtenerUltimaCartaJugadas();
            Color colorActual = controlador.obtenerColorActual();
            List<Carta> cartasJugador = jugadorActual.getCartas();

            System.out.println("\n--- Estado actual de la partida ---");
            System.out.println("Jugador actual: " + jugadorActual.getNombre());

            System.out.println("Cartas del jugador:");
            for (int i = 0; i < cartasJugador.size(); i++) {
                System.out.println(i + ": " + cartasJugador.get(i));
            }

            System.out.println("Última carta jugada: " + ultimaCarta);
            System.out.println("Color actual: " + colorActual);

        } catch (Exception e) {
            System.out.println("Error al mostrar estado: " + e.getMessage());
        }
    }

    private void manejarAccionJugador() {
        System.out.print("\nSeleccione una acción (1: Jugar carta, 2: Robar carta): ");
        try {
            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> jugarCarta();
                case 2 -> controlador.robarCarta();
                default -> System.out.println("Opción inválida.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }
    }

    private void jugarCarta() {
        try {
            List<Carta> cartasJugador = controlador.obtenerJugadorActual().getCartas();

            System.out.println("\nTus cartas:");
            for (int i = 0; i < cartasJugador.size(); i++) {
                System.out.println(i + ": " + cartasJugador.get(i));
            }

            System.out.print("Ingrese el índice de la carta a jugar: ");
            int indice = Integer.parseInt(scanner.nextLine());

            Carta carta = cartasJugador.get(indice);

            controlador.jugarCarta(indice);

            if (carta.getColor() == Color.SIN_COLOR) {
                cambiarColor();
            }

        } catch (Exception e) {
            System.out.println("Error al jugar carta: " + e.getMessage());
        }
    }

    private void cambiarColor() {
        Color nuevo = null;

        while (nuevo == null) {
            System.out.println("\nSeleccione un color:");
            System.out.println("1: ROJO");
            System.out.println("2: AZUL");
            System.out.println("3: VERDE");
            System.out.println("4: AMARILLO");

            try {
                int op = Integer.parseInt(scanner.nextLine());

                nuevo = switch (op) {
                    case 1 -> Color.ROJO;
                    case 2 -> Color.AZUL;
                    case 3 -> Color.VERDE;
                    case 4 -> Color.AMARILLO;
                    default -> null;
                };

                if (nuevo == null) {
                    System.out.println("Opción inválida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida.");
            }
        }

        controlador.manejarCambioDeColor(nuevo);
    }

    @Override
    public void actualizar() {
        mostrarEstadoPartida();
    }
}

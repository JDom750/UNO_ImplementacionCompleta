package Vista;

import Controlador.ControladorUNO;
import Modelo.Carta;
import Modelo.Jugador; // Import necesario
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
        System.out.println("Bienvenido a UNO!");

        System.out.print("Ingrese el número de jugadores: ");
        int numeroJugadores = Integer.parseInt(scanner.nextLine());
        List<String> nombresJugadores = new ArrayList<>();

        for (int i = 1; i <= numeroJugadores; i++) {
            System.out.print("Ingrese el nombre del jugador " + i + ": ");
            nombresJugadores.add(scanner.nextLine());
        }

        controlador.iniciarPartida(nombresJugadores);
        boolean primerTurno = true;
        while (controlador.isPartidaEnCurso()) {
            mostrarEstadoPartida(); // Mostrar estado del juego

            if (primerTurno) {
                System.out.println("Es tu primer turno, debes jugar una carta.");
                jugarCarta(); // Obligar al jugador a jugar una carta
                primerTurno = false; // Marcar que ya no es el primer turno
            } else {
                System.out.print("Seleccione una acción (1: Jugar carta, 2: Robar carta): ");
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> jugarCarta();
                    case 2 -> controlador.robarCarta();
                    default -> System.out.println("Opción inválida. Intente nuevamente.");
                }
            }
        }

        System.out.println("¡La partida ha terminado!");
    }

    private void mostrarEstadoPartida() {
        System.out.println(controlador.obtenerEstadoJuego()); // Nombre del método corregido
    }

    private void jugarCarta() {
        Jugador jugadorActual = controlador.partida.getJugadorActual(); // Obtener jugador actual
        System.out.println("Tus cartas: " + jugadorActual.getCartas());

        System.out.print("Ingrese el índice de la carta que desea jugar: ");
        try {
            int indiceCarta = Integer.parseInt(scanner.nextLine());
            controlador.jugarCarta(indiceCarta);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Debe ingresar un número.");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Índice de carta inválido.");
        } catch (IllegalArgumentException e) { // Captura excepciones del controlador
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Partida partida = new Partida(); // Instancia Partida
        ControladorUNO controlador = new ControladorUNO(partida); // Pasa la partida al controlador
        VistaConsola vistaConsola = new VistaConsola(controlador);
        vistaConsola.iniciarJuego();
    }
}
package Vista;

import Controlador.ControladorUNO;
import Modelo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VistaUNO extends JFrame {
    private final ControladorUNO controlador;
    private final JTextArea estadoPartida;
    private final JTextField indiceCartaInput;
    private final JComboBox<Modelo.Color> colorSelector;
    private final JButton jugarCartaBtn, robarCartaBtn, cambiarColorBtn;

    public VistaUNO(ControladorUNO controlador) {
        this.controlador = controlador;

        // Configuración de la ventana
        setTitle("Juego de UNO");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel para mostrar el estado del juego
        estadoPartida = new JTextArea();
        estadoPartida.setEditable(false);
        JScrollPane scroll = new JScrollPane(estadoPartida);
        add(scroll, BorderLayout.CENTER);

        // Panel para las acciones
        JPanel accionesPanel = new JPanel(new GridLayout(2, 1));
        add(accionesPanel, BorderLayout.SOUTH);

        // Subpanel para jugar carta y robar carta
        JPanel jugarRobarPanel = new JPanel(new FlowLayout());
        indiceCartaInput = new JTextField(5);
        jugarCartaBtn = new JButton("Jugar Carta");
        robarCartaBtn = new JButton("Robar Carta");

        jugarRobarPanel.add(new JLabel("Índice Carta:"));
        jugarRobarPanel.add(indiceCartaInput);
        jugarRobarPanel.add(jugarCartaBtn);
        jugarRobarPanel.add(robarCartaBtn);
        accionesPanel.add(jugarRobarPanel);

        // Subpanel para cambiar color
        JPanel cambiarColorPanel = new JPanel(new FlowLayout());
        colorSelector = new JComboBox<>(Modelo.Color.values());
        cambiarColorBtn = new JButton("Cambiar Color");

        cambiarColorPanel.add(new JLabel("Nuevo Color:"));
        cambiarColorPanel.add(colorSelector);
        cambiarColorPanel.add(cambiarColorBtn);
        accionesPanel.add(cambiarColorPanel);

        // Listeners para los botones
        jugarCartaBtn.addActionListener(new JugarCartaListener());
        robarCartaBtn.addActionListener(new RobarCartaListener());
        cambiarColorBtn.addActionListener(new CambiarColorListener());
    }

    private void actualizarEstado() {
        // Mostrar el estado del juego
        try {
            Jugador jugadorActual = controlador.obtenerJugadorActual();
            Carta ultimaCarta = controlador.obtenerUltimaCartaJugadas();
            Modelo.Color colorActual = controlador.obtenerColorActual();

            StringBuilder estado = new StringBuilder();
            estado.append("Turno de: ").append(jugadorActual.getNombre()).append("\n");
            estado.append("Cartas del jugador actual: ").append(jugadorActual.getCartas()).append("\n");
            estado.append("Última carta jugada: ").append(ultimaCarta).append("\n");
            estado.append("Color actual: ").append(colorActual).append("\n");

            estadoPartida.setText(estado.toString());
        } catch (Exception e) {
            estadoPartida.setText("Error al actualizar el estado: " + e.getMessage());
        }
    }

    private class JugarCartaListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int indiceCarta = Integer.parseInt(indiceCartaInput.getText());
                controlador.jugarCarta(indiceCarta);
                actualizarEstado();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(VistaUNO.this, "Ingrese un índice válido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(VistaUNO.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RobarCartaListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                controlador.robarCarta();
                actualizarEstado();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(VistaUNO.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class CambiarColorListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Modelo.Color nuevoColor = (Modelo.Color) colorSelector.getSelectedItem();
                controlador.manejarCambioDeColor(nuevoColor);
                actualizarEstado();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(VistaUNO.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        Partida partida = new Partida();
        ControladorUNO controlador = new ControladorUNO(partida);
        VistaUNO vista = new VistaUNO(controlador);

        // Registrar la vista como observador del controlador
        controlador.registrarVista(vista::actualizarEstado);

        vista.setVisible(true);

        // Iniciar partida de prueba
        controlador.iniciarPartida(List.of("Jugador 1", "Jugador 2"));

        // Actualizar estado una vez iniciada la partida
        vista.actualizarEstado();
    }
}

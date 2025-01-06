package Modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Representa un jugador
public class Jugador {
    private final String nombre;
    private final List<Carta> cartas;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.cartas = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public List<Carta> getCartas() {
        return Collections.unmodifiableList(cartas);
    }

    public void tomarCarta(Carta carta) {
        cartas.add(carta);
    }

    public void jugarCarta(Carta carta) {
        if (!cartas.remove(carta)) {
            throw new IllegalArgumentException("La carta no está en la mano del jugador");
        }
    }

    public boolean puedeJugar(Carta carta) {
        return cartas.contains(carta);
    }

    public boolean tieneCartaDelColor(Color colorActual) {
        for (Carta carta : cartas) {
            if (carta.getColor() == colorActual) {
                return true;
            }
        }
        return false;
    }

}
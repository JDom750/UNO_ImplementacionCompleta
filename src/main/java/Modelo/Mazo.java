package Modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

// Representa un mazo de cartas
class Mazo {
    private final Stack<Carta> mazo;
    private final List<Carta> descartes;

    public Mazo() {
        this.mazo = new Stack<>();
        this.descartes = new ArrayList<>();
        inicializarMazo();
    }

    private void inicializarMazo() {
        for (Color color : Color.values()) {
            for (int i = 0; i <= 9; i++) {
                mazo.add(new Carta(color, i));
            }
        }
    }

    public void barajar() {
        Collections.shuffle(mazo);
    }

    public Carta robarCarta() {
        if (mazo.isEmpty()) {
            reponerMazo();
        }
        return mazo.pop();
    }

    public void descartar(Carta carta) {
        descartes.add(carta);
    }

    private void reponerMazo() {
        mazo.addAll(descartes);
        descartes.clear();
        barajar();
    }

    public List<Carta> getDescartes() {
        return descartes;
    }
    public Carta getUltimaCartaJugadas() {
        if (descartes.isEmpty()) {
            throw new IllegalStateException("No hay cartas en el descarte.");
        }
        return descartes.get(descartes.size() - 1);
    }
}
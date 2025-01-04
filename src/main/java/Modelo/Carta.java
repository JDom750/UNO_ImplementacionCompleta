package Modelo;

// Representa una carta de UNO
public class Carta {
    private final Color color;
    private final int valor;

    public Carta(Color color, int valor) {
        this.color = color;
        this.valor = valor;
    }

    public Color getColor() {
        return color;
    }

    public int getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return color + " " + valor;
    }
}
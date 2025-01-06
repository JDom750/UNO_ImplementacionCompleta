package Modelo;

// Representa una carta de UNO
public class Carta {
    private final Color color;
    private final Numero valor;

    public Carta(Color color, Numero valor) {
        this.color = color;
        this.valor = valor;
    }

    public Color getColor() {
        return color;
    }

    public Numero getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return color + " " + valor;
    }
}
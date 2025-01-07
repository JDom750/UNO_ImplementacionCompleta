package Modelo;

public class Evento {
    private final String tipo;
    private final Object datos;

    public Evento(String tipo, Object datos) {
        this.tipo = tipo;
        this.datos = datos;
    }

    public String getTipo() {
        return tipo;
    }

    public Object getDatos() {
        return datos;
    }
}

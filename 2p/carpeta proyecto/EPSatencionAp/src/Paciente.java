import java.time.LocalTime;

public class Paciente {
    private String cedula;
    private int edad;
    private boolean discapacidad;
    private String servicio;
    private LocalTime horaRegistro;
    
    public Paciente(String cedula, int edad, boolean discapacidad, String servicio, LocalTime horaRegistro) {
        this.cedula = cedula;
        this.edad = edad;
        this.discapacidad = discapacidad;
        this.servicio = servicio;
        this.horaRegistro = horaRegistro;
    }

    public String getCedula() {
        return cedula;
    }

    public int getEdad() {
        return edad;
    }

    public boolean isDiscapacidad() {
        return discapacidad;
    }

    public String getServicio() {
        return servicio;
    }

    public LocalTime getHoraRegistro() {
        return horaRegistro;
    }

    public String getCategoriaEdad() {
        return edad >= 60 ? "Adulto Mayor" : "Menor de 60 años";
    }
    
    public String getCategoriaDiscapacidad() {
        return discapacidad ? "Persona con Discapacidad" : "Sin Discapacidad";
    }
}


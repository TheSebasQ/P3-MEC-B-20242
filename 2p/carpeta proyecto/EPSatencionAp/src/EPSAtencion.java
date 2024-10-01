import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EPSAtencion extends JFrame {
    private List<Paciente> listaPacientes;
    private DefaultListModel<String> modeloLista;
    private JList<String> listaPacientesGUI;
    private JLabel lblUsuarioActual;
    private Timer temporizador;
    private int tiempoAcelerado = 1000; // 1 segundo = 1 minuto
    private JSlider sliderTiempo;

    public EPSAtencion() {
        listaPacientes = new ArrayList<>();
        modeloLista = new DefaultListModel<>();
        inicializarComponentes();
        iniciarTemporizador();
    }

    private void inicializarComponentes() {
        setTitle("Sistema de Atención EPS");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel izquierdo - Registro de pacientes
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new GridLayout(6, 2));

        JLabel lblCedula = new JLabel("Cédula:");
        JTextField txtCedula = new JTextField();
        panelIzquierdo.add(lblCedula);
        panelIzquierdo.add(txtCedula);

        JLabel lblEdad = new JLabel("Edad:");
        JTextField txtEdad = new JTextField();
        panelIzquierdo.add(lblEdad);
        panelIzquierdo.add(txtEdad);

        JLabel lblDiscapacidad = new JLabel("Discapacidad:");
        JCheckBox chkDiscapacidad = new JCheckBox("Sí");
        panelIzquierdo.add(lblDiscapacidad);
        panelIzquierdo.add(chkDiscapacidad);

        JLabel lblServicio = new JLabel("Servicio:");
        JComboBox<String> cmbServicio = new JComboBox<>(new String[]{"Consulta Médico General", "Consulta Médica Especializada", "Prueba de Laboratorio", "Imágenes Diagnósticas"});
        panelIzquierdo.add(lblServicio);
        panelIzquierdo.add(cmbServicio);

        JButton btnRegistrar = new JButton("Registrar");
        panelIzquierdo.add(new JLabel()); // Espacio vacío
        panelIzquierdo.add(btnRegistrar);

        add(panelIzquierdo, BorderLayout.WEST);

        // Panel derecho - Lista de pacientes y usuario actual
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BorderLayout());

        lblUsuarioActual = new JLabel("Atendiendo a: Ningún paciente", JLabel.CENTER);
        panelDerecho.add(lblUsuarioActual, BorderLayout.NORTH);

        // Lista de pacientes registrados
        listaPacientesGUI = new JList<>(modeloLista);
        JScrollPane scrollPane = new JScrollPane(listaPacientesGUI);
        panelDerecho.add(scrollPane, BorderLayout.CENTER);

        // Añadir MouseListener a la lista de pacientes
        listaPacientesGUI.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Doble clic en un elemento de la lista
                    int index = listaPacientesGUI.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        Paciente pacienteSeleccionado = listaPacientes.get(index);
                        mostrarDetallesPaciente(pacienteSeleccionado);
                    }
                }
            }
        });

        // Panel de control deslizante para ajustar el tiempo de atención
        sliderTiempo = new JSlider(JSlider.HORIZONTAL, 1, 30, 1); // 1 minuto a 30 minutos
        sliderTiempo.setMajorTickSpacing(5);
        sliderTiempo.setMinorTickSpacing(1);
        sliderTiempo.setPaintTicks(true);
        sliderTiempo.setPaintLabels(true);
        sliderTiempo.addChangeListener(e -> {
            tiempoAcelerado = sliderTiempo.getValue() * 1000; // 1 segundo representa 1 minuto
        });

        panelDerecho.add(sliderTiempo, BorderLayout.SOUTH);

        add(panelDerecho, BorderLayout.CENTER);

        // Acción al registrar un paciente
        btnRegistrar.addActionListener(e -> {
            String cedula = txtCedula.getText();
            String edadStr = txtEdad.getText();
            boolean discapacidad = chkDiscapacidad.isSelected();
            String servicio = (String) cmbServicio.getSelectedItem();

            // Validación de datos
            if (!cedula.matches("\\d{6,12}")) {
                JOptionPane.showMessageDialog(this, "La cédula debe ser numérica y tener entre 6 y 12 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int edad;
            try {
                edad = Integer.parseInt(edadStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La edad debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear y agregar nuevo paciente
            Paciente paciente = new Paciente(cedula, edad, discapacidad, servicio, LocalTime.now());
            listaPacientes.add(paciente);
            modeloLista.addElement("Cédula: " + cedula + " - Servicio: " + servicio);
            actualizarLista();
        });
    }

    private void actualizarLista() {
        if (!listaPacientes.isEmpty()) {
            Paciente pacienteActual = listaPacientes.get(0);
            lblUsuarioActual.setText("Atendiendo a: " + pacienteActual.getCedula() + " - " + pacienteActual.getServicio());
        } else {
            lblUsuarioActual.setText("No hay pacientes en cola.");
        }
    }

    private void iniciarTemporizador() {
        temporizador = new Timer(tiempoAcelerado, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!listaPacientes.isEmpty()) {
                    // Finalizar automáticamente después de un tiempo aleatorio entre 15 y 20 segundos
                    int tiempoAleatorio = 15000 + (int) (Math.random() * 5000);
                    Timer temporizadorAleatorio = new Timer(tiempoAleatorio, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (!listaPacientes.isEmpty()) {
                                listaPacientes.remove(0);
                                actualizarLista();
                            }
                        }
                    });
                    temporizadorAleatorio.setRepeats(false);
                    temporizadorAleatorio.start();
                }
            }
        });
        temporizador.start();
    }

    // Método para mostrar los detalles del paciente
    private void mostrarDetallesPaciente(Paciente paciente) {
        // Calcula el tiempo de espera que le queda al paciente
        LocalTime horaActual = LocalTime.now();
        long minutosTranscurridos = java.time.Duration.between(paciente.getHoraRegistro(), horaActual).toMinutes();
        long tiempoEsperaRestante = 30 - minutosTranscurridos; // Ejemplo de cálculo

        // Asegurarse de que el tiempo de espera no sea negativo
        tiempoEsperaRestante = Math.max(tiempoEsperaRestante, 0);

        // Crear un mensaje con todos los detalles del paciente
        String mensaje = "Detalles del Paciente:\n" +
                         "Cédula: " + paciente.getCedula() + "\n" +
                         "Edad: " + paciente.getEdad() + " (" + paciente.getCategoriaEdad() + ")\n" +
                         "Discapacidad: " + paciente.getCategoriaDiscapacidad() + "\n" +
                         "Servicio Solicitado: " + paciente.getServicio() + "\n" +
                         "Hora de Registro: " + paciente.getHoraRegistro() + "\n" +
                         "Tiempo de Espera Restante: " + tiempoEsperaRestante + " minutos";

        // Mostrar el cuadro de diálogo con los detalles del paciente
        JOptionPane.showMessageDialog(this, mensaje, "Detalles del Paciente", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EPSAtencion().setVisible(true);
        });
    }
}


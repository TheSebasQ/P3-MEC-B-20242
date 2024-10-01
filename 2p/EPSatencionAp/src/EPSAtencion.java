import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EPSAtencion extends JFrame {
    private List<Paciente> listaPacientes;
    private DefaultListModel<String> modeloLista;
    private JList<String> listaPacientesGUI;
    private JLabel lblUsuarioActual, lblTiempo;
    private JSlider sliderTiempo;
    private Timer temporizador;
    private int tiempoAcelerado = 1000; // 1 segundo = 1 minuto

    public EPSAtencion() {
        listaPacientes = new ArrayList<>();
        modeloLista = new DefaultListModel<>();

        setTitle("Sistema de Atención EPS");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel izquierdo para registrar usuarios
        JPanel panelRegistro = new JPanel();
        panelRegistro.setLayout(new GridLayout(7, 2));

        JLabel lblCedula = new JLabel("Cédula:");
        JTextField txtCedula = new JTextField();
        panelRegistro.add(lblCedula);
        panelRegistro.add(txtCedula);

        JLabel lblEdad = new JLabel("Edad:");
        JTextField txtEdad = new JTextField();
        panelRegistro.add(lblEdad);
        panelRegistro.add(txtEdad);

        JLabel lblDiscapacidad = new JLabel("Discapacidad:");
        JCheckBox chkDiscapacidad = new JCheckBox("¿Tiene Discapacidad?");
        panelRegistro.add(lblDiscapacidad);
        panelRegistro.add(chkDiscapacidad);

        JLabel lblServicio = new JLabel("Servicio Solicitado:");
        String[] servicios = {"Consulta médico general", "Consulta médica especializada", "Prueba de laboratorio", "Imágenes diagnósticas"};
        JComboBox<String> cmbServicio = new JComboBox<>(servicios);
        panelRegistro.add(lblServicio);
        panelRegistro.add(cmbServicio);

        JLabel lblHora = new JLabel("Hora Registro:");
        JTextField txtHora = new JTextField();
        txtHora.setEditable(false);
        panelRegistro.add(lblHora);
        panelRegistro.add(txtHora);

        JButton btnRegistrar = new JButton("Registrar");
        panelRegistro.add(new JLabel()); // Espacio en blanco
        panelRegistro.add(btnRegistrar);

        add(panelRegistro, BorderLayout.WEST);

        // Panel derecho que contiene la lista de usuarios y el control de tiempo
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new GridLayout(2, 1));

        // Mostrar primer usuario en cola
        lblUsuarioActual = new JLabel("Atendiendo a: ");
        panelSuperior.add(lblUsuarioActual);

        JButton btnAtendido = new JButton("Ya Atendido");
        panelSuperior.add(btnAtendido);

        panelDerecho.add(panelSuperior, BorderLayout.NORTH);

        // Lista de pacientes registrados
        listaPacientesGUI = new JList<>(modeloLista);
        JScrollPane scrollPane = new JScrollPane(listaPacientesGUI);
        panelDerecho.add(scrollPane, BorderLayout.CENTER);

        // Slider para ajustar el tiempo
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BorderLayout());

        lblTiempo = new JLabel("Tiempo de atención acelerado: 1 minuto = 1s");
        sliderTiempo = new JSlider(JSlider.HORIZONTAL, 1, 30, 1);
        sliderTiempo.setMajorTickSpacing(5);
        sliderTiempo.setPaintTicks(true);
        sliderTiempo.setPaintLabels(true);

        panelInferior.add(lblTiempo, BorderLayout.NORTH);
        panelInferior.add(sliderTiempo, BorderLayout.CENTER);
        panelDerecho.add(panelInferior, BorderLayout.SOUTH);

        add(panelDerecho, BorderLayout.CENTER);

        // Listeners
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validar entrada
                String cedula = txtCedula.getText();
                String edadTexto = txtEdad.getText();
                boolean discapacidad = chkDiscapacidad.isSelected();
                String servicio = (String) cmbServicio.getSelectedItem();

                if (!cedula.matches("\\d{6,12}")) {
                    JOptionPane.showMessageDialog(null, "Cédula inválida. Debe contener entre 6 y 12 dígitos numéricos.");
                    return;
                }

                int edad;
                try {
                    edad = Integer.parseInt(edadTexto);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Edad inválida. Debe ser un número entero.");
                    return;
                }

                LocalTime horaActual = LocalTime.now();
                txtHora.setText(horaActual.toString());

                // Registrar el paciente
                Paciente nuevoPaciente = new Paciente(cedula, edad, discapacidad, servicio, horaActual);
                listaPacientes.add(nuevoPaciente);

                modeloLista.addElement(cedula + " - " + nuevoPaciente.getCategoriaEdad() + " - " + servicio);
                verificarIniciarServicio();
            }
        });

        btnAtendido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!listaPacientes.isEmpty()) {
                    listaPacientes.remove(0);
                    actualizarLista();
                }
            }
        });

        sliderTiempo.addChangeListener(e -> {
            tiempoAcelerado = sliderTiempo.getValue() * 1000;
            lblTiempo.setText("Tiempo de atención acelerado: 1 minuto = " + sliderTiempo.getValue() + "s");
        });

        iniciarTemporizador();
    }

    private void verificarIniciarServicio() {
        if (listaPacientes.size() == 10) {
            JOptionPane.showMessageDialog(this, "El servicio comienza. Hay 10 personas en espera.");
        }
    }

    private void actualizarLista() {
        modeloLista.clear();
        for (int i = 0; i < listaPacientes.size(); i++) {
            Paciente paciente = listaPacientes.get(i);
            modeloLista.addElement((i + 1) + ". " + paciente.getCedula() + " - " + paciente.getCategoriaEdad() + " - " + paciente.getServicio());
        }

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
                    int tiempoAleatorio = 15000 + (int) (Math.random() * 50000);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EPSAtencion().setVisible(true);
        });
    }
}

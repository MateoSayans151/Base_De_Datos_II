package ui;
/*
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import services.*;
import modelo.*;
import exceptions.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class DashboardFrame extends JFrame {
    private String token;
    private Usuario usuarioActual;
    private JTabbedPane tabbedPane;

    public DashboardFrame(String token) {
        this.token = token;
        setTitle("Polyglot Persistence - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        try {
            usuarioActual = AuthService.getInstance().validarToken(token);
            if (usuarioActual == null) {
                JOptionPane.showMessageDialog(this, "Invalid token", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
       }

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Sensores", crearPanelSensores());
        tabbedPane.addTab("Procesos", crearPanelProcesos());
        tabbedPane.addTab("Mensajería", crearPanelMensajeria());
        tabbedPane.addTab("Cuenta", crearPanelCuenta());

        add(tabbedPane);
    }

    private JPanel crearPanelSensores() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Crear Sensor"));

        JTextField nombreField = new JTextField();
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"temperatura", "humedad"});
        JTextField latitudField = new JTextField();
        JTextField longitudField = new JTextField();
        JTextField ciudadField = new JTextField();
        JTextField paisField = new JTextField();

        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(nombreField);
        formPanel.add(new JLabel("Tipo:"));
        formPanel.add(tipoCombo);
        formPanel.add(new JLabel("Latitud:"));
        formPanel.add(latitudField);
        formPanel.add(new JLabel("Longitud:"));
        formPanel.add(longitudField);
        formPanel.add(new JLabel("Ciudad:"));
        formPanel.add(ciudadField);
        formPanel.add(new JLabel("País:"));
        formPanel.add(paisField);

        JButton crearButton = new JButton("Crear Sensor");
        crearButton.addActionListener(e -> {
            try {
                SensorService.getInstance().crearSensor(
                    nombreField.getText(),
                    (String) tipoCombo.getSelectedItem(),
                    Double.parseDouble(latitudField.getText()),
                    Double.parseDouble(longitudField.getText()),
                    ciudadField.getText(),
                    paisField.getText()
                );
                JOptionPane.showMessageDialog(this, "Sensor creado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                nombreField.setText("");
                latitudField.setText("");
                longitudField.setText("");
                ciudadField.setText("");
                paisField.setText("");
            } catch (ErrorConexionCassandraException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(crearButton);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelProcesos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Gestión de Procesos");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea("Procesos disponibles:\n1. PROMEDIO_MENSUAL\n2. MAX_MIN\n3. ALERTAS");
        textArea.setEditable(false);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelMensajeria() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Enviar Mensaje"));

        JTextField destinatarioField = new JTextField();
        JTextArea contenidoArea = new JTextArea(5, 20);
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"privado", "grupal"});

        formPanel.add(new JLabel("Destinatario ID:"));
        formPanel.add(destinatarioField);
        formPanel.add(new JLabel("Tipo:"));
        formPanel.add(tipoCombo);
        formPanel.add(new JLabel("Contenido:"));
        formPanel.add(new JScrollPane(contenidoArea));

        JButton enviarButton = new JButton("Enviar");
        enviarButton.addActionListener(e -> {
            try {
                MensajeService.getInstance().enviarMensaje(
                    usuarioActual.getId(),
                    Integer.parseInt(destinatarioField.getText()),
                    contenidoArea.getText(),
                    (String) tipoCombo.getSelectedItem()
                );
                JOptionPane.showMessageDialog(this, "Mensaje enviado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                contenidoArea.setText("");
            } catch (ErrorConexionMongoException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(enviarButton);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelCuenta() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Cuenta Corriente");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        try {
            CuentaCorriente cuenta = repository.CuentaMySQLRepository.getInstance()
                    .obtenerPorUsuario(usuarioActual.getId());
            if (cuenta != null) {
                textArea.setText("Saldo: $" + cuenta.getSaldo() + "\n\nMovimientos:\n");
                List<Movimiento> movimientos = repository.MovimientoMySQLRepository.getInstance()
                        .obtenerPorCuenta(cuenta.getId());
                for (Movimiento m : movimientos) {
                    textArea.append(m.getTipo() + ": $" + m.getMonto() + " - " + m.getDescripcion() + "\n");
                }
            }
        } catch (ErrorConexionMySQLException ex) {
            textArea.setText("Error: " + ex.getMessage());
        }

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        return panel;
    }


}


 */
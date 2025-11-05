package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import org.json.JSONObject;
import service.*;
import entity.*;
import repository.redis.InicioSesionRepository;
import repository.mongo.*;
import repository.cassandra.*;
import java.util.List;
import java.util.Optional;

public class DashboardAdminFrame extends JFrame {
    private String userToken;
    private Usuario currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    private final UsuarioService usuarioService;
    private final SensorService sensorService;
    private final MedicionService medicionService;

    public DashboardAdminFrame(String token) {
        this.userToken = token;
        
        // Initialize services
        this.usuarioService = new UsuarioService();
        this.sensorService = new SensorService();
        this.medicionService = new MedicionService();
        
        try {
            JSONObject session = usuarioService.getSession(userToken);
            if (session != null) {
                int userId = session.getInt("id");
                Usuario usuario = usuarioService.getById(userId);
                if (usuario != null &&
                    (usuario.getRol().getNombre().equals("Admin") ||
                     usuario.getRol().getNombre().equals("Tecnico"))) {
                    this.currentUser = usuario;
                } else {
                    throw new SecurityException("Acceso no autorizado");
                }
            } else {
                throw new SecurityException("Sesión inválida");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de autenticación: " + e.getMessage());
            System.exit(1);
        }

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Panel de Administración - " + currentUser.getRol());
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal con CardLayout para cambiar entre vistas
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panel del menú lateral
        JPanel menuPanel = createMenuPanel();
        
        // Crear todos los paneles de funcionalidades
        mainPanel.add(createWelcomePanel(), "WELCOME");
        mainPanel.add(createRequestsPanel(), "REQUESTS");
        mainPanel.add(createSensorsPanel(), "SENSORS");
        mainPanel.add(createInvoicesPanel(), "INVOICES");
        mainPanel.add(createUsersPanel(), "USERS");

        // Layout principal
        setLayout(new BorderLayout());
        add(menuPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(44, 62, 80));
        menuPanel.setPreferredSize(new Dimension(250, getHeight()));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Información del usuario
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(new Color(44, 62, 80));
        userInfoPanel.setMaximumSize(new Dimension(250, 100));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel roleLabel = new JLabel(currentUser.getRol().getNombre());
        roleLabel.setForeground(new Color(46, 204, 113));
        roleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(currentUser.getNombre());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userInfoPanel.add(roleLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userInfoPanel.add(nameLabel);

        menuPanel.add(userInfoPanel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botones del menú
        addMenuButton(menuPanel, "Solicitudes Pendientes", "REQUESTS");
        addMenuButton(menuPanel, "Gestionar Sensores", "SENSORS");
        addMenuButton(menuPanel, "Gestionar Facturas", "INVOICES");
        addMenuButton(menuPanel, "Gestionar Usuarios", "USERS");
        
        // Botón de cerrar sesión
        menuPanel.add(Box.createVerticalGlue());
        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(200, 40));
        logoutButton.addActionListener(e -> logout());
        menuPanel.add(logoutButton);

        return menuPanel;
    }

    private void addMenuButton(JPanel panel, String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.addActionListener(e -> cardLayout.show(mainPanel, cardName));
        
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(236, 240, 241));

        JLabel welcomeLabel = new JLabel("Bienvenido al Panel de Administración");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(welcomeLabel);

        return panel;
    }

    private JPanel createRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(236, 240, 241));

        // Por ahora solo mostraremos un mensaje
        JLabel label = new JLabel("Implementando panel de solicitudes...");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSensorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(236, 240, 241));

        // Panel superior con botones
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(236, 240, 241));

        JButton addButton = new JButton("Agregar Sensor");
        JButton refreshButton = new JButton("Actualizar");
        
        topPanel.add(addButton);
        topPanel.add(refreshButton);

        // Tabla de sensores
        String[] columnNames = {"ID", "Nombre", "Tipo", "Ciudad", "País", "Estado"};
        JTable sensorsTable = new JTable(new DefaultTableModel(columnNames, 0));
        JScrollPane scrollPane = new JScrollPane(sensorsTable);

        // Panel de detalles
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(new Color(236, 240, 241));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Detalles del Sensor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Campos de detalles
        JLabel[] labels = {
            new JLabel("ID:"), new JLabel(""),
            new JLabel("Nombre:"), new JLabel(""),
            new JLabel("Tipo:"), new JLabel(""),
            new JLabel("Latitud:"), new JLabel(""),
            new JLabel("Longitud:"), new JLabel(""),
            new JLabel("Ciudad:"), new JLabel(""),
            new JLabel("País:"), new JLabel(""),
            new JLabel("Estado:"), new JLabel(""),
            new JLabel("Fecha Inicio:"), new JLabel("")
        };

        for (int i = 0; i < labels.length; i += 2) {
            gbc.gridx = 0;
            gbc.gridy = i/2;
            detailsPanel.add(labels[i], gbc);
            gbc.gridx = 1;
            detailsPanel.add(labels[i+1], gbc);
        }

        // Botón para actualizar estado
        JButton toggleStateButton = new JButton("Cambiar Estado");
        gbc.gridx = 0;
        gbc.gridy = labels.length/2;
        gbc.gridwidth = 2;
        detailsPanel.add(toggleStateButton, gbc);

        // Agregar listener a la tabla
        sensorsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && sensorsTable.getSelectedRow() != -1) {
                int row = sensorsTable.getSelectedRow();
                String sensorId = sensorsTable.getValueAt(row, 0).toString();
                
                try {
                    Sensor sensor = SensorService.getInstance().getSensor(Integer.parseInt(sensorId));
                    if (sensor != null) {
                        // Actualizar labels
                        labels[1].setText(sensorId);
                        labels[3].setText(sensor.getCod());
                        labels[5].setText(sensor.getTipo());
                        labels[7].setText(String.valueOf(sensor.getLatitud()));
                        labels[9].setText(String.valueOf(sensor.getLongitud()));
                        labels[11].setText(sensor.getCiudad());
                        labels[13].setText(sensor.getPais());
                        labels[15].setText(sensor.getEstado());
                        labels[17].setText(sensor.getFechaIni().toString());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, 
                        "Error al cargar detalles del sensor: " + ex.getMessage());
                }
            }
        });

        // Acción del botón Agregar
        addButton.addActionListener(e -> showAddSensorDialog());

        // Acción del botón Actualizar
        refreshButton.addActionListener(e -> refreshSensorsTable(sensorsTable));

        // Acción del botón Cambiar Estado
        toggleStateButton.addActionListener(e -> {
            int row = sensorsTable.getSelectedRow();
            if (row != -1) {
                String sensorId = sensorsTable.getValueAt(row, 0).toString();
                try {
                    Sensor sensor = SensorService.getInstance().getSensor(Integer.parseInt(sensorId));
                    if (sensor != null) {
                        String newState = sensor.getEstado().equals("activo") ? "inactivo" : "activo";
                        sensor.setEstado(newState);
                        //SensorService.getInstance().actualizarSensor(sensor);
                        refreshSensorsTable(sensorsTable);
                        JOptionPane.showMessageDialog(panel, 
                            "Estado del sensor actualizado correctamente.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, 
                        "Error al actualizar estado del sensor: " + ex.getMessage());
                }
            }
        });

        // Cargar datos iniciales
        refreshSensorsTable(sensorsTable);

        // Layout final
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(detailsPanel, BorderLayout.EAST);

        return panel;
    }

    private void showAddSensorDialog() {
        JDialog dialog = new JDialog(this, "Agregar Nuevo Sensor", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos del formulario
        JTextField nombreField = new JTextField(20);
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"temperatura", "humedad"});
        JTextField latitudField = new JTextField(20);
        JTextField longitudField = new JTextField(20);
        JTextField ciudadField = new JTextField(20);
        JTextField paisField = new JTextField(20);

        // Agregar componentes
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        dialog.add(nombreField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        dialog.add(tipoCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Latitud:"), gbc);
        gbc.gridx = 1;
        dialog.add(latitudField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Longitud:"), gbc);
        gbc.gridx = 1;
        dialog.add(longitudField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Ciudad:"), gbc);
        gbc.gridx = 1;
        dialog.add(ciudadField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("País:"), gbc);
        gbc.gridx = 1;
        dialog.add(paisField, gbc);

        // Botón guardar
        JButton saveButton = new JButton("Guardar");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        dialog.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            try {
                // Validar campos
                if (nombreField.getText().trim().isEmpty() ||
                    latitudField.getText().trim().isEmpty() ||
                    longitudField.getText().trim().isEmpty() ||
                    ciudadField.getText().trim().isEmpty() ||
                    paisField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Todos los campos son obligatorios");
                }

                double lat = Double.parseDouble(latitudField.getText());
                double lon = Double.parseDouble(longitudField.getText());

                // Crear y guardar sensor

                SensorService.getInstance().createSensor(nombreField.getText(),tipoCombo.getSelectedItem().toString(),lat, lon, ciudadField.getText(),paisField.getText(), java.time.LocalDateTime.now());
                JOptionPane.showMessageDialog(dialog, 
                    "Sensor agregado correctamente", "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Latitud y longitud deben ser números válidos",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error al guardar sensor: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void refreshSensorsTable(JTable table) {
        try {
            List<Sensor> sensores = SensorService.getInstance().getAllSensors();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            for (Sensor sensor : sensores) {
                model.addRow(new Object[]{
                    sensor.getId(),
                    sensor.getCod(),
                    sensor.getTipo(),
                    sensor.getCiudad(),
                    sensor.getPais(),
                    sensor.getEstado()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar sensores: " + e.getMessage());
        }
    }

    private JPanel createInvoicesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(236, 240, 241));

        // Por ahora solo mostraremos un mensaje
        JLabel label = new JLabel("Implementando panel de facturas...");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(236, 240, 241));

        // Por ahora solo mostraremos un mensaje
        JLabel label = new JLabel("Implementando panel de usuarios...");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private void logout() {
        try {
            usuarioService.logout(userToken);
            WelcomeFrame welcomeFrame = new WelcomeFrame();
            welcomeFrame.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cerrar sesión: " + e.getMessage());
        }
    }
}


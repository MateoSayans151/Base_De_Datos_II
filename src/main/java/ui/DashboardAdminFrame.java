package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import org.json.JSONObject;
import service.*;
import entity.*;
import java.util.List;

public class DashboardAdminFrame extends JFrame {
    private String userToken;
    private Usuario currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    private final UsuarioService usuarioService;

    public DashboardAdminFrame(String token) {
        this.userToken = token;
        
        // Initialize services
        this.usuarioService = new UsuarioService();
        
        try {
            JSONObject session = usuarioService.getSession(userToken);
            if (session != null) {
                int userId = session.getInt("id");
                Usuario usuario = usuarioService.getById(userId);
                if (usuario != null) {
                    System.out.println("Usuario encontrado: " + usuario.getNombre());
                    if (usuario.getRol() != null) {
                        System.out.println("Rol: " + usuario.getRol().getNombre());
                        if ("Admin".equalsIgnoreCase(usuario.getRol().getNombre()) ||
                            "Tecnico".equalsIgnoreCase(usuario.getRol().getNombre())) {
                            this.currentUser = usuario;
                        } else {
                            throw new SecurityException("Acceso denegado. Rol requerido: Admin o Técnico. Rol actual: " + usuario.getRol().getNombre());
                        }
                    } else {
                        throw new SecurityException("El usuario no tiene rol asignado");
                    }
                } else {
                    throw new SecurityException("Usuario no encontrado con ID: " + userId);
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
        
        // Configurar el layout principal
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        
        // Panel del menú lateral
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.WEST);
        
        // Agregar SOLO los tres paneles requeridos para Admin
        mainPanel.add(createUsersPanel(), "USERS");
        mainPanel.add(createSensorsPanel(), "SENSORS");
        mainPanel.add(createMessagesPanel(), "MESSAGES");
        
        // Mostrar panel de usuarios por defecto
        cardLayout.show(mainPanel, "USERS");
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

        // Botones del menú - Solo para Admin: Gestionar Usuarios, Gestionar Sensores, Mensajería
        addMenuButton(menuPanel, "Gestionar Usuarios", "USERS");
        addMenuButton(menuPanel, "Gestionar Sensores", "SENSORS");
        addMenuButton(menuPanel, "Mensajería", "MESSAGES");
        
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
        // Run sensor loading off the EDT to avoid freezing the UI when DB is slow/unreachable
        new Thread(() -> {
            try {
                List<Sensor> sensores = SensorService.getInstance().getAllSensors();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                // Update model on EDT
                javax.swing.SwingUtilities.invokeLater(() -> {
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
                });
            } catch (Exception e) {
                javax.swing.SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error al cargar sensores: " + e.getMessage())
                );
            }
        }).start();
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(236, 240, 241));

        // Título
        JLabel titleLabel = new JLabel("Gestionar Usuarios");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel superior con botones
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(236, 240, 241));

        JButton refreshButton = new JButton("Actualizar");
        JButton deleteButton = new JButton("Eliminar Seleccionado");
        JButton changeRoleButton = new JButton("Cambiar Rol");
        deleteButton.setForeground(Color.RED);

        topPanel.add(refreshButton);
        topPanel.add(deleteButton);
        topPanel.add(changeRoleButton);

        // Tabla de usuarios
        String[] columnNames = {"ID", "Nombre", "Email", "Rol", "Fecha Creación"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable usersTable = new JTable(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(usersTable);

        // Habilitar/deshabilitar botones según selección
        usersTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = usersTable.getSelectedRow() != -1;
            deleteButton.setEnabled(rowSelected);
            changeRoleButton.setEnabled(rowSelected);
        });
        deleteButton.setEnabled(false);
        changeRoleButton.setEnabled(false);

        // Acción del botón Actualizar
        refreshButton.addActionListener(e -> loadUsersAsync(tableModel, panel));

        // Acción del botón Eliminar
        deleteButton.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) tableModel.getValueAt(selectedRow, 0);
                String userName = (String) tableModel.getValueAt(selectedRow, 1);
                
                int confirm = JOptionPane.showConfirmDialog(panel,
                    "¿Está seguro de eliminar al usuario " + userName + "?",
                    "Confirmar Eliminación",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.OK_OPTION) {
                    try {
                        usuarioService.deleteUser(userId);
                        JOptionPane.showMessageDialog(panel,
                            "Usuario eliminado correctamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadUsersAsync(tableModel, panel);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel,
                            "Error al eliminar usuario: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Acción del botón Cambiar Rol
        changeRoleButton.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) tableModel.getValueAt(selectedRow, 0);
                String userName = (String) tableModel.getValueAt(selectedRow, 1);
                String currentRole = (String) tableModel.getValueAt(selectedRow, 3);

                // Diálogo para seleccionar el nuevo rol
                String[] roles = {"Admin", "Tecnico", "Cliente"};
                String selectedRole = (String) JOptionPane.showInputDialog(
                    panel,
                    "Seleccione el nuevo rol para " + userName + ":",
                    "Cambiar Rol",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    roles,
                    currentRole);

                if (selectedRole != null) {
                    try {
                        // Crear objeto Rol con los datos necesarios
                        Rol newRol = new Rol(selectedRole);
                        // Buscar el ID del rol (esto es una simplificación, idealmente consultarías la BD)
                        if ("Admin".equals(selectedRole)) {
                            newRol.setId(1);
                        } else if ("Tecnico".equals(selectedRole)) {
                            newRol.setId(2);
                        } else if ("Cliente".equals(selectedRole)) {
                            newRol.setId(3);
                        }

                        usuarioService.updateUserRole(userId, newRol);
                        JOptionPane.showMessageDialog(panel,
                            "Rol actualizado correctamente a: " + selectedRole,
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadUsersAsync(tableModel, panel);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel,
                            "Error al actualizar rol: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Cargar datos iniciales EN UN HILO SEPARADO para no bloquear la UI
        new Thread(() -> {
            loadUsersSynchronous(tableModel, panel);
        }).start();

        return panel;
    }

    private void loadUsersSynchronous(DefaultTableModel tableModel, JPanel parentPanel) {
        try {
            System.out.println("[DEBUG] Iniciando carga de usuarios desde MongoDB...");
            List<Usuario> usuarios = usuarioService.getAllUsuarios();
            System.out.println("[DEBUG] Usuarios obtenidos: " + (usuarios != null ? usuarios.size() : "null"));
            
            javax.swing.SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                if (usuarios != null && !usuarios.isEmpty()) {
                    System.out.println("[DEBUG] Llenando tabla con " + usuarios.size() + " usuarios");
                    for (Usuario user : usuarios) {
                        System.out.println("[DEBUG] Agregando usuario: " + user.getNombre() + " (" + user.getId() + ")");
                        tableModel.addRow(new Object[]{
                            user.getId(),
                            user.getNombre(),
                            user.getMail(),
                            user.getRol() != null ? user.getRol().getNombre() : "N/A",
                            user.getFechaRegistro() != null ? user.getFechaRegistro().toString() : "N/A"
                        });
                    }
                } else {
                    System.out.println("[DEBUG] Lista de usuarios vacía o nula");
                }
            });
        } catch (Exception e) {
            System.err.println("[ERROR] Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadUsersAsync(DefaultTableModel tableModel, JPanel parentPanel) {
        // Ejecutar en un hilo para no bloquear la UI al actualizar
        new Thread(() -> {
            try {
                List<Usuario> usuarios = usuarioService.getAllUsuarios();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    if (usuarios != null && !usuarios.isEmpty()) {
                        for (Usuario user : usuarios) {
                            tableModel.addRow(new Object[]{
                                user.getId(),
                                user.getNombre(),
                                user.getMail(),
                                user.getRol() != null ? user.getRol().getNombre() : "N/A",
                                user.getFechaRegistro() != null ? user.getFechaRegistro().toString() : "N/A"
                            });
                        }
                        JOptionPane.showMessageDialog(parentPanel, "Lista actualizada correctamente");
                    } else {
                        JOptionPane.showMessageDialog(parentPanel, "No hay usuarios disponibles");
                    }
                });
            } catch (Exception e) {
                javax.swing.SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(parentPanel, "Error al cargar usuarios: " + e.getMessage())
                );
            }
        }).start();
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

    private JPanel createMessagesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Show instructions instead of opening window automatically
        JLabel label = new JLabel("Haga clic en el botón para abrir la ventana de mensajes");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        // Add a button to reopen messages if closed
        JButton openMessagesButton = new JButton("Abrir Mensajes");
        openMessagesButton.addActionListener(e -> {
            VerMensajesFrame newFrame = new VerMensajesFrame(currentUser);
            newFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.add(openMessagesButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
}


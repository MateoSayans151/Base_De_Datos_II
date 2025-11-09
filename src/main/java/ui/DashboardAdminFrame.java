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
import java.time.LocalDate;

import entity.ControlFuncionamiento;
import entity.Sensor;
import exceptions.ErrorConectionMongoException;
import service.ControlFuncionamientoService;
import service.SensorService;

public class DashboardAdminFrame extends JFrame {
    private String userToken;
    private Usuario currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    private final UsuarioService usuarioService;
    private final SensorService sensorService;
    private final MedicionService medicionService;
    private final SolicitudProcesoService solicitudProcesoService;
    private final SolicitudProcesoRepository solicitudProcesoRepository;
    private final HistorialEjecucionRepository historialRepo;

    public DashboardAdminFrame(String token) {
        this.userToken = token;
        
        // Initialize services
        this.historialRepo = HistorialEjecucionRepository.getInstance();
        this.usuarioService = new UsuarioService();
        this.sensorService = new SensorService();
        this.medicionService = new MedicionService();
        this.solicitudProcesoRepository = SolicitudProcesoRepository.getInstance();
        this.solicitudProcesoService = new SolicitudProcesoService(solicitudProcesoRepository,historialRepo);
        
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
        mainPanel.add(createMessagesPanel(), "MESSAGES");
        
        // Para técnicos: Panel de aprobación de procesos
        if ("Tecnico".equals(currentUser.getRol().getNombre())) {
            mainPanel.add(createProcessApprovalPanel(), "PROCESS_APPROVAL");
        }

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
        
        // Si es técnico, agregar botón de aprobación de procesos
        if ("Tecnico".equals(currentUser.getRol().getNombre())) {
            JButton procesosBtn = createMenuButton("Aprobación de Procesos", "PROCESS_APPROVAL");
            menuPanel.add(procesosBtn);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
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

    // Helper that returns a styled menu button (used for conditional additions)
    private JButton createMenuButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.addActionListener(e -> cardLayout.show(mainPanel, cardName));
        return button;
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

    private JPanel createProcessApprovalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(236, 240, 241));

        // Título
        JLabel titleLabel = new JLabel("Aprobación de Procesos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel central con la lista de solicitudes
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(236, 240, 241));

        // Tabla de solicitudes
        String[] columnNames = {"ID", "Cliente", "Proceso", "Fecha", "Estado", "Costo"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable solicitudesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(solicitudesTable);

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton aprobarBtn = new JButton("Aprobar");
        JButton rechazarBtn = new JButton("Rechazar");
        JButton refreshBtn = new JButton("Actualizar");

        aprobarBtn.setEnabled(false);
        rechazarBtn.setEnabled(false);

        // Habilitar/deshabilitar botones según selección
        solicitudesTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = solicitudesTable.getSelectedRow() != -1;
            aprobarBtn.setEnabled(rowSelected);
            rechazarBtn.setEnabled(rowSelected);
        });

        // Actualizar lista de solicitudes (fetch in background to avoid blocking the EDT)
        refreshBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    List<SolicitudProceso> solicitudes = solicitudProcesoService.listarPorEstado("PENDIENTE");
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        tableModel.setRowCount(0);
                        for (SolicitudProceso sol : solicitudes) {
                            tableModel.addRow(new Object[]{
                                sol.getId(),
                                sol.getUsuario().getNombre(),
                                sol.getProceso().getNombre(),
                                sol.getFechaSolicitud(),
                                sol.getEstado(),
                                sol.getProceso().getCosto()
                            });
                        }
                    });
                } catch (Exception ex) {
                    javax.swing.SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
                        "Error al cargar solicitudes: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE));
                }
            }).start();
        });

        // Acción de aprobar
        aprobarBtn.addActionListener(e -> {
            int selectedRow = solicitudesTable.getSelectedRow();
            if (selectedRow != -1) {
                int solicitudId = (int) tableModel.getValueAt(selectedRow, 0);
                try {
                    Optional<SolicitudProceso> optSolicitud = solicitudProcesoService.obtenerPorId(solicitudId);
                    
                    if (optSolicitud.isPresent()) {
                        SolicitudProceso solicitud = optSolicitud.get();
                        solicitudProcesoService.actualizarEstado(solicitud.getId(), "APROBADO");
                        
                        // Crear factura
                        Factura factura = new Factura();
                        factura.setUsuario(solicitud.getUsuario());
                        factura.setFechaEmision(LocalDate.now());
                        factura.setProcesosFacturados(List.of(solicitud.getProceso()));
                        factura.setTotal(solicitud.getProceso().getCosto());
                        factura.setEstado("PENDIENTE");
                        
                        FacturaService facturaService = new FacturaService();
                        facturaService.createFactura(factura);
                        
                        JOptionPane.showMessageDialog(panel,
                            "Solicitud aprobada y factura generada",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                            
                        refreshBtn.doClick();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel,
                        "Error al aprobar solicitud: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Acción de rechazar
        rechazarBtn.addActionListener(e -> {
            int selectedRow = solicitudesTable.getSelectedRow();
            if (selectedRow != -1) {
                int solicitudId = (int) tableModel.getValueAt(selectedRow, 0);
                try {
                    Optional<SolicitudProceso> optSolicitud = solicitudProcesoService.obtenerPorId(solicitudId);
                    
                    if (optSolicitud.isPresent()) {
                        SolicitudProceso solicitud = optSolicitud.get();
                        solicitudProcesoService.actualizarEstado(solicitud.getId(), "RECHAZADO");
                        
                        JOptionPane.showMessageDialog(panel,
                            "Solicitud rechazada",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                            
                        refreshBtn.doClick();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel,
                        "Error al rechazar solicitud: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(refreshBtn);
        buttonPanel.add(aprobarBtn);
        buttonPanel.add(rechazarBtn);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);

    // Cargar datos iniciales (run the refresh action which itself fetches in background)
    refreshBtn.doClick();

        return panel;
    }

    private JPanel createVerControlesPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        // Table
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{"ID", "SensorId", "Cod", "Fecha", "Estado", "Observaciones"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateBtn = new JButton("Generar Controles");
        JButton refreshBtn = new JButton("Refrescar");
        top.add(generateBtn);
        top.add(refreshBtn);
        panel.add(top, BorderLayout.NORTH);

        // Services
        final ControlFuncionamientoService controlService = ControlFuncionamientoService.getInstance();
        final SensorService sensorService = SensorService.getInstance();

        // Helper to load and show controls
        Runnable loadControls = () -> {
            try {
                List<ControlFuncionamiento> controls = controlService.getAll();
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    if (controls == null) return;
                    for (ControlFuncionamiento c : controls) {
                        if (c == null) continue;
                        Object[] row = new Object[]{
                                c.getId(),
                                c.getSensor() != null ? c.getSensor().getId() : null,
                                c.getSensor() != null ? c.getSensor().getCod() : null,
                                c.getFechaControl() != null ? c.getFechaControl().toString() : null,
                                c.getEstado(),
                                c.getObservaciones()
                        };
                        tableModel.addRow(row);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Error loading controls: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        };

        // Generate button action: create one control per sensor and then refresh
        generateBtn.addActionListener(e -> {
            generateBtn.setEnabled(false);
            new Thread(() -> {
                try {
                    List<Sensor> sensors = sensorService.getAllSensors();
                    if (sensors != null) {
                        for (Sensor s : sensors) {
                            if (s == null) continue;
                            // create a control for each sensor (no null fields)
                            ControlFuncionamiento c = new ControlFuncionamiento(
                                    s,
                                    LocalDate.now(),
                                    "funcionando",
                                    "Generado desde UI"
                            );
                            controlService.create(c);
                        }
                    }
                    loadControls.run();
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                            "Error generating controls: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                } finally {
                    SwingUtilities.invokeLater(() -> generateBtn.setEnabled(true));
                }
            }).start();
        });

        // Refresh button
        refreshBtn.addActionListener(e -> new Thread(loadControls).start());

        // initial load
        new Thread(loadControls).start();

        return panel;
    }

    private JPanel createVerAlertasPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        // Table
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{"ID", "SensorId", "Cod", "Fecha", "Descripcion", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateBtn = new JButton("Generar Alertas");
        JButton refreshBtn = new JButton("Refrescar");
        top.add(generateBtn);
        top.add(refreshBtn);
        panel.add(top, BorderLayout.NORTH);

        // Service
        final AlertaService alertaService = AlertaService.getInstance();

        // Helper to load and show alerts
        Runnable loadAlerts = () -> {
            try {
                List<Alerta> alerts = alertaService.checkAlerts();
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    if (alerts == null) return;
                    for (Alerta a : alerts) {
                        if (a == null) continue;
                        Object[] row = new Object[]{
                                a.getId() != null ? a.getId().toString() : null,
                                a.getSensor() != null ? a.getSensor().getId() : null,
                                a.getSensor() != null ? a.getSensor().getCod() : null,
                                a.getFecha() != null ? a.getFecha().toString() : null,
                                a.getDescripcion(),
                                a.getEstado()
                        };
                        tableModel.addRow(row);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Error loading alerts: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        };

        // Generate button action: create alerts (uses service.createAllAlerts()) then refresh
        generateBtn.addActionListener(e -> {
            generateBtn.setEnabled(false);
            new Thread(() -> {
                try {
                    alertaService.createAllAlerts();
                    loadAlerts.run();
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                            "Error generating alerts: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                } finally {
                    SwingUtilities.invokeLater(() -> generateBtn.setEnabled(true));
                }
            }).start();
        });

        // Refresh button
        refreshBtn.addActionListener(e -> new Thread(loadAlerts).start());

        // initial load
        new Thread(loadAlerts).start();

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

    private JPanel createMessagesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Open VerMensajesFrame directly for the current user
        VerMensajesFrame mensajesFrame = new VerMensajesFrame(currentUser);
        mensajesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Allow closing messages without closing dashboard
        mensajesFrame.setVisible(true);

        // Show a message in the dashboard panel
        JLabel label = new JLabel("Ventana de mensajes abierta en una ventana separada");
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


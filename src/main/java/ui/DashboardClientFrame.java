package ui;

import javax.swing.*;
import java.awt.*;

import service.ExecuteService;
import service.UsuarioService;
import service.CuentaCorrienteService;
import service.FacturaService;
import entity.Usuario;
import entity.Factura;
import java.util.List;

public class DashboardClientFrame extends JFrame {
    private String userToken;
    private Usuario currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private UsuarioService usuarioService = new UsuarioService();

    public DashboardClientFrame(String token) {
        this.userToken = token;

        try {
            this.currentUser = usuarioService.validateToken(token);
            if (currentUser == null || !"Cliente".equalsIgnoreCase(currentUser.getRol().getNombre())) {
                throw new SecurityException("Acceso no autorizado: se requiere rol de cliente");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de autenticación: " + e.getMessage());
            System.exit(1);
        }

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Dashboard Cliente - " + currentUser.getNombre());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

    // Create panels for client sections (requested by user)
    mainPanel.add(createExecuteProcessPanel(), "PROCESS");
    mainPanel.add(createExecuteServicePanel(), "SERVICE");
    mainPanel.add(createViewBalancePanel(), "BALANCE");
    mainPanel.add(createPayInvoicePanel(), "PAY_INVOICE");
    mainPanel.add(new ViewProcessTypesPanel(currentUser), "REQUEST_PROCESS");
    mainPanel.add(new ViewProcessRequestsPanel(currentUser), "VIEW_REQUESTS");
    mainPanel.add(createAddFundsPanel(), "ADD_FUNDS");
        JPanel menuPanel = createMenuPanel();

        setLayout(new BorderLayout());
        add(menuPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(44, 62, 80));
        menuPanel.setPreferredSize(new Dimension(240, getHeight()));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // User info
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBackground(new Color(44, 62, 80));
        userInfo.setMaximumSize(new Dimension(220, 100));

        JLabel nameLabel = new JLabel(currentUser.getNombre());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("USUARIO");
        roleLabel.setForeground(new Color(46, 204, 113));
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userInfo.add(nameLabel);
        userInfo.add(Box.createRigidArea(new Dimension(0,5)));
        userInfo.add(roleLabel);

        menuPanel.add(userInfo);
        menuPanel.add(Box.createRigidArea(new Dimension(0,20)));

    // User-requested menu entries
    addMenuButton(menuPanel, "Solicitar Proceso", "REQUEST_PROCESS");
    addMenuButton(menuPanel, "Mis Solicitudes", "VIEW_REQUESTS");
    addMenuButton(menuPanel, "Ejecutar Proceso", "PROCESS");
    addMenuButton(menuPanel, "Ejecutar Servicio", "SERVICE");
    addMenuButton(menuPanel, "Ver Saldo", "BALANCE");
    addMenuButton(menuPanel, "Pagar Factura", "PAY_INVOICE");
    addMenuButton(menuPanel, "Agregar Fondos", "ADD_FUNDS");

    // Mensajería - abrir ventana de mensajes
    JButton mensajesBtn = new JButton("Mensajes");
    mensajesBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
    mensajesBtn.setMaximumSize(new Dimension(200, 40));
    mensajesBtn.addActionListener(e -> {
        VerMensajesFrame vm = new VerMensajesFrame(currentUser);
        vm.setVisible(true);
    });
    menuPanel.add(mensajesBtn);
    menuPanel.add(Box.createRigidArea(new Dimension(0,10)));

        menuPanel.add(Box.createVerticalGlue());
        JButton logoutBtn = new JButton("Cerrar Sesión");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(200, 40));
        logoutBtn.addActionListener(e -> logout());
        menuPanel.add(logoutBtn);

        return menuPanel;
    }

    private void addMenuButton(JPanel parent, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.addActionListener(e -> cardLayout.show(mainPanel, cardName));
        parent.add(btn);
        parent.add(Box.createRigidArea(new Dimension(0,10)));
    }

    private JPanel createWelcomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(236, 240, 241));
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel lbl = new JLabel("Bienvenido, " + currentUser.getNombre());
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        p.add(lbl, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1,2,10,10));
        content.setBackground(new Color(236, 240, 241));

        JPanel accountSummary = new JPanel(new BorderLayout());
        accountSummary.setBorder(BorderFactory.createTitledBorder("Resumen de Cuenta"));
        accountSummary.add(new JLabel("(Placeholder) Saldo, plan, información"), BorderLayout.CENTER);

        JPanel recentMeasurements = new JPanel(new BorderLayout());
        recentMeasurements.setBorder(BorderFactory.createTitledBorder("Mediciones Recientes"));
        recentMeasurements.add(new JLabel("(Placeholder) Últimas mediciones"), BorderLayout.CENTER);

        content.add(accountSummary);
        content.add(recentMeasurements);

        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private JPanel createAccountPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        p.add(new JLabel("Mi Cuenta"), BorderLayout.NORTH);
        p.add(new JLabel("(Placeholder) Detalles de la cuenta, información personal y métodos de pago"), BorderLayout.CENTER);
        return p;
    }

    private JPanel createMeasurementsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        p.add(new JLabel("Mis Mediciones"), BorderLayout.NORTH);
        p.add(new JLabel("(Placeholder) Tabla o gráfica de mediciones del usuario"), BorderLayout.CENTER);
        return p;
    }

    private JPanel createInvoicesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        p.add(new JLabel("Mis Facturas"), BorderLayout.NORTH);
        p.add(new JLabel("(Placeholder) Listado de facturas y estados"), BorderLayout.CENTER);
        return p;
    }

    private JPanel createMessagesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        p.add(new JLabel("Mensajes"), BorderLayout.NORTH);
        p.add(new JLabel("(Placeholder) Bandeja de entrada de mensajes"), BorderLayout.CENTER);
        return p;
    }

    private JPanel createSettingsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        p.add(new JLabel("Ajustes"), BorderLayout.NORTH);
        p.add(new JLabel("(Placeholder) Preferencias del usuario"), BorderLayout.CENTER);
        return p;
    }

    // --- New client-specific placeholder panels requested by the user ---
    private JPanel createExecuteProcessPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("Ejecutar Proceso");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(236, 240, 241));
        center.add(new JLabel("(Placeholder) Formulario para ejecutar un proceso"), BorderLayout.CENTER);

    JButton runBtn = new JButton("Ejecutar");
    // Visual-only: habilitado pero solo muestra tooltip (sin ejecutar lógica)
    runBtn.setToolTipText("Acción visual: no implementada aún");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(runBtn);

        p.add(center, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createExecuteServicePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("Ejecutar Servicio (MongoDB / Cassandra)");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10,10));
        center.setBackground(new Color(236, 240, 241));

        // Top controls: DB selector and simple instruction
        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topControls.setBackground(new Color(236, 240, 241));
        JLabel dbLabel = new JLabel("DB:");
        String[] dbOptions = {"MongoDB", "Cassandra"};
        JComboBox<String> dbCombo = new JComboBox<>(dbOptions);
        dbCombo.setSelectedIndex(0);
        topControls.add(dbLabel);
        topControls.add(dbCombo);

        JLabel hint = new JLabel("  Para Mongo: Por favor utilice `find <collection> <filterJson?>`.");
        hint.setFont(new Font("Arial", Font.PLAIN, 12));
        topControls.add(hint);

        center.add(topControls, BorderLayout.NORTH);

        // Query input area
        JTextArea queryArea = new JTextArea(6, 80);
        queryArea.setLineWrap(true);
        queryArea.setWrapStyleWord(true);
        queryArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        queryArea.setText("find myCollection {\"field\": \"value\"}");
        JScrollPane queryScroll = new JScrollPane(queryArea);
        queryScroll.setBorder(BorderFactory.createTitledBorder("Statement / Command"));
        center.add(queryScroll, BorderLayout.CENTER);

        // Result area
        JTextArea resultArea = new JTextArea(12, 80);
        resultArea.setEditable(false);
        resultArea.setLineWrap(false);
        resultArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createTitledBorder("Result"));
        resultScroll.setPreferredSize(new Dimension(800, 200)); // ensure visible height

        // Execute button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(new Color(236, 240, 241));
        JButton execBtn = new JButton("Execute");
        execBtn.setToolTipText("Send the statement to the selected database");
        bottom.add(execBtn);

        // Put result area and button panel into a single southPanel so they don't replace each other
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(resultScroll, BorderLayout.CENTER);
        southPanel.add(bottom, BorderLayout.SOUTH);

        center.add(southPanel, BorderLayout.SOUTH);

        p.add(center, BorderLayout.CENTER);

        // service that executes queries
        ExecuteService executeService = new ExecuteService();

        execBtn.addActionListener(e -> {
            String db = (String) dbCombo.getSelectedItem();
            String stmt = queryArea.getText();
            execBtn.setEnabled(false);
            resultArea.setText("Running...");
            // run in background
            new javax.swing.SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return executeService.execute(db, stmt);
                }
                @Override
                protected void done() {
                    try {
                        String res = get();
                        resultArea.setText(res);
                    } catch (Exception ex) {
                        resultArea.setText("Execution failed: " + ex.getMessage());
                    } finally {
                        execBtn.setEnabled(true);
                    }
                }
            }.execute();
        });

        return p;
    }

    private JPanel createViewBalancePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("Ver Saldo");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3,1,10,10));
        center.setBackground(new Color(236, 240, 241));
        
        JLabel saldoLabel = new JLabel("Saldo disponible:");
        JLabel montoLabel = new JLabel("$0.00");
        montoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JButton refreshBtn = new JButton("Actualizar Saldo");
        refreshBtn.addActionListener(e -> {
            try {
                double saldo = consultarSaldo();
                montoLabel.setText(String.format("$%.2f", saldo));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al consultar saldo: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        center.add(saldoLabel);
        center.add(montoLabel);
        center.add(refreshBtn);

        p.add(center, BorderLayout.CENTER);
        
        // Trigger initial balance load
        refreshBtn.doClick();
        
        return p;
    }

    private JPanel createPayInvoicePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("Pagar Factura");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(236, 240, 241));
        
        // Lista de facturas pendientes
        DefaultListModel<Factura> facturaListModel = new DefaultListModel<>();
        JList<Factura> facturaList = new JList<>(facturaListModel);
        facturaList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        facturaList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Factura) {
                    Factura factura = (Factura) value;
                    String fechaStr = factura.getFechaEmision() != null ? factura.getFechaEmision().toString() : "N/A";
                    Double total = factura.getTotal() != null ? factura.getTotal() : 0.0;
                    setText(String.format("Factura #%d - $%.2f - Fecha: %s",
                        factura.getId(),
                        total,
                        fechaStr));
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(facturaList);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5,5,5,5);
        
        // Label para facturas
        gbc.gridx = 0; gbc.gridy = 0;
        center.add(new JLabel("Facturas pendientes:"), gbc);
        
        // Lista de facturas
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        center.add(scrollPane, gbc);
        
        // Saldo disponible
        gbc.gridy = 2;
        gbc.weighty = 0;
        JLabel saldoLabel = new JLabel("Saldo disponible: $0.00");
        center.add(saldoLabel, gbc);
        
        // Botón de actualizar
        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.addActionListener(e -> {
            // Ejecutar en background para no bloquear la UI
            refreshBtn.setEnabled(false);
            facturaListModel.clear();
            new javax.swing.SwingWorker<List<Factura>, Void>() {
                private Exception error;

                @Override
                protected List<Factura> doInBackground() {
                    try {
                        // Actualizar saldo
                        double saldo = consultarSaldo();
                        SwingUtilities.invokeLater(() -> saldoLabel.setText(String.format("Saldo disponible: $%.2f", saldo)));

                        // Obtener facturas desde el servicio
                        FacturaService facturaService = FacturaService.getInstance();
                        var factura = facturaService.getFacturasByUsuario(currentUser.getId());
                        System.out.println(factura);
                        return factura;
                    } catch (Exception ex) {
                        this.error = ex;
                        return new java.util.ArrayList<>();
                    }
                }

                @Override
                protected void done() {
                    try {
                        facturaListModel.clear();
                        if (error != null) {
                            JOptionPane.showMessageDialog(p,
                                "Error al actualizar: " + error.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        List<Factura> facturas = get();
                        for (Factura f : facturas) {
                            if (f != null && "PENDIENTE".equalsIgnoreCase(f.getEstado())) {
                                facturaListModel.addElement(f);
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(p,
                            "Error al actualizar: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    } finally {
                        refreshBtn.setEnabled(true);
                    }
                }
            }.execute();
        });
        
        JButton payBtn = new JButton("Pagar Factura Seleccionada");
        payBtn.addActionListener(e -> {
            Factura selectedFactura = facturaList.getSelectedValue();
            if (selectedFactura == null) {
                JOptionPane.showMessageDialog(p,
                    "Por favor seleccione una factura para pagar",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                double saldo = consultarSaldo();
                
                if (saldo < selectedFactura.getTotal()) {
                    JOptionPane.showMessageDialog(p,
                        "Saldo insuficiente para pagar la factura",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Realizar el pago
                retirarFondos(selectedFactura.getTotal());
                
                // Actualizar estado de la factura
                FacturaService facturaService = new FacturaService();
                facturaService.updateEstado(selectedFactura.getId(), "PAGADA");
                
                JOptionPane.showMessageDialog(p,
                    "Factura pagada exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Actualizar la vista
                refreshBtn.doClick();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p,
                    "Error al procesar el pago: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshBtn);
        buttonPanel.add(payBtn);
        
        p.add(center, BorderLayout.CENTER);
        p.add(buttonPanel, BorderLayout.SOUTH);
        
        // Cargar datos iniciales
        refreshBtn.doClick();
        
        return p;
    }

    private JPanel createAddFundsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("Agregar Fondos");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(236, 240, 241));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);

        // Monto
        gbc.gridx = 0; gbc.gridy = 0;
        center.add(new JLabel("Monto: $"), gbc);
        JTextField montoField = new JTextField();
        gbc.gridx = 1;
        center.add(montoField, gbc);

        // Método de pago
        gbc.gridx = 0; gbc.gridy = 1;
        center.add(new JLabel("Método de pago:"), gbc);
        JComboBox<String> metodoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta de Débito", "Transferencia"});
        gbc.gridx = 1;
        center.add(metodoPago, gbc);

        // Saldo actual
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel saldoActual = new JLabel("Saldo actual: $0.00");
        center.add(saldoActual, gbc);

        // Actualizar saldo actual
        try {
            double saldo = consultarSaldo();
            saldoActual.setText(String.format("Saldo actual: $%.2f", saldo));
        } catch (Exception ex) {
            saldoActual.setText("Error al consultar saldo");
        }

        JButton addBtn = new JButton("Agregar Fondos");
        addBtn.addActionListener(e -> {
            try {
                String montoStr = montoField.getText().trim();
                double monto = Double.parseDouble(montoStr);
                
                if (monto <= 0) {
                    throw new IllegalArgumentException("El monto debe ser mayor a 0");
                }

                agregarFondos(monto);
                // Actualizar saldo mostrado
                double nuevoSaldo = consultarSaldo();
                saldoActual.setText(String.format("Saldo actual: $%.2f", nuevoSaldo));

                JOptionPane.showMessageDialog(this,
                    String.format("Se agregaron $%.2f a su cuenta", monto),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                
                montoField.setText("");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Por favor ingrese un monto válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al agregar fondos: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(new Color(236, 240, 241));
        bottom.add(addBtn);

        p.add(center, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    // Helper methods for cuenta corriente operations
    private double consultarSaldo() {
        try {
            CuentaCorrienteService ccService = CuentaCorrienteService.getInstance();
            return ccService.obtenerSaldo(currentUser.getId());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al consultar saldo: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return 0.0;
        }
    }

    private void agregarFondos(double monto) {
        try {
            CuentaCorrienteService ccService = new CuentaCorrienteService();
            ccService.agregarFondos(currentUser.getId(), monto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al agregar fondos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void retirarFondos(double monto) {
        try {
            CuentaCorrienteService ccService = CuentaCorrienteService.getInstance();
            ccService.debitarFondos(currentUser.getId(), monto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al retirar fondos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        try {
            usuarioService.logout(userToken);
            WelcomeFrame w = new WelcomeFrame();
            w.setVisible(true);
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cerrar sesión: " + e.getMessage());
        }
    }
}

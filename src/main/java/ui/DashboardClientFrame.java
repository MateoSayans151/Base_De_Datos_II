package ui;

import javax.swing.*;
import java.awt.*;

import service.ExecuteService;
import service.UsuarioService;
import entity.Usuario;
import exceptions.*;
import java.awt.event.*;

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
    addMenuButton(menuPanel, "Ejecutar Proceso", "PROCESS");
    addMenuButton(menuPanel, "Ejecutar Servicio", "SERVICE");
    addMenuButton(menuPanel, "Ver Saldo", "BALANCE");
    addMenuButton(menuPanel, "Pagar Factura", "PAY_INVOICE");
    addMenuButton(menuPanel, "Agregar Fondos", "ADD_FUNDS");

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

        JPanel center = new JPanel(new GridLayout(2,1,10,10));
        center.setBackground(new Color(236, 240, 241));
        center.add(new JLabel("Saldo disponible:"));
        center.add(new JLabel("(Placeholder) $0.00"));

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JPanel createPayInvoicePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("Pagar Factura");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(236, 240, 241));
        center.add(new JLabel("(Placeholder) Selección de factura y método de pago"), BorderLayout.CENTER);

    JButton payBtn = new JButton("Pagar");
    // Visual-only: habilitado pero solo muestra tooltip (sin ejecutar lógica)
    payBtn.setToolTipText("Acción visual: no implementada aún");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(payBtn);

        p.add(center, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createAddFundsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("Agregar Fondos");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2,2,10,10));
        center.setBackground(new Color(236, 240, 241));
        center.add(new JLabel("Monto:"));
        center.add(new JTextField("0.00"));
        center.add(new JLabel("Método de pago:"));
        center.add(new JLabel("(Placeholder)"));

    JButton addBtn = new JButton("Agregar Fondos");
    // Visual-only: habilitado pero solo muestra tooltip (sin ejecutar lógica)
    addBtn.setToolTipText("Acción visual: no implementada aún");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(addBtn);

        p.add(center, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
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

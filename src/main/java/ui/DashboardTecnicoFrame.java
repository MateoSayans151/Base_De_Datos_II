package ui;

import javax.swing.*;
import java.awt.*;

import org.json.JSONObject;
import service.*;
import entity.*;

public class DashboardTecnicoFrame extends JFrame {
    private String userToken;
    private Usuario currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    private final UsuarioService usuarioService;

    public DashboardTecnicoFrame(String token) {
        this.userToken = token;
        
        // Initialize services
        this.usuarioService = new UsuarioService();
        
        try {
            JSONObject session = usuarioService.getSession(userToken);
            if (session != null) {
                int userId = session.getInt("id");
                Usuario usuario = usuarioService.getById(userId);
                if (usuario != null &&
                    "Tecnico".equals(usuario.getRol().getNombre())) {
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
        setTitle("Panel de Técnico - " + currentUser.getNombre());
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
        
        // Agregar los tres paneles requeridos para Técnico
        mainPanel.add(createProcessManagementPanel(), "PROCESS_MANAGEMENT");
        mainPanel.add(createMessagesPanel(), "MESSAGES");
        mainPanel.add(createControlsPanel(), "CONTROLS");
        
        // Mostrar panel de gestión de procesos por defecto
        cardLayout.show(mainPanel, "PROCESS_MANAGEMENT");
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

        // Botones del menú - Solo para Técnico: Gestión de Procesos, Mensajería, Ver Controles
        addMenuButton(menuPanel, "Gestión de Procesos", "PROCESS_MANAGEMENT");
        addMenuButton(menuPanel, "Mensajería", "MESSAGES");
        addMenuButton(menuPanel, "Ver Controles", "CONTROLS");
        
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

    private JPanel createProcessManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Aquí irá el panel de gestión de procesos con funcionalidad completa
        // Por ahora, agregamos el ManageProcessRequestsPanel
        panel.add(new ManageProcessRequestsPanel(currentUser), BorderLayout.CENTER);

        return panel;
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

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(236, 240, 241));

        // Sin funcionalidad por ahora
        JLabel label = new JLabel("Ver Controles (sin funcionalidad)");
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

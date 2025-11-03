package ui;

import javax.swing.*;
import service.UsuarioService;
import repository.redis.InicioSesionRepository;
import repository.mongo.UsuarioRepository;
import java.awt.*;

public class WelcomeFrame extends JFrame {
    private final UsuarioService usuarioService;
    private final InicioSesionRepository sesionRepository;
    public WelcomeFrame() {
        this.usuarioService = new UsuarioService(new UsuarioRepository());
        this.sesionRepository = new InicioSesionRepository();
        
        setTitle("Polyglot Persistence - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Welcome to Sensor Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        mainPanel.add(titleLabel, gbc);

        // Panel de opciones
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints optGbc = new GridBagConstraints();
        optGbc.insets = new Insets(5, 20, 5, 20);

        // Panel izquierdo (Login)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(240, 240, 240));

        JLabel loginLabel = new JLabel("¿Ya tienes una cuenta?");
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton loginButton = new JButton("Log In");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setMaximumSize(new Dimension(120, 40));
        loginButton.addActionListener(e -> openLoginFrame());

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(loginLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(loginButton);
        leftPanel.add(Box.createVerticalGlue());

        // Separador vertical
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 150));
        JPanel separatorPanel = new JPanel(new BorderLayout());
        separatorPanel.add(separator);
        separatorPanel.setBackground(new Color(240, 240, 240));

        // Panel derecho (Sign Up)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(240, 240, 240));

        JLabel registerLabel = new JLabel("¿Es tu primera vez ingresando?");
        registerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setPreferredSize(new Dimension(120, 40));
        signUpButton.setMaximumSize(new Dimension(120, 40));
        signUpButton.addActionListener(e -> openRegisterFrame());

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(registerLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(signUpButton);
        rightPanel.add(Box.createVerticalGlue());

        // Agregar paneles al panel de opciones
        optGbc.gridx = 0;
        optGbc.gridy = 0;
        optionsPanel.add(leftPanel, optGbc);

        optGbc.gridx = 1;
        optionsPanel.add(separatorPanel, optGbc);

        optGbc.gridx = 2;
        optionsPanel.add(rightPanel, optGbc);

        // Agregar panel de opciones al panel principal
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 10, 20, 10);
        mainPanel.add(optionsPanel, gbc);

        add(mainPanel);
    }

    private void openLoginFrame() {
        LoginFrame loginFrame = new LoginFrame(false); // false indica que es para login
        loginFrame.setVisible(true);
        this.dispose();
    }

    private void openRegisterFrame() {
        LoginFrame registerFrame = new LoginFrame(true); // true indica que es para registro
        registerFrame.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomeFrame welcomeFrame = new WelcomeFrame();
            welcomeFrame.setVisible(true);
        });
    }
}
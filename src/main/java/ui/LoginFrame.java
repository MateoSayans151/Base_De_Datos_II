package ui;

import javax.swing.*;

import entity.Rol;
import exceptions.ErrorConectionMongoException;
import service.RolService;
import service.UsuarioService;
import repository.redis.InicioSesionRepository;
import repository.mongo.UsuarioRepository;
import entity.Usuario;
import entity.Sesion;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private String currentToken;
    private final UsuarioService usuarioService;
    private final InicioSesionRepository sesionRepository;

    public LoginFrame(UsuarioService usuarioService, InicioSesionRepository sesionRepository) {
        this.usuarioService = usuarioService;
        this.sesionRepository = sesionRepository;
        
        setTitle("Polyglot Persistence - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Sensor Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Email
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Botones
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));

        loginButton = new JButton("Login");
        loginButton.addActionListener(this::handleLogin);
        buttonPanel.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.addActionListener(this::handleRegister);
        buttonPanel.add(registerButton);

        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void handleLogin(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            Usuario user = usuarioService.getByMail(email);


                String token = usuarioService.login(email, password);
                if (token != null) {

                currentToken = token;
                System.out.println(token);
                System.out.println(user.getRol().getNombre());
                
                if (user.getRol().getNombre().equals("Admin")  || user.getRol().getNombre().equals("Tecnico")) {
                    new DashboardAdminFrame(currentToken).setVisible(true);
                } else {
                    new DashboardClientFrame(currentToken).setVisible(true);
                }
                
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Rol role = RolService.getInstance().getRolByName("Cliente");

            Usuario newUser = new Usuario("New User",email,password,role);
            newUser.setNombre("New User");
            newUser.setMail(email);
            newUser.setContrasena(password);

            usuarioService.create(newUser);
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (ErrorConectionMongoException ex) {
            // Mostrar mensaje más detallado para facilitar debugging: mensaje custom y causa SQL (si existe)
            String detail = ex.getMessage();
            if (ex.getCause() != null) {
                detail += " - " + ex.getCause().getMessage();
            }
            JOptionPane.showMessageDialog(this, "Registration error: " + detail, "Error", JOptionPane.ERROR_MESSAGE);
            // También imprimir stacktrace en consola para diagnóstico local
            ex.printStackTrace();

        }
    }
    /*
    private void openDashboard() {
        DashboardFrame dashboard = new DashboardFrame(currentToken);
        dashboard.setVisible(true);
        this.dispose();
    }

     */
}

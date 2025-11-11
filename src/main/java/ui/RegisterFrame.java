package ui;

import javax.swing.*;
import entity.Rol;
import entity.Usuario;
import exceptions.ErrorConectionMongoException;
import service.RolService;
import service.UsuarioService;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegisterFrame extends JFrame {
    private JTextField emailField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private final UsuarioService usuarioService;

    public RegisterFrame(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        
        setTitle("Polyglot Persistence - Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("User Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Name
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Role selection
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Role:"), gbc);
        roleComboBox = new JComboBox<>(new String[]{"Cliente", "Admin", "Tecnico"});
        gbc.gridx = 1;
        panel.add(roleComboBox, gbc);

        // Register button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(this::handleRegister);
        panel.add(registerButton, gbc);

        // Back to login button
        gbc.gridy = 6;
        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> {
            this.dispose();
        });
        panel.add(backButton, gbc);

        add(panel);
    }

    private void handleRegister(ActionEvent e) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String selectedRoleName = (String) roleComboBox.getSelectedItem();
            Rol role = RolService.getInstance().getRolByName(selectedRoleName);
            
            if (role == null) {
                RolService.getInstance().createRol(selectedRoleName);
                role = RolService.getInstance().getRolByName(selectedRoleName);
            }

            if (role == null) {
                JOptionPane.showMessageDialog(this, 
                    "Role '" + selectedRoleName + "' could not be created. Contact administrator.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario newUser = new Usuario(name, email, password, role);
            usuarioService.create(newUser);
            
            JOptionPane.showMessageDialog(this, 
                "Registration successful! Please login.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            this.dispose();
        } catch (ErrorConectionMongoException ex) {
            String detail = ex.getMessage();
            if (ex.getCause() != null) {
                detail += " - " + ex.getCause().getMessage();
            }
            JOptionPane.showMessageDialog(this, 
                "Registration error: " + detail, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
package ui;

import javax.swing.*;
import java.awt.*;
import entity.Usuario;
import service.CuentaCorrienteService;

public class AddFundsPanel extends JPanel {
    private final Usuario currentUser;
    private final CuentaCorrienteService cuentaService;
    private JTextField amountField;
    private JLabel balanceLabel;

    public AddFundsPanel(Usuario user) {
        this.currentUser = user;
        this.cuentaService = CuentaCorrienteService.getInstance();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = new JLabel("Agregar Fondos a la Cuenta", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Panel central
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Saldo actual
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balancePanel.add(new JLabel("Saldo actual: $"));
        balanceLabel = new JLabel("0.00");
        balancePanel.add(balanceLabel);
        updateBalance();

        // Campo para nuevo monto
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.add(new JLabel("Monto a agregar: $"));
        amountField = new JTextField(10);
        amountPanel.add(amountField);

        // Botón de agregar fondos
        JButton addButton = new JButton("Agregar Fondos");
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.addActionListener(e -> handleAddFunds());

        // Agregar componentes al panel central
        centerPanel.add(balancePanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(amountPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(addButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void updateBalance() {
        try {
            double balance = cuentaService.obtenerSaldo(currentUser.getId());
            balanceLabel.setText(String.format("%.2f", balance));
        } catch (Exception ex) {
            balanceLabel.setText("Error al obtener saldo");
            ex.printStackTrace();
        }
    }

    private void handleAddFunds() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this,
                    "El monto debe ser mayor a cero",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Agregar fondos a la cuenta
            cuentaService.agregarFondos(currentUser.getId(), amount);
            
            // Actualizar saldo mostrado
            updateBalance();
            
            // Limpiar campo
            amountField.setText("");
            
            JOptionPane.showMessageDialog(this,
                String.format("Se agregaron $%.2f a su cuenta", amount),
                "Fondos Agregados",
                JOptionPane.INFORMATION_MESSAGE);
                
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
    }
}
package ui;

import entity.Grupo;
import entity.Mensaje;
import entity.Usuario;
import service.GrupoService;
import service.MensajeService;
import service.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EnviarMensajeFrame extends JFrame {
    private final MensajeService mensajeService;
    private final UsuarioService usuarioService;
    private final GrupoService grupoService;
    private final Usuario usuarioActual;
    
    private JComboBox<String> tipoMensajeCombo;
    private JTextField emailDestinatarioField;
    private JComboBox<Grupo> grupoCombo;
    private JTextArea mensajeArea;
    private JButton enviarButton;
    private JButton crearGrupoButton;

    public EnviarMensajeFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        this.mensajeService = new MensajeService();
        this.usuarioService = new UsuarioService();
        this.grupoService = new GrupoService();
        
        setupUI();
    }

    private void setupUI() {
        setTitle("Enviar Mensaje");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tipo de mensaje
        JPanel tipoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tipoPanel.add(new JLabel("Tipo de mensaje:"));
        tipoMensajeCombo = new JComboBox<>(new String[]{"Personal", "Grupo"});
        tipoPanel.add(tipoMensajeCombo);
        mainPanel.add(tipoPanel);

        // Destinatario (para mensajes personales)
        JPanel destinatarioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        destinatarioPanel.add(new JLabel("Email del destinatario:"));
        emailDestinatarioField = new JTextField(20);
        destinatarioPanel.add(emailDestinatarioField);
        mainPanel.add(destinatarioPanel);

        // Grupo (para mensajes grupales)
        JPanel grupoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        grupoPanel.add(new JLabel("Grupo:"));
        grupoCombo = new JComboBox<>();
        cargarGrupos();
        grupoPanel.add(grupoCombo);
        crearGrupoButton = new JButton("Crear Grupo");
        grupoPanel.add(crearGrupoButton);
        mainPanel.add(grupoPanel);

        // Área de mensaje
        JPanel mensajePanel = new JPanel(new BorderLayout());
        mensajePanel.add(new JLabel("Mensaje:"), BorderLayout.NORTH);
        mensajeArea = new JTextArea(5, 30);
        mensajeArea.setLineWrap(true);
        mensajePanel.add(new JScrollPane(mensajeArea), BorderLayout.CENTER);
        mainPanel.add(mensajePanel);

        // Botón enviar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        enviarButton = new JButton("Enviar Mensaje");
        buttonPanel.add(enviarButton);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Event Listeners
        setupEventListeners();

        // Ensure initial visibility / enabled state so fields are editable and selectable
        tipoMensajeCombo.setSelectedItem("Personal"); // trigger visibility logic
        emailDestinatarioField.setEnabled(true);
        emailDestinatarioField.setEditable(true);
        mensajeArea.setEditable(true);
        // Try to give focus to the email field so user can start typing immediately
        SwingUtilities.invokeLater(() -> {
            emailDestinatarioField.requestFocusInWindow();
        });
    }

    private void setupEventListeners() {
        tipoMensajeCombo.addActionListener(e -> {
            String tipo = (String) tipoMensajeCombo.getSelectedItem();
            emailDestinatarioField.setVisible("Personal".equals(tipo));
            grupoCombo.setVisible("Grupo".equals(tipo));
            crearGrupoButton.setVisible("Grupo".equals(tipo));
        });

        crearGrupoButton.addActionListener(e -> mostrarDialogoCrearGrupo());

        enviarButton.addActionListener(e -> enviarMensaje());
    }

    private void cargarGrupos() {
        List<Grupo> grupos = grupoService.getGruposByUsuario(usuarioActual);
        grupos.forEach(grupoCombo::addItem);
    }

    private void mostrarDialogoCrearGrupo() {
        String nombreGrupo = JOptionPane.showInputDialog(this, "Ingrese el nombre del nuevo grupo:");
        if (nombreGrupo != null && !nombreGrupo.trim().isEmpty()) {
            Grupo nuevoGrupo = grupoService.crearGrupo(nombreGrupo, usuarioActual);
            if (nuevoGrupo != null) {
                grupoCombo.addItem(nuevoGrupo);
                grupoCombo.setSelectedItem(nuevoGrupo);
                JOptionPane.showMessageDialog(this, "Grupo creado exitosamente");
            }
        }
    }

    private void enviarMensaje() {
        String contenido = mensajeArea.getText();
        if (contenido.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El mensaje no puede estar vacío");
            return;
        }

        try {
            if ("Personal".equals(tipoMensajeCombo.getSelectedItem())) {
                String emailDestinatario = emailDestinatarioField.getText().trim();
                if (emailDestinatario.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor ingrese el email del destinatario");
                    return;
                }
                
                Usuario destinatario = usuarioService.getUsuarioByEmail(emailDestinatario);
                if (destinatario == null || destinatario.getId() <= 0) {
                    JOptionPane.showMessageDialog(this, "No se encontró ningún usuario con ese email");
                    return;
                }
                
                mensajeService.enviarMensajePersonal(usuarioActual, destinatario, contenido);
            } else {
                Grupo grupo = (Grupo) grupoCombo.getSelectedItem();
                mensajeService.enviarMensajeGrupo(usuarioActual, grupo, contenido);
            }
            
            JOptionPane.showMessageDialog(this, "Mensaje enviado exitosamente");
            mensajeArea.setText("");
            emailDestinatarioField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al enviar el mensaje: " + ex.getMessage());
        }
    }
}
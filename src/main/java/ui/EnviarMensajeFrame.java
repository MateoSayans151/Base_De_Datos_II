package ui;

import entity.Grupo;
import entity.Usuario;
import service.GrupoService;
import service.MensajeService;
import service.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

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
    private JButton agregarParticipanteButton;

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
        // Panel para grupos
        JPanel grupoPanel = new JPanel();
        grupoPanel.setLayout(new BoxLayout(grupoPanel, BoxLayout.Y_AXIS));

        // Subpanel para selector de grupo
        JPanel grupoSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        grupoSelectionPanel.add(new JLabel("Grupo:"));
        grupoCombo = new JComboBox<>();
        grupoCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Grupo) {
                    value = ((Grupo) value).getNombre();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        cargarGrupos();
        grupoSelectionPanel.add(grupoCombo);
        grupoPanel.add(grupoSelectionPanel);

        // Subpanel para gestión de grupos
        JPanel grupoManagementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        crearGrupoButton = new JButton("Crear Nuevo Grupo");
        grupoManagementPanel.add(crearGrupoButton);
        grupoPanel.add(grupoManagementPanel);

        // Subpanel para gestión de participantes
        JPanel participantesPanel = new JPanel();
        participantesPanel.setLayout(new BoxLayout(participantesPanel, BoxLayout.Y_AXIS));
        participantesPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Participantes"));
        
        JPanel addParticipantPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        agregarParticipanteButton = new JButton("Agregar Participante");
        addParticipantPanel.add(agregarParticipanteButton);
        participantesPanel.add(addParticipantPanel);
        
        // Solo mostrar el panel de participantes cuando se selecciona un grupo
        participantesPanel.setVisible(false);
        grupoPanel.add(participantesPanel);
        
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
            boolean isPersonal = "Personal".equals(tipo);
            emailDestinatarioField.getParent().setVisible(isPersonal);
            Component[] grupoComponents = ((JPanel)grupoCombo.getParent().getParent()).getComponents();
            for (Component c : grupoComponents) {
                c.setVisible(!isPersonal);
            }
            revalidate();
            repaint();
        });

        // When a group is selected, show participant management options
        grupoCombo.addActionListener(e -> {
            Grupo selectedGrupo = (Grupo) grupoCombo.getSelectedItem();
            boolean hasGroupSelected = selectedGrupo != null;
            
            // Find participantes panel (the one with the border title)
            for (Component c : ((JPanel)grupoCombo.getParent().getParent()).getComponents()) {
                if (c instanceof JPanel && ((JPanel)c).getBorder() != null) {
                    c.setVisible(hasGroupSelected);
                    if (hasGroupSelected) {
                        actualizarListaParticipantes(selectedGrupo);
                    }
                }
            }
            revalidate();
            repaint();
        });

        crearGrupoButton.addActionListener(e -> mostrarDialogoCrearGrupo());
        agregarParticipanteButton.addActionListener(e -> mostrarDialogoAgregarParticipante());
        enviarButton.addActionListener(e -> enviarMensaje());
    }

    private void cargarGrupos() {
        try {
            List<Grupo> grupos = grupoService.getGroupByUserId(usuarioActual.getId());
            // Sort groups by ID descending (most recent first)
            grupos.sort((g1, g2) -> Integer.compare(g2.getId(), g1.getId()));
            grupos.forEach(grupoCombo::addItem);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar grupos: " + ex.getMessage());
        }
    }

    private void mostrarDialogoCrearGrupo() {
        String nombreGrupo = JOptionPane.showInputDialog(this, "Ingrese el nombre del nuevo grupo:");
        if (nombreGrupo != null && !nombreGrupo.trim().isEmpty()) {
            try {
                // Create new group and initialize with creator as first member
                Grupo nuevoGrupo = new Grupo(nombreGrupo.trim());
                List<Usuario> miembros = new ArrayList<>();
                miembros.add(usuarioActual);
                nuevoGrupo.setMiembros(miembros);
                
                grupoService.createGroup(nuevoGrupo);
                
                // Refresh group list
                grupoCombo.removeAllItems();
                cargarGrupos();
                
                // Select the new group (find it by name since ID is assigned by DB)
                for (int i = 0; i < grupoCombo.getItemCount(); i++) {
                    Grupo g = (Grupo) grupoCombo.getItemAt(i);
                    if (g.getNombre().equals(nombreGrupo.trim())) {
                        grupoCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Grupo creado exitosamente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear el grupo: " + ex.getMessage());
            }
        }
    }

    private void mostrarDialogoAgregarParticipante() {
        Grupo grupo = (Grupo) grupoCombo.getSelectedItem();
        if (grupo == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un grupo primero");
            return;
        }

        String email = JOptionPane.showInputDialog(this, "Ingrese el email del usuario a agregar:");
        if (email == null || email.trim().isEmpty()) return;

        Usuario usuario = usuarioService.getUsuarioByEmail(email.trim());
        if (usuario == null || usuario.getId() <= 0) {
            JOptionPane.showMessageDialog(this, "No se encontró ningún usuario con ese email");
            return;
        }

        // Check if user is already in the group
        List<Usuario> miembros = grupo.getMiembros();
        if (miembros != null && miembros.stream().anyMatch(u -> u.getId() == usuario.getId())) {
            JOptionPane.showMessageDialog(this, "El usuario ya es miembro del grupo");
            return;
        }

        try {
            grupoService.addParticipantToGroup(grupo.getId(), usuario);
            // Reload groups to reflect the added participant
            grupoCombo.removeAllItems();
            cargarGrupos();
            JOptionPane.showMessageDialog(this, "Participante agregado correctamente");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar participante: " + ex.getMessage());
        }
    }

    private void actualizarListaParticipantes(Grupo grupo) {
        // Find participantes panel
        JPanel participantesPanel = null;
        for (Component c : ((JPanel)grupoCombo.getParent().getParent()).getComponents()) {
            if (c instanceof JPanel && ((JPanel)c).getBorder() != null) {
                participantesPanel = (JPanel)c;
                break;
            }
        }
        
        if (participantesPanel == null) return;

        // Remove old participant list if exists
        Component[] components = participantesPanel.getComponents();
        for (Component c : components) {
            if (c instanceof JScrollPane) {
                participantesPanel.remove(c);
            }
        }

        // Get current participants
        List<Usuario> participantes = grupo.getMiembros();
        
        // Create and add new participant list
        DefaultListModel<String> listModel = new DefaultListModel<>();
        participantes.forEach(p -> listModel.addElement(p.getNombre() + " (" + p.getMail() + ")"));
        
        JList<String> participantesList = new JList<>(listModel);
        participantesList.setVisibleRowCount(4);
        
        JScrollPane scrollPane = new JScrollPane(participantesList);
        participantesPanel.add(scrollPane);
        
        participantesPanel.revalidate();
        participantesPanel.repaint();
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

                // Validate that we're not sending to ourselves
                if (destinatario.getId() == usuarioActual.getId()) {
                    JOptionPane.showMessageDialog(this, "No puedes enviarte mensajes a ti mismo");
                    return;
                }
                
                mensajeService.enviarMensajePersonal(usuarioActual, destinatario, contenido);
            } else {
                Grupo grupo = (Grupo) grupoCombo.getSelectedItem();
                if (grupo == null) {
                    JOptionPane.showMessageDialog(this, "Seleccione un grupo válido");
                    return;
                }
                mensajeService.enviarMensajeGrupo(usuarioActual, grupo.getId(), contenido);
            }
            
            JOptionPane.showMessageDialog(this, "Mensaje enviado exitosamente");
            mensajeArea.setText("");
            emailDestinatarioField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al enviar el mensaje: " + ex.getMessage());
        }
    }
}
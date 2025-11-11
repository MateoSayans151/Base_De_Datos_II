package ui;

import javax.swing.*;
import java.awt.*;
import service.ProcesoService;
import service.SolicitudProcesoService;
import entity.Proceso;
import entity.Usuario;
import java.util.List;

public class ViewProcessTypesPanel extends JPanel {
    private final Usuario currentUser;
    private final ProcesoService procesoService;
    private final SolicitudProcesoService solicitudService;
    private final JPanel processCardsPanel;

    public ViewProcessTypesPanel(Usuario user) {
        this.currentUser = user;
        // ProcesoService isn't a singleton; instantiate directly
        this.procesoService = new ProcesoService();
        // SolicitudProcesoService provides a singleton accessor
        this.solicitudService = SolicitudProcesoService.getInstance();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = new JLabel("Tipos de Procesos Disponibles", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Panel para las tarjetas de procesos con scroll
        processCardsPanel = new JPanel();
        processCardsPanel.setLayout(new GridLayout(0, 2, 20, 20));
        processCardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(processCardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        loadProcessTypes();
    }

    private void loadProcessTypes() {
        try {
            // Limpiar el panel antes de cargar
            processCardsPanel.removeAll();
            
            // Cargar procesos desde MongoDB
            List<Proceso> procesos = procesoService.obtenerTodosLosProcesos();
            
            if (procesos == null || procesos.isEmpty()) {
                JLabel noProcessLabel = new JLabel("No hay procesos disponibles en este momento.");
                noProcessLabel.setHorizontalAlignment(SwingConstants.CENTER);
                processCardsPanel.add(noProcessLabel);
                return;
            }

            // Crear tarjeta para cada proceso
            for (Proceso proceso : procesos) {
                ProcessTypeInfo typeInfo = new ProcessTypeInfo(
                    proceso.getNombre(),
                    proceso.getDescripcion(),
                    String.format("Tipo: %s | Costo: $%.2f", proceso.getTipo(), proceso.getCosto())
                );
                addProcessCard(typeInfo, proceso);
            }
            
            // Refrescar el panel
            processCardsPanel.revalidate();
            processCardsPanel.repaint();
        } catch (Exception ex) {
            JLabel errorLabel = new JLabel("Error al cargar procesos: " + ex.getMessage());
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setForeground(Color.RED);
            processCardsPanel.add(errorLabel);
            processCardsPanel.revalidate();
            processCardsPanel.repaint();
        }
    }

    private void addProcessCard(ProcessTypeInfo processType, Proceso proceso) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        // Título del proceso
        JLabel titleLabel = new JLabel(processType.name);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Descripción del proceso
        JTextArea descArea = new JTextArea(processType.description + "\n\n" + processType.details);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setBackground(null);
        descArea.setBorder(null);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botón de solicitud
        JButton requestButton = new JButton("Solicitar Proceso");
        requestButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        requestButton.addActionListener(e -> handleProcessRequest(proceso));

        // Agregar componentes a la tarjeta
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(descArea);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(requestButton);

        processCardsPanel.add(card);
    }

    private void handleProcessRequest(Proceso proceso) {
        try {
            // Pedir ciudad o país al usuario (al menos uno requerido)
            JTextField ciudadField = new JTextField();
            JTextField paisField = new JTextField();
            Object[] message = {
                "Ingrese la ciudad (opcional):", ciudadField,
                "Ingrese el país (opcional):", paisField,
                "(Al menos uno de los dos debe completarse)"
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Especificar ubicación", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                return; // usuario canceló
            }

            String ciudad = ciudadField.getText() != null ? ciudadField.getText().trim() : "";
            String pais = paisField.getText() != null ? paisField.getText().trim() : "";

            if ((ciudad == null || ciudad.isEmpty()) && (pais == null || pais.isEmpty())) {
                JOptionPane.showMessageDialog(this, "Debe especificar al menos una ciudad o un país.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Crear solicitud de proceso con ubicación
            solicitudService.crearSolicitudProceso(currentUser, proceso, ciudad.isEmpty() ? null : ciudad, pais.isEmpty() ? null : pais);

            JOptionPane.showMessageDialog(
                this,
                "Solicitud enviada exitosamente.\nUn técnico revisará su solicitud pronto.",
                "Solicitud Enviada",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error al enviar la solicitud: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Clase interna para manejar la información de tipos de procesos
    private static class ProcessTypeInfo {
        String name;
        String description;
        String details;

        ProcessTypeInfo(String name, String description, String details) {
            this.name = name;
            this.description = description;
            this.details = details;
        }
    }
}
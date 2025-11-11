package ui;

import javax.swing.*;
import java.awt.*;
import entity.SolicitudProceso;
import entity.Usuario;
import service.SolicitudProcesoService;
import java.util.List;

public class ViewProcessRequestsPanel extends JPanel {
    private final Usuario currentUser;
    private final SolicitudProcesoService solicitudService;
    private final JPanel requestsPanel;

    public ViewProcessRequestsPanel(Usuario user) {
        this.currentUser = user;
        this.solicitudService = SolicitudProcesoService.getInstance();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = new JLabel("Mis Solicitudes de Procesos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Panel para las solicitudes con scroll
        requestsPanel = new JPanel();
        requestsPanel.setLayout(new BoxLayout(requestsPanel, BoxLayout.Y_AXIS));
        requestsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(requestsPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Botón de actualizar
        JButton refreshButton = new JButton("Actualizar Lista");
        refreshButton.addActionListener(e -> loadRequests());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadRequests();
    }

    private void loadRequests() {
        requestsPanel.removeAll();

        try {
            List<SolicitudProceso> solicitudes = solicitudService.obtenerSolicitudesUsuario(currentUser.getId());

            if (solicitudes.isEmpty()) {
                JLabel emptyLabel = new JLabel("No hay solicitudes pendientes");
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                requestsPanel.add(emptyLabel);
            } else {
                for (SolicitudProceso solicitud : solicitudes) {
                    addRequestCard(solicitud);
                }
            }
        } catch (Exception ex) {
            JLabel errorLabel = new JLabel("Error al cargar solicitudes: " + ex.getMessage());
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            requestsPanel.add(errorLabel);
        }

        requestsPanel.revalidate();
        requestsPanel.repaint();
    }

    private void addRequestCard(SolicitudProceso solicitud) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        card.setBackground(Color.WHITE);

        // Información del proceso
        JLabel processLabel = new JLabel("Proceso: " + solicitud.getProceso().getNombre());
        processLabel.setFont(new Font("Arial", Font.BOLD, 14));
        processLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Estado de la solicitud
        JLabel statusLabel = new JLabel("Estado: " + solicitud.getEstado());
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fecha de solicitud
        JLabel dateLabel = new JLabel("Fecha: " + solicitud.getFechaSolicitud().toString());
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Si está aprobada y hay un costo asignado
        if ("aprobada".equals(solicitud.getEstado()) && solicitud.getProceso().getCosto() > 0) {
            JLabel costLabel = new JLabel(String.format("Costo asignado: $%.2f", solicitud.getProceso().getCosto()));
            costLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(costLabel);
        }

        // Agregar componentes a la tarjeta
        card.add(processLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(statusLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(dateLabel);

        // Agregar espacio entre tarjetas
        requestsPanel.add(card);
        requestsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
}
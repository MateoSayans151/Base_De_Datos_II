package ui;

import javax.swing.*;
import java.awt.*;
import entity.SolicitudProceso;
import entity.Usuario;
import service.SolicitudProcesoService;
import service.FacturaService;
import java.util.List;

public class ManageProcessRequestsPanel extends JPanel {
    private final Usuario currentUser;
    private final SolicitudProcesoService solicitudService;
    private final FacturaService facturaService;
    private final JPanel requestsPanel;

    public ManageProcessRequestsPanel(Usuario admin) {
        this.currentUser = admin;
        this.solicitudService = SolicitudProcesoService.getInstance();
        this.facturaService = FacturaService.getInstance();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = new JLabel("Gestión de Solicitudes de Procesos", SwingConstants.CENTER);
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
        refreshButton.addActionListener(e -> loadPendingRequests());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadPendingRequests();
    }

    private void loadPendingRequests() {
        requestsPanel.removeAll();

        try {
            List<SolicitudProceso> solicitudes = solicitudService.obtenerSolicitudesPendientes();

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
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        card.setBackground(Color.WHITE);

        // Información del proceso
        JLabel processLabel = new JLabel("Proceso: " + solicitud.getProceso().getNombre());
        processLabel.setFont(new Font("Arial", Font.BOLD, 14));
        processLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Solicitante
        JLabel userLabel = new JLabel("Solicitante: " + solicitud.getUsuario().getNombre());
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fecha de solicitud
        JLabel dateLabel = new JLabel("Fecha: " + solicitud.getFechaSolicitud().toString());
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel para los botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botón de aprobar
        JButton approveButton = new JButton("Aprobar");
        approveButton.addActionListener(e -> showApprovalDialog(solicitud));
        
        // Botón de rechazar
        JButton rejectButton = new JButton("Rechazar");
        rejectButton.addActionListener(e -> showRejectDialog(solicitud));

        actionPanel.add(approveButton);
        actionPanel.add(rejectButton);

        // Agregar componentes a la tarjeta
        card.add(processLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(userLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(dateLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(actionPanel);

        // Agregar espacio entre tarjetas
        requestsPanel.add(card);
        requestsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void showApprovalDialog(SolicitudProceso solicitud) {
        String message = String.format("¿Está seguro de aprobar el proceso \"%s\" con un costo de $%.2f?", 
            solicitud.getProceso().getNombre(), 
            solicitud.getProceso().getCosto());
            
        int result = JOptionPane.showConfirmDialog(this, message, 
            "Aprobar Solicitud", JOptionPane.OK_CANCEL_OPTION);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Aprobar la solicitud usando el usuario actual (técnico)
                solicitudService.aprobarSolicitud(solicitud.getId(), currentUser);
                // Crear factura usando la solicitud completa
                facturaService.crearFacturaProceso(solicitud);
                loadPendingRequests();
                JOptionPane.showMessageDialog(this, 
                    "Solicitud aprobada y factura creada exitosamente.\n" +
                    "Monto facturado: $" + solicitud.getProceso().getCosto());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al aprobar la solicitud: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRejectDialog(SolicitudProceso solicitud) {
        int result = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de rechazar la solicitud?", 
            "Rechazar Solicitud", 
            JOptionPane.OK_CANCEL_OPTION);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Rechazar la solicitud sin requerir motivo
                solicitudService.rechazarSolicitud(solicitud.getId(), currentUser);
                loadPendingRequests();
                JOptionPane.showMessageDialog(this, "Solicitud rechazada");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al rechazar la solicitud: " + ex.getMessage());
            }
        }
    }
}
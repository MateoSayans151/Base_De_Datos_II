package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import entity.*;
import service.*;
import repository.mongo.*;

public class ProcessApprovalFrame extends JFrame {
    private final SolicitudProcesoService solicitudService;
    private final FacturaService facturaService;
    private final SolicitudProcesoRepository solicitudRepo;
    private JList<SolicitudProceso> solicitudList;
    private DefaultListModel<SolicitudProceso> listModel;
    private final HistorialEjecucionRepository historialRepo;

    public ProcessApprovalFrame(Usuario user) {
        this.historialRepo = HistorialEjecucionRepository.getInstance();
        this.solicitudRepo = SolicitudProcesoRepository.getInstance();
        this.solicitudService = new SolicitudProcesoService(solicitudRepo,historialRepo);
        this.facturaService = new FacturaService();
        
        setupUI();
        loadPendingSolicitudes();
    }

    private void setupUI() {
        setTitle("Process Approval - Technician View");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Requests list
        listModel = new DefaultListModel<>();
        solicitudList = new JList<>(listModel);
        solicitudList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        solicitudList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SolicitudProceso) {
                    SolicitudProceso solicitud = (SolicitudProceso) value;
                    setText(String.format("Request #%d - Process: %s - User: %s - Status: %s",
                        solicitud.getId(),
                        solicitud.getProceso().getNombre(),
                        solicitud.getUsuario().getNombre(),
                        solicitud.getEstado()));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(solicitudList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton approveButton = new JButton("Approve");
        approveButton.addActionListener(e -> handleApproval(true));
        
        JButton rejectButton = new JButton("Reject");
        rejectButton.addActionListener(e -> handleApproval(false));
        
        JButton refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(e -> loadPendingSolicitudes());

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadPendingSolicitudes() {
        listModel.clear();
        try {
            List<SolicitudProceso> pendientes = solicitudService.listarPorEstado("pendiente");
            pendientes.forEach(listModel::addElement);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading pending requests: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleApproval(boolean approved) {
        SolicitudProceso selectedSolicitud = solicitudList.getSelectedValue();
        if (selectedSolicitud == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a request to process",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (approved) {
                // Update solicitud status
                solicitudService.actualizarEstado(selectedSolicitud.getId(), "APROBADO");
                
                // Create invoice
                List<Proceso> procesos = new ArrayList<>();
                procesos.add(selectedSolicitud.getProceso());
                
                Factura factura = new Factura(
                    selectedSolicitud.getUsuario(),
                    LocalDate.now(),
                    "PENDIENTE",
                    procesos,
                    selectedSolicitud.getProceso().getCosto()
                );
                
                facturaService.createFactura(factura);

                JOptionPane.showMessageDialog(this,
                    "Request approved and invoice created.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Just update status to rejected
                solicitudService.actualizarEstado(selectedSolicitud.getId(), "rechazado");
                JOptionPane.showMessageDialog(this,
                    "Request rejected.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            // Refresh the list
            loadPendingSolicitudes();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error processing request: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
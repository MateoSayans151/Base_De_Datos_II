package ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import entity.*;
import repository.mongo.HistorialEjecucionRepository;
import service.*;
import exceptions.ErrorConectionMongoException;
import repository.mongo.SolicitudProcesoRepository;

public class ProcessRequestFrame extends JFrame {
    private final Usuario currentUser;
    private final ProcesoService procesoService;
    private final SolicitudProcesoService solicitudService;
    private JList<Proceso> procesoList;
    private DefaultListModel<Proceso> listModel;
    private final SolicitudProcesoRepository solicitudRepo;
    private final HistorialEjecucionRepository historialRepo;

    public ProcessRequestFrame(Usuario user) {
        this.historialRepo = HistorialEjecucionRepository.getInstance();
        this.currentUser = user;
        this.procesoService = new ProcesoService();
        this.solicitudRepo = SolicitudProcesoRepository.getInstance();
        this.solicitudService = new SolicitudProcesoService(solicitudRepo,historialRepo);
        
        setupUI();
        loadProcesos();
    }

    private void setupUI() {
        setTitle("Request Process");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Process list
        listModel = new DefaultListModel<>();
        procesoList = new JList<>(listModel);
        procesoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        procesoList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Proceso) {
                    Proceso proceso = (Proceso) value;
                    setText(String.format("%s - %s (Cost: $%.2f)", proceso.getNombre(), proceso.getDescripcion(), proceso.getCosto()));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(procesoList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton requestButton = new JButton("Request Selected Process");
        requestButton.addActionListener(e -> requestProcess());
        buttonPanel.add(requestButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadProcesos() {
        try {

            
            // Load all processes
            procesoService.listarPorTipo("cualquiera").forEach(listModel::addElement);
        } catch (ErrorConectionMongoException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading processes: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }


    private void requestProcess() {
        Proceso selectedProceso = procesoList.getSelectedValue();
        if (selectedProceso == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a process to request",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            solicitudService.crearSolicitud(
                currentUser,
                selectedProceso,
                "Pendiente"
            );

            JOptionPane.showMessageDialog(this,
                "Solicitud enviada correctamente.\nEsperando aprobación del técnico.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error submitting request: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
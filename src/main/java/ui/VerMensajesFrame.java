package ui;

import entity.Mensaje;
import entity.Usuario;
import service.MensajeService;
import exceptions.ErrorConectionMongoException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VerMensajesFrame extends JFrame {
    private final Usuario usuarioActual;
    private final MensajeService mensajeService;
    private JTable mensajesTable;
    private JTabbedPane tabbedPane;
    private DefaultTableModel modeloRecibidos;
    private DefaultTableModel modeloEnviados;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public VerMensajesFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        this.mensajeService = MensajeService.getInstance();
        setupUI();
        cargarMensajes();
    }

    private void setupUI() {
        setTitle("Mensajes - " + usuarioActual.getNombre());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear el panel con pestañas
        tabbedPane = new JTabbedPane();
        
        // Panel de mensajes recibidos
        JPanel recibidosPanel = crearPanelMensajes("Recibidos");
        modeloRecibidos = (DefaultTableModel) ((JTable)((JScrollPane)recibidosPanel.getComponent(0)).getViewport().getView()).getModel();
        
        // Panel de mensajes enviados
        JPanel enviadosPanel = crearPanelMensajes("Enviados");
        modeloEnviados = (DefaultTableModel) ((JTable)((JScrollPane)enviadosPanel.getComponent(0)).getViewport().getView()).getModel();

        tabbedPane.addTab("Mensajes Recibidos", recibidosPanel);
        tabbedPane.addTab("Mensajes Enviados", enviadosPanel);

        // Top panel with title + action buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Mensajes - " + usuarioActual.getNombre());
        title.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton nuevoBtn = new JButton("Nuevo Mensaje");
        nuevoBtn.addActionListener(e -> {
            EnviarMensajeFrame enviar = new EnviarMensajeFrame(usuarioActual);
            enviar.setVisible(true);
        });
        JButton actualizarButton = new JButton("Actualizar Mensajes");
        actualizarButton.addActionListener(e -> cargarMensajes());
        actions.add(nuevoBtn);
        actions.add(actualizarButton);
        topPanel.add(actions, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelMensajes(String tipo) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear modelo de tabla
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Definir columnas
        modelo.addColumn("Fecha");
        if (tipo.equals("Recibidos")) {
            modelo.addColumn("De");
        } else {
            modelo.addColumn("Para");
        }
        modelo.addColumn("Mensaje");

        // Crear tabla
        JTable tabla = new JTable(modelo);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(150); // Fecha
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150); // De/Para
        tabla.getColumnModel().getColumn(2).setPreferredWidth(500); // Mensaje
        
        // Hacer que el texto se envuelva en la columna del mensaje
        tabla.getColumnModel().getColumn(2).setCellRenderer(new TextAreaRenderer());

        // Agregar tabla a un scroll pane
        JScrollPane scrollPane = new JScrollPane(tabla);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void cargarMensajes() {
        try {
            // Limpiar tablas
            modeloRecibidos.setRowCount(0);
            modeloEnviados.setRowCount(0);

            // Cargar mensajes recibidos
            List<Mensaje> mensajesRecibidos = mensajeService.getMensajesPorDestinatario(usuarioActual.getId());
            for (Mensaje mensaje : mensajesRecibidos) {
                String tipo = mensaje.getTipo() == null ? "" : mensaje.getTipo();
                if ("PERSONAL".equalsIgnoreCase(tipo) || "privado".equalsIgnoreCase(tipo)) {
                    modeloRecibidos.addRow(new Object[]{
                        mensaje.getFechaEnvio().format(formatter),
                        mensaje.getRemitente().getNombre() + " (" + mensaje.getRemitente().getMail() + ")",
                        mensaje.getContenido()
                    });
                }
            }

            // Cargar mensajes enviados
            List<Mensaje> mensajesEnviados = mensajeService.getMensajesPorRemitente(usuarioActual.getId());
            for (Mensaje mensaje : mensajesEnviados) {
                String tipo = mensaje.getTipo() == null ? "" : mensaje.getTipo();
                if ("PERSONAL".equalsIgnoreCase(tipo) || "privado".equalsIgnoreCase(tipo)) {
                    modeloEnviados.addRow(new Object[]{
                        mensaje.getFechaEnvio().format(formatter),
                        mensaje.getDestinatario().getNombre() + " (" + mensaje.getDestinatario().getMail() + ")",
                        mensaje.getContenido()
                    });
                }
            }

        } catch (ErrorConectionMongoException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar los mensajes: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

// Renderer personalizado para mostrar texto con saltos de línea
class TextAreaRenderer extends JTextArea implements javax.swing.table.TableCellRenderer {
    public TextAreaRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value != null ? value.toString() : "");
        setSize(table.getColumnModel().getColumn(column).getWidth(),
                getPreferredSize().height);
        if (table.getRowHeight(row) != getPreferredSize().height) {
            table.setRowHeight(row, getPreferredSize().height);
        }
        
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        
        return this;
    }
}
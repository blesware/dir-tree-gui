import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileExplorerGUI extends JFrame {

    private JTextArea textArea;
    private final List<String> ignorados = new ArrayList<>();

    public FileExplorerGUI() {
        setTitle("Explorador de Carpetas - Estructura de Proyecto");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializar ignorados por defecto
        ignorados.add(".git");
        ignorados.add(".mvn");
        ignorados.add("node_modules");
        ignorados.add("dist");

        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnSeleccionar = new JButton("Seleccionar Carpeta");
        btnSeleccionar.addActionListener(this::seleccionarCarpeta);

        JButton btnCopiar = new JButton("Copiar");
        btnCopiar.addActionListener(this::copiarAlPortapapeles);

        JButton btnAgregarIgnorado = new JButton("Agregar Ignorado");
        btnAgregarIgnorado.addActionListener(this::agregarIgnorado);

        JButton btnVerIgnorados = new JButton("Ver Ignorados");
        btnVerIgnorados.addActionListener(this::verIgnorados);

        JButton btnEliminarIgnorado = new JButton("Eliminar Ignorado"); // Nuevo botón
        btnEliminarIgnorado.addActionListener(this::eliminarIgnorado);

        panelBotones.add(btnSeleccionar);
        panelBotones.add(btnCopiar);
        panelBotones.add(btnAgregarIgnorado);
        panelBotones.add(btnVerIgnorados);
        panelBotones.add(btnEliminarIgnorado); // Agregado al panel

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setCursor(Cursor.getDefaultCursor());

        JScrollPane scrollPane = new JScrollPane(textArea);

        getContentPane().add(panelBotones, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        StringBuilder sb = new StringBuilder("Carpetas actualmente ignoradas:\n\n");
        for (String carpeta : ignorados) {
            sb.append("• ").append(carpeta).append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Lista de Ignorados", JOptionPane.INFORMATION_MESSAGE);
    }

    private void seleccionarCarpeta(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setFocusable(false);

        int opcion = fileChooser.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            File carpeta = fileChooser.getSelectedFile();
            textArea.setText("");
            mostrarEstructura(carpeta, 0);
        }
    }

    private void copiarAlPortapapeles(ActionEvent e) {
        try {
            String contenido = textArea.getText();
            if (contenido.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay contenido para copiar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection stringSelection = new StringSelection(contenido);
            clipboard.setContents(stringSelection, null);

            JOptionPane.showMessageDialog(this, "Contenido copiado al portapapeles exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al copiar al portapapeles: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarIgnorado(ActionEvent e) {
        String nuevo = JOptionPane.showInputDialog(this, "Nombre exacto de la carpeta a ignorar:", "Agregar Carpeta Ignorada", JOptionPane.PLAIN_MESSAGE);

        if (nuevo != null) {
            nuevo = nuevo.trim();
            if (nuevo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (ignorados.contains(nuevo)) {
                JOptionPane.showMessageDialog(this, "Esa carpeta ya está en la lista de ignorados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            } else {
                ignorados.add(nuevo);
                JOptionPane.showMessageDialog(this, "Agregada exitosamente a la lista de ignorados.", "Hecho", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void verIgnorados(ActionEvent e) {
        if (ignorados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La lista de ignorados está vacía.", "Ignorados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Carpetas actualmente ignoradas:\n\n");
        for (String carpeta : ignorados) {
            sb.append("• ").append(carpeta).append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Lista de Ignorados", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarIgnorado(ActionEvent e) {
        if (ignorados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay carpetas para eliminar.", "Ignorados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] opciones = ignorados.toArray(new String[0]);
        String seleccionado = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona una carpeta a eliminar de la lista de ignorados:",
                "Eliminar Ignorado",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccionado != null && ignorados.contains(seleccionado)) {
            ignorados.remove(seleccionado);
            JOptionPane.showMessageDialog(this, "Eliminado de la lista de ignorados.", "Hecho", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarEstructura(File archivo, int nivel) {
        StringBuilder indentacion = new StringBuilder();
        for (int i = 0; i < nivel; i++) indentacion.append("│   ");

        String nombre = archivo.getName();
        if (archivo.isDirectory()) nombre += "/";

        textArea.append(indentacion + "├── " + nombre + "\n");

        if (archivo.isDirectory() && ignorados.contains(archivo.getName())) {
            return;
        }

        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();
            if (hijos != null) {
                for (File hijo : hijos) {
                    mostrarEstructura(hijo, nivel + 1);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FileExplorerGUI().setVisible(true);
        });
    }
}

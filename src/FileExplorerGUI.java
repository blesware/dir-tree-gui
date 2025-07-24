import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;

public class FileExplorerGUI extends JFrame {

    private JTextArea textArea;

    public FileExplorerGUI() {
        setTitle("Explorador de Carpetas - Estructura de Proyecto");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel superior para los botones
        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnSeleccionar = new JButton("Seleccionar Carpeta");
        btnSeleccionar.addActionListener(this::seleccionarCarpeta);

        JButton btnCopiar = new JButton("Copiar");
        btnCopiar.addActionListener(this::copiarAlPortapapeles);

        panelBotones.add(btnSeleccionar);
        panelBotones.add(btnCopiar);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setFocusable(false); // Evita que reciba foco
        textArea.setCursor(Cursor.getDefaultCursor()); // Cursor normal en lugar de texto

        JScrollPane scrollPane = new JScrollPane(textArea);

        getContentPane().add(panelBotones, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void seleccionarCarpeta(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setFocusable(false); // Evita que se pueda editar nombres de carpetas

        int opcion = fileChooser.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            File carpeta = fileChooser.getSelectedFile();
            textArea.setText(""); // Limpiar
            mostrarEstructura(carpeta, 0);
        }
    }

    private void copiarAlPortapapeles(ActionEvent e) {
        try {
            String contenido = textArea.getText();

            if (contenido.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay contenido para copiar. Primero selecciona una carpeta.",
                        "Aviso",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Obtener el portapapeles del sistema
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            // Crear un StringSelection con el contenido del textArea
            StringSelection stringSelection = new StringSelection(contenido);

            // Copiar al portapapeles
            clipboard.setContents(stringSelection, null);

            // Mostrar mensaje de confirmación
            JOptionPane.showMessageDialog(this,
                    "Contenido copiado al portapapeles exitosamente!",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al copiar al portapapeles: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarEstructura(File archivo, int nivel) {
        StringBuilder indentacion = new StringBuilder();
        for (int i = 0; i < nivel; i++) indentacion.append("│   ");

        String nombre = archivo.getName();
        if (archivo.isDirectory()) nombre += "/";

        textArea.append(indentacion + "├── " + nombre + "\n");

        if (archivo.isDirectory() && !archivo.getName().equals(".git")) {
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class FileExplorerGUI extends JFrame {

    private JTextArea textArea;

    public FileExplorerGUI() {
        setTitle("Explorador de Carpetas - Estructura de Proyecto");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton btnSeleccionar = new JButton("Seleccionar Carpeta");
        btnSeleccionar.addActionListener(this::seleccionarCarpeta);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        getContentPane().add(btnSeleccionar, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void seleccionarCarpeta(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int opcion = fileChooser.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            File carpeta = fileChooser.getSelectedFile();
            textArea.setText(""); // Limpiar
            mostrarEstructura(carpeta, 0);
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

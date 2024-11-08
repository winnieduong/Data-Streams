import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsApp extends JFrame {

    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private Path filePath;

    public DataStreamsApp() {
        setTitle("Data Streams Processing");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create GUI components
        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();
        searchField = new JTextField(20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        // Disable search button until a file is loaded
        searchButton.setEnabled(false);

        // Setup the panels
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Search String:"));
        topPanel.add(searchField);
        topPanel.add(loadButton);
        topPanel.add(searchButton);
        topPanel.add(quitButton);

        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        textPanel.add(new JScrollPane(originalTextArea));
        textPanel.add(new JScrollPane(filteredTextArea));

        add(topPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);

        // Action Listeners
        loadButton.addActionListener(new LoadFileAction());
        searchButton.addActionListener(new SearchAction());
        quitButton.addActionListener(e -> System.exit(0));
    }

    // Action to load a file using JFileChooser
    private class LoadFileAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(DataStreamsApp.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                filePath = fileChooser.getSelectedFile().toPath();
                try (Stream<String> lines = Files.lines(filePath)) {
                    List<String> allLines = lines.collect(Collectors.toList());
                    originalTextArea.setText(String.join("\n", allLines));
                    filteredTextArea.setText("");
                    searchButton.setEnabled(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(DataStreamsApp.this, "Error loading file: " + ex.getMessage());
                }
            }
        }
    }

    // Action to search the file using the input search string
    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchString = searchField.getText();
            if (filePath == null || searchString.isEmpty()) {
                JOptionPane.showMessageDialog(DataStreamsApp.this, "Please load a file and enter a search string.");
                return;
            }
            try (Stream<String> lines = Files.lines(filePath)) {
                List<String> filteredLines = lines
                        .filter(line -> line.contains(searchString))
                        .collect(Collectors.toList());
                filteredTextArea.setText(String.join("\n", filteredLines));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(DataStreamsApp.this, "Error processing file: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataStreamsApp app = new DataStreamsApp();
            app.setVisible(true);
        });
    }
}

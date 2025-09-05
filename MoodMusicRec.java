import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoodMusicRec {

    static class Song {
        String title, artist, mood;

        Song(String title, String artist, String mood) {
            this.title = title;
            this.artist = artist;
            this.mood = mood.trim().toLowerCase();
        }
    }

    private final List<Song> songs = new ArrayList<>();
    private final JTextArea resultArea = new JTextArea();

    public MoodMusicRec() {
        loadSongs("songs.csv");
        createUI();
    }

    private void createUI() {
        JFrame frame = new JFrame("Mood Music Recommender");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());

        // Top panel: label + text field + button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel moodLabel = new JLabel("Enter your mood:");
        JTextField moodField = new JTextField(10);
        JButton generateButton = new JButton("Generate");

        inputPanel.add(moodLabel);
        inputPanel.add(moodField);
        inputPanel.add(generateButton);

        // Results area
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);

        generateButton.addActionListener(event -> {
            String mood = moodField.getText();
            showRecommendations(mood);
        });

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void loadSongs(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    songs.add(new Song(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            resultArea.setText("Error loading songs file.");
        }
    }

    private void showRecommendations(String mood) {
        if (mood == null || mood.isBlank()) {
            resultArea.setText("Please enter a mood.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Song s : songs) {
            if (s.mood.equalsIgnoreCase(mood.trim())) {
                sb.append(s.title).append(" - ").append(s.artist).append("\n");
            }
        }

        if (sb.length() == 0) {
            resultArea.setText("No songs found for mood: " + mood);
        } else {
            resultArea.setText(sb.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MoodMusicRec::new);
    }
}

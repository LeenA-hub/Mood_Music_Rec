import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.prefs.Preferences;
import javax.sound.sampled.*;

public class MoodMusicRec {
    static class Song {
        String title, artist, mood, file;

        Song(String title, String artist, String mood, String file) {
            this.title = title;
            this.artist = artist;
            this.mood = mood.trim().toLowerCase();
            this.file = file;
        }
    }

    private final List<Song> songs = new ArrayList<>();
    private final JTextArea resultArea = new JTextArea();
    private final Preferences prefs = Preferences.userNodeForPackage(MoodMusicRec.class);

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    public MoodMusicRec() {
        loadSongs("songs.csv"); // CSV format: title,artist,mood,filePath
        createUI();
    }

    private void createUI() {
        JFrame frame = new JFrame("🎵 Mood Music Recommender");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // --- Home Page ---
        JPanel homePanel = new JPanel(new BorderLayout(10, 10));
        JLabel titleLabel = new JLabel("🎵 Mood Music", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        JButton startButton = new JButton("Start");
        JButton lastButton = new JButton("Last Mood Results");

        JPanel homeButtons = new JPanel();
        homeButtons.add(startButton);
        homeButtons.add(lastButton);

        homePanel.add(titleLabel, BorderLayout.CENTER);
        homePanel.add(homeButtons, BorderLayout.SOUTH);

        // --- Recommendation Page ---
        JPanel recPanel = new JPanel(new BorderLayout(10, 10));
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel moodLabel = new JLabel("Enter your mood:");
        JTextField moodField = new JTextField(12);
        JButton generateButton = new JButton("Generate 🎶");
        JButton backButton = new JButton("⬅ Back");

        inputPanel.add(moodLabel);
        inputPanel.add(moodField);
        inputPanel.add(generateButton);

        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(backButton, BorderLayout.WEST);

        recPanel.add(inputPanel, BorderLayout.NORTH);
        recPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        recPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- Main Panel with CardLayout ---
        mainPanel.add(homePanel, "home");
        mainPanel.add(recPanel, "rec");

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // --- Actions ---
        startButton.addActionListener(e -> cardLayout.show(mainPanel, "rec"));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "home"));
        lastButton.addActionListener(e -> showLastMood());

        generateButton.addActionListener(e -> {
            String mood = moodField.getText().trim();
            if (!mood.isEmpty()) {
                showRecommendations(mood);
            }
        });
    }

    private void loadSongs(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    songs.add(new Song(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading songs file.");
        }
    }

    private void showRecommendations(String mood) {
        StringBuilder sb = new StringBuilder("Results for: " + mood + "\n\n");
        List<Song> matches = new ArrayList<>();

        for (Song s : songs) {
            if (s.mood.equalsIgnoreCase(mood)) {
                sb.append("• ").append(s.title).append(" - ").append(s.artist).append("\n");
                matches.add(s);
            }
        }

        if (matches.isEmpty()) {
            resultArea.setText("No songs found for mood: " + mood);
        } else {
            resultArea.setText(sb.toString());
            prefs.put("lastMood", mood);
            prefs.put("lastResults", sb.toString());

            // play first match as demo
            playSong(matches.get(0).file);
        }
    }

    private void showLastMood() {
        String lastMood = prefs.get("lastMood", null);
        String lastResults = prefs.get("lastResults", null);

        if (lastMood != null && lastResults != null) {
            cardLayout.show(mainPanel, "rec");
            resultArea.setText(lastResults);
        } else {
            JOptionPane.showMessageDialog(null, "No previous results found.");
        }
    }

    private void playSong(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists())
                return;

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error playing song: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MoodMusicRec::new);
    }
}

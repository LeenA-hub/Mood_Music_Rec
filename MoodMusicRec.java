import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;

public class MoodMusicRec {
    private JFrame frame; // main app window
    private JPanel listPanel; // songs display
    private JTextField moodInput; // text field where user types the mood

    public MoodMusicRec() {
        initUI();
    }

    // -------------------------------
    // Start Screen
    // -------------------------------
    private static void showStartScreen() {
        JFrame startFrame = new JFrame("Welcome");
        startFrame.setSize(400, 300);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.getContentPane().setBackground(Color.PINK);
        startFrame.setLayout(new BorderLayout());

        JButton startBtn = new JButton("Start");
        startBtn.setFont(new Font("Arial", Font.BOLD, 22));
        startBtn.setBackground(Color.WHITE);

        startFrame.add(startBtn, BorderLayout.CENTER);

        // When Start is clicked → close start screen + open player
        startBtn.addActionListener(e -> {
            startFrame.dispose();
            new MoodMusicRec(); // launch main music player
        });

        startFrame.setVisible(true);
    }

    // -------------------------------
    // Main UI
    // -------------------------------
    private void initUI() {
        frame = new JFrame("Mood Music Player");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.PINK);

        // title
        JLabel title = new JLabel("🎵 Mood Music", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        frame.add(title, BorderLayout.NORTH);

        // mood input + load button
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.PINK);
        moodInput = new JTextField("happy", 10);
        JButton loadBtn = new JButton("Load Playlist");
        topPanel.add(new JLabel("Mood:"));
        topPanel.add(moodInput);
        topPanel.add(loadBtn);
        frame.add(topPanel, BorderLayout.SOUTH);

        // list panel to show songs
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.PINK);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // when Load Playlist clicked → read CSV
        loadBtn.addActionListener(e -> {
            String mood = moodInput.getText().trim();
            showPlaylist(loadFromCSV("songs.csv", mood));
        });

        frame.setVisible(true);
    }

    // -------------------------------
    // Show Playlist
    // -------------------------------
    private void showPlaylist(List<Track> playlist) {
        listPanel.removeAll();
        if (playlist.isEmpty()) {
            listPanel.add(new JLabel("No songs found for this mood."));
        } else {
            for (Track t : playlist) {
                JPanel songPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                songPanel.setBackground(Color.PINK);

                JLabel lbl = new JLabel(t.title + " – " + t.artist + " [" + t.mood + "]");
                JButton playBtn = new JButton("▶ Play");

                playBtn.addActionListener(e -> openPlayerWindow(t));

                songPanel.add(lbl);
                songPanel.add(playBtn);
                listPanel.add(songPanel);
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    // -------------------------------
    // Player Window
    // -------------------------------
    private void openPlayerWindow(Track t) {
        JFrame playerFrame = new JFrame("Now Playing");
        playerFrame.setSize(400, 200);
        playerFrame.setLayout(new BorderLayout());

        JLabel songLabel = new JLabel("Playing: " + t.title + " – " + t.artist, SwingConstants.CENTER);
        songLabel.setFont(new Font("Arial", Font.BOLD, 18));
        playerFrame.add(songLabel, BorderLayout.NORTH);

        JPanel controls = new JPanel();
        JButton playBtn = new JButton("Play");
        JButton stopBtn = new JButton("Stop");
        controls.add(playBtn);
        controls.add(stopBtn);
        playerFrame.add(controls, BorderLayout.CENTER);

        // Holder for the currently playing clip
        final Clip[] clipHolder = new Clip[1];

        // Play button
        playBtn.addActionListener(e -> {
            try {
                if (clipHolder[0] != null && clipHolder[0].isRunning()) {
                    clipHolder[0].stop();
                    clipHolder[0].close();
                }

                File file = new File(t.file); // use file path from CSV
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(playerFrame,
                            "File not found: " + t.file + "\nPlace the WAV in the correct folder.");
                    return;
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
                clipHolder[0] = clip;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(playerFrame, "Error playing song: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Stop button
        stopBtn.addActionListener(e -> {
            if (clipHolder[0] != null) {
                clipHolder[0].stop();
                clipHolder[0].close();
            }
        });

        playerFrame.setVisible(true);
    }

    // -------------------------------
    // CSV Loader
    // -------------------------------
    public static List<Track> loadFromCSV(String filePath, String mood) {
        List<Track> tracks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    if (parts[2].equalsIgnoreCase(mood)) {
                        tracks.add(new Track(parts[0], parts[1], parts[2], parts[3]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tracks;
    }

    // -------------------------------
    // Main
    // -------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MoodMusicRec::showStartScreen);
    }
}

// -------------------------------
// Track Class
// -------------------------------
class Track {
    String title;
    String artist;
    String mood;
    String file; // path to WAV file

    public Track(String title, String artist, String mood, String file) {
        this.title = title;
        this.artist = artist;
        this.mood = mood;
        this.file = file;
    }
}

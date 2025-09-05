import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MoodMusicRec {
    private JFrame frame; // main app window
    private JPanel listPanel; // songs display
    private JTextField moodInput; // text field where user type the mood

    public MoodMusicRec() {
        initUI();
    }

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

    private void initUI() {
        frame = new JFrame("Mood Music Player");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.PINK);

        // add title label at the top "MOOD MUSIC"
        JLabel title = new JLabel("🎵 Mood Music", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        frame.add(title, BorderLayout.NORTH);

        // button mood and button load playlist
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.PINK);
        moodInput = new JTextField("happy", 10);
        JButton loadBtn = new JButton("Load Playlist");
        topPanel.add(new JLabel("Mood:"));
        topPanel.add(moodInput);
        topPanel.add(loadBtn);
        frame.add(topPanel, BorderLayout.SOUTH);

        // list panel to show songs vertically and jscrolllpane so if there are many
        // songs you can scroll
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.PINK);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // taken in user mood and load the song from the cvs file and passes them to
        // showplaylist()
        loadBtn.addActionListener(e -> {
            String mood = moodInput.getText().trim();
            showPlaylist(loadFromCSV("songs.csv", mood));
        });

        frame.setVisible(true);
    }

    // If no songs → show "No songs found...". Otherwise: For each song (Track):
    // Create a row with label: title – artist [mood]. Add Spotify button → opens
    // search for that song. Refresh panel to display new rows.
    private void showPlaylist(List<Track> playlist) {
        listPanel.removeAll();
        if (playlist.isEmpty()) {
            listPanel.add(new JLabel("No songs found for this mood."));
        } else {
            for (Track t : playlist) {
                JPanel songPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                songPanel.setBackground(Color.PINK);

                JLabel lbl = new JLabel(t.title + " – " + t.artist + " [" + t.mood + "]");
                JButton spotifyBtn = new JButton("Spotify 🎧");

                spotifyBtn.addActionListener(e -> openSpotify(t));

                songPanel.add(lbl);
                songPanel.add(spotifyBtn);
                listPanel.add(songPanel);
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    // open spotify search for that song
    private void openSpotify(Track t) {
        try {
            String query = URLEncoder.encode(t.title + " " + t.artist, "UTF-8");
            Desktop.getDesktop().browse(new URI("https://open.spotify.com/search/" + query));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // --- CSV Loader ---
    public static List<Track> loadFromCSV(String filePath, String mood) {
        List<Track> tracks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    if (parts[2].equalsIgnoreCase(mood)) {
                        tracks.add(new Track(parts[0], parts[1], parts[2]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tracks;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MoodMusicRec::new);
    }
}

// --- Track Class ---
class Track {
    String title;
    String artist;
    String mood;

    public Track(String title, String artist, String mood) {
        this.title = title;
        this.artist = artist;
        this.mood = mood;
    }
}

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MoodMusicRec {
    private JFrame frame;
    private JButton playBtn, nextBtn, prevBtn;
    private JLabel songLabel;
    private List<Track> playlist;
    private int currentIndex = 0;
    private Thread playThread;

    public MoodMusicRec(List<Track> playlist) {
        this.playlist = playlist;
        initUI();
    }

    private void initUI() {
        frame = new JFrame("Spotify Mood Player");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        songLabel = new JLabel("No song playing", SwingConstants.CENTER);

        prevBtn = new JButton("⏮");
        playBtn = new JButton("▶");
        nextBtn = new JButton("⏭");

        prevBtn.addActionListener(e -> prevSong());
        playBtn.addActionListener(e -> togglePlay());
        nextBtn.addActionListener(e -> nextSong());

        JPanel controls = new JPanel();
        controls.add(prevBtn);
        controls.add(playBtn);
        controls.add(nextBtn);

        frame.setLayout(new BorderLayout());
        frame.add(songLabel, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.SOUTH);

        frame.setVisible(true);

        if (!playlist.isEmpty()) {
            loadSong(0);
        }
    }

    private void loadSong(int index) {
        stopSong();
        currentIndex = index;
        Track track = playlist.get(index);
        songLabel.setText(track.title + " - " + track.artist);
        playThread = new Thread(() -> {
            try {
                // Just simulate playback since we can't play audio without external libraries
                Thread.sleep(5000); // Simulate 5 seconds of playback
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        playThread.start();
    }

    private void stopSong() {
        if (playThread != null && playThread.isAlive()) {
            playThread.interrupt();
        }
    }

    private void nextSong() {
        if (!playlist.isEmpty()) {
            int nextIndex = (currentIndex + 1) % playlist.size();
            loadSong(nextIndex);
        }
    }

    private void prevSong() {
        if (!playlist.isEmpty()) {
            int prevIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
            loadSong(prevIndex);
        }
    }

    private void togglePlay() {
        if (playThread != null && playThread.isAlive()) {
            stopSong();
            playBtn.setText("▶");
            songLabel.setText("Paused: " + playlist.get(currentIndex).title);
        } else {
            loadSong(currentIndex);
            playBtn.setText("⏸");
        }
    }

    // --- Spotify API integration ---
    public static List<Track> searchTracksByMood(String mood, String accessToken) {
        List<Track> tracks = new ArrayList<>();
        try {
            String query = URLEncoder.encode(mood, "UTF-8");
            URL url = new URL("https://api.spotify.com/v1/search?q=" + query + "&type=track&limit=5");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // Simple JSON parsing using String methods (since org.json is not available)
            String resp = response.toString();
            if (resp.contains("\"items\":[")) {
                String[] items = resp.split("\"items\":\\[")[1].split("]")[0].split("\\},\\{");
                for (String item : items) {
                    String title = extractJsonValue(item, "\"name\":\"");
                    String previewUrl = extractJsonValue(item, "\"preview_url\":\"");
                    String artist = extractJsonValue(item, "\"artists\":[\\{\"name\":\"");
                    if (previewUrl != null && !previewUrl.isEmpty()) {
                        tracks.add(new Track(title, artist, previewUrl));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tracks;
    }

    private static String extractJsonValue(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1)
            return "";
        int start = idx + key.length();
        int end = json.indexOf("\"", start);
        if (end == -1)
            return "";
        return json.substring(start, end);
    }

    public static void main(String[] args) {
        // 🔑 Replace with your actual Spotify Bearer token
        String accessToken = "YOUR_SPOTIFY_ACCESS_TOKEN";

        String mood = JOptionPane.showInputDialog("Enter a mood (happy, sad, chill...):");
        List<Track> results = searchTracksByMood(mood, accessToken);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tracks found for mood: " + mood);
        } else {
            new MoodMusicRec(results);
        }
    }
}

// Track class definition
class Track {
    String title;
    String artist;
    String previewUrl;

    public Track(String title, String artist, String previewUrl) {
        this.title = title;
        this.artist = artist;
        this.previewUrl = previewUrl;
    }
}

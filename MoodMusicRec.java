import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MoodMusicRec {

    static class Song {
        String title;
        String artist;
        String mood;

        Song(String title, String artist, String mood) {
            this.title = title;
            this.artist = artist;
            this.mood = mood.toLowerCase();
        }
    }

    // ANSI colors for console
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        List<Song> songs = loadSongs("songs.csv");

        if (songs.isEmpty()) {
            System.out.println("No songs loaded. Make sure songs.csv is in the same folder!");
            return;
        }

        System.out.println("Welcome to Mood-Based Music Recommender!");
        System.out.print("Enter your current mood (happy, sad, chill, energetic): ");
        String mood = sc.nextLine().toLowerCase();

        List<Song> recommended = new ArrayList<>();
        for (Song s : songs) {
            if (s.mood.equals(mood)) {
                recommended.add(s);
            }
        }

        if (recommended.isEmpty()) {
            System.out.println("Sorry, no songs found for that mood yet.");
        } else {
            System.out.println("\n🎵 Recommended songs for mood: " + mood.toUpperCase() + " 🎵\n");
            for (int i = 0; i < recommended.size(); i++) {
                Song s = recommended.get(i);
                System.out.println((i + 1) + ". " + s.title + " - " + s.artist);
            }
        }

        // Mood animation
        System.out.println("\nVisualizing your mood:\n");
        animateMood(mood);

        System.out.println("\nEnjoy your music! 🎶");
        sc.close();
    }

    private static List<Song> loadSongs(String fileName) {
        List<Song> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    list.add(new Song(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return list;
    }

    private static void animateMood(String mood) throws InterruptedException {
        String[] symbols;
        String color;

        switch (mood) {
            case "happy":
                symbols = new String[] { "😄", "😁", "😆" };
                color = YELLOW;
                break;
            case "sad":
                symbols = new String[] { "☁️", "🌧️", "💧" };
                color = BLUE;
                break;
            case "chill":
                symbols = new String[] { "🌊", "🍃", "🕊️" };
                color = CYAN;
                break;
            case "energetic":
                symbols = new String[] { "⚡", "🔥", "💥" };
                color = RED;
                break;
            default:
                symbols = new String[] { "✨", "🌟", "💫" };
                color = PURPLE;
        }

        // Animate 10 frames
        for (int i = 0; i < 10; i++) {
            int idx = ThreadLocalRandom.current().nextInt(symbols.length);
            System.out.print(color + symbols[idx] + " " + RESET);
            Thread.sleep(300);
        }
        System.out.println();
    }
}

public class Track {
    private String title;
    private String artist;
    private String mood;

    public Track(String title, String artist, String mood) {
        this.title = title;
        this.artist = artist;
        this.mood = mood;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getMood() {
        return mood;
    }

    @Override
    public String toString() {
        return title + " - " + artist + " [" + mood + "]";
    }
}

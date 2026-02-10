package geo.toy.word;

public record Word(
    Long id,
    String word,
    String reading,
    String meaning,
    String status
) {
    public Word(String word, String reading, String meaning) {
        this(null, word, reading, meaning, "unseen");
    }
}

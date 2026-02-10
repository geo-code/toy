package geo.toy.word;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WordRepository {
    private final JdbcTemplate jdbc;

    private final RowMapper<Word> rowMapper = (rs, rowNum) -> new Word(
            rs.getLong("id"),
            rs.getString("word"),
            rs.getString("reading"),
            rs.getString("meaning"),
            rs.getBoolean("answer")
    );

    public List<Word> findAll() {
        return jdbc.query("select id, word, reading, meaning, answer from words order by id", rowMapper);
    }

    public List<Word> findAllWithWeightedRandom(int limit) {
        // views 낮을수록 + 오답 우선 + 랜덤
        return jdbc.query("""
                        select id, word, reading, meaning, answer from words
                        order by views * 0.5 + case when answer then 3 else 0 end + random()
                        limit ?
                        """,
                rowMapper, limit);
    }

    public void incrementViews(List<Long> ids) {
        if (ids.isEmpty()) return;
        var placeholders = String.join(",", ids.stream().map(_ -> "?").toList());
        jdbc.update("update words set views = views + 1 where id in (" + placeholders + ")", ids.toArray());
    }

    public void save(Word word) {
        jdbc.update("insert into words (word, reading, meaning) values (?, ?, ?)", word.word(), word.reading(), word.meaning());
    }

    public void updateAnswer(Long id, boolean answer) {
        jdbc.update("update words set answer = ? where id = ?", answer, id);
    }

    public void deleteById(Long id) {
        jdbc.update("delete from words where id = ?", id);
    }
}

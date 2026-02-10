package geo.toy.word;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WordRepository {
    private final JdbcTemplate jdbc;

    private final RowMapper<Word> rowMapper = (rs, rowNum) -> new Word(
            rs.getLong("id"),
            rs.getString("word"),
            rs.getString("reading"),
            rs.getString("meaning"),
            rs.getString("status")
    );

    public List<Word> findAll() {
        return jdbc.query(
                "select id, word, reading, meaning, status from words order by id",
                rowMapper
        );
    }

    public List<Word> findAllWithWeightedRandom(int limit) {
        // 오답(wrong) > 미노출(unseen) > 정답(correct) 순으로 가중치
        return jdbc.query("""
                        select id, word, reading, meaning, status from words
                        order by
                            case status
                                when 'wrong' then random() * 0.3
                                when 'unseen' then random() * 0.6 + 0.3
                                when 'correct' then random() * 0.1 + 0.9
                                else random()
                            end
                        limit ?
                        """,
                rowMapper,
                limit
        );
    }

    public Optional<Word> findById(Long id) {
        var list = jdbc.query(
                "select id, word, reading, meaning, status from words where id = ?",
                rowMapper,
                id
        );
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    public void save(Word word) {
        jdbc.update("insert into words (word, reading, meaning) values (?, ?, ?)", word.word(), word.reading(), word.meaning());
    }

    public void updateStatus(Long id, String status) {
        jdbc.update("update words set status = ? where id = ?", status, id);
    }

    public void deleteById(Long id) {
        jdbc.update("delete from words where id = ?", id);
    }
}

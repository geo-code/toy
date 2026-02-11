package geo.toy.word;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/word")
@RequiredArgsConstructor
public class WordController {
    private final WordRepository repository;
    private final AtomicInteger correctCount = new AtomicInteger(0);

    @GetMapping
    public List<Word> getAllWords() {
        return repository.findAll();
    }

    @GetMapping("/today")
    public Response<List<Word>> getTodayWords() {
        var words = repository.findAllWithWeightedRandom(5);
        repository.incrementViews(words.stream().map(Word::id).toList());
        return new Response<>(correctCount.get(), words);
    }

    @PostMapping
    public void addWord(@RequestBody Word word) {
        repository.save(word);
    }

    @DeleteMapping("/{id}")
    public void deleteWord(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PostMapping("/today")
    public Response<List<Word>> nextTodayWords(@RequestBody Map<Long, Boolean> results) {
        results.forEach((id, answer) -> {
            repository.updateAnswer(id, answer);
            if (answer) correctCount.incrementAndGet();

        });
        var words = repository.findAllWithWeightedRandom(5);
        repository.incrementViews(words.stream().map(Word::id).toList());
        return new Response<>(correctCount.get(), words);
    }

    public record Response<T>(int correctCount, T data) {
    }
}

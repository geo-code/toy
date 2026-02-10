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
        return new Response<>(correctCount.get(), repository.findAllWithWeightedRandom(5));
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
    public Response<List<Word>> nextWords(@RequestBody Map<Long, Boolean> results) {
        results.forEach((id, correct) -> {
            repository.updateStatus(id, correct ? "correct" : "wrong");
            if (correct) {
                correctCount.incrementAndGet();
            }
        });
        return new Response<>(correctCount.get(), repository.findAllWithWeightedRandom(5));
    }

    public record Response<T>(int correctCount, T data) {
    }
}

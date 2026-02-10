package geo.toy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@SpringBootApplication
@RestController
public class ToyApplication {
    static void main(String[] args) {
        SpringApplication.run(ToyApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/word")
    public ResponseEntity<Void> word() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/word/index.html"))
                .build();
    }
}

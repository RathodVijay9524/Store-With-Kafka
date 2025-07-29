package in.vijay.id_generations.idgenerations.controller;

import in.vijay.id_generations.idgenerations.IdGeneratorServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ids")
@RequiredArgsConstructor
public class IdGeneratorController {

    private final IdGeneratorServices idGeneratorService;

    @GetMapping("/generate")
    public ResponseEntity<String> generateId(
            @RequestParam String entityKey,
            @RequestParam String prefix,
            @RequestParam(defaultValue = "6") int width
    ) {
        String id = idGeneratorService.generateId(entityKey, prefix, width);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/generate-date-based")
    public ResponseEntity<String> generateDateBasedId(
            @RequestParam String entityKey,
            @RequestParam String prefix
    ) {
        String id = idGeneratorService.generateDateBasedId(entityKey, prefix);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/generate-long")
    public ResponseEntity<Long> generateLongId(
            @RequestParam String entityKey
    ) {
        Long id = idGeneratorService.generateLongId(entityKey);
        return ResponseEntity.ok(id);
    }
}


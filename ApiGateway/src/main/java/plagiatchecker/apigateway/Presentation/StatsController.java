package plagiatchecker.apigateway.Presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plagiatchecker.apigateway.Application.StatsService;
import plagiatchecker.apigateway.Domain.Entities.FileStats;
import plagiatchecker.apigateway.Domain.Interfaces.Services.StatsServiceI;

import java.io.File;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Статистика", description = "запросы для получения статистики по файлу и анализа на плагиат, а также для получения карты слов")
public class StatsController {
    private final StatsServiceI statsService;

    @GetMapping("/{id}")
    @Operation(summary = "Получить статистику файла и анализ на плагиат по id")
    public ResponseEntity<FileStats> getFileStatsById(@PathVariable("id") int id) {
        FileStats st = statsService.getFileStats(id);

        if (st == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return ResponseEntity.ok().body(st);
    }

    @GetMapping("/word-cloud/{id}")
    @Operation(summary = "Получить облако слов по id")
    public ResponseEntity<byte[]> getFileWordsCloud(@PathVariable("id") int id) {
        File file = statsService.getWordMap(id);

        try {
            if (file == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            byte[] fileContent = Files.readAllBytes(file.toPath());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .contentLength(fileContent.length)
                    .body(fileContent);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

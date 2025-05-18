package plagiatchecker.apigateway.Presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import plagiatchecker.apigateway.Application.FilesService;
import plagiatchecker.apigateway.Domain.Interfaces.Services.FilesServiceI;

import java.io.File;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "Файлы", description = "запросы для загрузки и получения файлов")
public class    FilesController {
    private final FilesServiceI filesService;

    @PostMapping
    @Operation(
            summary = "Загрузить новый файл в систему",
            description = "Загружает файл на сервер"
    )
    public ResponseEntity<String> uploadFile(@Parameter(description = "Файл для загрузки") @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a non-empty file");
        }

        if (!file.getOriginalFilename().endsWith(".txt")) {
            return ResponseEntity.badRequest().body("Please upload txt file.");
        }

        long t;
        try {
            t = filesService.uploadFile(file);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body(String.valueOf(t));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить файл из хранилища по id")
    public ResponseEntity<byte[]> getFileById(@PathVariable("id") Integer id) {
        File file = filesService.getFile(id);

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileContent.length)
                    .body(fileContent);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

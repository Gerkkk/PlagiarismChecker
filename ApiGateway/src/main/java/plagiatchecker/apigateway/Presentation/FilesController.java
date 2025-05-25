package plagiatchecker.apigateway.Presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
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
import plagiatchecker.apigateway.Domain.Dto.UploadFileRequest;
import plagiatchecker.apigateway.Domain.Interfaces.Services.FilesServiceI;

//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.SchemaProperty;

import java.io.File;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "Файлы", description = "запросы для загрузки и получения файлов")
public class    FilesController {
    private final FilesServiceI filesService;

    @Operation(summary = "Загрузить файл", description = "Загружает файл на сервер")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@Parameter(description = "Файл для загрузки") @RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a non-empty file");
        }

        if (!file.getOriginalFilename().endsWith(".txt")) {
            return ResponseEntity.badRequest().body("Please upload txt file.");
        }

        long t;
        try {
            t = filesService.uploadFile(file);
            if (t == -1) {
                return ResponseEntity.badRequest().body("File with the same body already exists");
            }
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
            if (file != null) {
                byte[] fileContent = Files.readAllBytes(file.toPath());

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .contentLength(fileContent.length)
                        .body(fileContent);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

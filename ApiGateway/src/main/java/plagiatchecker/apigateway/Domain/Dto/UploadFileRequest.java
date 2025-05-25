package plagiatchecker.apigateway.Domain.Dto;


import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public class UploadFileRequest {

    @Schema(type = "string", format = "binary", description = "Файл для загрузки (только .txt)")
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
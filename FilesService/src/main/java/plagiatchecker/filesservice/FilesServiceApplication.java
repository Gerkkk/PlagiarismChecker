package plagiatchecker.filesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilesServiceApplication.class, args);

        System.out.println("gRPC Server is running...");
    }

}

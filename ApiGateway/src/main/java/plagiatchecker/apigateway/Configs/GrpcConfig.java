package plagiatchecker.apigateway.Configs;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import plagiatchecker.filesservice.proto.FileServiceGrpc;

@Configuration
public class GrpcConfig {

    @GrpcClient("file-service")
    private FileServiceGrpc.FileServiceStub fileServiceStub;

    @Bean
    public FileServiceGrpc.FileServiceStub fileServiceStub() {
        return fileServiceStub;
    }
}
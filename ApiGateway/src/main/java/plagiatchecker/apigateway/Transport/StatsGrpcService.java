package plagiatchecker.apigateway.Transport;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import plagiatchecker.apigateway.Domain.Entities.FileStats;
import plagiatchecker.filesservice.proto.FileServiceGrpc;
import plagiatchecker.statsservice.proto.StatServiceProto;
import plagiatchecker.statsservice.proto.StatServiceGrpc;

import plagiatchecker.apigateway.Domain.Interfaces.Repositories.StatsGrpcServiceI;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Component
public class StatsGrpcService implements StatsGrpcServiceI {

    @GrpcClient("stat-service")
    private StatServiceGrpc.StatServiceBlockingStub statServiceStub;

    @PostConstruct
    public void init() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("stat-service", 9090)
                .usePlaintext()
                .build();
        this.statServiceStub = StatServiceGrpc.newBlockingStub(channel);
    }

    public FileStats getFileStats(int fileId) {
        StatServiceProto.GetFileInfoRequest request = StatServiceProto.GetFileInfoRequest.newBuilder()
                .setId(fileId)
                .build();

        var resp = statServiceStub.getFileInfoById(request);

        if (resp == null) return null;
        return FileStats.builder()
                      .id(resp.getFileId())
                      .maxSimilarity(resp.getMaxSimilarity())
                      .averageSimilarity(resp.getAverageSimilarity())
                      .numWords(resp.getNumWords())
                      .numSentences(resp.getNumSentences())
                      .numArticles(resp.getNumArticles())
                      .closestFileId(resp.getClosestFileId())
                      .build();
    }

    public byte[] getWordMap(int fileId) {
        StatServiceProto.GetFileInfoRequest request = StatServiceProto.GetFileInfoRequest.newBuilder()
                .setId(fileId)
                .build();

        List<byte[]> chunks = new ArrayList<>();

        statServiceStub.getWordCloud(request).forEachRemaining(chunk -> {
            chunks.add(chunk.getData().toByteArray());
        });

        int totalSize = chunks.stream().mapToInt(b -> b.length).sum();
        byte[] fullImage = new byte[totalSize];

        int offset = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, fullImage, offset, chunk.length);
            offset += chunk.length;
        }

        return fullImage;
    }
}
package plagiatchecker.statservice.Presentation;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import plagiatchecker.filesservice.proto.FileServiceProto;
import plagiatchecker.statservice.Services.WordStatsService;
import plagiatchecker.statsservice.proto.StatServiceGrpc;
import plagiatchecker.statsservice.proto.StatServiceProto;


@GrpcService
@Component
@RequiredArgsConstructor
public class  StatServiceController extends StatServiceGrpc.StatServiceImplBase {
    @Autowired
    private final WordStatsService wordStatsService;

    @Override
    public void getFileInfoById(StatServiceProto.GetFileInfoRequest request, StreamObserver<StatServiceProto.FileInfoResponse> responseObserver) {
        long fileId = request.getId();
        var stats = wordStatsService.getFileStats((int)fileId);
        StatServiceProto.FileInfoResponse response = StatServiceProto.FileInfoResponse.newBuilder()
                .setFileId(stats.getId())
                .setNumWords(stats.getNumWords())
                .setNumSentences(stats.getNumSentences())
                .setNumArticles(stats.getNumArticles())
                .setMaxSimilarity(stats.getMaxSimilarity())
                .setClosestFileId(stats.getClosestFileId())
                .setAverageSimilarity(stats.getAverageSimilarity())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getWordCloud(StatServiceProto.GetFileInfoRequest request, StreamObserver<FileServiceProto.FileChunk> responseObserver) {
        long fileId = request.getId();

        byte[] imageBytes = wordStatsService.getWordMap((int)fileId);

        int chunkSize = 64 * 1024;
        for (int i = 0; i < imageBytes.length; i += chunkSize) {
            int end = Math.min(imageBytes.length, i + chunkSize);
            byte[] chunk = java.util.Arrays.copyOfRange(imageBytes, i, end);

            FileServiceProto.FileChunk fileChunk = FileServiceProto.FileChunk.newBuilder()
                    .setData(com.google.protobuf.ByteString.copyFrom(chunk))
                    .build();

            responseObserver.onNext(fileChunk);
        }

        responseObserver.onCompleted();
    }
}
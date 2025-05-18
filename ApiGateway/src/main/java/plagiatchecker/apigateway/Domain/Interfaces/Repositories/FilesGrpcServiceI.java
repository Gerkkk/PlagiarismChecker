package plagiatchecker.apigateway.Domain.Interfaces.Repositories;

import plagiatchecker.apigateway.Domain.Entities.StoredFile;

public interface FilesGrpcServiceI {
    long uploadFile(String fileName, byte[] fileData);
    StoredFile getFile(int fileId);
}

package plagiatchecker.filesservice.Domain.Interfaces.Repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.multipart.MultipartFile;
import plagiatchecker.filesservice.Domain.Entities.FileInfo;
import plagiatchecker.filesservice.Domain.Entities.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FilesRepositoryI extends JpaRepository<FileInfo, Integer> {
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM FileInfo f WHERE f.hash = :hash")
    boolean checkFileExists(@Param("hash") String hash);

    @Modifying
    @Transactional
    @Query("UPDATE FileInfo f SET f.path = :filepath WHERE f.id = :id")
    void updateFilepath(@Param("id") int id, @Param("filepath") String filepath);
}

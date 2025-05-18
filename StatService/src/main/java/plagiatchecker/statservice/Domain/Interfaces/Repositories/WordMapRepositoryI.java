package plagiatchecker.statservice.Domain.Interfaces.Repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import plagiatchecker.statservice.Domain.Entities.FileStats;

public interface WordMapRepositoryI extends JpaRepository<FileStats, Integer> {
    @Modifying
    @Transactional
    @Query("UPDATE FileStats f SET f.path = :filepath WHERE f.id = :id")
    void updateFilepath(@Param("id") int id, @Param("filepath") String filepath);
}


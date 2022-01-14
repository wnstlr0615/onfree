package com.onfree.core.repository;

import com.onfree.core.entity.fileitem.FileItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FileItemRepository extends JpaRepository<FileItem, Long> {
    @Query("select f from FileItem f where f.uploadFile.storeFileName = :fileName")
    Optional<FileItem> findByStoreFileName(@Param("fileName") String fileName);
}

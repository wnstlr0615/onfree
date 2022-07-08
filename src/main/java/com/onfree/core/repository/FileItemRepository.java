package com.onfree.core.repository;

import com.onfree.core.entity.fileitem.FileItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileItemRepository extends JpaRepository<FileItem, Long> {
    @Query("select f from FileItem f where f.uploadFile.storeFilename = :filename")
    Optional<FileItem> findByStoreFileName(@Param("filename") String filename);

    @Query("select f from FileItem f where f.uploadFile.storeFilename in :filenames")
    List<FileItem> findAllByStoreFilenameIn(@Param("filenames") List<String> filenames);
}

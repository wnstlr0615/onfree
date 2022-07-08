package com.onfree.core.service;

import com.onfree.common.error.code.FileErrorCode;
import com.onfree.common.error.exception.FileException;
import com.onfree.core.entity.fileitem.FileItem;
import com.onfree.core.repository.FileItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileItemService {
    private final FileItemRepository fileItemRepository;

    /** 파일 조회 */
    public FileItem getFileItem(String filename) {
        //TODO 여기서부터 시작
        return findFileItemByFileName(filename);
    }

    private FileItem findFileItemByFileName(String filename) {
        return fileItemRepository.findByStoreFileName(filename)
                .orElseThrow(() ->new FileException(FileErrorCode.NOT_FOUND_FILENAME));
    }
}



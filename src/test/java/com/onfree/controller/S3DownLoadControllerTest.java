package com.onfree.controller;

import com.onfree.common.ControllerBaseTest;
import com.onfree.common.error.code.FileErrorCode;
import com.onfree.common.error.exception.FileException;
import com.onfree.common.model.UploadFile;
import com.onfree.controller.aws.S3DownLoadController;
import com.onfree.core.entity.fileitem.FileItem;
import com.onfree.core.entity.fileitem.FileStatus;
import com.onfree.core.entity.fileitem.FileType;
import com.onfree.core.service.FileItemService;
import com.onfree.utils.AwsS3Component;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.net.URL;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = S3DownLoadController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class S3DownLoadControllerTest extends ControllerBaseTest {
    @MockBean
    FileItemService fileItemService;
    @MockBean
    AwsS3Component s3Component;

    @Test
    @DisplayName("[성공][GET} 이미지 불러오기")
    public void givenImageUUID_whenDisplay_thenReturnImageResource() throws Exception{
        //given
        String uploadFileName = "테스트이미지.png";
        String storeFilename = "0393e471-98df-459f-a314-292c910bcc44.PNG";
        UploadFile uploadFile = UploadFile.createUploadFile(uploadFileName, storeFilename);
        String buckPath = "onfree-store/users/request/reference-file";
        String imageUrl = "https://onfree-store.s3.ap-northeast-2.amazonaws.com/portfolio/main-image/test-main-image.png";

        FileItem fileItem = FileItem.createFileItem(uploadFile, buckPath, FileType.REQUEST_REFERENCE_FILE, FileStatus.USED);

        when(fileItemService.getFileItem(anyString()))
                .thenReturn(
                        fileItem
                );
        when(s3Component.getFileUrl(any(FileItem.class)))
                .thenReturn(new URL(imageUrl));

        //when //then
        mvc.perform(get("/images/{filename}", storeFilename)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
        ;
        verify(fileItemService).getFileItem(anyString());
        verify(s3Component).getFileUrl(any(FileItem.class));
    }

    @Test
    @DisplayName("[실패][GET} 잘못된 파일이름으로 이미지 불러오기 - NOT_FOUND_FILENAME 에러 발생")
    public void givenWrongImageUUID_whenDisplay_thenReturnNotFoundFilenameError() throws Exception{
        //given
        final String storeFilename = "0393e471-98df-459f-a314-292c910bcc44.PNG";
        final FileErrorCode errorCode = FileErrorCode.NOT_FOUND_FILENAME;
        when(fileItemService.getFileItem(anyString()))
                .thenThrow(
                        new FileException(FileErrorCode.NOT_FOUND_FILENAME)
                );

        //when //then
        mvc.perform(get("/images/{filename}", storeFilename)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(fileItemService).getFileItem(anyString());
        verify(s3Component, never()).getFileUrl(any(FileItem.class));
    }
}
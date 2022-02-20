package com.onfree.controller;

import com.onfree.anotation.WithArtistUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.common.error.code.FileErrorCode;
import com.onfree.common.error.code.SignUpErrorCode;
import com.onfree.common.error.exception.FileException;
import com.onfree.core.entity.fileitem.FileType;
import com.onfree.core.service.AwsS3Service;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = S3UploadController.class)
class S3UploadControllerTest extends ControllerBaseTest {
    @MockBean
    AwsS3Service awsS3Service;

    @Test
    @DisplayName("[성공][POST] 프로필 사진 업로드")
    public void givenProfileImageMultipartFile_whenProfileImageUpload_thenReturnImageLoadUrl() throws Exception{
        //given
        final String storeFilename = "0393e471-98df-459f-a314-292c910bcc44.PNG";
        final MockMultipartFile file = new MockMultipartFile("file", "aaaa.png","image/png", "test".getBytes(StandardCharsets.UTF_8));
        when(awsS3Service.s3ImageFileUpload(any(), eq(FileType.PROFILE_IMAGE_FILE)))
                .thenReturn(storeFilename);
        //when
        //then
        mvc.perform(multipart("/api/upload/profile-image")
                .file(file)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString(storeFilename)))
        ;
        verify(awsS3Service).s3ImageFileUpload(any(), eq(FileType.PROFILE_IMAGE_FILE));
    }

    @Test
    @DisplayName("[실패][POST] 프로필 사진 업로드 - 파일이 비었을 경우")
    public void givenImageEmptyMultipartFile_whenProfileImageUpload_thenFileIsEmptyError() throws Exception{
        //given
        final MockMultipartFile file = new MockMultipartFile("file", "aaaa.png","image/png", (byte[]) null);
        final SignUpErrorCode errorCode = SignUpErrorCode.FILE_IS_EMPTY;

        //when //then
        mvc.perform(multipart("/api/upload/profile-image")
                .file(file)
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(awsS3Service, never()).s3ImageFileUpload(any(), any(FileType.class));
    }

    @Test
    @DisplayName("[실패][POST] 프로필 사진 업로드 - 파일 확장자를 지원 하지 않는 경우")
    public void givenNotAllowMultipartFile_whenProfileImageUpload_thenFileIsEmptyError() throws Exception{
        //given
        final MockMultipartFile file = new MockMultipartFile("file", "aaaa.csv","image/png", "test".getBytes(StandardCharsets.UTF_8));
        final FileErrorCode errorCode = FileErrorCode.NOT_ALLOW_UPLOAD_FILETYPE;
        //when//then
        mvc.perform(multipart("/api/upload/profile-image")
                .file(file)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(awsS3Service, never()).s3ImageFileUpload(any(), any(FileType.class));

    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][POST] 포트폴리오 내용 사진 업로드")
    public void givenPortfolioContentImageMultipartFile_whenPortfolioContentImageUpload_thenReturnImageLoadUrl() throws Exception{
        //given
        final String storeFilename = "0393e471-98df-459f-a314-292c910bcc44.PNG";
        final MockMultipartFile file = new MockMultipartFile("file", "aaaa.png","image/png", "test".getBytes(StandardCharsets.UTF_8));
        final FileType fileType = FileType.PORTFOLIO_CONTENT_IMAGE;
        when(awsS3Service.s3ImageFileUpload(any(), eq(fileType)))
                .thenReturn(storeFilename);
        //when
        //then
        mvc.perform(multipart("/api/upload/portfolio-content-image")
                .file(file)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString(storeFilename)))
        ;
        verify(awsS3Service).s3ImageFileUpload(any(), eq(fileType));
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][POST] 포트폴리오 메인 사진 업로드")
    public void givenPortfolioMainImageMultipartFile_whenPortfolioMainImageUpload_thenReturnImageLoadUrl() throws Exception{
        //given
        final String storeFilename = "0393e471-98df-459f-a314-292c910bcc44.PNG";
        final MockMultipartFile file = new MockMultipartFile("file", "aaaa.png","image/png", "test".getBytes(StandardCharsets.UTF_8));
        final FileType fileType = FileType.PORTFOLIO_MAIN_IMAGE;
        when(awsS3Service.s3ImageFileUpload(any(), eq(fileType)))
                .thenReturn(storeFilename);
        //when
        //then
        mvc.perform(multipart("/api/upload/portfolio-main-image")
                .file(file)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString(storeFilename)))
        ;
        verify(awsS3Service).s3ImageFileUpload(any(), eq(fileType));
    }


    @Test
    @WithArtistUser
    @DisplayName("[성공][GET} 이미지 불러오기")
    public void givenImageUUID_whenDisplay_thenReturnImageResource() throws Exception{
        //given
        final String storeFilename = "0393e471-98df-459f-a314-292c910bcc44.PNG";
        final String imageUrl = "https://onfree-store.s3.ap-northeast-2.amazonaws.com/portfolio/main-image/test-main-image.png";
        when(awsS3Service.getFile(anyString()))
                .thenReturn(
                        new URL(imageUrl)
                );
        //when //then
        mvc.perform(get("/images/{filename}", storeFilename)
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
        ;
        verify(awsS3Service).getFile(anyString());
    }

    @Test
    @DisplayName("[실패][GET} 잘못된 파일이름으로 이미지 불러오기 - NOT_FOUND_FILENAME 에러 발생")
    public void givenWrongImageUUID_whenDisplay_thenReturnNotFoundFilenameError() throws Exception{
        //given
        final String storeFilename = "0393e471-98df-459f-a314-292c910bcc44.PNG";
        final FileErrorCode errorCode = FileErrorCode.NOT_FOUND_FILENAME;
        when(awsS3Service.getFile(anyString()))
                .thenThrow(new FileException(errorCode));
        //when //then
        mvc.perform(get("/images/{filename}", storeFilename)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(awsS3Service).getFile(anyString());
    }

}
package com.onfree.controller;


import com.onfree.common.error.code.FileErrorCode;
import com.onfree.common.error.code.SignUpErrorCode;
import com.onfree.common.error.exception.FileException;
import com.onfree.common.error.exception.SignUpException;
import com.onfree.core.entity.fileitem.FileType;
import com.onfree.core.service.AwsS3Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class S3UploadController {

    private final AwsS3Service awsS3Service;

    /** 프로필 사진 업로드 */
    @ApiOperation(value = "프로필 사진 업로드 API")
    @PostMapping(value = "/upload/profile-image", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String profileImageUpload(
            @ApiParam(value = "이미지 파일", allowableValues = "png,jpeg,jpg")
            @RequestParam MultipartFile file
    ) {
        validateFileType(file);
        final String storefilename
                = awsS3Service.s3ImageFileUpload(file, FileType.PROFILE_IMAGE_FILE);

        return getImageLoadUrl(storefilename);
    }

    private void validateFileType(MultipartFile file) {
        if(file == null || file.isEmpty()){
            throw new SignUpException(SignUpErrorCode.FILE_IS_EMPTY);
        }
        final List<String> allowFileType = Arrays.asList("jpg", "jpeg", "png");
        final String ext = extractExt(file);
        if(!allowFileType.contains(ext)){
            throw new FileException(FileErrorCode.NOT_ALLOW_UPLOAD_FILETYPE);
        }
    }

    private String extractExt(@NonNull MultipartFile file) {
        String fileName = file.getOriginalFilename();
        final int pos = fileName != null ? fileName.lastIndexOf(".") : 0;
        return fileName != null ? fileName.substring(pos + 1).toLowerCase(Locale.ROOT) : null;
    }

    @ApiOperation(value = "포트폴리오 내용 사진 업로드 API")
    @PreAuthorize("hasRole('ARTIST')")
    @PostMapping(value = "/upload/portfolio-content-image", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String portfolioContentImageUpload(
            @ApiParam(value = "이미지 파일", allowableValues = "png,jpeg,jpg")
            @RequestParam MultipartFile file){
        validateFileType(file);
        final String filename
                = awsS3Service.s3ImageFileUpload(file, FileType.PORTFOLIO_CONTENT_IMAGE);

        return getImageLoadUrl(filename);
    }

    @ApiOperation(value = "포트폴리오 메인 이미지 업로드 API")
    @PreAuthorize("hasRole('ARTIST')")
    @PostMapping(value = "/upload/portfolio-main-image", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String portfolioMainImageUpload(
            @ApiParam(value = "이미지 파일", allowableValues = "png,jpeg,jpg")
            @RequestParam MultipartFile file
    ){
        validateFileType(file);
        final String filename
                = awsS3Service.s3ImageFileUpload(file, FileType.PORTFOLIO_MAIN_IMAGE);

        return getImageLoadUrl(filename);

    }

    private String getImageLoadUrl(String storeFilename) {
        return linkTo(S3UploadController.class).slash("images")
                .slash(storeFilename)
                .toString();
    }

    @GetMapping("/images/{filename}")
    public Resource display(
            @ApiParam(value = "이미지 파일 UUID", defaultValue = "82d9c665-a4ba-4d4e-98d9-f02f069f21dd.PNG")
            @PathVariable("filename") String filename){
        return new UrlResource(
                awsS3Service.getFile(filename)
        );
    }
}

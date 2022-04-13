package com.onfree.controller;

import com.onfree.core.entity.fileitem.FileItem;
import com.onfree.core.service.FileItemService;
import com.onfree.utils.AwsS3Component;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
public class S3DownLoadController {
    private final FileItemService fileItemService;
    private final AwsS3Component s3Component;

    @ApiOperation("실시간 의뢰 참고 파일 다운로드")
    @GetMapping("/reference-files/{fileName}")
    public ResponseEntity<Resource> getReferenceFile(@PathVariable("fileName") String fileName){
        FileItem fileItem = fileItemService.getFileItem(fileName);
        URL fileUrl = s3Component.getFileUrl(fileItem);
        UrlResource urlResource = new UrlResource(fileUrl);

        String encodedUploadFileName = UriUtils.encode(fileItem.getUploadFile().getUploadFilename(), StandardCharsets.UTF_8);
        String contemptDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contemptDisposition)
                .body(urlResource)
                ;
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> display(
            @ApiParam(value = "이미지 파일 UUID", defaultValue = "82d9c665-a4ba-4d4e-98d9-f02f069f21dd.PNG")
            @PathVariable("filename") String filename){
        FileItem fileItem = fileItemService.getFileItem(filename);
        UrlResource urlResource = new UrlResource(
                s3Component.getFileUrl(fileItem)
        );
        String contentType = getContentType(fileItem.getUploadFile().getUploadFilename());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(urlResource);
    }
    private String extractExt(String filename) {
        final int pos = filename != null ? filename.lastIndexOf(".") : 0;
        return filename != null ? filename.substring(pos + 1).toLowerCase(Locale.ROOT) : null;
    }

    public String getContentType(String filename){
        String ext = extractExt(filename);
        switch (ext){
            case "png":
            case "PNG":
                return MediaType.IMAGE_PNG_VALUE;
            case "jpeg":
            case "jpg":
            case "JPEG":
            case "JPG":
                return MediaType.IMAGE_JPEG_VALUE;
            case "gif":
            case "GIF":
                return MediaType.IMAGE_GIF_VALUE;
            default:
                return MediaType.ALL_VALUE;
        }
    }

}

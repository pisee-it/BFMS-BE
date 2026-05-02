package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "Quản lý File", description = "Các API phục vụ upload và tải file hợp đồng, tài liệu")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADVERTISING', 'ADMIN', 'OWNER')")
    @Operation(summary = "Upload file", description = "Tải file lên hệ thống. Hỗ trợ PDF, JPG, PNG. Dung lượng tối đa 5MB.")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = fileService.store(file);
        
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/files/")
                .path(filename)
                .toUriString();

        return ResponseEntity.ok(Map.of(
                "filename", filename,
                "url", fileDownloadUri
        ));
    }

    @GetMapping("/{filename:.+}")
    @PreAuthorize("hasAnyRole('ADVERTISING', 'ACCOUNTANT', 'ADMIN', 'OWNER')")
    @Operation(summary = "Tải/Xem file", description = "Lấy nội dung file dựa trên tên file.")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileService.loadAsResource(filename);
        
        String contentType = "application/octet-stream";
        try {
            if (filename.toLowerCase().endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (filename.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            }
        } catch (Exception ignored) {}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}

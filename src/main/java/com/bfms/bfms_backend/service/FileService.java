package com.bfms.bfms_backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileService {
    /**
     * Khởi tạo thư mục lưu trữ file.
     */
    void init();

    /**
     * Lưu trữ file từ MultipartFile.
     * @param file đối tượng file từ request
     * @return tên file đã được lưu trữ (thường là UUID + extension)
     */
    String store(MultipartFile file);

    /**
     * Tải file dưới dạng Resource.
     * @param filename tên file cần tải
     * @return Resource của file
     */
    Resource loadAsResource(String filename);

    /**
     * Xóa một file.
     * @param filename tên file cần xóa
     */
    void delete(String filename);

    /**
     * Lấy đường dẫn tuyệt đối của file.
     * @param filename tên file
     * @return Path của file
     */
    Path load(String filename);
}

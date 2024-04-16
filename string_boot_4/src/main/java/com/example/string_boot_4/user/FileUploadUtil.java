package com.example.string_boot_4.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class FileUploadUtil {
    @Value("${upload.directory}")
    private String uploadDirPath;

    public String uploadFile(MultipartFile file) throws IOException {
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String originalFileName = file.getOriginalFilename();
        String filePath = uploadDirPath + File.separator + originalFileName;
        File dest = new File(filePath);
        file.transferTo(dest);

        return filePath;
    }

    public void saveFile(String directoryPath, String fileName, MultipartFile file) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = directoryPath + File.separator + fileName;
        File dest = new File(filePath);
        file.transferTo(dest);
    }

    public String getUploadDirPath() {
        return uploadDirPath;
    }
}

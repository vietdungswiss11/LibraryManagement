package Ecommerce.BookWeb.Project.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) throws IOException {
        File tempFile = null;
        try {
            // Tạo file tạm
            tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            // Upload lên Cloudinary
            Map option = ObjectUtils.asMap("folder", "books");  // Upload vào folder books
            Map uploadResult = cloudinary.uploader().upload(tempFile, option);

            // Lấy URL an toàn (HTTPS)
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary", e);
            throw new IOException("Could not upload file", e);
        } finally {
            // Đảm bảo xóa file tạm
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    log.warn("Could not delete temp file: {}", tempFile.getAbsolutePath());
                }
            }
        }
    }
}

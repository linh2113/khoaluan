package com.example.electronics_store.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class CloudinaryUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryUtils.class);
    
    /**
     * Xóa hình ảnh từ Cloudinary dựa trên URL
     * 
     * @param cloudinary instance của Cloudinary
     * @param imageUrl URL của hình ảnh cần xóa
     * @return true nếu xóa thành công, false nếu có lỗi
     */
    public static boolean deleteImage(Cloudinary cloudinary, String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }
        
        try {
            // Trích xuất public_id từ URL Cloudinary
            String[] urlParts = imageUrl.split("/");
            String publicId = String.join("/", 
                    Arrays.copyOfRange(urlParts, urlParts.length - 2, urlParts.length))
                    .replaceFirst("[.][^.]+$", ""); // Loại bỏ phần mở rộng file
            
            // Xóa hình ảnh từ Cloudinary
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String status = (String) result.get("result");
            
            return "ok".equals(status);
        } catch (Exception e) {
            logger.error("Failed to delete image from Cloudinary: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Upload hình ảnh lên Cloudinary
     * 
     * @param cloudinary instance của Cloudinary
     * @param file file hình ảnh cần upload
     * @param folder thư mục lưu trữ trên Cloudinary
     * @return URL của hình ảnh đã upload
     * @throws IOException nếu có lỗi khi upload
     */
    public static String uploadImage(Cloudinary cloudinary, MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto"
        );
        
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return uploadResult.get("secure_url").toString();
    }
    
    /**
     * Upload hình ảnh mới và xóa hình ảnh cũ (nếu có)
     * 
     * @param cloudinary instance của Cloudinary
     * @param file file hình ảnh mới
     * @param oldImageUrl URL của hình ảnh cũ cần xóa
     * @param folder thư mục lưu trữ trên Cloudinary
     * @return URL của hình ảnh mới đã upload
     * @throws IOException nếu có lỗi khi upload
     */
    public static String replaceImage(Cloudinary cloudinary, MultipartFile file, String oldImageUrl, String folder) throws IOException {
        // Xóa ảnh cũ nếu có
        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
            deleteImage(cloudinary, oldImageUrl);
        }
        
        // Upload ảnh mới
        return uploadImage(cloudinary, file, folder);
    }
}
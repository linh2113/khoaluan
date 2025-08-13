package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.RatingDTO;
import com.example.electronics_store.dto.ReplyDTO;
import com.example.electronics_store.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createRating(
            @RequestParam Integer userId,
            @RequestParam Integer productId,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestPart("rating") @Valid RatingDTO ratingDTO) {
            RatingDTO rating = ratingService.createRating(userId, productId, ratingDTO);
            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = ratingService.uploadRatingImages(rating.getId(), images);
                rating.setImageUrls(imageUrls);
            }
            return ResponseEntity.ok(ApiResponse.success("Đánh giá thành công", rating));
        
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<?>> updateRating(
//            @PathVariable Integer id,
//            @Valid @RequestBody RatingDTO ratingDTO) {
//        try {
//            RatingDTO rating = ratingService.updateRating(id, ratingDTO);
//            return ResponseEntity.ok(ApiResponse.success("Rating updated successfully", rating));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(ApiResponse.error(e.getMessage()));
//        }
//    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getRatingById(@PathVariable Integer id) {
            RatingDTO rating = ratingService.getRatingById(id);
            return ResponseEntity.ok(ApiResponse.success(rating));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<?>> getRatingsByProductId(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<RatingDTO> ratings = ratingService.getRatingsByProductIdWithPagination(productId, pageable);

            return ResponseEntity.ok(ApiResponse.success(ratings));
     
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getRatingsByUserId(@PathVariable Integer userId) {
            List<RatingDTO> ratings = ratingService.getRatingsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(ratings));
    }

    @GetMapping("/star/{starRating}")
    public ResponseEntity<ApiResponse<?>> getRatingsByStar(
            @PathVariable Integer starRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
            if (starRating < 1 || starRating > 5) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Đánh giá phải từ 1-5"));
            }
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<RatingDTO> ratings = ratingService.getRatingsByStarWithPagination(starRating, pageable);
            return ResponseEntity.ok(ApiResponse.success(ratings));
    }

    @PostMapping(value = "/reply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> replyToRating(
            @RequestParam Integer userId,
            @RequestParam Integer parentRatingId,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestPart("reply") @Valid ReplyDTO replyDTO) {
            RatingDTO reply = ratingService.replyToRating(userId, parentRatingId, replyDTO.getComment());
            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = ratingService.uploadRatingImages(reply.getId(), images);
                reply.setImageUrls(imageUrls);
            }
            return ResponseEntity.ok(ApiResponse.success("Trả lời thành công", reply));
    }


    @GetMapping("/replies/{parentId}")
    public ResponseEntity<ApiResponse<?>> getRepliesByParentId(@PathVariable Integer parentId) {
            List<RatingDTO> replies = ratingService.getRepliesByParentId(parentId);
            return ResponseEntity.ok(ApiResponse.success(replies));
       
    }

    @GetMapping("/product/{productId}/average")
    public ResponseEntity<ApiResponse<?>> getAverageRatingForProduct(@PathVariable Integer productId) {
            Double averageRating = ratingService.getAverageRatingForProduct(productId);
            return ResponseEntity.ok(ApiResponse.success(averageRating));
       
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<ApiResponse<?>> countRatingsByProduct(@PathVariable Integer productId) {
            Long count = ratingService.countRatingsByProduct(productId);
            return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/product/{productId}/distribution")
    public ResponseEntity<ApiResponse<?>> getRatingDistributionForProduct(@PathVariable Integer productId) {
            Map<Integer, Long> distribution = ratingService.getRatingDistributionForProduct(productId);
            return ResponseEntity.ok(ApiResponse.success(distribution));
    }


}

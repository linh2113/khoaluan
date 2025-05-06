import http from '@/lib/http'
import {
   RatingDTO,
   RatingResponse,
   RatingsResponse,
   AverageRatingResponse,
   RatingCountResponse,
   RatingDistributionResponse,
   CreateRatingParams,
   ReplyRatingParams
} from '@/types/rating.type'

// Tạo đánh giá mới
export const createRating = ({ userId, productId, rating }: CreateRatingParams) =>
   http.post<RatingResponse>(`/ratings?userId=${userId}&productId=${productId}`, rating)

// Lấy đánh giá theo ID
export const getRatingById = (id: number) => http.get<RatingResponse>(`/ratings/${id}`)

// Lấy danh sách đánh giá theo sản phẩm
export const getRatingsByProductId = (productId: number) => http.get<RatingsResponse>(`/ratings/product/${productId}`)

// Lấy danh sách đánh giá theo người dùng
export const getRatingsByUserId = (userId: number) => http.get<RatingsResponse>(`/ratings/user/${userId}`)

// Trả lời đánh giá
export const replyToRating = ({ userId, parentRatingId, reply }: ReplyRatingParams) =>
   http.post<RatingResponse>(`/ratings/reply?userId=${userId}&parentRatingId=${parentRatingId}`, reply)

// Lấy danh sách trả lời cho một đánh giá
export const getRepliesByParentId = (parentId: number) => http.get<RatingsResponse>(`/ratings/replies/${parentId}`)

// Lấy điểm đánh giá trung bình của sản phẩm
export const getAverageRatingForProduct = (productId: number) =>
   http.get<AverageRatingResponse>(`/ratings/product/${productId}/average`)

// Đếm số lượng đánh giá của sản phẩm
export const countRatingsByProduct = (productId: number) =>
   http.get<RatingCountResponse>(`/ratings/product/${productId}/count`)

// Lấy phân phối đánh giá của sản phẩm
export const getRatingDistributionForProduct = (productId: number) =>
   http.get<RatingDistributionResponse>(`/ratings/product/${productId}/distribution`)

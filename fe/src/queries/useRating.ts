import {
   createRating,
   getRatingById,
   getRatingsByProductId,
   getRatingsByUserId,
   replyToRating,
   getRepliesByParentId,
   getAverageRatingForProduct,
   countRatingsByProduct,
   getRatingDistributionForProduct
} from '@/apiRequest/rating'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { toast } from 'react-toastify'

// Hook lấy đánh giá theo ID
export const useGetRatingById = (id: number) => {
   return useQuery({
      queryKey: ['rating', id],
      queryFn: () => getRatingById(id),
      enabled: !!id
   })
}

// Hook lấy danh sách đánh giá theo sản phẩm
export const useGetRatingsByProductId = (productId: number) => {
   return useQuery({
      queryKey: ['ratings', 'product', productId],
      queryFn: () => getRatingsByProductId(productId),
      enabled: !!productId
   })
}

// Hook lấy danh sách đánh giá theo người dùng
export const useGetRatingsByUserId = (userId: number) => {
   return useQuery({
      queryKey: ['ratings', 'user', userId],
      queryFn: () => getRatingsByUserId(userId),
      enabled: !!userId
   })
}

// Hook tạo đánh giá mới
export const useCreateRating = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createRating,
      onSuccess: (_, variables) => {
         queryClient.invalidateQueries({ queryKey: ['ratings', 'product', variables.productId] })
         queryClient.invalidateQueries({ queryKey: ['product', variables.productId] })
         toast.success('Đánh giá của bạn đã được gửi thành công')
      }
      // onError: (error: any) => {
      //    toast.error(error.response?.data?.message || 'Có lỗi xảy ra khi gửi đánh giá')
      // }
   })
}

// Hook trả lời đánh giá
export const useReplyToRating = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: replyToRating,
      onSuccess: (_, variables) => {
         queryClient.invalidateQueries({ queryKey: ['ratings', 'replies', variables.parentRatingId] })
         toast.success('Phản hồi của bạn đã được gửi thành công')
      },
      onError: (error: any) => {
         toast.error(error.response?.data?.message || 'Có lỗi xảy ra khi gửi phản hồi')
      }
   })
}

// Hook lấy danh sách trả lời cho một đánh giá
export const useGetRepliesByParentId = (parentId: number) => {
   return useQuery({
      queryKey: ['ratings', 'replies', parentId],
      queryFn: () => getRepliesByParentId(parentId),
      enabled: !!parentId
   })
}

// Hook lấy điểm đánh giá trung bình của sản phẩm
export const useGetAverageRatingForProduct = (productId: number) => {
   return useQuery({
      queryKey: ['ratings', 'average', productId],
      queryFn: () => getAverageRatingForProduct(productId),
      enabled: !!productId
   })
}

// Hook đếm số lượng đánh giá của sản phẩm
export const useCountRatingsByProduct = (productId: number) => {
   return useQuery({
      queryKey: ['ratings', 'count', productId],
      queryFn: () => countRatingsByProduct(productId),
      enabled: !!productId
   })
}

// Hook lấy phân phối đánh giá của sản phẩm
export const useGetRatingDistributionForProduct = (productId: number) => {
   return useQuery({
      queryKey: ['ratings', 'distribution', productId],
      queryFn: () => getRatingDistributionForProduct(productId),
      enabled: !!productId
   })
}

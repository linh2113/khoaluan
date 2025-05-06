import {
   createProduct,
   deleteProductImage,
   getAllAdminProduct,
   getAllBrand,
   getAllCategories,
   getAllDiscount,
   getAllPaymentMethod,
   getAllShippingMethod,
   updateProduct,
   uploadProductImage
} from '@/apiRequest/admin'
import { GetBrandQueryParamsType } from '@/types/admin.type'
import { GetProductQueryParamsType } from '@/types/product.type'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { toast } from 'react-toastify'

// Shipping Method
export const useGetAllShippingMethod = () => {
   return useQuery({
      queryKey: ['shippingMethod'],
      queryFn: getAllShippingMethod
   })
}

// Payment Method
export const useGetAllPaymentMethod = () => {
   return useQuery({
      queryKey: ['paymentMethod'],
      queryFn: getAllPaymentMethod
   })
}

// Brand
export const useGetAllBrand = (queryParams: GetBrandQueryParamsType) => {
   return useQuery({
      queryKey: ['brand', queryParams],
      queryFn: () => getAllBrand(queryParams)
   })
}

// Discount
export const useGetAllDiscount = () => {
   return useQuery({
      queryKey: ['discount'],
      queryFn: getAllDiscount
   })
}

// Category
export const useGetAllCategories = () => {
   return useQuery({
      queryKey: ['categories'],
      queryFn: getAllCategories
   })
}

// Product
export const useGetAllAdminProduct = (
   queryParams: Pick<GetProductQueryParamsType, 'page' | 'size' | 'sortBy' | 'sortDir'>
) => {
   return useQuery({
      queryKey: ['product', queryParams],
      queryFn: () => getAllAdminProduct(queryParams)
   })
}
export const useCreateProduct = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createProduct,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['product']
         })
      },
      onError: () => {
         toast.error('Thêm sản phẩm thất bại')
      }
   })
}

// Thêm hook upload hình ảnh sản phẩm
export const useUploadProductImage = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, file, isPrimary = false }: { id: number; file: File; isPrimary?: boolean }) =>
         uploadProductImage(id, file, isPrimary),
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['product']
         })
      }
   })
}

export const useUpdateProduct = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: updateProduct,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['product']
         })
      },
      onError: () => {
         toast.error('Cập nhật sản phẩm thất bại')
      }
   })
}

// Thêm hook xóa hình ảnh sản phẩm
export const useDeleteProductImage = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: deleteProductImage,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['product']
         })
      },
      onError: () => {
         toast.error('Xóa ảnh thất bại')
      }
   })
}

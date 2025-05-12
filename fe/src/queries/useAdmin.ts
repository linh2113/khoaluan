import {
   createBrand,
   createCategory,
   createPaymentMethod,
   createProduct,
   createShippingMethod,
   createUser,
   deleteProductImage,
   getAllAdminOrder,
   getAllAdminProduct,
   getAllBrand,
   getAllCategories,
   getAllDiscount,
   getAllPaymentMethod,
   getAllRating,
   getAllShippingMethod,
   getAllUser,
   getDashboardStatistics,
   updateBrand,
   updateCategory,
   updateOrderPayment,
   updateOrderStatus,
   updatePaymentMethod,
   updatePrimaryImage,
   updateProduct,
   updateShippingMethod,
   updateUser,
   uploadProductImage
} from '@/apiRequest/admin'
import {
   GetBrandQueryParamsType,
   GetCategoryQueryParamsType,
   GetOrderQueryParamsType,
   GetUserQueryParamsType
} from '@/types/admin.type'
import { GetProductQueryParamsType } from '@/types/product.type'
import { RatingQueryParamsType } from '@/types/rating.type'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { toast } from 'react-toastify'

// Shipping Method
export const useGetAllShippingMethod = () => {
   return useQuery({
      queryKey: ['shippingMethod'],
      queryFn: getAllShippingMethod
   })
}
export const useCreateShippingMethod = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createShippingMethod,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['shippingMethod']
         })
         toast.success('Thêm phương thức giao hàng thành công')
      },
      onError: () => {
         toast.error('Thêm phương thức giao hàng thất bại')
      }
   })
}
export const useUpdateShippingMethod = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: updateShippingMethod,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['shippingMethod']
         })
         toast.success('Cập nhật phương thức giao hàng thành công')
      },
      onError: () => {
         toast.error('Cập nhật phương thức giao hàng thất bại')
      }
   })
}

// Payment Method
export const useGetAllPaymentMethod = () => {
   return useQuery({
      queryKey: ['paymentMethod'],
      queryFn: getAllPaymentMethod
   })
}
export const useCreatePaymentMethod = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createPaymentMethod,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['paymentMethod']
         })
         toast.success('Thêm phương thức thanh toán thành công')
      },
      onError: () => {
         toast.error('Thêm phương thức thanh toán thất bại')
      }
   })
}
export const useUpdatePaymentMethod = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: updatePaymentMethod,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['paymentMethod']
         })
         toast.success('Cập nhật phương thức thanh toán thành công')
      },
      onError: () => {
         toast.error('Cập nhật phương thức thanh toán thất bại')
      }
   })
}
// Brand
export const useGetAllBrand = (queryParams: GetBrandQueryParamsType) => {
   return useQuery({
      queryKey: ['brand', queryParams],
      queryFn: () => getAllBrand(queryParams)
   })
}
export const useCreateBrand = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createBrand,

      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['brand']
         })
         toast.success('Thêm thương hiệu thành công')
      },
      onError: () => {
         toast.error('Thêm thương hiệu thất bại')
      }
   })
}
export const useUpdateBrand = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: updateBrand,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['brand']
         })
         toast.success('Cập nhật thương hiệu thành công')
      },
      onError: () => {
         toast.error('Cập nhật thương hiệu thất bại')
      }
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
export const useGetAllCategories = (queryParams: GetCategoryQueryParamsType) => {
   return useQuery({
      queryKey: ['categories', queryParams],
      queryFn: () => getAllCategories(queryParams)
   })
}

export const useCreateCategory = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createCategory,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['categories']
         })
         toast.success('Thêm danh mục thành công')
      },
      onError: () => {
         toast.error('Thêm danh mục thất bại')
      }
   })
}
export const useUpdateCategory = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: updateCategory,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['categories']
         })
         toast.success('Cập nhật danh mục thành công')
      },
      onError: () => {
         toast.error('Cập nhật danh mục thất bại')
      }
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

export const useUpdatePrimaryImage = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: updatePrimaryImage,
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

// order
export const useGetAllAdminOrder = (queryParams: GetOrderQueryParamsType) => {
   return useQuery({
      queryKey: ['order', queryParams],
      queryFn: () => getAllAdminOrder(queryParams)
   })
}
export const useUpdateOrderStatus = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, status }: { id: number; status: number }) => updateOrderStatus(id, status),
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['order']
         })
         toast.success('Cập nhật trạng thái đơn hàng thành công')
      },
      onError: () => {
         toast.error('Cập nhật trạng thái đơn hàng thất bại')
      }
   })
}
export const useUpdateOrderPayment = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, paymentStatus }: { id: number; paymentStatus: string }) =>
         updateOrderPayment(id, paymentStatus),
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['order']
         })
         toast.success('Cập nhật trạng thái thanh toán thành công')
      },
      onError: () => {
         toast.error('Cập nhật trạng thái thanh toán thất bại')
      }
   })
}

// statistics
export const useGetDashboardStatistics = () => {
   return useQuery({
      queryKey: ['statistics'],
      queryFn: getDashboardStatistics
   })
}

//user
export const useGetAllUser = (queryParams: GetUserQueryParamsType) => {
   return useQuery({
      queryKey: ['user', queryParams],
      queryFn: () => getAllUser(queryParams)
   })
}
export const useUpdateUser = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: updateUser,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['user']
         }),
            toast.success('Cập nhật người dùng thành công')
      },
      onError: () => {
         toast.error('Cập nhật người dùng thất bại')
      }
   })
}
export const useCreateUser = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createUser,
      onSuccess: () => {
         queryClient.invalidateQueries({
            queryKey: ['user']
         }),
            toast.success('Thêm người dùng thành công')
      },
      onError: () => {
         toast.error('Thêm người dùng thất bại')
      }
   })
}

//rating
export const useGetAllRating = (queryParams: RatingQueryParamsType & { search?: string }) => {
   return useQuery({
      queryKey: ['rating', queryParams],
      queryFn: () => getAllRating(queryParams)
   })
}

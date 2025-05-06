import {
   createBrand,
   createCategory,
   createDiscount,
   createPaymentMethod,
   createProduct,
   createShippingMethod,
   getAllAdmins,
   getAllBrand,
   getAllCategories,
   getAllCustomers,
   getAllDiscounts,
   getAllOrders,
   getAllPaymentMethod,
   getAllProducts,
   getAllRatings,
   getAllShippingMethod,
   getAllUsers,
   getBrandById,
   getCategoryById,
   getDashboardStatistics,
   getLowStockProducts,
   getOrdersByPaymentStatus,
   getOrdersByStatus,
   getPaymentMethodById,
   getSalesStatistics,
   getShippingMethodById,
   togglePaymentMethodStatus,
   toggleShippingMethodStatus,
   updateBrand,
   updateCategory,
   updateDiscount,
   updateOrderStatus,
   updatePaymentMethod,
   updatePaymentStatus,
   updatePrimaryImage,
   updateProduct,
   updateShippingMethod,
   updateUser,
   uploadProductImage
} from '@/apiRequest/admin'
import { GetBrandQueryParamsType } from '@/types/admin.type'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { toast } from 'react-toastify'

// Shipping Methods
export const useGetAllShippingMethod = () => {
   return useQuery({
      queryKey: ['shippingMethod'],
      queryFn: getAllShippingMethod
   })
}

export const useGetShippingMethodById = (id: number) => {
   return useQuery({
      queryKey: ['shippingMethod', id],
      queryFn: () => getShippingMethodById(id),
      enabled: !!id
   })
}

export const useCreateShippingMethod = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createShippingMethod,
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['shippingMethod'] })
         toast.success('Tạo phương thức vận chuyển thành công')
      },
      onError: () => {
         toast.error('Tạo phương thức vận chuyển thất bại')
      }
   })
}

export const useUpdateShippingMethod = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, data }: { id: number; data: any }) => updateShippingMethod(id, data),
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['shippingMethod'] })
         toast.success('Cập nhật phương thức vận chuyển thành công')
      },
      onError: () => {
         toast.error('Cập nhật phương thức vận chuyển thất bại')
      }
   })
}

export const useToggleShippingMethodStatus = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: toggleShippingMethodStatus,
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['shippingMethod'] })
         toast.success('Cập nhật trạng thái phương thức vận chuyển thành công')
      },
      onError: () => {
         toast.error('Cập nhật trạng thái phương thức vận chuyển thất bại')
      }
   })
}

// Payment Methods
export const useGetAllPaymentMethod = () => {
   return useQuery({
      queryKey: ['paymentMethod'],
      queryFn: getAllPaymentMethod
   })
}

export const useGetPaymentMethodById = (id: number) => {
   return useQuery({
      queryKey: ['paymentMethod', id],
      queryFn: () => getPaymentMethodById(id),
      enabled: !!id
   })
}

export const useCreatePaymentMethod = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createPaymentMethod,
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['paymentMethod'] })
         toast.success('Tạo phương thức thanh toán thành công')
      },
      onError: () => {
         toast.error('Tạo phương thức thanh toán thất bại')
      }
   })
}

export const useUpdatePaymentMethod = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, data }: { id: number; data: any }) => updatePaymentMethod(id, data),
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['paymentMethod'] })
         toast.success('Cập nhật phương thức thanh toán thành công')
      },
      onError: () => {
         toast.error('Cập nhật phương thức thanh toán thất bại')
      }
   })
}

export const useTogglePaymentMethodStatus = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: togglePaymentMethodStatus,
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['paymentMethod'] })
         toast.success('Cập nhật trạng thái phương thức thanh toán thành công')
      },
      onError: () => {
         toast.error('Cập nhật trạng thái phương thức thanh toán thất bại')
      }
   })
}

// Brands
export const useGetAllBrand = (queryParams: GetBrandQueryParamsType) => {
   return useQuery({
      queryKey: ['brand', queryParams],
      queryFn: () => getAllBrand(queryParams)
   })
}

export const useGetBrandById = (id: number) => {
   return useQuery({
      queryKey: ['brand', id],
      queryFn: () => getBrandById(id),
      enabled: !!id
   })
}

export const useCreateBrand = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createBrand,
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['brand'] })
         toast.success('Tạo thương hiệu thành công')
      },
      onError: () => {
         toast.error('Tạo thương hiệu thất bại')
      }
   })
}

export const useUpdateBrand = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, data }: { id: number; data: any }) => updateBrand(id, data),
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['brand'] })
         toast.success('Cập nhật thương hiệu thành công')
      },
      onError: () => {
         toast.error('Cập nhật thương hiệu thất bại')
      }
   })
}

// Categories
export const useGetAllCategories = () => {
   return useQuery({
      queryKey: ['categories'],
      queryFn: getAllCategories
   })
}

export const useGetCategoryById = (id: number) => {
   return useQuery({
      queryKey: ['category', id],
      queryFn: () => getCategoryById(id),
      enabled: !!id
   })
}

export const useCreateCategory = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createCategory,
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['categories'] })
         toast.success('Tạo danh mục thành công')
      },
      onError: () => {
         toast.error('Tạo danh mục thất bại')
      }
   })
}

export const useUpdateCategory = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, data }: { id: number; data: any }) => updateCategory(id, data),
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['categories'] })
         toast.success('Cập nhật danh mục thành công')
      },
      onError: () => {
         toast.error('Cập nhật danh mục thất bại')
      }
   })
}

// Users
export const useGetAllUsers = () => {
   return useQuery({
      queryKey: ['users'],
      queryFn: getAllUsers
   })
}

export const useGetAllAdmins = () => {
   return useQuery({
      queryKey: ['admins'],
      queryFn: getAllAdmins
   })
}

export const useGetAllCustomers = () => {
   return useQuery({
      queryKey: ['customers'],
      queryFn: getAllCustomers
   })
}

export const useUpdateUser = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, data }: { id: number; data: Partial<any> }) => updateUser(id, data),
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['users'] })
         queryClient.invalidateQueries({ queryKey: ['admins'] })
         queryClient.invalidateQueries({ queryKey: ['customers'] })
         toast.success('Cập nhật người dùng thành công')
      },
      onError: () => {
         toast.error('Cập nhật người dùng thất bại')
      }
   })
}

// Orders
export const useGetAllOrders = (
   page: number = 0,
   size: number = 10,
   sortBy: string = 'id',
   sortDir: string = 'desc'
) => {
   return useQuery({
      queryKey: ['orders', page, size, sortBy, sortDir],
      queryFn: () => getAllOrders(page, size, sortBy, sortDir)
   })
}

export const useGetOrdersByStatus = (status: number) => {
   return useQuery({
      queryKey: ['orders', 'status', status],
      queryFn: () => getOrdersByStatus(status),
      enabled: status !== undefined
   })
}

export const useGetOrdersByPaymentStatus = (paymentStatus: string) => {
   return useQuery({
      queryKey: ['orders', 'payment', paymentStatus],
      queryFn: () => getOrdersByPaymentStatus(paymentStatus),
      enabled: !!paymentStatus
   })
}

export const useUpdateOrderStatus = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, status }: { id: number; status: number }) => updateOrderStatus(id, status),
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['orders'] })
         toast.success('Cập nhật trạng thái đơn hàng thành công')
      },
      onError: () => {
         toast.error('Cập nhật trạng thái đơn hàng thất bại')
      }
   })
}

export const useUpdatePaymentStatus = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, paymentStatus }: { id: number; paymentStatus: string }) =>
         updatePaymentStatus(id, paymentStatus),
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['orders'] })
         toast.success('Cập nhật trạng thái thanh toán thành công')
      },
      onError: () => {
         toast.error('Cập nhật trạng thái thanh toán thất bại')
      }
   })
}

// Products
export const useGetAllProducts = (
   page: number = 0,
   size: number = 10,
   sortBy: string = 'id',
   sortDir: string = 'desc'
) => {
   return useQuery({
      queryKey: ['products', page, size, sortBy, sortDir],
      queryFn: () => getAllProducts(page, size, sortBy, sortDir)
   })
}

export const useGetLowStockProducts = () => {
   return useQuery({
      queryKey: ['products', 'low-stock'],
      queryFn: getLowStockProducts
   })
}

export const useCreateProduct = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createProduct,
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['products'] })
         toast.success('Tạo sản phẩm thành công')
      },
      onError: () => {
         toast.error('Tạo sản phẩm thất bại')
      }
   })
}

export const useUpdateProduct = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, data }: { id: number; data: any }) => updateProduct(id, data),
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['products'] })
         toast.success('Cập nhật sản phẩm thành công')
      },
      onError: () => {
         toast.error('Cập nhật sản phẩm thất bại')
      }
   })
}

export const useUploadProductImage = () => {
   return useMutation({
      mutationFn: ({ id, file, isPrimary }: { id: number; file: File; isPrimary?: boolean }) =>
         uploadProductImage(id, file, isPrimary || false),
      onSuccess: () => {
         toast.success('Tải lên hình ảnh thành công')
      },
      onError: () => {
         toast.error('Tải lên hình ảnh thất bại')
      }
   })
}

export const useUpdatePrimaryImage = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: updatePrimaryImage,
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['products'] })
         toast.success('Cập nhật hình ảnh chính thành công')
      },
      onError: () => {
         toast.error('Cập nhật hình ảnh chính thất bại')
      }
   })
}

// Discounts
export const useGetAllDiscounts = () => {
   return useQuery({
      queryKey: ['discounts'],
      queryFn: getAllDiscounts
   })
}

export const useCreateDiscount = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: createDiscount,
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['discounts'] })
         toast.success('Tạo mã giảm giá thành công')
      },
      onError: () => {
         toast.error('Tạo mã giảm giá thất bại')
      }
   })
}

export const useUpdateDiscount = () => {
   const queryClient = useQueryClient()
   return useMutation({
      mutationFn: ({ id, data }: { id: number; data: any }) => updateDiscount(id, data),
      onSuccess: () => {
         queryClient.invalidateQueries({ queryKey: ['discounts'] })
         toast.success('Cập nhật mã giảm giá thành công')
      },
      onError: () => {
         toast.error('Cập nhật mã giảm giá thất bại')
      }
   })
}

// Ratings
export const useGetAllRatings = () => {
   return useQuery({
      queryKey: ['ratings'],
      queryFn: getAllRatings
   })
}

// Dashboard
export const useGetDashboardStatistics = () => {
   return useQuery({
      queryKey: ['dashboard'],
      queryFn: getDashboardStatistics
   })
}

export const useGetSalesStatistics = (startDate?: string, endDate?: string) => {
   return useQuery({
      queryKey: ['sales', startDate, endDate],
      queryFn: () => getSalesStatistics(startDate, endDate)
   })
}

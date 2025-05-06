import http from '@/lib/http'
import {
   BrandType,
   CategoryType,
   CategoryUpdateDTO,
   DashboardStatisticsType,
   DiscountType,
   GetAllBrandType,
   GetBrandQueryParamsType,
   OrderType,
   PaymentMethodType,
   RatingType,
   ShippingMethodType,
   UserType
} from '@/types/admin.type'
import { ResponseData, ResponseDataWithPaginate } from '@/types/utils.type'
import queryString from 'query-string'

// Shipping Methods
export const getAllShippingMethod = () => http.get<ResponseData<ShippingMethodType[]>>(`/admin/shipping-methods`)
export const getShippingMethodById = (id: number) =>
   http.get<ResponseData<ShippingMethodType>>(`/admin/shipping-methods/${id}`)
export const createShippingMethod = (data: Omit<ShippingMethodType, 'id' | 'createdAt' | 'updatedAt'>) =>
   http.post<ResponseData<ShippingMethodType>>(`/admin/shipping-methods`, data)
export const updateShippingMethod = (id: number, data: Omit<ShippingMethodType, 'id' | 'createdAt' | 'updatedAt'>) =>
   http.put<ResponseData<ShippingMethodType>>(`/admin/shipping-methods/${id}`, data)
export const toggleShippingMethodStatus = (id: number) =>
   http.put<ResponseData<null>>(`/admin/shipping-methods/${id}/toggle-status`)

// Payment Methods
export const getAllPaymentMethod = () => http.get<ResponseData<PaymentMethodType[]>>(`/admin/payment-methods`)
export const getPaymentMethodById = (id: number) =>
   http.get<ResponseData<PaymentMethodType>>(`/admin/payment-methods/${id}`)
export const createPaymentMethod = (data: Omit<PaymentMethodType, 'id' | 'createdAt' | 'updatedAt'>) =>
   http.post<ResponseData<PaymentMethodType>>(`/admin/payment-methods`, data)
export const updatePaymentMethod = (id: number, data: Omit<PaymentMethodType, 'id' | 'createdAt' | 'updatedAt'>) =>
   http.put<ResponseData<PaymentMethodType>>(`/admin/payment-methods/${id}`, data)
export const togglePaymentMethodStatus = (id: number) =>
   http.put<ResponseData<null>>(`/admin/payment-methods/${id}/toggle-status`)

// Brands
export const getAllBrand = (queryParams: GetBrandQueryParamsType) =>
   http.get<GetAllBrandType>(`/admin/brands?` + queryString.stringify(queryParams))
export const getBrandById = (id: number) => http.get<ResponseData<BrandType>>(`/admin/brands/${id}`)
export const createBrand = (data: Omit<BrandType, 'id' | 'createdAt' | 'updatedAt' | 'productCount'>) =>
   http.post<ResponseData<BrandType>>(`/admin/brands`, data)
export const updateBrand = (id: number, data: Omit<BrandType, 'id' | 'createdAt' | 'updatedAt' | 'productCount'>) =>
   http.put<ResponseData<BrandType>>(`/admin/brands/${id}`, data)

// Categories
export const getAllCategories = () => http.get<ResponseData<CategoryType[]>>(`/admin/categories`)
export const getCategoryById = (id: number) => http.get<ResponseData<CategoryType>>(`/admin/categories/${id}`)
export const createCategory = (data: Omit<CategoryType, 'id' | 'createdAt' | 'updatedAt'>) =>
   http.post<ResponseData<CategoryType>>(`/admin/categories`, data)
export const updateCategory = (id: number, data: CategoryUpdateDTO) =>
   http.put<ResponseData<CategoryType>>(`/admin/categories/${id}`, data)

// Users
export const getAllUsers = () => http.get<ResponseData<UserType[]>>(`/admin/users`)
export const getAllAdmins = () => http.get<ResponseData<UserType[]>>(`/admin/users/admins`)
export const getAllCustomers = () => http.get<ResponseData<UserType[]>>(`/admin/users/customers`)
export const updateUser = (id: number, data: Partial<UserType>) =>
   http.put<ResponseData<UserType>>(`/admin/users/${id}`, data)

// Orders
export const getAllOrders = (page: number = 0, size: number = 10, sortBy: string = 'id', sortDir: string = 'desc') =>
   http.get<ResponseDataWithPaginate<OrderType[]>>(
      `/admin/orders?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`
   )
export const getOrdersByStatus = (status: number) =>
   http.get<ResponseData<OrderType[]>>(`/admin/orders/status/${status}`)
export const getOrdersByPaymentStatus = (paymentStatus: string) =>
   http.get<ResponseData<OrderType[]>>(`/admin/orders/payment/${paymentStatus}`)
export const updateOrderStatus = (id: number, status: number) =>
   http.put<ResponseData<OrderType>>(`/admin/orders/${id}/status?status=${status}`)
export const updatePaymentStatus = (id: number, paymentStatus: string) =>
   http.put<ResponseData<OrderType>>(`/admin/orders/${id}/payment?paymentStatus=${paymentStatus}`)

// Products
export const getAllProducts = (page: number = 0, size: number = 10, sortBy: string = 'id', sortDir: string = 'desc') =>
   http.get<ResponseDataWithPaginate<any>>(
      `/admin/products?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`
   )
export const getLowStockProducts = () => http.get<ResponseData<any[]>>(`/admin/products/low-stock`)
export const createProduct = (data: any) => http.post<ResponseData<any>>(`/admin/products`, data)
export const updateProduct = (id: number, data: any) => http.put<ResponseData<any>>(`/admin/products/${id}`, data)
export const uploadProductImage = (id: number, file: File, isPrimary: boolean = false) => {
   const formData = new FormData()
   formData.append('file', file)
   formData.append('isPrimary', isPrimary.toString())
   return http.post<ResponseData<string>>(`/admin/products/${id}/upload-image`, formData, {
      headers: {
         'Content-Type': 'multipart/form-data'
      }
   })
}
export const updatePrimaryImage = (imageId: number) =>
   http.put<ResponseData<null>>(`/admin/products/images/${imageId}/primary`)

// Discounts
export const getAllDiscounts = () => http.get<ResponseData<DiscountType[]>>(`/admin/discounts`)
export const createDiscount = (data: Omit<DiscountType, 'id' | 'createdAt' | 'updatedAt'>) =>
   http.post<ResponseData<DiscountType>>(`/admin/discounts`, data)
export const updateDiscount = (id: number, data: Omit<DiscountType, 'id' | 'createdAt' | 'updatedAt'>) =>
   http.put<ResponseData<DiscountType>>(`/admin/discounts/${id}`, data)

// Ratings
export const getAllRatings = () => http.get<ResponseData<RatingType[]>>(`/admin/ratings`)

// Dashboard
export const getDashboardStatistics = () => http.get<ResponseData<DashboardStatisticsType>>(`/admin/dashboard`)
export const getSalesStatistics = (startDate?: string, endDate?: string) => {
   let url = `/admin/statistics/sales`
   if (startDate || endDate) {
      url += '?'
      if (startDate) url += `startDate=${startDate}`
      if (startDate && endDate) url += '&'
      if (endDate) url += `endDate=${endDate}`
   }
   return http.get<ResponseData<any>>(url)
}

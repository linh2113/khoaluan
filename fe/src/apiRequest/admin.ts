import http from '@/lib/http'
import {
   BrandType,
   CategoryType,
   CreateProductType,
   DashboardStatisticsType,
   DiscountType,
   GetAllBrandType,
   GetBrandQueryParamsType,
   GetCategoryQueryParamsType,
   GetOrderQueryParamsType,
   GetUserQueryParamsType,
   OrderType,
   PaymentMethodType,
   ShippingMethodType,
   UserType
} from '@/types/admin.type'
import { GetAllProductType, GetProductQueryParamsType } from '@/types/product.type'
import { RatingDTO, RatingQueryParamsType } from '@/types/rating.type'
import { ResponseData, ResponseDataWithPaginate } from '@/types/utils.type'
import queryString from 'query-string'

// ShippingMethod
export const getAllShippingMethod = () => http.get<ResponseData<ShippingMethodType[]>>(`/admin/shipping-methods`)
export const createShippingMethod = (body: ShippingMethodType) => http.post(`/admin/shipping-methods`, body)
export const updateShippingMethod = (body: ShippingMethodType) => http.put(`/admin/shipping-methods/${body.id}`, body)

// PaymentMethod
export const getAllPaymentMethod = () => http.get<ResponseData<PaymentMethodType[]>>(`/admin/payment-methods`)
export const createPaymentMethod = (body: PaymentMethodType) => http.post(`/admin/payment-methods`, body)
export const updatePaymentMethod = (body: PaymentMethodType) => http.put(`/admin/payment-methods/${body.id}`, body)

// Brand
export const getAllBrand = (queryParams: GetBrandQueryParamsType) =>
   http.get<GetAllBrandType>(`/admin/brands?` + queryString.stringify(queryParams))
export const createBrand = (body: BrandType) => http.post(`/admin/brands`, body)
export const updateBrand = (body: BrandType) => http.put(`/admin/brands/${body.id}`, body)

// Discount
export const getAllDiscount = () => http.get<ResponseData<ResponseDataWithPaginate<DiscountType[]>>>(`/admin/discounts`)

// Category
export const getAllCategories = (queryParams: GetCategoryQueryParamsType) =>
   http.get<ResponseData<ResponseDataWithPaginate<CategoryType[]>>>(
      '/admin/categories?' + queryString.stringify(queryParams)
   )
export const createCategory = (body: CategoryType) => http.post('/admin/categories', body)
export const updateCategory = (body: CategoryType) => http.put(`/admin/categories/${body.id}`, body)

// Product
export const getAllAdminProduct = (
   queryParams: Pick<GetProductQueryParamsType, 'page' | 'size' | 'sortBy' | 'sortDir'>
) => http.get<GetAllProductType>(`/admin/products?` + queryString.stringify(queryParams))

export const createProduct = (body: CreateProductType) => http.post(`/admin/products`, body)
// Thêm API upload hình ảnh sản phẩm
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
export const updateProduct = (body: CreateProductType & { id: number }) => http.put(`/admin/products/${body.id}`, body)
// Thêm API xóa hình ảnh sản phẩm
export const deleteProductImage = (imageId: number) => http.delete(`/admin/products/images/${imageId}`)
export const updatePrimaryImage = (imageId: number) => http.put(`/admin/products/images/${imageId}/primary`)

// Order
export const getAllAdminOrder = (queryParams: GetOrderQueryParamsType) =>
   http.get<ResponseData<ResponseDataWithPaginate<OrderType[]>>>(`/admin/orders?` + queryString.stringify(queryParams))
export const updateOrderStatus = (id: number, status: number) => http.put(`/admin/orders/${id}/status?status=${status}`)
export const updateOrderPayment = (id: number, paymentStatus: string) =>
   http.put(`/admin/orders/${id}/payment?paymentStatus=${paymentStatus}`)

//statistics
export const getDashboardStatistics = () => http.get<ResponseData<DashboardStatisticsType>>(`/admin/dashboard`)

//user
export const getAllUser = (queryParams: GetUserQueryParamsType) =>
   http.get<ResponseData<ResponseDataWithPaginate<UserType[]>>>(`/admin/users?` + queryString.stringify(queryParams))
export const updateUser = (body: UserType) => http.put(`/admin/users/${body.id}`, body)
export const createUser = (body: UserType) => http.post(`/admin/users`, body)

//rating
export const getAllRating = (queryParams: RatingQueryParamsType & { search?: string }) =>
   http.get<ResponseData<ResponseDataWithPaginate<RatingDTO[]>>>(`/admin/ratings?` + queryString.stringify(queryParams))

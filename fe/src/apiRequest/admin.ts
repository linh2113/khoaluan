import http from '@/lib/http'
import {
   CategoryType,
   CreateProductType,
   DiscountType,
   GetAllBrandType,
   GetBrandQueryParamsType,
   PaymentMethodType,
   ShippingMethodType
} from '@/types/admin.type'
import { GetAllProductType, GetProductQueryParamsType } from '@/types/product.type'
import { ResponseData } from '@/types/utils.type'
import queryString from 'query-string'

// ShippingMethod
export const getAllShippingMethod = () => http.get<ResponseData<ShippingMethodType[]>>(`/admin/shipping-methods`)

// PaymentMethod
export const getAllPaymentMethod = () => http.get<ResponseData<PaymentMethodType[]>>(`/admin/payment-methods`)
export const getPaymentMethod = (id: number) =>
   http.get<ResponseData<PaymentMethodType>>(`/admin/payment-methods/${id}`)
export const createPaymentMethod = (body: PaymentMethodType) => http.post(`/admin/payment-methods`, body)
export const updatePaymentMethod = (body: PaymentMethodType) => http.put(`/admin/payment-methods/${body.id}`, body)

// Brand
export const getAllBrand = (queryParams: GetBrandQueryParamsType) =>
   http.get<GetAllBrandType>(`/admin/brands?` + queryString.stringify(queryParams))

// Discount
export const getAllDiscount = () => http.get<ResponseData<DiscountType[]>>(`/admin/discounts`)

// Category
export const getAllCategories = () => http.get<ResponseData<CategoryType[]>>('/admin/categories')
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

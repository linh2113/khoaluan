import { ResponseData, ResponseDataWithPaginate } from '@/types/utils.type'

// ShippingMethod
export interface ShippingMethodType {
   id: number
   methodName: string
   description: string
   baseCost: number
   estimatedDays: string
   isActive: boolean
   createdAt: string
   updatedAt: string
}

// PaymentMethod
export interface PaymentMethodType {
   id: number
   methodName: string
   description: string
   isActive: boolean
   createdAt: string
   updatedAt: string
}

// Brand
export interface GetBrandQueryParamsType {
   page?: number
   size?: number
   sortBy?: string | 'id'
   sortDir?: string | 'desc'
}
export type GetAllBrandType = ResponseData<ResponseDataWithPaginate<BrandType[]>>

export interface BrandType {
   id: number
   brandName: string
   description: string
   logo: string
   createdAt: string
   updatedAt: string
   status: boolean
   productCount: number
}

// Discount
export interface DiscountType {
   id: number
   code: string
   discountName: string
   description: string
   value: number
   quantity: number
   startDate: string
   endDate: string
   isActive: boolean
}

// Category
export interface CategoryType {
   id: number
   categoryName: string
   status: number
}

// Product
export interface CreateProductType {
   categoryId: number
   discountId: number | null
   brandId: number
   name: string
   price: number
   description: string
   warranty: string
   weight: number
   dimensions: string
   status: boolean
   stock: number
   processor: string
   ram: string
   storage: string
   display: string
   graphics: string
   battery: string
   camera: string
   operatingSystem: string
   connectivity: string
   otherFeatures: string
}

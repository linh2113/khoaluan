import { ResponseData, ResponseDataWithPaginate } from '@/types/utils.type'

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
export interface PaymentMethodType {
   id: number
   methodName: string
   description: string
   isActive: boolean
   createdAt: string
   updatedAt: string
}

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

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

export interface BrandType {
   id: number
   brandName: string
   description: string
   logo: string
   status: boolean
   createdAt: string
   updatedAt: string
   productCount?: number
}

export interface GetAllBrandType extends ResponseDataWithPaginate<BrandType[]> {}

export interface CategoryType {
   id: number
   categoryName: string
   status: number
   createdAt: string
   updatedAt: string
}

export interface CategoryUpdateDTO {
   categoryName: string
   status: number
}

export interface UserType {
   id: number
   userName: string
   email: string
   phone: string
   fullName: string
   address: string
   picture: string
   role: boolean
   active: number
   createdAt: string
   updatedAt: string
}

export interface OrderType {
   id: number
   userId: number
   orderDate: string
   status: number
   totalAmount: number
   shippingAddress: string
   paymentMethod: string
   paymentStatus: string
   shippingMethod: string
   shippingFee: number
   note: string
   createdAt: string
   updatedAt: string
   orderDetails: OrderDetailType[]
   user?: UserType
}

export interface OrderDetailType {
   id: number
   orderId: number
   productId: number
   quantity: number
   price: number
   productName: string
   productImage: string
}

export interface DiscountType {
   id: number
   code: string
   description: string
   discountPercent: number
   maxAmount: number
   startDate: string
   endDate: string
   isActive: boolean
   createdAt: string
   updatedAt: string
}

export interface RatingType {
   id: number
   userId: number
   productId: number
   rating: number
   comment: string
   createdAt: string
   updatedAt: string
   userName: string
   userAvatar: string
   replies?: RatingType[]
   parentId?: number
}

export interface DashboardStatisticsType {
   totalRevenue: number
   totalOrders: number
   totalProducts: number
   totalUsers: number
   recentOrders: OrderType[]
   topSellingProducts: {
      productId: number
      productName: string
      totalSold: number
      revenue: number
   }[]
}

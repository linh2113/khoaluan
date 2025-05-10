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
   search?: string
}
export type GetAllBrandType = ResponseData<ResponseDataWithPaginate<BrandType[]>>

export interface BrandType {
   id: number
   brandName: string
   description: string
   logo: string
   createdAt: string
   updatedAt: string
   status: number
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
export interface GetCategoryQueryParamsType {
   page?: number
   size?: number
   sortBy?: string | 'id'
   sortDir?: string | 'desc'
   search?: string
}
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

// order
export interface GetOrderQueryParamsType {
   page?: number
   size?: number
   sortBy?: string | 'id'
   sortDir?: string | 'desc'
   search?: string
}
export interface OrderType {
   id: number
   userId: number
   userName: string
   totalPrice: number
   shippingFee: number
   address: string
   phoneNumber: string
   shippingMethodId: number
   shippingMethodName: string
   paymentMethodId: number
   paymentMethodName: string
   trackingNumber: any
   createAt: string
   paymentStatus: string
   orderStatus: number
   orderStatusText: string
   orderDetails: OrderDetail[]
}
export interface OrderDetail {
   id: number
   orderId: number
   productId: number
   productName: string
   productImage: string
   quantity: number
   price: number
   totalPrice: number
   reviewStatus: boolean
}

// statistics
export interface DashboardStatisticsType {
   totalUsers: number
   topSellingProducts: TopSellingProduct[]
   shippedOrders: number
   deliveredOrders: number
   topCustomers: TopCustomer[]
   pendingOrders: number
   revenueToday: number
   cancelledOrders: number
   recentOrders: RecentOrder[]
   lowStockProducts: number
   totalProducts: number
   newUsersToday: number
   totalOrders: number
   totalRevenue: number
   processingOrders: number
   newOrdersToday: number
}
export interface TopSellingProduct {
   price: number
   name: string
   id: number
   soldQuantity: number
}

export interface TopCustomer {
   orderCount: number
   id: number
   userName: string
   email: string
}

export interface RecentOrder {
   totalPrice: number
   orderStatus: number
   id: number
   userName: string
   userId: number
   createAt: string
}

//user
export interface GetUserQueryParamsType {
   page?: number
   size?: number
   sortBy?: string | 'id'
   sortDir?: string | 'desc'
   role?: boolean
   search?: string
}

export interface UserType {
   id: number
   role: boolean
   userName: string
   email: string
   phone: string
   surName: string
   lastName: string
   active: number
   picture: string
   createAt: string
   loginTimes: number
   address: string
   dateOfBirth: string
   gender: string
}

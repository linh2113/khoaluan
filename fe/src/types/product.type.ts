import { ResponseData, ResponseDataWithPaginate } from '@/types/utils.type'

export type GetAllProductType = ResponseData<ResponseDataWithPaginate<ProductType[]>>

export interface ProductType {
   id: number
   categoryId: number
   categoryName: string
   discountId: any
   discountName: any
   name: string
   brand: string
   image: string
   price: number
   discountedPrice: number
   description: string
   warranty: string
   weight: number
   dimensions: string
   createAt: string
   createBy: string
   status: boolean
   updateAt: string
   updateBy: string
   stock: number
   averageRating: number
   reviewCount: number
   imageUrls: any[]
   productDetail: {
      id: number
      productId: number
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
}

//////GetProductQueryParamsType
export interface GetProductQueryParamsType {
   page?: number
   size?: number
   sortBy?: string | 'id'
   sortDir?: string | 'desc'
   keyword?: string
   brand?: string
   minPrice?: number
   maxPrice?: number
   categoryId?: number
   isDiscount?: boolean
   inStock?: boolean
   filterType?: 'ALL' | 'TOP_SELLING' | 'NEW_ARRIVALS' | 'TOP_RATED' | 'RELATED' | 'DISCOUNTED'
}

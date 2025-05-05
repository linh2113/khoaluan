import { ResponseData } from '@/types/utils.type'

export type GetAllProductType = ResponseData<Data>

export interface Data {
   content: ProductType[]
   pageable: Pageable
   last: boolean
   totalPages: number
   totalElements: number
   first: boolean
   size: number
   number: number
   sort: Sort2
   numberOfElements: number
   empty: boolean
}

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
   productDetail: any
}

export interface Pageable {
   pageNumber: number
   pageSize: number
   sort: Sort
   offset: number
   unpaged: boolean
   paged: boolean
}

export interface Sort {
   empty: boolean
   sorted: boolean
   unsorted: boolean
}

export interface Sort2 {
   empty: boolean
   sorted: boolean
   unsorted: boolean
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

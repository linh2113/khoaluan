import { ResponseData } from '@/types/utils.type'

export type GetAllProductType = ResponseData<Data>

export interface Data {
   content: ProductType[]
   pageable: Pageable
   last: boolean
   totalPages: number
   totalElements: number
   size: number
   number: number
   sort: Sort2
   numberOfElements: number
   first: boolean
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
   image?: string
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
   updateBy?: string
   stock: number
   averageRating: number
   reviewCount: number
   imageUrls: any[]
   productDetail?: ProductDetail
}

export interface ProductDetail {
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

export interface Pageable {
   pageNumber: number
   pageSize: number
   sort: Sort
   offset: number
   paged: boolean
   unpaged: boolean
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
   sortBy?: number
   discountId?: number
   name?: string
   price?: string
   sort?: string
   order?: string
   categoryId?: number
}

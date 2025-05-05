import http from '@/lib/http'
import { ResponseData } from '@/types/utils.type'

export const getAllCategories = () =>
   http.get<
      ResponseData<
         {
            id: number
            categoryName: string
            status: number
         }[]
      >
   >('/admin/categories')

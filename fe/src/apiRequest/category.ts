import http from '@/lib/http'
import { CategoryType } from '@/types/category.type'
import { ResponseData } from '@/types/utils.type'

export const getAllCategories = () => http.get<ResponseData<CategoryType[]>>('/admin/categories')

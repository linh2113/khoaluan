import http from '@/lib/http'
import { ProductType } from '@/types/product.type'
import { ResponseData } from '@/types/utils.type'

export const getProduct = (id: number) => http.get<ResponseData<ProductType>>(`/products/${id}`)

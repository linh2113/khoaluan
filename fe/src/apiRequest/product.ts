import http from '@/lib/http'
import { GetProductQueryParamsType, ProductType, GetAllProductType } from '@/types/product.type'
import { ResponseData } from '@/types/utils.type'
import queryString from 'query-string'

export const getProduct = (id: number) => http.get<ResponseData<ProductType>>(`/products/${id}`)

export const getAllProducts = (queryParams: GetProductQueryParamsType) =>
   http.get<GetAllProductType>('/products?' + queryString.stringify(queryParams))

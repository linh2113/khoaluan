import http from '@/lib/http'
import { Cart } from '@/types/cart.type'
import { ResponseData } from '@/types/utils.type'
import queryString from 'query-string'
interface UpdateCartParams {
   userId: number
   cartItemId: number
   quantity: number
}

export const getAllCart = (userId: number) => http.get<ResponseData<Cart>>(`/cart?userId=${userId}`)

export const updateCart = (queryParams: UpdateCartParams) =>
   http.put<ResponseData<Cart>>(`/cart/update?` + queryString.stringify(queryParams))

export const deleteCartItem = (queryParams: Omit<UpdateCartParams, 'quantity'>) =>
   http.delete(`/cart/remove?` + queryString.stringify(queryParams))

export const clearCart = (queryParams: Pick<UpdateCartParams, 'userId'>) =>
   http.delete(`/cart/clear?` + queryString.stringify(queryParams))

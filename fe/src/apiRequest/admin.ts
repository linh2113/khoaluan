import http from '@/lib/http'
import { GetAllBrandType, GetBrandQueryParamsType, PaymentMethodType, ShippingMethodType } from '@/types/admin.type'
import { ResponseData } from '@/types/utils.type'
import queryString from 'query-string'

export const getAllShippingMethod = () => http.get<ResponseData<ShippingMethodType[]>>(`/admin/shipping-methods`)

export const getAllPaymentMethod = () => http.get<ResponseData<PaymentMethodType[]>>(`/admin/payment-methods`)

export const getAllBrand = (queryParams: GetBrandQueryParamsType) =>
   http.get<GetAllBrandType>(`admin/brands?` + queryString.stringify(queryParams))

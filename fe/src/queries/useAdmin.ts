import { getAllBrand, getAllPaymentMethod, getAllShippingMethod } from '@/apiRequest/admin'
import { GetBrandQueryParamsType } from '@/types/admin.type'
import { useQuery } from '@tanstack/react-query'

export const useGetAllShippingMethod = () => {
   return useQuery({
      queryKey: ['shippingMethod'],
      queryFn: getAllShippingMethod
   })
}
export const useGetAllPaymentMethod = () => {
   return useQuery({
      queryKey: ['paymentMethod'],
      queryFn: getAllPaymentMethod
   })
}

export const useGetAllBrand = (queryParams: GetBrandQueryParamsType) => {
   return useQuery({
      queryKey: ['brand', queryParams],
      queryFn: () => getAllBrand(queryParams)
   })
}

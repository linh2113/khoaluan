import { getAllProducts, getProduct } from '@/apiRequest/product'
import { GetProductQueryParamsType } from '@/types/product.type'
import { useQuery } from '@tanstack/react-query'

export const useGetProduct = (id: number) => {
   return useQuery({
      queryKey: ['product', id],
      queryFn: () => getProduct(id),
      enabled: !!id
   })
}

export const useGetAllProducts = (queryParams: GetProductQueryParamsType) => {
   return useQuery({
      queryKey: ['products', queryParams],
      queryFn: () => getAllProducts(queryParams)
   })
}

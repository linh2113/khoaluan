import { getAllProducts, getProduct, compareProducts } from '@/apiRequest/product'
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

export const useCompareProducts = (productIds: number[]) => {
   return useQuery({
      queryKey: ['products', 'compare', productIds],
      queryFn: () => compareProducts(productIds),
      enabled: productIds.length >= 2 && productIds.length <= 4
   })
}

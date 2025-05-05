import { getProduct } from '@/apiRequest/product'
import { useQuery } from '@tanstack/react-query'

export const useGetProduct = (id: number) => {
   return useQuery({
      queryKey: ['product', id],
      queryFn: () => getProduct(id),
      enabled: !!id
   })
}

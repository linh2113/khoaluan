import { getAllCategories } from '@/apiRequest/category'
import { useQuery } from '@tanstack/react-query'

export const useGetAllCategories = () => {
   return useQuery({
      queryKey: ['categories'],
      queryFn: getAllCategories
   })
}

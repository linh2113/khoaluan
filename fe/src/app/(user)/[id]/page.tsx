import ProductDetail from '@/app/(user)/[id]/product-detail'
import { getIdFromNameId } from '@/lib/utils'
import { ProductType } from '@/types/product.type'
import { ResponseData } from '@/types/utils.type'
import { Metadata } from 'next'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api/v1'

export async function generateMetadata({ params }: { params: Promise<{ id: string }> }): Promise<Metadata> {
   const id = (await params).id

   try {
      const product: ResponseData<ProductType> = await fetch(
         `${API_BASE_URL}/products/${getIdFromNameId(id)}`
      ).then((res) => res.json())

      return {
         title: product.data?.name ?? 'TechShop',
         description: product.data?.description ?? 'Chi tiết sản phẩm TechShop'
      }
   } catch {
      return {
         title: 'TechShop',
         description: 'Chi tiết sản phẩm TechShop'
      }
   }
}
export default async function Page({ params }: { params: Promise<{ id: string }> }) {
   const id = (await params).id
   return <ProductDetail id={id} />
}

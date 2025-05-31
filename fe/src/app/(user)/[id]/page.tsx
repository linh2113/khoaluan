import ProductDetail from '@/app/(user)/[id]/product-detail'
import { getIdFromNameId } from '@/lib/utils'
import { ProductType } from '@/types/product.type'
import { ResponseData } from '@/types/utils.type'
import { Metadata } from 'next'
export async function generateMetadata({ params }: { params: Promise<{ id: string }> }): Promise<Metadata> {
   const id = (await params).id

   // fetch product information
   const product: ResponseData<ProductType> = await fetch(
      `http://localhost:8080/api/v1/products/${getIdFromNameId(id)}`
   ).then((res) => res.json())

   return {
      title: product.data.name,
      description: product.data.description
   }
}
export default async function Page({ params }: { params: Promise<{ id: string }> }) {
   const id = (await params).id
   return <ProductDetail id={id} />
}

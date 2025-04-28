import ProductDetail from '@/app/[id]/product-detail'

export default async function Page({ params }: { params: Promise<{ id: string }> }) {
   const id = (await params).id
   return <ProductDetail id={id} />
}

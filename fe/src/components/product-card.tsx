import { ProductType } from '@/types/product.type'
import { formatCurrency, generateNameId } from '@/lib/utils'
import Image from 'next/image'
import Link from 'next/link'
import { Card, CardContent, CardFooter } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { ShoppingCart, Star } from 'lucide-react'
import { Badge } from '@/components/ui/badge'

interface ProductCardProps {
   product: ProductType
}

export default function ProductCard({ product }: ProductCardProps) {
   // Đảm bảo product.name và product.id tồn tại trước khi tạo URL
   const productUrl = product?.name && product?.id ? `/${generateNameId({ name: product.name, id: product.id })}` : '#'

   // Tính phần trăm giảm giá
   const discountPercentage =
      product.discountedPrice && product.price && product.discountedPrice < product.price
         ? Math.round(((product.price - product.discountedPrice) / product.price) * 100)
         : 0

   return (
      <Card className='overflow-hidden transition-all hover:shadow-lg group border border-border/40'>
         <Link href={productUrl}>
            <div className='h-52 overflow-hidden relative'>
               {discountPercentage > 0 && (
                  <Badge className='absolute top-2 left-2 z-10 bg-secondaryColor hover:bg-secondaryColor'>
                     -{discountPercentage}%
                  </Badge>
               )}
               {product.stock <= 5 && product.stock > 0 && (
                  <Badge className='absolute top-2 right-2 z-10 bg-amber-500 hover:bg-amber-500'>Sắp hết hàng</Badge>
               )}
               {product.stock === 0 && (
                  <Badge className='absolute top-2 right-2 z-10 bg-gray-500 hover:bg-gray-500'>Hết hàng</Badge>
               )}
               <Image
                  src={'/placeholder.svg'}
                  alt={product.name || 'Product image'}
                  width={300}
                  height={300}
                  className='w-full h-full object-cover transition-transform group-hover:scale-105'
               />
            </div>
         </Link>
         <CardContent className='p-4'>
            <div className='flex items-center gap-1 mb-1.5'>
               <Badge variant='outline' className='text-xs font-normal px-1.5 py-0 h-5 bg-muted/50'>
                  {product.categoryName || 'Danh mục'}
               </Badge>
               <Badge variant='outline' className='text-xs font-normal px-1.5 py-0 h-5 bg-muted/50'>
                  {product.brand || 'Thương hiệu'}
               </Badge>
            </div>
            <Link href={productUrl}>
               <h3 className='font-medium text-sm line-clamp-2 min-h-[40px] group-hover:text-primaryColor transition-colors'>
                  {product.name || 'Tên sản phẩm'}
               </h3>
            </Link>
            <div className='mt-2 flex items-center justify-between'>
               <div>
                  <div className='text-secondaryColor font-bold'>
                     {formatCurrency(product.discountedPrice || product.price || 0)}
                  </div>
                  {product.discountedPrice && product.price && product.discountedPrice < product.price && (
                     <div className='text-gray-500 text-xs line-through'>{formatCurrency(product.price)}</div>
                  )}
               </div>
               {product.averageRating > 0 && (
                  <div className='flex items-center text-xs bg-yellow-50 px-1.5 py-0.5 rounded'>
                     <Star className='h-3 w-3 fill-yellow-400 text-yellow-400 mr-0.5' />
                     <span className='font-medium'>{product.averageRating.toFixed(1)}</span>
                  </div>
               )}
            </div>
         </CardContent>
         <CardFooter className='p-4 pt-0'>
            <Button
               variant='outline'
               size='sm'
               className='w-full border-primaryColor text-primaryColor hover:bg-primaryColor hover:text-white transition-colors'
            >
               <ShoppingCart className='h-4 w-4 mr-2' />
               Thêm vào giỏ
            </Button>
         </CardFooter>
      </Card>
   )
}

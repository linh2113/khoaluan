import { ProductType } from '@/types/product.type'
import { formatCurrency, generateNameId } from '@/lib/utils'
import Image from 'next/image'
import Link from 'next/link'
import { Card, CardContent, CardFooter } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Heart, ShoppingCart, Star, Scale } from 'lucide-react'
import { Badge } from '@/components/ui/badge'
import { useAddToCart } from '@/queries/useCart'
import { useAppContext } from '@/context/app.context'
import { useAddToWishlist, useCheckProductInWishlist, useRemoveFromWishlist } from '@/queries/useWishlist'
import { useState } from 'react'
import { toast } from 'react-toastify'
import { useTranslations } from 'next-intl'

interface ProductCardProps {
   product: ProductType
   onSelectForCompare?: (product: ProductType) => void
   isSelectedForCompare?: boolean
}

export default function ProductCard({ product, onSelectForCompare, isSelectedForCompare = false }: ProductCardProps) {
   const { userId } = useAppContext()
   const addToCart = useAddToCart()
   const addToWishlist = useAddToWishlist()
   const removeFromWishlist = useRemoveFromWishlist()
   const { data: isInWishlist } = useCheckProductInWishlist(userId || 0, product.id)
   const [isWishlistLoading, setIsWishlistLoading] = useState(false)
   const t = useTranslations('ProductCard')

   // Xử lý thêm/xóa sản phẩm khỏi danh sách yêu thích
   const handleWishlistToggle = (e: React.MouseEvent) => {
      e.preventDefault()
      e.stopPropagation()

      if (!userId) {
         toast.warning(t('loginRequired'))
         return
      }

      setIsWishlistLoading(true)

      if (isInWishlist?.data.data) {
         removeFromWishlist.mutate({ userId, productId: product.id }, { onSettled: () => setIsWishlistLoading(false) })
      } else {
         addToWishlist.mutate({ userId, productId: product.id }, { onSettled: () => setIsWishlistLoading(false) })
      }
   }

   // Đảm bảo product.name và product.id tồn tại trước khi tạo URL
   const productUrl = product?.name && product?.id ? `/${generateNameId({ name: product.name, id: product.id })}` : '#'

   // Tính phần trăm giảm giá
   const discountPercentage =
      product.discountedPrice && product.price && product.discountedPrice < product.price
         ? Math.round(((product.price - product.discountedPrice) / product.price) * 100)
         : 0

   return (
      <Card className='overflow-hidden bg-secondary transition-all hover:shadow-lg group border border-border/40'>
         <Link href={productUrl}>
            <div className='h-52 overflow-hidden relative'>
               {discountPercentage > 0 && (
                  <Badge className='absolute top-2 left-2 z-10 bg-secondaryColor hover:bg-secondaryColor'>
                     -{discountPercentage}%
                  </Badge>
               )}
               {product.stock <= 5 && product.stock > 0 && (
                  <Badge className='absolute top-2 right-2 z-10 bg-amber-500 hover:bg-amber-500'>{t('lowStock')}</Badge>
               )}
               {product.stock === 0 && (
                  <Badge className='absolute top-2 right-2 z-10 bg-gray-500 hover:bg-gray-500'>{t('outOfStock')}</Badge>
               )}
               <Image
                  src={product.image}
                  alt={product.name || t('productName')}
                  width={300}
                  height={300}
                  className='w-full h-full object-cover aspect-square transition-transform group-hover:scale-105'
               />
            </div>
         </Link>
         <CardContent className='p-4 relative'>
            {/* Nút yêu thích */}
            <button
               onClick={handleWishlistToggle}
               disabled={isWishlistLoading}
               className='absolute top-2 right-2 z-10 p-1.5 bg-white/80 rounded-full shadow-sm hover:bg-white transition-colors'
            >
               <Heart
                  className={`h-5 w-5 ${isInWishlist?.data.data ? 'fill-red-500 text-red-500' : 'text-gray-400'}`}
               />
            </button>
            <div className='flex items-center gap-1 mb-1.5'>
               <Badge variant='outline' className='text-xs font-normal px-1.5 py-0 h-5 bg-primary-foreground'>
                  {product.categoryName || t('category')}
               </Badge>
               <Badge variant='outline' className='text-xs font-normal px-1.5 py-0 h-5 bg-primary-foreground'>
                  {product.brandName || t('brand')}
               </Badge>
            </div>
            <Link href={productUrl}>
               <h3 className='font-medium text-sm line-clamp-2 min-h-[40px] group-hover:text-primaryColor transition-colors'>
                  {product.name || t('productName')}
               </h3>
            </Link>
            <div className='mt-2 flex items-center justify-between'>
               <div className='flex items-center gap-2'>
                  <div className='text-secondaryColor font-bold'>
                     {formatCurrency(product.discountedPrice || product.price || 0)}
                  </div>
                  {product.discountedPrice && product.price && product.discountedPrice < product.price && (
                     <div className='text-gray-500 text-xs line-through'>{formatCurrency(product.price)}</div>
                  )}
               </div>
               {product.averageRating > 0 && (
                  <div className='flex items-center text-xs px-1.5 py-0.5 rounded'>
                     <Star className='h-3 w-3 fill-yellow-400 text-yellow-400 mr-0.5' />
                     <span className='font-medium'>{product.averageRating.toFixed(1)}</span>
                  </div>
               )}
            </div>
         </CardContent>
         <CardFooter className='p-4 pt-0 flex flex-col gap-2'>
            <Button
               onClick={() => addToCart.mutate({ userId: userId!, productId: product.id, quantity: 1 })}
               variant='outline'
               size='sm'
               className='w-full border-primaryColor text-primaryColor hover:bg-primaryColor hover:text-white transition-colors'
            >
               <ShoppingCart className='h-4 w-4 mr-2' />
               {t('addToCart')}
            </Button>

            {onSelectForCompare && (
               <Button
                  onClick={() => onSelectForCompare(product)}
                  variant={isSelectedForCompare ? 'default' : 'outline'}
                  size='sm'
                  className={`w-full ${isSelectedForCompare ? 'bg-primaryColor' : 'border-gray-300'}`}
               >
                  <Scale className='h-4 w-4 mr-2' />
                  {isSelectedForCompare ? t('selected') : t('compare')}
               </Button>
            )}
         </CardFooter>
      </Card>
   )
}

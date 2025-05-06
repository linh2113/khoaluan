'use client'
import ProductRating from '@/components/product-rating'
import QuantityController from '@/components/quantity-controller'
import { formatCurrency, formatNumberToK, getIdFromNameId } from '@/lib/utils'
import { ChevronLeft, ChevronRight, Heart, ShoppingBasket } from 'lucide-react'
import Image from 'next/image'
import React, { useState, useRef, useEffect, useMemo } from 'react'
import { Textarea } from '@/components/ui/textarea'
import { useGetProduct } from '@/queries/useProduct'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { Separator } from '@/components/ui/separator'
import { useAddToCart } from '@/queries/useCart'
import { useAppContext } from '@/context/app.context'
import { useAddToWishlist, useCheckProductInWishlist, useRemoveFromWishlist } from '@/queries/useWishlist'
import { toast } from 'react-toastify'

export default function ProductDetail({ id }: { id: string }) {
   const { userId } = useAppContext()
   const [buyCount, setBuyCount] = useState<number>(1)
   const handleBuyCount = (value: number) => {
      setBuyCount(value)
   }
   const addToCart = useAddToCart()
   const addToWishlist = useAddToWishlist()
   const removeFromWishlist = useRemoveFromWishlist()
   const { data: isInWishlist } = useCheckProductInWishlist(userId || 0, Number(getIdFromNameId(id)))
   const [isWishlistLoading, setIsWishlistLoading] = useState(false)
   const { data, isLoading } = useGetProduct(Number(getIdFromNameId(id)))
   const product = data?.data.data

   // State cho hình ảnh chính đang được hiển thị
   const [activeImageIndex, setActiveImageIndex] = useState(0)
   const [currentIndexImg, setCurrentIndexImg] = useState([0, 5])
   const imageRef = useRef<HTMLImageElement>(null)

   // Lấy danh sách ảnh hiện tại để hiển thị trong carousel
   const currentImages = useMemo(
      () => (product?.imageUrls ? product.imageUrls.slice(currentIndexImg[0], currentIndexImg[1]) : []),
      [product, currentIndexImg]
   )

   // Cập nhật activeImageIndex khi product thay đổi
   useEffect(() => {
      if (product?.imageUrls && product.imageUrls.length > 0) {
         setActiveImageIndex(0)
      }
   }, [product])

   // Hàm xử lý khi click vào hình ảnh nhỏ
   const handleThumbnailClick = (index: number) => {
      // Cập nhật index của hình ảnh chính
      setActiveImageIndex(currentIndexImg[0] + index)
   }

   // Hàm điều hướng carousel
   const next = () => {
      if (product?.imageUrls && currentIndexImg[1] < product.imageUrls.length) {
         setCurrentIndexImg((prev) => [prev[0] + 5, prev[1] + 5])
      }
   }

   const prev = () => {
      if (currentIndexImg[0] > 0) {
         setCurrentIndexImg((prev) => [Math.max(0, prev[0] - 5), Math.max(5, prev[1] - 5)])
      }
   }

   // Xử lý zoom hình ảnh khi hover
   const handleImageZoom = (event: React.MouseEvent<HTMLDivElement>) => {
      if (!imageRef.current) return

      const { left, top, width, height } = event.currentTarget.getBoundingClientRect()
      const x = (event.clientX - left) / width
      const y = (event.clientY - top) / height

      imageRef.current.style.transformOrigin = `${x * 100}% ${y * 100}%`
      imageRef.current.style.transform = 'scale(1.5)'
      imageRef.current.style.cursor = 'zoom-in'
   }

   const handleImageZoomLeave = () => {
      if (!imageRef.current) return
      imageRef.current.style.transform = 'scale(1)'
   }

   // Xử lý thêm/xóa sản phẩm khỏi danh sách yêu thích
   const handleWishlistToggle = () => {
      if (!userId) {
         toast.warning('Vui lòng đăng nhập để sử dụng tính năng này')
         return
      }

      setIsWishlistLoading(true)

      if (isInWishlist?.data.data) {
         removeFromWishlist.mutate(
            { userId, productId: Number(getIdFromNameId(id)) },
            { onSettled: () => setIsWishlistLoading(false) }
         )
      } else {
         addToWishlist.mutate(
            { userId, productId: Number(getIdFromNameId(id)) },
            { onSettled: () => setIsWishlistLoading(false) }
         )
      }
   }

   // Tính phần trăm giảm giá
   const discountPercentage =
      product?.discountedPrice && product?.price && product.discountedPrice < product.price
         ? Math.round(((product.price - product.discountedPrice) / product.price) * 100)
         : 0

   if (isLoading) {
      return (
         <div className='my-5 container'>
            <div className='bg-secondary rounded-lg p-5 flex flex-col md:flex-row gap-10'>
               <div className='w-full md:w-[40%] flex flex-col gap-4'>
                  <Skeleton className='aspect-square w-full h-auto rounded-md' />
                  <div className='grid grid-cols-5 gap-3'>
                     {Array(5)
                        .fill(0)
                        .map((_, index) => (
                           <Skeleton key={index} className='aspect-square w-full h-auto rounded-md' />
                        ))}
                  </div>
               </div>
               <div className='w-full md:w-[60%] flex flex-col gap-5'>
                  <Skeleton className='h-8 w-3/4' />
                  <Skeleton className='h-5 w-1/3' />
                  <Skeleton className='h-12 w-1/2' />
                  <Skeleton className='h-10 w-full' />
                  <Skeleton className='h-12 w-full' />
               </div>
            </div>
         </div>
      )
   }

   if (!product) {
      return (
         <div className='my-5 container'>
            <div className='bg-secondary rounded-lg p-5 text-center py-20'>
               <h2 className='text-2xl font-medium mb-4'>Không tìm thấy sản phẩm</h2>
               <p className='text-muted-foreground'>Sản phẩm không tồn tại hoặc đã bị xóa.</p>
            </div>
         </div>
      )
   }

   return (
      <div className='my-5 container'>
         <div className='bg-secondary rounded-lg p-5 flex flex-col md:flex-row gap-10'>
            <div className='w-full md:w-[40%] flex flex-col gap-2'>
               {/* Hình ảnh chính */}
               <div
                  onMouseMove={handleImageZoom}
                  onMouseLeave={handleImageZoomLeave}
                  className='relative aspect-square w-full overflow-hidden rounded-md bg-white'
               >
                  {discountPercentage > 0 && (
                     <Badge className='absolute top-2 left-2 z-10 bg-secondaryColor hover:bg-secondaryColor'>
                        -{discountPercentage}%
                     </Badge>
                  )}
                  {product.imageUrls && product.imageUrls.length > 0 ? (
                     <Image
                        ref={imageRef}
                        src={product.imageUrls[activeImageIndex]}
                        alt={product.name}
                        width={500}
                        height={500}
                        className='h-full w-full object-contain transition-transform duration-300'
                     />
                  ) : (
                     <Image
                        src={product.image || '/placeholder.svg'}
                        alt={product.name}
                        width={500}
                        height={500}
                        className='h-full w-full object-contain'
                     />
                  )}
               </div>

               {/* Hình ảnh nhỏ */}
               {product.imageUrls && product.imageUrls.length > 0 && (
                  <div className='relative'>
                     <div className='grid grid-cols-5 gap-2'>
                        {currentImages.map((imageUrl, index) => (
                           <div
                              key={index}
                              className={`relative aspect-square cursor-pointer overflow-hidden rounded-md border-2 ${
                                 currentIndexImg[0] + index === activeImageIndex
                                    ? 'border-primaryColor'
                                    : 'border-transparent hover:border-gray-300'
                              }`}
                              onClick={() => handleThumbnailClick(index)}
                           >
                              <Image
                                 src={imageUrl}
                                 alt={`${product.name} - ảnh ${index + 1}`}
                                 width={100}
                                 height={100}
                                 className='h-full w-full object-cover'
                              />
                           </div>
                        ))}
                     </div>

                     {/* Nút điều hướng */}
                     {product.imageUrls.length > 5 && (
                        <>
                           <button
                              onClick={prev}
                              disabled={currentIndexImg[0] <= 0}
                              className={`absolute -left-3 top-1/2 flex h-8 w-8 -translate-y-1/2 items-center justify-center rounded-full bg-white shadow-md ${
                                 currentIndexImg[0] <= 0 ? 'cursor-not-allowed opacity-50' : 'hover:bg-gray-100'
                              }`}
                           >
                              <ChevronLeft className='h-5 w-5' />
                           </button>
                           <button
                              onClick={next}
                              disabled={currentIndexImg[1] >= product.imageUrls.length}
                              className={`absolute -right-3 top-1/2 flex h-8 w-8 -translate-y-1/2 items-center justify-center rounded-full bg-white shadow-md ${
                                 currentIndexImg[1] >= product.imageUrls.length
                                    ? 'cursor-not-allowed opacity-50'
                                    : 'hover:bg-gray-100'
                              }`}
                           >
                              <ChevronRight className='h-5 w-5' />
                           </button>
                        </>
                     )}
                  </div>
               )}
            </div>

            <div className='w-full md:w-[60%] flex flex-col gap-5'>
               {/* Tên sản phẩm */}
               <h1 className='font-medium text-2xl'>{product.name}</h1>

               {/* Đánh giá và số lượng bán */}
               <div className='flex items-center gap-3 text-base'>
                  <ProductRating
                     classNameStar1='w-5 h-5 fill-yellow-300 text-yellow-300'
                     classNameStar2='w-5 h-5 fill-current text-gray-300'
                     rating={product.averageRating || 0}
                  />
                  <span>({product.reviewCount || 0} đánh giá)</span>|
                  <span>{formatNumberToK(product.stock || 0)} đã bán</span>
               </div>

               {/* Giá */}
               <div className='bg-background p-5 rounded flex items-center gap-5'>
                  {product.discountedPrice && product.discountedPrice < product.price ? (
                     <>
                        <span className='line-through text-base text-gray-500'>{formatCurrency(product.price)}</span>
                        <span className='text-secondaryColor text-2xl font-medium'>
                           {formatCurrency(product.discountedPrice)}
                        </span>
                        <span className='rounded-sm bg-secondaryColor px-1 py-[2px] text-xs font-semibold text-white'>
                           {discountPercentage}% GIẢM
                        </span>
                     </>
                  ) : (
                     <span className='text-secondaryColor text-2xl font-medium'>{formatCurrency(product.price)}</span>
                  )}
               </div>

               {/* Thông tin cơ bản */}
               <div className='bg-background p-4 rounded space-y-2 text-sm'>
                  <div className='grid grid-cols-12'>
                     <span className='col-span-3 text-gray-500'>Thương hiệu:</span>
                     <span className='col-span-9 font-medium'>{product.brandName || 'Đang cập nhật'}</span>
                  </div>
                  <div className='grid grid-cols-12'>
                     <span className='col-span-3 text-gray-500'>Danh mục:</span>
                     <span className='col-span-9 font-medium'>{product.categoryName || 'Đang cập nhật'}</span>
                  </div>
                  <div className='grid grid-cols-12'>
                     <span className='col-span-3 text-gray-500'>Bảo hành:</span>
                     <span className='col-span-9 font-medium'>{product.warranty || 'Đang cập nhật'}</span>
                  </div>
                  <div className='grid grid-cols-12'>
                     <span className='col-span-3 text-gray-500'>Tình trạng:</span>
                     <span className='col-span-9 font-medium'>
                        {product.stock > 0 ? (
                           product.stock <= 5 ? (
                              <span className='text-amber-500'>Sắp hết hàng (còn {product.stock} sản phẩm)</span>
                           ) : (
                              <span className='text-green-500'>Còn hàng ({product.stock} sản phẩm)</span>
                           )
                        ) : (
                           <span className='text-red-500'>Hết hàng</span>
                        )}
                     </span>
                  </div>
               </div>

               {/* Số lượng */}
               <div className='flex items-center gap-4 text-base'>
                  <span>Số lượng</span>
                  <QuantityController
                     value={buyCount}
                     max={product.stock}
                     onIncrease={handleBuyCount}
                     onDecrease={handleBuyCount}
                     onType={handleBuyCount}
                  />
                  <span>{product.stock} sản phẩm có sẵn</span>
               </div>

               {/* Nút mua hàng */}
               <div className='flex items-center gap-4 text-base'>
                  <button
                     onClick={() => addToCart.mutate({ userId: userId!, productId: product.id, quantity: buyCount })}
                     className='px-5 py-3 flex items-center gap-2 rounded border border-secondaryColor bg-secondaryColor/10 hover:bg-secondaryColor/0 text-secondaryColor'
                     disabled={product.stock === 0}
                  >
                     <ShoppingBasket />
                     Thêm vào giỏ hàng
                  </button>
                  <button
                     className='px-5 py-3 bg-secondaryColor text-white rounded hover:bg-secondaryColor/90'
                     disabled={product.stock === 0}
                  >
                     Mua ngay
                  </button>
                  <button
                     onClick={handleWishlistToggle}
                     disabled={isWishlistLoading}
                     className={`p-3 rounded-full border ${
                        isInWishlist?.data.data
                           ? 'border-red-500 bg-red-50 text-red-500'
                           : 'border-gray-300 hover:border-red-500 hover:bg-red-50 hover:text-red-500'
                     }`}
                     aria-label={isInWishlist?.data.data ? 'Xóa khỏi yêu thích' : 'Thêm vào yêu thích'}
                  >
                     <Heart className={isInWishlist?.data.data ? 'fill-red-500' : ''} />
                  </button>
               </div>
            </div>
         </div>

         {/* Thông số kỹ thuật */}
         {product.productDetail && (
            <div className='bg-secondary rounded-lg p-5 mt-5'>
               <h2 className='font-medium text-xl mb-5 p-3 rounded-lg bg-background'>Thông số kỹ thuật</h2>
               <div className='grid grid-cols-1 md:grid-cols-2 gap-4'>
                  {product.productDetail.processor && (
                     <div className='p-3 border rounded-lg bg-background'>
                        <span className='font-medium'>CPU:</span> {product.productDetail.processor}
                     </div>
                  )}
                  {product.productDetail.ram && (
                     <div className='p-3 border rounded-lg bg-background'>
                        <span className='font-medium'>RAM:</span> {product.productDetail.ram}
                     </div>
                  )}
                  {product.productDetail.storage && (
                     <div className='p-3 border rounded-lg bg-background'>
                        <span className='font-medium'>Bộ nhớ:</span> {product.productDetail.storage}
                     </div>
                  )}
                  {product.productDetail.display && (
                     <div className='p-3 border rounded-lg bg-background'>
                        <span className='font-medium'>Màn hình:</span> {product.productDetail.display}
                     </div>
                  )}
                  {product.productDetail.graphics && (
                     <div className='p-3 border rounded-lg bg-background'>
                        <span className='font-medium'>Card đồ họa:</span> {product.productDetail.graphics}
                     </div>
                  )}
                  {product.productDetail.battery && (
                     <div className='p-3 border rounded-lg bg-background'>
                        <span className='font-medium'>Pin:</span> {product.productDetail.battery}
                     </div>
                  )}
                  {product.productDetail.camera && (
                     <div className='p-3 border rounded-lg bg-background'>
                        <span className='font-medium'>Camera:</span> {product.productDetail.camera}
                     </div>
                  )}
                  {product.productDetail.operatingSystem && (
                     <div className='p-3 border rounded-lg bg-background'>
                        <span className='font-medium'>Hệ điều hành:</span> {product.productDetail.operatingSystem}
                     </div>
                  )}
                  {product.productDetail.connectivity && (
                     <div className='p-3 border rounded-lg bg-background'>
                        <span className='font-medium'>Kết nối:</span> {product.productDetail.connectivity}
                     </div>
                  )}
               </div>
            </div>
         )}

         {/* Mô tả sản phẩm */}
         <div className='bg-secondary rounded-lg p-5 mt-5'>
            <h2 className='font-medium text-xl mb-5 p-3 rounded-lg bg-background'>Mô Tả Sản Phẩm</h2>
            <div className='prose max-w-none'>
               {product.description ? (
                  <p className='whitespace-pre-line'>{product.description}</p>
               ) : (
                  <p className='text-muted-foreground italic'>Chưa có mô tả cho sản phẩm này.</p>
               )}
            </div>
         </div>

         {/* Đánh giá sản phẩm */}
         <div className='bg-secondary rounded-lg p-5 mt-5'>
            <h2 className='font-medium text-xl mb-5 p-3 rounded-lg bg-background'>Đánh giá sản phẩm</h2>
            <div className='space-y-4'>
               {product.reviewCount > 0 ? (
                  // Hiển thị đánh giá thực tế (giả lập)
                  Array(2)
                     .fill(0)
                     .map((_, index) => (
                        <div key={index} className='p-3 border rounded-lg'>
                           <div className='flex items-center gap-2 mb-2'>
                              <ProductRating
                                 rating={4.5}
                                 classNameStar1='w-5 h-5 fill-yellow-300 text-yellow-300'
                                 classNameStar2='w-5 h-5 fill-current text-gray-300'
                              />
                              <span className='text-sm text-gray-500'>1 ngày trước</span>
                           </div>
                           <p>Chất lượng sản phẩm tốt, màu sắc đẹp, giao hàng nhanh.</p>
                        </div>
                     ))
               ) : (
                  <div className='text-center py-8'>
                     <p className='text-muted-foreground'>Chưa có đánh giá nào cho sản phẩm này.</p>
                  </div>
               )}
            </div>

            <Separator className='my-6' />

            {/* Form đánh giá */}
            <div className='flex flex-col gap-4 mt-6'>
               <div className='flex items-center gap-2'>
                  <label>Đánh giá:</label>
                  <ProductRating
                     rating={0} // Giá trị tĩnh
                     classNameStar1='w-5 h-5 fill-yellow-300 text-yellow-300'
                     classNameStar2='w-5 h-5 fill-current text-gray-300'
                  />
               </div>
               <Textarea placeholder='Nhập đánh giá của bạn' className='border-primary' />
               <button className='px-4 py-2 bg-secondaryColor text-white rounded hover:bg-secondaryColor/90 self-start'>
                  Gửi đánh giá
               </button>
            </div>
         </div>

         {/* Sản phẩm liên quan */}
         <div className='bg-secondary rounded-lg p-5 mt-5'>
            <h2 className='font-medium text-xl mb-5 p-3 rounded-lg bg-background'>Có thể bạn cũng thích</h2>
         </div>
      </div>
   )
}

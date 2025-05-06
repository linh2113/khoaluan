'use client'
import ProductRating from '@/components/product-rating'
import QuantityController from '@/components/quantity-controller'
import { formatCurrency, formatNumberToK, getIdFromNameId } from '@/lib/utils'
import { ChevronLeft, ChevronRight, Heart, ShoppingBasket, Star } from 'lucide-react'
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
import {
   FacebookShareButton,
   TwitterShareButton,
   LinkedinShareButton,
   TelegramShareButton,
   WhatsappShareButton,
   FacebookIcon,
   TwitterIcon,
   LinkedinIcon,
   TelegramIcon,
   WhatsappIcon
} from 'react-share'
import {
   useCreateRating,
   useGetRatingsByProductId,
   useReplyToRating,
   useGetRepliesByParentId
} from '@/queries/useRating'
import { RatingDTO } from '@/types/rating.type'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { format } from 'date-fns'
import { vi } from 'date-fns/locale'
import { Reply } from 'lucide-react'
import { Button } from '@/components/ui/button'
import Link from 'next/link'
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

   // Thêm state và hooks cho chức năng đánh giá
   const [ratingValue, setRatingValue] = useState<number>(5)
   const [ratingComment, setRatingComment] = useState<string>('')
   const [replyText, setReplyText] = useState<string>('')
   const [replyingTo, setReplyingTo] = useState<number | null>(null)

   const productId = Number(getIdFromNameId(id))
   const { data: ratingsData, isLoading: isRatingsLoading, refetch } = useGetRatingsByProductId(productId)
   const ratings = ratingsData?.data.data || []

   const createRating = useCreateRating()
   const replyToRating = useReplyToRating()

   // Hàm xử lý gửi đánh giá
   const handleSubmitRating = () => {
      if (!userId) {
         toast.warning('Vui lòng đăng nhập để đánh giá sản phẩm')
         return
      }

      if (ratingComment.trim() === '') {
         toast.warning('Vui lòng nhập nội dung đánh giá')
         return
      }

      const ratingData: RatingDTO = {
         rating: ratingValue,
         comment: ratingComment.trim()
      }

      createRating.mutate(
         {
            userId,
            productId,
            rating: ratingData
         },
         {
            onSuccess: () => {
               setRatingComment('')
               setRatingValue(5)
            },
            onError: () => {
               toast.warning('Bạn đã đánh giá sản phẩm này rồi')
            }
         }
      )
   }

   // Hàm xử lý gửi phản hồi đánh giá
   const handleSubmitReply = (parentId: number) => {
      if (!userId) {
         toast.warning('Vui lòng đăng nhập để phản hồi đánh giá')
         return
      }

      if (replyText.trim() === '') {
         toast.warning('Vui lòng nhập nội dung phản hồi')
         return
      }

      replyToRating.mutate(
         {
            userId,
            parentRatingId: parentId,
            reply: { comment: replyText.trim() }
         },
         {
            onSuccess: () => {
               setReplyText('')
               refetch()
               setReplyingTo(null)
            }
         }
      )
   }

   // Lấy URL hiện tại cho việc chia sẻ
   const shareUrl = typeof window !== 'undefined' ? window.location.href : ''
   const shareTitle = product?.name || 'Sản phẩm tuyệt vời'
   const shareDescription = product?.description?.substring(0, 100) || 'Xem chi tiết sản phẩm tại đây'

   // State cho hình ảnh chính đang được hiển thị
   const [activeImageIndex, setActiveImageIndex] = useState(0)
   const [currentIndexImg, setCurrentIndexImg] = useState([0, 5])
   const imageRef = useRef<HTMLImageElement>(null)

   // Lấy danh sách ảnh hiện tại để hiển thị trong carousel
   const currentImages = useMemo(
      () => (product?.productImages ? product.productImages.slice(currentIndexImg[0], currentIndexImg[1]) : []),
      [product, currentIndexImg]
   )

   // Cập nhật activeImageIndex khi product thay đổi
   useEffect(() => {
      if (product?.productImages && product.productImages.length > 0) {
         setActiveImageIndex(0)
      }
   }, [product])

   // Hàm xử lý khi click vào hình ảnh nhỏ
   const handleThumbnailClick = (index: number) => {
      // Cập nhật index của hình ảnh chính
      setActiveImageIndex(currentIndexImg[0] + index)
   }

   // Hàm điều hướng carousel - sửa lại để chỉ dịch chuyển 1 tấm ảnh mỗi lần
   const next = () => {
      if (product?.productImages && currentIndexImg[1] < product.productImages.length) {
         // Dịch chuyển chỉ 1 tấm ảnh mỗi lần
         setCurrentIndexImg((prev) => [prev[0] + 1, prev[1] + 1])
      }
   }

   const prev = () => {
      if (currentIndexImg[0] > 0) {
         // Dịch chuyển chỉ 1 tấm ảnh mỗi lần
         setCurrentIndexImg((prev) => [prev[0] - 1, prev[1] - 1])
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
               <div className='w-full md:w-1/2 flex flex-col gap-4'>
                  <Skeleton className='aspect-square w-full h-auto rounded-md' />
                  <div className='grid grid-cols-5 gap-3'>
                     {Array(5)
                        .fill(0)
                        .map((_, index) => (
                           <Skeleton key={index} className='aspect-square w-full h-auto rounded-md' />
                        ))}
                  </div>
               </div>
               <div className='w-full md:w-1/2 flex flex-col gap-5'>
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
            <div className='w-full md:w-1/2 flex flex-col gap-2'>
               {/* Hình ảnh chính */}
               <div
                  onMouseMove={handleImageZoom}
                  onMouseLeave={handleImageZoomLeave}
                  className='relative w-full overflow-hidden rounded-md'
               >
                  {discountPercentage > 0 && (
                     <Badge className='absolute top-2 left-2 z-10 bg-secondaryColor hover:bg-secondaryColor'>
                        -{discountPercentage}%
                     </Badge>
                  )}
                  {product.productImages && product.productImages.length > 0 ? (
                     <Image
                        ref={imageRef}
                        src={product.productImages[activeImageIndex].imageUrl}
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
               {product.productImages && product.productImages.length > 0 && (
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
                                 src={imageUrl.imageUrl}
                                 alt={`${product.name} - ảnh ${index + 1}`}
                                 width={100}
                                 height={100}
                                 className='h-full w-full object-cover'
                              />
                           </div>
                        ))}
                     </div>

                     {/* Nút điều hướng - hiển thị khi có nhiều hơn 5 ảnh */}
                     {product.productImages.length > 5 && (
                        <>
                           <button
                              onClick={prev}
                              disabled={currentIndexImg[0] <= 0}
                              className={`absolute -left-3 top-1/2 flex h-8 w-8 -translate-y-1/2 items-center justify-center rounded-full bg-white shadow-md ${
                                 currentIndexImg[0] <= 0 ? 'cursor-not-allowed opacity-50' : 'hover:bg-gray-100'
                              }`}
                              aria-label='Ảnh trước'
                           >
                              <ChevronLeft className='h-5 w-5 text-primaryColor' />
                           </button>
                           <button
                              onClick={next}
                              disabled={currentIndexImg[1] >= product.productImages.length}
                              className={`absolute -right-3 top-1/2 flex h-8 w-8 -translate-y-1/2 items-center justify-center rounded-full bg-white shadow-md ${
                                 currentIndexImg[1] >= product.productImages.length
                                    ? 'cursor-not-allowed opacity-50'
                                    : 'hover:bg-gray-100'
                              }`}
                              aria-label='Ảnh tiếp theo'
                           >
                              <ChevronRight className='h-5 w-5 text-primaryColor' />
                           </button>
                        </>
                     )}
                  </div>
               )}
            </div>

            <div className='w-full md:w-1/2 flex flex-col gap-5'>
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

               {/* Chia sẻ mạng xã hội */}
               <div className='flex gap-2'>
                  <FacebookShareButton url={shareUrl} hashtag={shareTitle} className='focus:outline-none'>
                     <FacebookIcon size={32} round />
                  </FacebookShareButton>

                  <TwitterShareButton url={shareUrl} title={shareTitle} className='focus:outline-none'>
                     <TwitterIcon size={32} round />
                  </TwitterShareButton>

                  <LinkedinShareButton
                     url={shareUrl}
                     title={shareTitle}
                     summary={shareDescription}
                     className='focus:outline-none'
                  >
                     <LinkedinIcon size={32} round />
                  </LinkedinShareButton>

                  <TelegramShareButton url={shareUrl} title={shareTitle} className='focus:outline-none'>
                     <TelegramIcon size={32} round />
                  </TelegramShareButton>

                  <WhatsappShareButton url={shareUrl} title={shareTitle} separator=' - ' className='focus:outline-none'>
                     <WhatsappIcon size={32} round />
                  </WhatsappShareButton>
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

            {/* Hiển thị danh sách đánh giá */}
            <div className='space-y-4 mb-6'>
               {isRatingsLoading ? (
                  <div className='flex justify-center py-4'>
                     <div className='animate-spin rounded-full h-8 w-8 border-b-2 border-primaryColor'></div>
                  </div>
               ) : ratings.length > 0 ? (
                  ratings.map((rating) => (
                     <div key={rating.id} className='p-4 border rounded-lg bg-background'>
                        <div className='flex items-start gap-3'>
                           <Avatar className='h-10 w-10'>
                              <AvatarImage src={rating.userPicture} alt={rating.userName} />
                              <AvatarFallback>{rating.userName?.charAt(0) || 'U'}</AvatarFallback>
                           </Avatar>
                           <div className='flex-1'>
                              <div className='flex items-center gap-2'>
                                 <span className='font-medium'>{rating.userName}</span>
                                 <span className='text-xs text-gray-500'>
                                    {rating.createAt &&
                                       format(new Date(rating.createAt), 'dd/MM/yyyy HH:mm', { locale: vi })}
                                 </span>
                              </div>
                              <div className='flex items-center gap-2 my-1'>
                                 <ProductRating
                                    rating={rating.rating}
                                    classNameStar1='w-4 h-4 fill-yellow-300 text-yellow-300'
                                    classNameStar2='w-4 h-4 fill-current text-gray-300'
                                 />
                              </div>
                              <p className='mt-1'>{rating.comment}</p>

                              {/* Nút trả lời */}
                              {userId && (
                                 <button
                                    onClick={() => setReplyingTo(replyingTo === rating.id ? null : rating.id!)}
                                    className='mt-2 text-sm flex items-center gap-1 text-gray-500 hover:text-primaryColor'
                                 >
                                    <Reply className='h-4 w-4' />
                                    Trả lời
                                 </button>
                              )}

                              {/* Form trả lời */}
                              {replyingTo === rating.id && (
                                 <div className='mt-3 space-y-2'>
                                    <Textarea
                                       placeholder='Nhập phản hồi của bạn'
                                       value={replyText}
                                       onChange={(e) => setReplyText(e.target.value)}
                                       className='min-h-[80px]'
                                    />
                                    <div className='flex justify-end gap-2'>
                                       <Button
                                          variant='outline'
                                          size='sm'
                                          onClick={() => {
                                             setReplyingTo(null)
                                             setReplyText('')
                                          }}
                                       >
                                          Hủy
                                       </Button>
                                       <Button
                                          size='sm'
                                          onClick={() => handleSubmitReply(rating.id!)}
                                          disabled={replyToRating.isPending}
                                       >
                                          {replyToRating.isPending ? 'Đang gửi...' : 'Gửi phản hồi'}
                                       </Button>
                                    </div>
                                 </div>
                              )}

                              {/* Hiển thị các phản hồi */}
                              {rating.replies && rating.replies.length > 0 && (
                                 <div className='mt-3 space-y-3 pl-4 border-l-2 border-gray-200'>
                                    {rating.replies.map((reply) => (
                                       <div key={reply.id} className='pt-3'>
                                          <div className='flex items-start gap-3'>
                                             <Avatar className='h-8 w-8'>
                                                <AvatarImage src={reply.userPicture} alt={reply.userName} />
                                                <AvatarFallback>{reply.userName?.charAt(0) || 'U'}</AvatarFallback>
                                             </Avatar>
                                             <div>
                                                <div className='flex items-center gap-2'>
                                                   <span className='font-medium'>{reply.userName}</span>
                                                   <span className='text-xs text-gray-500'>
                                                      {reply.createAt &&
                                                         format(new Date(reply.createAt), 'dd/MM/yyyy HH:mm', {
                                                            locale: vi
                                                         })}
                                                   </span>
                                                </div>
                                                <p className='mt-1'>{reply.comment}</p>
                                             </div>
                                          </div>
                                       </div>
                                    ))}
                                 </div>
                              )}
                           </div>
                        </div>
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
               <h3 className='font-medium'>Viết đánh giá của bạn</h3>

               {!userId ? (
                  <div className='bg-blue-50 text-blue-700 p-4 rounded-md'>
                     Vui lòng{' '}
                     <Link href='/login' className='font-medium underline'>
                        đăng nhập
                     </Link>{' '}
                     để đánh giá sản phẩm
                  </div>
               ) : (
                  <>
                     <div className='flex items-center gap-2'>
                        <label>Đánh giá:</label>
                        <div className='flex items-center'>
                           {[1, 2, 3, 4, 5].map((star) => (
                              <button
                                 key={star}
                                 type='button'
                                 onClick={() => setRatingValue(star)}
                                 className='focus:outline-none'
                              >
                                 <Star
                                    className={`w-6 h-6 ${
                                       star <= ratingValue
                                          ? 'fill-yellow-300 text-yellow-300'
                                          : 'fill-current text-gray-300'
                                    }`}
                                 />
                              </button>
                           ))}
                        </div>
                        <span className='text-sm text-gray-500 ml-2'>({ratingValue}/5)</span>
                     </div>
                     <Textarea
                        placeholder='Nhập đánh giá của bạn về sản phẩm này'
                        value={ratingComment}
                        onChange={(e) => setRatingComment(e.target.value)}
                        className='min-h-[120px] border-primary'
                     />
                     <Button onClick={handleSubmitRating} disabled={createRating.isPending} className='self-start'>
                        {createRating.isPending ? 'Đang gửi...' : 'Gửi đánh giá'}
                     </Button>
                  </>
               )}
            </div>
         </div>

         {/* Sản phẩm liên quan */}
         <div className='bg-secondary rounded-lg p-5 mt-5'>
            <h2 className='font-medium text-xl mb-5 p-3 rounded-lg bg-background'>Có thể bạn cũng thích</h2>
         </div>
      </div>
   )
}

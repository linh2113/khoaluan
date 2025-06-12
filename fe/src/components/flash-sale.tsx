'use client'
import { useGetProductFlashSale } from '@/queries/useFlashSale'
import React, { useEffect, useState } from 'react'
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from '@/components/ui/carousel'
import Autoplay from 'embla-carousel-autoplay'
import { useTranslations } from 'next-intl'
import { Card, CardContent, CardFooter } from '@/components/ui/card'
import Link from 'next/link'
import { formatCurrency, generateNameId } from '@/lib/utils'
import { Badge } from '@/components/ui/badge'
import Image from 'next/image'
import { ShoppingCart, Zap } from 'lucide-react'
import { useAddToCart } from '@/queries/useCart'
import { Button } from '@/components/ui/button'
import { useAppContext } from '@/context/app.context'
// Countdown Timer Component
function CountdownTimer({ startDate, endDate }: { startDate: string; endDate: string }) {
   const [timeRemaining, setTimeRemaining] = useState<{
      days: number
      hours: number
      minutes: number
      seconds: number
   }>({
      days: 0,
      hours: 0,
      minutes: 0,
      seconds: 0
   })

   useEffect(() => {
      const calculateTimeRemaining = () => {
         const now = new Date()
         const start = new Date(startDate)
         const end = new Date(endDate)

         // Check if discount period hasn't started yet
         if (now < start) {
            const diff = start.getTime() - now.getTime()
            return {
               days: Math.floor(diff / (1000 * 60 * 60 * 24)),
               hours: Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
               minutes: Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60)),
               seconds: Math.floor((diff % (1000 * 60)) / 1000)
            }
         }
         // Check if discount period is active
         else if (now >= start && now <= end) {
            const diff = end.getTime() - now.getTime()
            return {
               days: Math.floor(diff / (1000 * 60 * 60 * 24)),
               hours: Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
               minutes: Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60)),
               seconds: Math.floor((diff % (1000 * 60)) / 1000)
            }
         }
         // Discount period has expired
         else {
            return {
               days: 0,
               hours: 0,
               minutes: 0,
               seconds: 0
            }
         }
      }

      // Initial calculation
      setTimeRemaining(calculateTimeRemaining())

      // Update countdown every second
      const timer = setInterval(() => {
         setTimeRemaining(calculateTimeRemaining())
      }, 1000)

      return () => clearInterval(timer)
   }, [startDate, endDate])

   // Format number to always have two digits
   const formatNumber = (num: number) => num.toString().padStart(2, '0')

   // Render different content based on discount status
   return (
      <div className='text-center'>
         <div className='inline-flex flex-col items-center bg-background p-4 rounded-md border border-amber-200'>
            <div className='text-red-600 font-medium mb-1 flex items-center gap-1'>
               <Zap size={16} className='animate-pulse' />
               Khuyến mãi kết thúc sau
            </div>
            <div className='flex items-center gap-2 text-center'>
               <div className='rounded-md p-2 min-w-[40px]'>
                  <div className='text-lg font-bold'>{formatNumber(timeRemaining.days)}</div>
                  <div className='text-xs'>Ngày</div>
               </div>
               <div className='text-lg font-bold'>:</div>
               <div className=' rounded-md p-2 min-w-[40px]'>
                  <div className='text-lg font-bold'>{formatNumber(timeRemaining.hours)}</div>
                  <div className='text-xs'>Giờ</div>
               </div>
               <div className='text-lg font-bold'>:</div>
               <div className=' rounded-md p-2 min-w-[40px]'>
                  <div className='text-lg font-bold'>{formatNumber(timeRemaining.minutes)}</div>
                  <div className='text-xs'>Phút</div>
               </div>
               <div className='text-lg font-bold'>:</div>
               <div className=' rounded-md p-2 min-w-[40px]'>
                  <div className='text-lg font-bold'>{formatNumber(timeRemaining.seconds)}</div>
                  <div className='text-xs'>Giây</div>
               </div>
            </div>
         </div>
      </div>
   )
}
export default function FlashSale() {
   const { userId } = useAppContext()
   const { data } = useGetProductFlashSale()
   const t = useTranslations('ProductCard')
   const addToCart = useAddToCart()
   const productFlashSale = data?.data.data[0]
   if (!productFlashSale) return
   return (
      <div className='mb-10'>
         <div className='text-2xl font-bold mb-5'>
            <Image src={'/flashsale.png'} alt='' className='w-[200px] h-[89px]' width={200} height={89} />
         </div>
         <CountdownTimer startDate={productFlashSale?.startTime!} endDate={productFlashSale?.endTime!} />
         <Carousel
            plugins={[
               Autoplay({
                  delay: 4000
               })
            ]}
            className='w-full'
            opts={{
               loop: true
            }}
         >
            <CarouselContent>
               {productFlashSale?.items?.map((item) => (
                  <CarouselItem key={item.id} className='basis-1/2 md:basis-1/4'>
                     <Card className='overflow-hidden bg-secondary transition-all hover:shadow-lg group border border-border/40'>
                        <Link href={`/${generateNameId({ name: item.productName, id: item.productId })}`}>
                           <div className='h-52 overflow-hidden relative'>
                              {item.discountPercentage > 0 && (
                                 <Badge className='absolute top-2 left-2 z-10 bg-secondaryColor hover:bg-secondaryColor text-white'>
                                    -{item.discountPercentage.toFixed(0)}%
                                 </Badge>
                              )}
                              {item.availableStock <= 5 && item.availableStock > 0 && (
                                 <Badge className='absolute top-2 right-2 z-10 bg-amber-500 hover:bg-amber-500'>
                                    {t('lowStock')}
                                 </Badge>
                              )}
                              {item.availableStock === 0 && (
                                 <Badge className='absolute top-2 right-2 z-10 bg-secondaryColor text-white hover:bg-secondaryColor hover:text-white'>
                                    {t('outOfStock')}
                                 </Badge>
                              )}

                              <Image
                                 src={item.productImage}
                                 alt={item.productName || t('productName')}
                                 width={300}
                                 height={300}
                                 className='w-full h-full object-cover aspect-square transition-transform group-hover:scale-105'
                              />
                           </div>
                        </Link>
                        <CardContent className='p-4 relative'>
                           <Link href={`/${generateNameId({ name: item.productName, id: item.productId })}`}>
                              <h3 className='font-medium text-sm line-clamp-2 min-h-[40px] group-hover:text-primaryColor transition-colors'>
                                 {item.productName || t('productName')}
                              </h3>
                           </Link>
                           <div className='mt-2 flex items-center justify-between'>
                              <div className='flex items-center gap-2'>
                                 <div className='text-gray-500 text-xs line-through'>
                                    {formatCurrency(item.originalPrice)}
                                 </div>
                                 <div className='text-secondaryColor font-bold'>{formatCurrency(item.flashPrice)}</div>
                              </div>
                           </div>
                        </CardContent>
                        <CardFooter className='p-4 pt-0 flex flex-col gap-2 relative'>
                           <Image
                              src={'/fire.png'}
                              alt=''
                              className='w-6 h-7 absolute left-3 -top-1 z-50'
                              width={32}
                              height={36}
                           />
                           <div className='relative w-full h-6 rounded-full overflow-hidden bg-gray-400'>
                              {/* Gradient background chiếm theo % số suất còn */}
                              <div
                                 className='absolute top-0 left-0 h-full'
                                 style={{
                                    width: `${(item.availableStock / item.stockLimit) * 100}%`,
                                    background: 'linear-gradient(to right, #fdf494, #FACC15)' // yellow-100 to yellow-400
                                 }}
                              />
                              {/* Text hiển thị thông tin */}
                              <div className='absolute text-black left-1/2 -translate-x-1/2 text-center z-10 flex items-center h-full px-2 text-sm font-medium'>
                                 Còn {item.availableStock}/{item.stockLimit} suất
                              </div>
                           </div>

                           <Button
                              onClick={() =>
                                 addToCart.mutate({ userId: userId!, productId: item.productId, quantity: 1 })
                              }
                              variant='outline'
                              size='sm'
                              className='w-full border-primaryColor text-primaryColor hover:bg-primaryColor hover:text-white transition-colors'
                           >
                              <ShoppingCart className='h-4 w-4 mr-2' />
                              {t('addToCart')}
                           </Button>
                        </CardFooter>
                     </Card>
                  </CarouselItem>
               ))}
            </CarouselContent>
            <CarouselPrevious />
            <CarouselNext />
         </Carousel>
      </div>
   )
}

'use client'
import React, { useState } from 'react'
import Autoplay from 'embla-carousel-autoplay'
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from '@/components/ui/carousel'
import Image from 'next/image'
import { Filter, ListFilter } from 'lucide-react'
import { Separator } from '@/components/ui/separator'
import { Input } from '@/components/ui/input'
import RatingStars from '@/components/rating-stars'
import { Button } from '@/components/ui/button'
import Paginate from '@/components/paginate'
import ProductItem from '@/components/product-item'
export default function Home() {
   const [currentPage, setCurrentPage] = useState<number>(1)
   return (
      <>
         <Carousel
            plugins={[
               Autoplay({
                  delay: 4000
               })
            ]}
         >
            <CarouselContent>
               <CarouselItem className='basis-1/2 aspect-[3/1] pl-4'>
                  <Image
                     src={'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H2_612x212_e05bf4a220.png'}
                     alt=''
                     priority
                     width={300}
                     height={100}
                     className='rounded-lg'
                  />
               </CarouselItem>
               <CarouselItem className='basis-1/2 aspect-[3/1] pl-4'>
                  <Image
                     src={'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H2_614x212_c6fb24bf6b.png'}
                     alt=''
                     priority
                     width={300}
                     height={100}
                     className='rounded-lg'
                  />
               </CarouselItem>
               <CarouselItem className='basis-1/2 aspect-[3/1] pl-4'>
                  <Image
                     src={'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H2_612x212_e05bf4a220.png'}
                     alt=''
                     priority
                     width={300}
                     height={100}
                     className='rounded-lg'
                  />
               </CarouselItem>
               <CarouselItem className='basis-1/2 aspect-[3/1] pl-4'>
                  <Image
                     src={'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H2_614x212_c6fb24bf6b.png'}
                     alt=''
                     priority
                     width={300}
                     height={100}
                     className='rounded-lg'
                  />
               </CarouselItem>
            </CarouselContent>
            <CarouselPrevious />
            <CarouselNext />
         </Carousel>
         <div className='mt-5 grid grid-cols-4 gap-3'>
            <div className='col-span-1 bg-secondary p-3 rounded-lg flex flex-col gap-4'>
               <div className='flex items-center uppercase gap-1 font-semibold text-base'>
                  <Filter size={20} strokeWidth={1.5} />
                  Bộ lọc tìm kiếm
               </div>
               <div className='flex flex-col gap-3'>
                  <p className='font-medium flex items-center gap-1'>
                     <ListFilter size={20} strokeWidth={1.5} />
                     Tất cả danh mục
                  </p>
                  <div className='ml-6 flex flex-col gap-2'>
                     <p>Điện thoại</p>
                     <p>Laptop</p>
                     <p>Tai nghe</p>
                  </div>
               </div>
               <Separator />
               <div className='flex flex-col gap-3'>
                  <p className='font-medium'>Khoảng giá</p>
                  <div className='flex items-center gap-3'>
                     <Input className='border-primary' placeholder='₫ TỪ' />-
                     <Input className='border-primary' placeholder='₫ ĐẾN' />
                  </div>
                  <button className='w-full bg-primaryColor text-white h-9 rounded button-primary'>Áp dụng</button>
               </div>
               <Separator />
               <div className='flex flex-col gap-3'>
                  <p className='font-medium'>Đánh giá</p>
                  <RatingStars />
               </div>
               <Separator />
               <button className='w-full bg-primaryColor text-white h-9 rounded button-primary'>Xoá tất cả</button>
            </div>
            <div className='col-span-3 flex flex-col gap-3'>
               <div className='bg-secondary rounded-lg p-3 flex items-center gap-3'>
                  <span>Sắp xếp theo</span>
                  {/* <button className='px-5 py-2 bg-primaryColor text-white button-primary rounded'>Tất cả</button>
                  <button className='px-5 py-2 bg-primaryColor text-white button-primary rounded'>Phổ biến</button>
                  <button className='px-5 py-2 bg-primaryColor text-white button-primary rounded'>Bán chạy</button>
                  <button className='px-5 py-2 bg-primaryColor text-white button-primary rounded'>Tất cả</button> */}
                  <Button>Tất cả</Button>
                  <Button>Phổ biến</Button>
                  <Button>Bán chạy</Button>
                  <Button>Giá thấp đến cao</Button>
                  <Button>Giá cao đến thấp</Button>
               </div>
               <div className='bg-secondary rounded-lg'>
                  <div className='grid grid-cols-5'>
                     {Array(10)
                        .fill(0)
                        .map((item, index) => (
                           <ProductItem key={index} />
                        ))}
                  </div>
                  <Paginate
                     currentPage={currentPage}
                     setCurrentPage={setCurrentPage}
                     totalPages={10}
                     handlePageClick={() => 1}
                  />
               </div>
            </div>
         </div>
      </>
   )
}

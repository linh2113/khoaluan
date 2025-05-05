'use client'
import React, { useState, useEffect } from 'react'
import Autoplay from 'embla-carousel-autoplay'
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from '@/components/ui/carousel'
import Image from 'next/image'
import { useGetAllProducts } from '@/queries/useProduct'
import { GetProductQueryParamsType } from '@/types/product.type'
import Paginate from '@/components/paginate'
import ProductCard from '@/components/product-card'
import { Skeleton } from '@/components/ui/skeleton'
import ProductFilter from '@/components/product-filter'
import { ShoppingBag, Star, TrendingUp, Zap } from 'lucide-react'
import { Separator } from '@/components/ui/separator'
import { useGetAllCategories } from '@/queries/useCategory'

const mockBrands = ['Apple', 'Samsung', 'Xiaomi', 'Dell', 'HP', 'Asus', 'Lenovo']

export default function Home() {
   const getAllCategories = useGetAllCategories()
   const categories = getAllCategories.data?.data.data || []
   const [currentPage, setCurrentPage] = useState<number>(1)
   const [queryParams, setQueryParams] = useState<GetProductQueryParamsType>({
      page: currentPage - 1,
      size: 3,
      sortBy: 'id',
      sortDir: 'desc'
   })

   const { data, isLoading } = useGetAllProducts(queryParams)
   const products = data?.data.data.content || []
   const totalPages = data?.data.data.totalPages || 0

   // Cập nhật page trong queryParams khi currentPage thay đổi
   useEffect(() => {
      setQueryParams((prev) => ({
         ...prev,
         page: currentPage - 1
      }))
   }, [currentPage])

   const handlePageClick = (e: { selected: number }) => {
      setCurrentPage(e.selected + 1)
   }

   const handleFilterChange = (newFilters: GetProductQueryParamsType) => {
      // Reset về trang 1 khi thay đổi filter
      setCurrentPage(1)
      setQueryParams({
         ...newFilters,
         page: 0
      })
   }

   // Hiển thị icon tương ứng với loại sản phẩm
   const renderFilterTypeIcon = () => {
      switch (queryParams.filterType) {
         case 'TOP_SELLING':
            return <ShoppingBag className='h-6 w-6 mr-2 text-primaryColor' />
         case 'NEW_ARRIVALS':
            return <Zap className='h-6 w-6 mr-2 text-primaryColor' />
         case 'TOP_RATED':
            return <Star className='h-6 w-6 mr-2 text-primaryColor' />
         case 'DISCOUNTED':
            return <TrendingUp className='h-6 w-6 mr-2 text-primaryColor' />
         default:
            return null
      }
   }

   return (
      <div className='container py-6'>
         {/* Banner Carousel */}
         <div className='mb-8 rounded-xl overflow-hidden shadow-md'>
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
                  <CarouselItem className='basis-full md:basis-1/2 aspect-[3/1]'>
                     <Image
                        src={'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H2_612x212_e05bf4a220.png'}
                        alt='Banner 1'
                        priority
                        width={1200}
                        height={400}
                        className='rounded-lg w-full h-full object-cover'
                     />
                  </CarouselItem>
                  <CarouselItem className='basis-full md:basis-1/2 aspect-[3/1]'>
                     <Image
                        src={'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H2_614x212_c6fb24bf6b.png'}
                        alt='Banner 2'
                        priority
                        width={1200}
                        height={400}
                        className='rounded-lg w-full h-full object-cover'
                     />
                  </CarouselItem>
                  <CarouselItem className='basis-full md:basis-1/2 aspect-[3/1]'>
                     <Image
                        src={'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H2_612x212_e05bf4a220.png'}
                        alt='Banner 3'
                        priority
                        width={1200}
                        height={400}
                        className='rounded-lg w-full h-full object-cover'
                     />
                  </CarouselItem>
                  <CarouselItem className='basis-full md:basis-1/2 aspect-[3/1]'>
                     <Image
                        src={'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:quality(100)/H2_614x212_c6fb24bf6b.png'}
                        alt='Banner 4'
                        priority
                        width={1200}
                        height={400}
                        className='rounded-lg w-full h-full object-cover'
                     />
                  </CarouselItem>
               </CarouselContent>
               <CarouselPrevious className='left-4' />
               <CarouselNext className='right-4' />
            </Carousel>
         </div>

         {/* Layout chính: Filter bên trái, Sản phẩm bên phải */}
         <div className='flex flex-col lg:flex-row gap-6'>
            {/* Bộ lọc sản phẩm - Cột bên trái */}
            <div className='w-full lg:w-1/4 lg:sticky lg:top-4 lg:self-start '>
               <ProductFilter
                  initialFilters={queryParams}
                  onFilterChange={handleFilterChange}
                  brands={mockBrands}
                  categories={categories}
                  maxPriceValue={50000000}
               />
            </div>

            {/* Danh sách sản phẩm - Cột bên phải */}
            <div className='w-full lg:w-3/4'>
               <div className='flex items-center mb-6'>
                  {renderFilterTypeIcon()}
                  <h2 className='text-2xl font-bold'>
                     {queryParams.filterType === 'TOP_SELLING' && 'Sản phẩm bán chạy'}
                     {queryParams.filterType === 'NEW_ARRIVALS' && 'Sản phẩm mới'}
                     {queryParams.filterType === 'TOP_RATED' && 'Sản phẩm đánh giá cao'}
                     {queryParams.filterType === 'DISCOUNTED' && 'Sản phẩm giảm giá'}
                     {(!queryParams.filterType || queryParams.filterType === 'ALL') && 'Tất cả sản phẩm'}
                  </h2>
               </div>

               <Separator className='mb-6' />

               {isLoading ? (
                  <div className='grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6'>
                     {Array(9)
                        .fill(0)
                        .map((_, index) => (
                           <div key={index} className='border rounded-xl p-4 shadow-sm'>
                              <Skeleton className='h-48 w-full mb-4 rounded-lg' />
                              <Skeleton className='h-4 w-1/3 mb-2' />
                              <Skeleton className='h-5 w-3/4 mb-4' />
                              <Skeleton className='h-4 w-1/2 mb-2' />
                              <Skeleton className='h-8 w-full mt-4' />
                           </div>
                        ))}
                  </div>
               ) : (
                  <>
                     {products.length > 0 ? (
                        <div className='grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6'>
                           {products.map((product) => (
                              <ProductCard key={product.id} product={product} />
                           ))}
                        </div>
                     ) : (
                        <div className='text-center py-16 bg-muted/30 rounded-xl'>
                           <Image
                              src='/placeholder.svg'
                              alt='No products found'
                              width={120}
                              height={120}
                              className='mx-auto mb-4 opacity-50'
                           />
                           <p className='text-muted-foreground text-lg'>
                              Không tìm thấy sản phẩm nào phù hợp với bộ lọc.
                           </p>
                        </div>
                     )}
                  </>
               )}

               {totalPages > 0 && (
                  <div className='mt-8'>
                     <Paginate
                        totalPages={totalPages}
                        currentPage={currentPage}
                        handlePageClick={handlePageClick}
                        setCurrentPage={setCurrentPage}
                     />
                  </div>
               )}
            </div>
         </div>
      </div>
   )
}

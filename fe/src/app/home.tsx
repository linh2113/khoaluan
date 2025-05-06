'use client'
import React, { useState, useEffect } from 'react'
import Autoplay from 'embla-carousel-autoplay'
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from '@/components/ui/carousel'
import Image from 'next/image'
import { useGetAllProducts } from '@/queries/useProduct'
import { GetProductQueryParamsType, ProductType } from '@/types/product.type'
import Paginate from '@/components/paginate'
import ProductCard from '@/components/product-card'
import { Skeleton } from '@/components/ui/skeleton'
import ProductFilter from '@/components/product-filter'
import { ShoppingBag, Star, TrendingUp, Zap, Scale } from 'lucide-react'
import { Separator } from '@/components/ui/separator'
import { useGetAllCategories } from '@/queries/useCategory'
import { Button } from '@/components/ui/button'
import { useRouter } from 'next/navigation'
import { toast } from 'react-toastify'
import { useGetAllBrand } from '@/queries/useAdmin'

export default function Home() {
   const router = useRouter()
   // Lấy danh sách thương hiệu sản phẩm
   const getAllBrand = useGetAllBrand({})
   const brands = getAllBrand.data?.data.data.content || []

   // Lấy danh sách danh mục sản phẩm
   const getAllCategories = useGetAllCategories()
   const categories = getAllCategories.data?.data.data || []
   const [currentPage, setCurrentPage] = useState<number>(1)
   const [queryParams, setQueryParams] = useState<GetProductQueryParamsType>({
      page: currentPage - 1,
      size: 6,
      sortBy: 'id',
      sortDir: 'desc'
   })
   const [selectedProducts, setSelectedProducts] = useState<ProductType[]>([])

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

   // Xử lý chọn sản phẩm để so sánh
   const handleSelectProductForCompare = (product: ProductType) => {
      // Kiểm tra nếu sản phẩm đã được chọn
      if (selectedProducts.some((p) => p.id === product.id)) {
         setSelectedProducts((prev) => prev.filter((p) => p.id !== product.id))
         return
      }

      // Kiểm tra số lượng sản phẩm đã chọn
      if (selectedProducts.length >= 4) {
         toast.warning('Chỉ có thể so sánh tối đa 4 sản phẩm')
         return
      }

      // Kiểm tra danh mục sản phẩm
      if (selectedProducts.length > 0 && selectedProducts[0].categoryId !== product.categoryId) {
         toast.warning('Chỉ có thể so sánh các sản phẩm cùng danh mục')
         return
      }

      setSelectedProducts((prev) => [...prev, product])
   }

   // Xử lý so sánh sản phẩm
   const handleCompareProducts = () => {
      if (selectedProducts.length < 2) {
         toast.warning('Vui lòng chọn ít nhất 2 sản phẩm để so sánh')
         return
      }

      // Chuyển đến trang so sánh với danh sách ID sản phẩm
      const productIds = selectedProducts.map((p) => p.id).join(',')
      router.push(`/compare?ids=${productIds}`)
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
                  brands={brands}
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
                              <ProductCard
                                 key={product.id}
                                 product={product}
                                 onSelectForCompare={handleSelectProductForCompare}
                                 isSelectedForCompare={selectedProducts.some((p) => p.id === product.id)}
                              />
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

         {/* Hiển thị thanh so sánh sản phẩm khi có sản phẩm được chọn */}
         {selectedProducts.length > 0 && (
            <div className='fixed bottom-0 left-0 right-0 bg-primary-foreground shadow-lg border-t p-4 z-50'>
               <div className='container mx-auto flex items-center justify-between'>
                  <div className='flex items-center gap-4'>
                     <Scale className='h-5 w-5 text-primaryColor' />
                     <span className='font-medium'>So sánh sản phẩm ({selectedProducts.length}/4)</span>
                     <div className='flex items-center gap-5'>
                        {selectedProducts.map((product) => (
                           <div
                              key={product.id}
                              className='relative aspect-square w-20
                           '
                           >
                              <Image
                                 src={product.imageUrls?.[0] || '/placeholder.svg'}
                                 alt={product.name}
                                 width={40}
                                 height={40}
                                 className='rounded border'
                              />
                              <button
                                 onClick={() => setSelectedProducts((prev) => prev.filter((p) => p.id !== product.id))}
                                 className='absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs'
                              >
                                 ×
                              </button>
                           </div>
                        ))}
                     </div>
                  </div>
                  <div className='flex items-center gap-2'>
                     <Button variant='outline' size='sm' onClick={() => setSelectedProducts([])}>
                        Xóa tất cả
                     </Button>
                     <Button
                        variant='default'
                        size='sm'
                        onClick={handleCompareProducts}
                        disabled={selectedProducts.length < 2}
                     >
                        So sánh ngay
                     </Button>
                  </div>
               </div>
            </div>
         )}
      </div>
   )
}

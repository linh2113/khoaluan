'use client'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Slider } from '@/components/ui/slider'
import { Switch } from '@/components/ui/switch'
import { Checkbox } from '@/components/ui/checkbox'
import type { GetProductQueryParamsType } from '@/types/product.type'
import { useState, useEffect, useRef } from 'react'
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible'
import {
   ChevronDown,
   ChevronUp,
   Filter,
   SlidersHorizontal,
   Tag,
   Smartphone,
   Star,
   TrendingUp,
   RotateCcw
} from 'lucide-react'
import { Separator } from '@/components/ui/separator'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import type { BrandType, CategoryType } from '@/types/admin.type'
import { useTranslations } from 'next-intl'
import Image from 'next/image'

interface ProductFilterProps {
   initialFilters: GetProductQueryParamsType
   onFilterChange: (filters: GetProductQueryParamsType) => void
   brands: BrandType[]
   categories: CategoryType[]
   maxPriceValue?: number
}

function useDebounce(value: any, duration = 500) {
   const [debounceValue, setDebounceValue] = useState(value)
   useEffect(() => {
      const timer = setTimeout(() => {
         setDebounceValue(value)
      }, duration)
      return () => {
         clearTimeout(timer)
      }
   }, [value])
   return debounceValue
}

export default function ProductFilter({
   initialFilters,
   onFilterChange,
   brands,
   categories,
   maxPriceValue = 50000000
}: ProductFilterProps) {
   const [filters, setFilters] = useState<GetProductQueryParamsType>(initialFilters)
   const [priceRange, setPriceRange] = useState<[number, number]>([0, maxPriceValue])
   const [selectedBrands, setSelectedBrands] = useState<string[]>([])
   const [selectedCategories, setSelectedCategories] = useState<number[]>([])

   // Trạng thái mở/đóng của từng section
   const [openSections, setOpenSections] = useState({
      category: true,
      brand: true,
      price: true,
      type: true,
      sort: false,
      options: false
   })

   const t = useTranslations('ProductFilter')

   const debouncedFilters = useDebounce(
      {
         ...filters,
         minPrice: priceRange[0],
         maxPrice: priceRange[1]
      },
      500
   )

   useEffect(() => {
      setFilters(initialFilters)
   }, [initialFilters])

   const prevFiltersRef = useRef<GetProductQueryParamsType>()

   useEffect(() => {
      const currentFilters = {
         ...filters,
         minPrice: priceRange[0],
         maxPrice: priceRange[1]
      }

      if (prevFiltersRef.current && JSON.stringify(prevFiltersRef.current) !== JSON.stringify(currentFilters)) {
         onFilterChange(currentFilters)
      }

      prevFiltersRef.current = currentFilters
   }, [debouncedFilters])

   const handleFilterChange = (name: keyof GetProductQueryParamsType, value: any) => {
      setFilters((prev) => ({ ...prev, [name]: value }))
   }

   const handlePriceChange = (value: number[]) => {
      setPriceRange([value[0], value[1]])
   }

   const handleBrandChange = (brandName: string, checked: boolean) => {
      let newSelectedBrands: string[]
      if (checked) {
         newSelectedBrands = [...selectedBrands, brandName]
      } else {
         newSelectedBrands = selectedBrands.filter((b) => b !== brandName)
      }
      setSelectedBrands(newSelectedBrands)
      handleFilterChange('brand', newSelectedBrands.length > 0 ? newSelectedBrands.join(',') : undefined)
   }

   const handleCategoryChange = (categoryId: number, checked: boolean) => {
      let newSelectedCategories: number[]
      if (checked) {
         newSelectedCategories = [...selectedCategories, categoryId]
      } else {
         newSelectedCategories = selectedCategories.filter((c) => c !== categoryId)
      }
      setSelectedCategories(newSelectedCategories)
      handleFilterChange('categoryId', newSelectedCategories.length > 0 ? newSelectedCategories[0] : undefined)
   }

   const resetFilters = () => {
      const defaultFilters = {
         page: 0,
         size: initialFilters.size,
         sortBy: 'id',
         sortDir: 'desc'
      }
      setFilters(defaultFilters)
      setPriceRange([0, maxPriceValue])
      setSelectedBrands([])
      setSelectedCategories([])
      onFilterChange(defaultFilters)
   }

   const toggleSection = (section: keyof typeof openSections) => {
      setOpenSections((prev) => ({ ...prev, [section]: !prev[section] }))
   }

   const formatPrice = (price: number) => {
      return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
   }

   const getActiveFiltersCount = () => {
      let count = 0
      if (selectedBrands.length > 0) count++
      if (selectedCategories.length > 0) count++
      if (priceRange[0] > 0 || priceRange[1] < maxPriceValue) count++
      if (filters.filterType && filters.filterType !== 'ALL') count++
      if (filters.inStock) count++
      return count
   }

   return (
      <div className='space-y-4'>
         {/* Header với số lượng filter đang active */}
         <Card className='border-2 border-primary/20'>
            <CardHeader className='pb-3'>
               <div className='flex items-center justify-between'>
                  <div className='flex items-center gap-2'>
                     <SlidersHorizontal className='h-5 w-5 text-primary' />
                     <CardTitle className='text-lg'>Bộ lọc sản phẩm</CardTitle>
                     {getActiveFiltersCount() > 0 && (
                        <Badge variant='secondary' className='bg-primary/10 text-primary'>
                           {getActiveFiltersCount()}
                        </Badge>
                     )}
                  </div>
                  <Button
                     variant='ghost'
                     size='sm'
                     onClick={resetFilters}
                     className='text-muted-foreground hover:text-primary'
                  >
                     <RotateCcw className='h-4 w-4 mr-1' />
                     Đặt lại
                  </Button>
               </div>
            </CardHeader>
         </Card>

         {/* Danh mục sản phẩm */}
         <Card>
            <Collapsible open={openSections.category} onOpenChange={() => toggleSection('category')}>
               <CollapsibleTrigger asChild>
                  <CardHeader className='cursor-pointer hover:bg-muted/50 transition-colors pb-3'>
                     <div className='flex items-center justify-between'>
                        <div className='flex items-center gap-2'>
                           <Tag className='h-4 w-4 text-primary' />
                           <CardTitle className='text-base'>Danh mục </CardTitle>
                           {selectedCategories.length > 0 && (
                              <Badge variant='outline' className='text-xs'>
                                 {selectedCategories.length}
                              </Badge>
                           )}
                        </div>
                        {openSections.category ? (
                           <ChevronUp className='h-4 w-4' />
                        ) : (
                           <ChevronDown className='h-4 w-4' />
                        )}
                     </div>
                  </CardHeader>
               </CollapsibleTrigger>
               <CollapsibleContent>
                  <CardContent className='pt-0 space-y-3'>
                     {categories.slice(0, 8).map((category) => (
                        <div
                           key={category.id}
                           className='flex items-center space-x-2 p-2 rounded-lg hover:bg-muted/30 transition-colors'
                        >
                           <Checkbox
                              id={`category-${category.id}`}
                              checked={selectedCategories.includes(category.id)}
                              onCheckedChange={(checked) => handleCategoryChange(category.id, checked as boolean)}
                           />
                           <Label
                              htmlFor={`category-${category.id}`}
                              className='cursor-pointer flex-1 flex items-center gap-1 text-sm font-medium'
                           >
                              {category.imageUrl && (
                                 <Image
                                    src={category.imageUrl || ''}
                                    alt={category.categoryName}
                                    className='w-8 h-8 rounded'
                                    width={32}
                                    height={32}
                                 />
                              )}
                              {category.categoryName}
                           </Label>
                        </div>
                     ))}
                     {categories.length > 8 && (
                        <Button variant='ghost' size='sm' className='w-full text-primary'>
                           Xem thêm {categories.length - 8} danh mục
                        </Button>
                     )}
                  </CardContent>
               </CollapsibleContent>
            </Collapsible>
         </Card>

         {/* Thương hiệu */}
         <Card>
            <Collapsible open={openSections.brand} onOpenChange={() => toggleSection('brand')}>
               <CollapsibleTrigger asChild>
                  <CardHeader className='cursor-pointer hover:bg-muted/50 transition-colors pb-3'>
                     <div className='flex items-center justify-between'>
                        <div className='flex items-center gap-2'>
                           <Smartphone className='h-4 w-4 text-primary' />
                           <CardTitle className='text-base'>Thương hiệu</CardTitle>
                           {selectedBrands.length > 0 && (
                              <Badge variant='outline' className='text-xs'>
                                 {selectedBrands.length}
                              </Badge>
                           )}
                        </div>
                        {openSections.brand ? <ChevronUp className='h-4 w-4' /> : <ChevronDown className='h-4 w-4' />}
                     </div>
                  </CardHeader>
               </CollapsibleTrigger>
               <CollapsibleContent>
                  <CardContent className='pt-0 space-y-3'>
                     <div className='grid grid-cols-1 gap-2'>
                        {brands.slice(0, 10).map((brand) => (
                           <div
                              key={brand.id}
                              className='flex items-center space-x-2 p-2 rounded-lg hover:bg-muted/30 transition-colors'
                           >
                              <Checkbox
                                 id={`brand-${brand.id}`}
                                 checked={selectedBrands.includes(brand.brandName)}
                                 onCheckedChange={(checked) => handleBrandChange(brand.brandName, checked as boolean)}
                              />
                              <Label
                                 htmlFor={`brand-${brand.id}`}
                                 className='cursor-pointer flex items-center gap-1 flex-1 text-sm font-medium'
                              >
                                 {brand.imageUrl && (
                                    <Image
                                       src={brand.imageUrl || ''}
                                       alt={brand.brandName}
                                       className='w-8 h-8 rounded'
                                       width={32}
                                       height={32}
                                    />
                                 )}
                                 {brand.brandName}
                              </Label>
                           </div>
                        ))}
                     </div>
                     {brands.length > 10 && (
                        <Button variant='ghost' size='sm' className='w-full text-primary'>
                           Xem thêm {brands.length - 10} thương hiệu
                        </Button>
                     )}
                  </CardContent>
               </CollapsibleContent>
            </Collapsible>
         </Card>

         {/* Khoảng giá */}
         <Card>
            <Collapsible open={openSections.price} onOpenChange={() => toggleSection('price')}>
               <CollapsibleTrigger asChild>
                  <CardHeader className='cursor-pointer hover:bg-muted/50 transition-colors pb-3'>
                     <div className='flex items-center justify-between'>
                        <div className='flex items-center gap-2'>
                           <TrendingUp className='h-4 w-4 text-primary' />
                           <CardTitle className='text-base'>Khoảng giá</CardTitle>
                           {(priceRange[0] > 0 || priceRange[1] < maxPriceValue) && (
                              <Badge variant='outline' className='text-xs'>
                                 Đã chọn
                              </Badge>
                           )}
                        </div>
                        {openSections.price ? <ChevronUp className='h-4 w-4' /> : <ChevronDown className='h-4 w-4' />}
                     </div>
                  </CardHeader>
               </CollapsibleTrigger>
               <CollapsibleContent>
                  <CardContent className='pt-0 space-y-4'>
                     {/* Quick price buttons */}
                     <div className='grid grid-cols-2 gap-2'>
                        <Button
                           variant='outline'
                           size='sm'
                           onClick={() => setPriceRange([0, 5000000])}
                           className={
                              priceRange[0] === 0 && priceRange[1] === 5000000 ? 'border-primary bg-primary/10' : ''
                           }
                        >
                           Dưới 5 triệu
                        </Button>
                        <Button
                           variant='outline'
                           size='sm'
                           onClick={() => setPriceRange([5000000, 10000000])}
                           className={
                              priceRange[0] === 5000000 && priceRange[1] === 10000000
                                 ? 'border-primary bg-primary/10'
                                 : ''
                           }
                        >
                           5 - 10 triệu
                        </Button>
                        <Button
                           variant='outline'
                           size='sm'
                           onClick={() => setPriceRange([10000000, 20000000])}
                           className={
                              priceRange[0] === 10000000 && priceRange[1] === 20000000
                                 ? 'border-primary bg-primary/10'
                                 : ''
                           }
                        >
                           10 - 20 triệu
                        </Button>
                        <Button
                           variant='outline'
                           size='sm'
                           onClick={() => setPriceRange([20000000, maxPriceValue])}
                           className={
                              priceRange[0] === 20000000 && priceRange[1] === maxPriceValue
                                 ? 'border-primary bg-primary/10'
                                 : ''
                           }
                        >
                           Trên 20 triệu
                        </Button>
                     </div>

                     <Separator />

                     {/* Custom price slider */}
                     <div className='space-y-3'>
                        <Label className='text-sm font-medium'>Tùy chỉnh khoảng giá</Label>
                        <Slider
                           defaultValue={[0, maxPriceValue]}
                           max={maxPriceValue}
                           step={100000}
                           value={[priceRange[0], priceRange[1]]}
                           onValueChange={handlePriceChange}
                           className='w-full'
                        />
                        <div className='flex justify-between items-center'>
                           <div className='text-sm font-medium text-primary'>{formatPrice(priceRange[0])}</div>
                           <div className='text-sm font-medium text-primary'>{formatPrice(priceRange[1])}</div>
                        </div>
                     </div>
                  </CardContent>
               </CollapsibleContent>
            </Collapsible>
         </Card>

         {/* Loại sản phẩm */}
         <Card>
            <Collapsible open={openSections.type} onOpenChange={() => toggleSection('type')}>
               <CollapsibleTrigger asChild>
                  <CardHeader className='cursor-pointer hover:bg-muted/50 transition-colors pb-3'>
                     <div className='flex items-center justify-between'>
                        <div className='flex items-center gap-2'>
                           <Star className='h-4 w-4 text-primary' />
                           <CardTitle className='text-base'>Loại sản phẩm</CardTitle>
                           {filters.filterType && filters.filterType !== 'ALL' && (
                              <Badge variant='outline' className='text-xs'>
                                 Đã chọn
                              </Badge>
                           )}
                        </div>
                        {openSections.type ? <ChevronUp className='h-4 w-4' /> : <ChevronDown className='h-4 w-4' />}
                     </div>
                  </CardHeader>
               </CollapsibleTrigger>
               <CollapsibleContent>
                  <CardContent className='pt-0 space-y-0'>
                     {[
                        { value: 'ALL', label: 'Tất cả sản phẩm', icon: '📱' },
                        { value: 'TOP_SELLING', label: 'Bán chạy nhất', icon: '🔥' },
                        { value: 'NEW_ARRIVALS', label: 'Hàng mới về', icon: '✨' },
                        { value: 'TOP_RATED', label: 'Đánh giá cao', icon: '⭐' },
                        { value: 'DISCOUNTED', label: 'Đang giảm giá', icon: '💰' }
                     ].map((type) => (
                        <div
                           key={type.value}
                           className={`flex items-center space-x-3 p-3 rounded-lg cursor-pointer transition-colors ${
                              filters.filterType === type.value
                                 ? 'bg-primary/10 border border-primary/20'
                                 : 'hover:bg-muted/30'
                           }`}
                           onClick={() =>
                              handleFilterChange('filterType', type.value as GetProductQueryParamsType['filterType'])
                           }
                        >
                           <span className='text-lg'>{type.icon}</span>
                           <Label className='cursor-pointer flex-1 text-sm font-medium'>{type.label}</Label>
                           {filters.filterType === type.value && <div className='w-2 h-2 bg-primary rounded-full' />}
                        </div>
                     ))}
                  </CardContent>
               </CollapsibleContent>
            </Collapsible>
         </Card>

         {/* Sắp xếp */}
         <Card>
            <Collapsible open={openSections.sort} onOpenChange={() => toggleSection('sort')}>
               <CollapsibleTrigger asChild>
                  <CardHeader className='cursor-pointer hover:bg-muted/50 transition-colors pb-3'>
                     <div className='flex items-center justify-between'>
                        <div className='flex items-center gap-2'>
                           <Filter className='h-4 w-4 text-primary' />
                           <CardTitle className='text-base'>Sắp xếp theo</CardTitle>
                        </div>
                        {openSections.sort ? <ChevronUp className='h-4 w-4' /> : <ChevronDown className='h-4 w-4' />}
                     </div>
                  </CardHeader>
               </CollapsibleTrigger>
               <CollapsibleContent>
                  <CardContent className='pt-0'>
                     <Select
                        value={`${filters.sortBy || 'id'}-${filters.sortDir || 'desc'}`}
                        onValueChange={(value) => {
                           const [sortBy, sortDir] = value.split('-')
                           handleFilterChange('sortBy', sortBy)
                           handleFilterChange('sortDir', sortDir)
                        }}
                     >
                        <SelectTrigger className='w-full'>
                           <SelectValue placeholder='Chọn cách sắp xếp' />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='id-desc'>Mới nhất</SelectItem>
                           <SelectItem value='price-asc'>Giá thấp đến cao</SelectItem>
                           <SelectItem value='price-desc'>Giá cao đến thấp</SelectItem>
                           <SelectItem value='name-asc'>Tên A-Z</SelectItem>
                           <SelectItem value='name-desc'>Tên Z-A</SelectItem>
                        </SelectContent>
                     </Select>
                  </CardContent>
               </CollapsibleContent>
            </Collapsible>
         </Card>

         {/* Tùy chọn khác */}
         <Card>
            <Collapsible open={openSections.options} onOpenChange={() => toggleSection('options')}>
               <CollapsibleTrigger asChild>
                  <CardHeader className='cursor-pointer hover:bg-muted/50 transition-colors pb-3'>
                     <div className='flex items-center justify-between'>
                        <div className='flex items-center gap-2'>
                           <SlidersHorizontal className='h-4 w-4 text-primary' />
                           <CardTitle className='text-base'>Tùy chọn khác</CardTitle>
                           {filters.inStock && (
                              <Badge variant='outline' className='text-xs'>
                                 1
                              </Badge>
                           )}
                        </div>
                        {openSections.options ? <ChevronUp className='h-4 w-4' /> : <ChevronDown className='h-4 w-4' />}
                     </div>
                  </CardHeader>
               </CollapsibleTrigger>
               <CollapsibleContent>
                  <CardContent className='pt-0'>
                     <div className='flex items-center justify-between p-3 bg-muted/30 rounded-lg'>
                        <Label htmlFor='inStock' className='cursor-pointer text-sm font-medium'>
                           Chỉ hiển thị sản phẩm còn hàng
                        </Label>
                        <Switch
                           id='inStock'
                           checked={filters.inStock || false}
                           onCheckedChange={(value) => handleFilterChange('inStock', value)}
                        />
                     </div>
                  </CardContent>
               </CollapsibleContent>
            </Collapsible>
         </Card>
      </div>
   )
}

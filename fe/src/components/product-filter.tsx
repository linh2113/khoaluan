'use client'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Slider } from '@/components/ui/slider'
import { Switch } from '@/components/ui/switch'
import type { GetProductQueryParamsType } from '@/types/product.type'
import { useState, useEffect, useRef } from 'react'
import { Collapsible, CollapsibleContent } from '@/components/ui/collapsible'
import { ChevronDown, ChevronUp, Filter, SlidersHorizontal } from 'lucide-react'
import { Separator } from '@/components/ui/separator'
import type { BrandType, CategoryType } from '@/types/admin.type'
import { useTranslations } from 'next-intl'

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
   const [isOpen, setIsOpen] = useState(true) // Mặc định mở filter
   const t = useTranslations('ProductFilter')

   const debouncedFilters = useDebounce(
      {
         ...filters,
         minPrice: priceRange[0],
         maxPrice: priceRange[1]
      },
      500
   )

   // Cập nhật filters khi initialFilters thay đổi
   useEffect(() => {
      setFilters(initialFilters)
   }, [initialFilters])

   const prevFiltersRef = useRef<GetProductQueryParamsType>()

   // Automatically apply filters when debounced values change
   useEffect(() => {
      const currentFilters = {
         ...filters,
         minPrice: priceRange[0],
         maxPrice: priceRange[1]
      }

      // Only call onFilterChange if filters actually changed
      if (prevFiltersRef.current && JSON.stringify(prevFiltersRef.current) !== JSON.stringify(currentFilters)) {
         onFilterChange(currentFilters)
      }

      prevFiltersRef.current = currentFilters
   }, [debouncedFilters])

   // Xử lý khi thay đổi giá trị filter
   const handleFilterChange = (name: keyof GetProductQueryParamsType, value: any) => {
      setFilters((prev) => ({ ...prev, [name]: value }))
   }

   // Xử lý khi thay đổi khoảng giá
   const handlePriceChange = (value: number[]) => {
      setPriceRange([value[0], value[1]])
   }

   // Reset filter
   const resetFilters = () => {
      const defaultFilters = {
         page: 0,
         size: initialFilters.size,
         sortBy: 'id',
         sortDir: 'desc'
      }
      setFilters(defaultFilters)
      setPriceRange([0, maxPriceValue])
      onFilterChange(defaultFilters)
   }

   return (
      <div className='bg-secondary rounded-xl shadow-md overflow-hidden border border-border/40'>
         <div className='flex items-center justify-between p-4 bg-muted/30'>
            <div className='flex items-center gap-2'>
               <SlidersHorizontal className='h-5 w-5 text-primaryColor' />
               <h3 className='text-lg font-medium'>{t('title')}</h3>
            </div>
            <Button variant='ghost' size='sm' onClick={() => setIsOpen(!isOpen)} className='hover:bg-muted'>
               <Filter className='h-4 w-4 mr-2 text-primaryColor' />
               {isOpen ? <ChevronUp className='h-4 w-4' /> : <ChevronDown className='h-4 w-4' />}
            </Button>
         </div>

         <Collapsible open={isOpen} onOpenChange={setIsOpen}>
            <CollapsibleContent>
               <div className='p-4 pt-2 space-y-3'>
                  {/* Lọc theo danh mục */}
                  <div>
                     <Select
                        value={filters.categoryId?.toString() || 'all'}
                        onValueChange={(value) =>
                           handleFilterChange('categoryId', value === 'all' ? undefined : Number.parseInt(value))
                        }
                     >
                        <SelectTrigger id='category' className='bg-background w-full'>
                           <SelectValue placeholder={t('category.label')} />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='all'>{t('category.all')}</SelectItem>
                           {categories.map((category) => (
                              <SelectItem key={category.id} value={category.id.toString()}>
                                 {category.categoryName}
                              </SelectItem>
                           ))}
                        </SelectContent>
                     </Select>
                  </div>

                  {/* Lọc theo thương hiệu */}
                  <div>
                     <Select
                        value={filters.brand || 'all'}
                        onValueChange={(value) => handleFilterChange('brand', value === 'all' ? undefined : value)}
                     >
                        <SelectTrigger id='brand' className='bg-background w-full'>
                           <SelectValue placeholder={t('brand.label')} />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='all'>{t('brand.all')}</SelectItem>
                           {brands.map((brand) => (
                              <SelectItem key={brand.id} value={brand.brandName}>
                                 {brand.brandName}
                              </SelectItem>
                           ))}
                        </SelectContent>
                     </Select>
                  </div>

                  {/* Loại sản phẩm */}
                  <div>
                     <Select
                        value={filters.filterType || 'ALL'}
                        onValueChange={(value) =>
                           handleFilterChange('filterType', value as GetProductQueryParamsType['filterType'])
                        }
                     >
                        <SelectTrigger id='filterType' className='bg-background w-full'>
                           <SelectValue placeholder={t('filterType.label')} />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='ALL'>{t('filterType.all')}</SelectItem>
                           <SelectItem value='TOP_SELLING'>{t('filterType.topSelling')}</SelectItem>
                           <SelectItem value='NEW_ARRIVALS'>{t('filterType.newArrivals')}</SelectItem>
                           <SelectItem value='TOP_RATED'>{t('filterType.topRated')}</SelectItem>
                           <SelectItem value='DISCOUNTED'>{t('filterType.discounted')}</SelectItem>
                        </SelectContent>
                     </Select>
                  </div>

                  {/* Sắp xếp theo */}
                  <div>
                     <Select
                        value={`${filters.sortBy || 'id'}-${filters.sortDir || 'desc'}`}
                        onValueChange={(value) => {
                           const [sortBy, sortDir] = value.split('-')
                           handleFilterChange('sortBy', sortBy)
                           handleFilterChange('sortDir', sortDir)
                        }}
                     >
                        <SelectTrigger id='sort' className='bg-background w-full'>
                           <SelectValue placeholder={t('sort.label')} />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='id-desc'>{t('sort.newest')}</SelectItem>
                           <SelectItem value='price-asc'>{t('sort.priceAsc')}</SelectItem>
                           <SelectItem value='price-desc'>{t('sort.priceDesc')}</SelectItem>
                        </SelectContent>
                     </Select>
                  </div>

                  {/* Khoảng giá */}
                  <div className='px-2 pt-2'>
                     <Slider
                        defaultValue={[0, maxPriceValue]}
                        max={maxPriceValue}
                        step={100000}
                        value={[priceRange[0], priceRange[1]]}
                        onValueChange={handlePriceChange}
                        className='mb-2'
                     />
                     <div className='flex justify-between text-sm text-muted-foreground'>
                        <span>
                           {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(
                              priceRange[0]
                           )}
                        </span>
                        <span>
                           {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(
                              priceRange[1]
                           )}
                        </span>
                     </div>
                  </div>

                  {/* Các tùy chọn bổ sung */}
                  <div>
                     <div className='flex items-center space-x-2 bg-muted/30 p-3 rounded-lg'>
                        <Switch
                           id='isDiscount'
                           checked={filters.isDiscount || false}
                           onCheckedChange={(value) => handleFilterChange('isDiscount', value)}
                           className='data-[state=checked]:bg-secondaryColor'
                        />
                        <Label htmlFor='isDiscount' className='cursor-pointer'>
                           {t('options.showDiscounted')}
                        </Label>
                     </div>
                     <div className='flex items-center space-x-2 bg-muted/30 p-3 rounded-lg'>
                        <Switch
                           id='inStock'
                           checked={filters.inStock || false}
                           onCheckedChange={(value) => handleFilterChange('inStock', value)}
                           className='data-[state=checked]:bg-secondaryColor'
                        />
                        <Label htmlFor='inStock' className='cursor-pointer'>
                           {t('options.showInStock')}
                        </Label>
                     </div>
                  </div>

                  <Separator className='my-4' />

                  {/* Nút điều khiển */}
                  <div className='flex gap-3'>
                     <Button
                        variant='outline'
                        onClick={resetFilters}
                        className='border-primaryColor text-primaryColor hover:bg-primaryColor/10 flex-1'
                     >
                        {t('buttons.reset')}
                     </Button>
                  </div>
               </div>
            </CollapsibleContent>
         </Collapsible>
      </div>
   )
}

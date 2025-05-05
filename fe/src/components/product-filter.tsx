'use client'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Slider } from '@/components/ui/slider'
import { Switch } from '@/components/ui/switch'
import { GetProductQueryParamsType } from '@/types/product.type'
import { useState, useEffect } from 'react'
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible'
import { ChevronDown, ChevronUp, Filter, Search, SlidersHorizontal, Tag } from 'lucide-react'
import { Separator } from '@/components/ui/separator'

interface ProductFilterProps {
   initialFilters: GetProductQueryParamsType
   onFilterChange: (filters: GetProductQueryParamsType) => void
   brands: string[]
   categories: { id: number; name: string }[]
   maxPriceValue?: number
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

   // Cập nhật filters khi initialFilters thay đổi
   useEffect(() => {
      setFilters(initialFilters)
   }, [initialFilters])

   // Xử lý khi thay đổi giá trị filter
   const handleFilterChange = (name: keyof GetProductQueryParamsType, value: any) => {
      setFilters((prev) => ({ ...prev, [name]: value }))
   }

   // Xử lý khi thay đổi khoảng giá
   const handlePriceChange = (value: number[]) => {
      setPriceRange([value[0], value[1]])
   }

   // Áp dụng filter
   const applyFilters = () => {
      onFilterChange({
         ...filters,
         minPrice: priceRange[0],
         maxPrice: priceRange[1]
      })
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
      <div className='bg-card rounded-xl shadow-md overflow-hidden border border-border/40'>
         <div className='flex items-center justify-between p-4 bg-muted/30'>
            <div className='flex items-center gap-2'>
               <SlidersHorizontal className='h-5 w-5 text-primaryColor' />
               <h3 className='text-lg font-medium'>Bộ lọc sản phẩm</h3>
            </div>
            <Button variant='ghost' size='sm' onClick={() => setIsOpen(!isOpen)} className='hover:bg-muted'>
               <Filter className='h-4 w-4 mr-2 text-primaryColor' />
               {isOpen ? <ChevronUp className='h-4 w-4' /> : <ChevronDown className='h-4 w-4' />}
            </Button>
         </div>

         <Collapsible open={isOpen} onOpenChange={setIsOpen}>
            <CollapsibleContent>
               <div className='p-4 pt-2 space-y-5'>
                  {/* Tìm kiếm theo từ khóa */}
                  <div>
                     <Label htmlFor='keyword' className='text-sm font-medium mb-1.5 block'>
                        Tìm kiếm sản phẩm
                     </Label>
                     <div className='relative'>
                        <Search className='absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground' />
                        <Input
                           id='keyword'
                           placeholder='Nhập từ khóa tìm kiếm...'
                           value={filters.keyword || ''}
                           onChange={(e) => handleFilterChange('keyword', e.target.value)}
                           className='pl-9 bg-background'
                        />
                     </div>
                  </div>

                  {/* Lọc theo danh mục */}
                  <div>
                     <Label htmlFor='category' className='text-sm font-medium mb-1.5 block'>
                        Danh mục
                     </Label>
                     <Select
                        value={filters.categoryId?.toString() || 'all'}
                        onValueChange={(value) =>
                           handleFilterChange('categoryId', value === 'all' ? undefined : parseInt(value))
                        }
                     >
                        <SelectTrigger id='category' className='bg-background w-full'>
                           <SelectValue placeholder='Chọn danh mục' />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='all'>Tất cả danh mục</SelectItem>
                           {categories.map((category) => (
                              <SelectItem key={category.id} value={category.id.toString()}>
                                 {category.name}
                              </SelectItem>
                           ))}
                        </SelectContent>
                     </Select>
                  </div>

                  {/* Lọc theo thương hiệu */}
                  <div>
                     <Label htmlFor='brand' className='text-sm font-medium mb-1.5 block'>
                        Thương hiệu
                     </Label>
                     <Select
                        value={filters.brand || 'all'}
                        onValueChange={(value) => handleFilterChange('brand', value === 'all' ? undefined : value)}
                     >
                        <SelectTrigger id='brand' className='bg-background w-full'>
                           <SelectValue placeholder='Chọn thương hiệu' />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='all'>Tất cả thương hiệu</SelectItem>
                           {brands.map((brand) => (
                              <SelectItem key={brand} value={brand}>
                                 {brand}
                              </SelectItem>
                           ))}
                        </SelectContent>
                     </Select>
                  </div>

                  {/* Loại sản phẩm */}
                  <div>
                     <Label htmlFor='filterType' className='text-sm font-medium mb-1.5 block'>
                        Loại sản phẩm
                     </Label>
                     <Select
                        value={filters.filterType || 'ALL'}
                        onValueChange={(value) =>
                           handleFilterChange('filterType', value as GetProductQueryParamsType['filterType'])
                        }
                     >
                        <SelectTrigger id='filterType' className='bg-background w-full'>
                           <SelectValue placeholder='Chọn loại sản phẩm' />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='ALL'>Tất cả sản phẩm</SelectItem>
                           <SelectItem value='TOP_SELLING'>Bán chạy nhất</SelectItem>
                           <SelectItem value='NEW_ARRIVALS'>Sản phẩm mới</SelectItem>
                           <SelectItem value='TOP_RATED'>Đánh giá cao</SelectItem>
                           <SelectItem value='DISCOUNTED'>Đang giảm giá</SelectItem>
                        </SelectContent>
                     </Select>
                  </div>

                  {/* Sắp xếp theo */}
                  <div>
                     <Label htmlFor='sort' className='text-sm font-medium mb-1.5 block'>
                        Sắp xếp theo
                     </Label>
                     <Select
                        value={`${filters.sortBy || 'id'}-${filters.sortDir || 'desc'}`}
                        onValueChange={(value) => {
                           const [sortBy, sortDir] = value.split('-')
                           handleFilterChange('sortBy', sortBy)
                           handleFilterChange('sortDir', sortDir)
                        }}
                     >
                        <SelectTrigger id='sort' className='bg-background w-full'>
                           <SelectValue placeholder='Sắp xếp theo' />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='id-desc'>Mới nhất</SelectItem>
                           <SelectItem value='price-asc'>Giá tăng dần</SelectItem>
                           <SelectItem value='price-desc'>Giá giảm dần</SelectItem>
                           <SelectItem value='averageRating-desc'>Đánh giá cao nhất</SelectItem>
                        </SelectContent>
                     </Select>
                  </div>

                  {/* Khoảng giá */}
                  <div>
                     <Label className='text-sm font-medium mb-1.5 block'>Khoảng giá</Label>
                     <div className='px-2 py-4'>
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
                  </div>

                  {/* Các tùy chọn bổ sung */}
                  <div className='space-y-3'>
                     <div className='flex items-center space-x-2 bg-muted/30 p-3 rounded-lg'>
                        <Switch
                           id='isDiscount'
                           checked={filters.isDiscount || false}
                           onCheckedChange={(value) => handleFilterChange('isDiscount', value)}
                           className='data-[state=checked]:bg-secondaryColor'
                        />
                        <Label htmlFor='isDiscount' className='cursor-pointer'>
                           Chỉ hiện sản phẩm giảm giá
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
                           Chỉ hiện sản phẩm còn hàng
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
                        Đặt lại
                     </Button>
                     <Button onClick={applyFilters} className='bg-primaryColor hover:bg-primaryColor/90 flex-1'>
                        Áp dụng
                     </Button>
                  </div>
               </div>
            </CollapsibleContent>
         </Collapsible>
      </div>
   )
}

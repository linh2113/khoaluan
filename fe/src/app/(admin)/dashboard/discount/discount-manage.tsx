'use client'
import {
   useCreateDiscount,
   useGetAllDiscount,
   useUpdateDiscount,
   useAssignDiscountToProducts,
   useAssignDiscountToCategories,
   useDeleteDiscountToProducts,
   useDeleteDiscountToCategories,
   useGetAllAdminProduct,
   useGetAllCategories,
   useEditPriceDiscountToProducts,
} from '@/queries/useAdmin'
import { format } from 'date-fns'
import type React from 'react'
import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Switch } from '@/components/ui/switch'
import { Edit, Plus, Search, X } from 'lucide-react'
import type {
   DiscountType,
   GetDiscountQueryParamsType,
   CreateDiscountType,
   UpdateDiscountType
} from '@/types/admin.type'
import { toast } from 'react-toastify'
import Paginate from '@/components/paginate'
import Image from 'next/image'
import { decodeHTML } from '@/lib/utils'

export default function DiscountManage() {
   const [currentPage, setCurrentPage] = useState<number>(1)
   const [queryParams, setQueryParams] = useState<GetDiscountQueryParamsType>({
      page: currentPage - 1,
      size: 10,
      sortBy: 'id',
      sortDir: 'desc',
      search: ''
   })
   const [searchTerm, setSearchTerm] = useState('')

   const createDiscount = useCreateDiscount()
   const updateDiscount = useUpdateDiscount()
   const assignDiscountToProducts = useAssignDiscountToProducts()
   const assignDiscountToCategories = useAssignDiscountToCategories()
   const deleteDiscountToProducts = useDeleteDiscountToProducts()
   const deleteDiscountToCategories = useDeleteDiscountToCategories()
   const editPriceDiscountToProducts = useEditPriceDiscountToProducts()

   const getAllDiscount = useGetAllDiscount(queryParams)
   const discounts = getAllDiscount.data?.data.data.content || []
   const totalPages = getAllDiscount.data?.data.data.totalPages || 0

   const [isAddDialogOpen, setIsAddDialogOpen] = useState(false)
   const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
   const [bannerFile, setBannerFile] = useState<File | null>(null)
   const [bannerPreview, setBannerPreview] = useState<string | null>(null)

   const [newDiscount, setNewDiscount] = useState<CreateDiscountType>({
      name: '',
      type: 'PRODUCT',
      value: 0,
      startDate: '',
      endDate: '',
      isActive: true,
      productIds: [],
      discountedPrices: {},
      categoryIds: []
   })

   const [editingDiscount, setEditingDiscount] = useState<UpdateDiscountType | null>(null)
   const [productSearch, setProductSearch] = useState('')
   const getAllAdminProduct = useGetAllAdminProduct({ search: productSearch })
   const products = getAllAdminProduct.data?.data.data.content || []

   const [categorySearch, setCategorySearch] = useState('')
   const getAllCategories = useGetAllCategories({ search: categorySearch })
   const categories = getAllCategories.data?.data.data.content.filter((item) => item.status) || []

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

   const handleSortChange = (value: string) => {
      const [newSortBy, newSortDir] = value.split('-')
      setCurrentPage(1)
      setQueryParams({
         ...queryParams,
         page: 0,
         sortBy: newSortBy,
         sortDir: newSortDir
      })
   }

   const handlePageSizeChange = (value: string) => {
      const newSize = Number(value)
      setCurrentPage(1)
      setQueryParams({
         ...queryParams,
         page: 0,
         size: newSize
      })
   }

   const handleSearch = () => {
      setCurrentPage(1)
      setQueryParams({
         ...queryParams,
         page: 0,
         search: searchTerm.trim()
      })
   }

   const handleSearchKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.key === 'Enter') {
         handleSearch()
      }
   }

   const handleClearSearch = () => {
      setSearchTerm('')
      setCurrentPage(1)
      setQueryParams({
         ...queryParams,
         page: 0,
         search: ''
      })
   }

   const handleBannerFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0]
      if (file) {
         setBannerFile(file)
         const reader = new FileReader()
         reader.onload = (e) => {
            setBannerPreview(e.target?.result as string)
         }
         reader.readAsDataURL(file)
      }
   }

   const removeBannerFile = () => {
      setBannerFile(null)
      setBannerPreview(null)
   }

   const formatDateTime = (dateTimeString: string) => {
      if (!dateTimeString) return ''
      const date = new Date(dateTimeString)
      return date.toLocaleString('vi-VN', {
         day: '2-digit',
         month: '2-digit',
         year: 'numeric',
         hour: '2-digit',
         minute: '2-digit'
      })
   }

   const formatDiscountValue = (value: number) => {
      return `${value}%`
   }

   const handleAddDiscount = () => {
      if (!newDiscount.name.trim()) {
         toast.error('Tên mã giảm giá không được để trống')
         return
      }
      if (newDiscount.value <= 0) {
         toast.error('Giá trị giảm giá phải lớn hơn 0')
         return
      }
      if (!newDiscount.startDate || !newDiscount.endDate) {
         toast.error('Vui lòng chọn ngày giờ bắt đầu và kết thúc')
         return
      }
      if (new Date(newDiscount.startDate) >= new Date(newDiscount.endDate)) {
         toast.error('Ngày giờ bắt đầu phải nhỏ hơn ngày giờ kết thúc')
         return
      }
      if (newDiscount.type === 'PRODUCT' && newDiscount?.productIds?.length === 0) {
         toast.error('Vui lòng chọn ít nhất một sản phẩm')
         return
      }
      if (newDiscount.type === 'CATEGORY' && newDiscount?.categoryIds?.length === 0) {
         toast.error('Vui lòng chọn ít nhất một danh mục')
         return
      }

      // Tạo FormData để gửi multipart request
      const formData = new FormData()
      const formattedStartDate = format(new Date(newDiscount.startDate), 'yyyy-MM-dd HH:mm:ss')
      const formattedEndDate = format(new Date(newDiscount.endDate), 'yyyy-MM-dd HH:mm:ss')
      // Tạo discount object theo format backend mong đợi
      const discountData = {
         name: newDiscount.name,
         type: newDiscount.type,
         value: newDiscount.value,
         startDate: formattedStartDate,
         endDate: formattedEndDate,
         isActive: newDiscount.isActive,
         productIds: newDiscount.productIds,
         discountedPrices: newDiscount.discountedPrices,
         categoryIds: newDiscount.categoryIds
      }


      formData.append(
      'discount',
      new Blob([JSON.stringify(discountData)], { type: 'application/json' })
      )

      // Append banner file if exists
      if (bannerFile) {
         formData.append('banner', bannerFile)
      }

      createDiscount.mutate(formData as any, {
         onSuccess: () => {
            setIsAddDialogOpen(false)
            setNewDiscount({
               name: '',
               type: 'PRODUCT',
               value: 0,
               startDate: '',
               endDate: '',
               isActive: true,
               productIds: [],
               discountedPrices: {},
               categoryIds: []
            })
            setBannerFile(null)
            setBannerPreview(null)
         },
         onError: (error: any) => {
            toast.error(error?.message || 'Có lỗi xảy ra khi tạo mã giảm giá')
         }
      })
   }

   const handleEditDiscount = async () => {
      if (!editingDiscount || !editingDiscount.name.trim()) {
         toast.error('Tên mã giảm giá không được để trống')
         return
      }
      if (editingDiscount.value <= 0) {
         toast.error('Giá trị giảm giá phải lớn hơn 0')
         return
      }
      if (!editingDiscount.startDate || !editingDiscount.endDate) {
         toast.error('Vui lòng chọn ngày giờ bắt đầu và kết thúc')
         return
      }
      if (new Date(editingDiscount.startDate) >= new Date(editingDiscount.endDate)) {
         toast.error('Ngày giờ bắt đầu phải nhỏ hơn ngày giờ kết thúc')
         return
      }

      try {
         
         const formattedStartDate = format(new Date(editingDiscount.startDate), 'yyyy-MM-dd HH:mm:ss')
         const formattedEndDate = format(new Date(editingDiscount.endDate), 'yyyy-MM-dd HH:mm:ss')
         const formData = new FormData()
         // 1. Update thông tin cơ bản của discount
         const basicUpdateData = {
            id: editingDiscount.id,
            name: editingDiscount.name,
            type: editingDiscount.type,
            value: editingDiscount.value,
            startDate: formattedStartDate,
            endDate: formattedEndDate,
            isActive: editingDiscount.isActive,
            discountedPrices: editingDiscount.discountedPrices
         }
         formData.append('discount', new Blob([JSON.stringify(basicUpdateData)], { type: 'application/json' }))

         if (bannerFile) {
         formData.append('banner', bannerFile)
         }

         await updateDiscount.mutateAsync({ id: editingDiscount.id, formData })

         // 2. Tìm discount gốc để so sánh thay đổi
         const originalDiscount = discounts.find((d) => d.id === editingDiscount.id)

         if (editingDiscount.type === 'PRODUCT') {
            // Xử lý thay đổi sản phẩm
            const originalProductIds = originalDiscount?.productIds || []
            const newProductIds = editingDiscount.productIds || []

            // Tìm sản phẩm cần thêm
            const productsToAdd = newProductIds.filter((id) => !originalProductIds.includes(id))
            // Tìm sản phẩm cần xóa
            const productsToRemove = originalProductIds.filter((id: any) => !newProductIds.includes(id))

            // Thêm sản phẩm mới
            if (productsToAdd.length > 0) {
               const addProductsData = {
                  discountId: editingDiscount.id,
                  productIds: productsToAdd,
                  discountedPrices: Object.fromEntries(
                     productsToAdd.map((id) => [id, editingDiscount.discountedPrices?.[id] || 0])
                  )
               }
               await assignDiscountToProducts.mutateAsync(addProductsData)
            }

            // Xóa sản phẩm
            if (productsToRemove.length > 0) {
               const removeProductsData = {
                  discountId: editingDiscount.id,
                  productIds: productsToRemove
               }
               await deleteDiscountToProducts.mutateAsync(removeProductsData)
            }

            // Cập nhật giá giảm tùy chỉnh
            const productPrices = editingDiscount.discountedPrices || {}
            if (Object.keys(productPrices).length > 0) {
               const priceUpdateData = {
                  discountId: editingDiscount.id,
                  productPrices
               }
               await editPriceDiscountToProducts.mutateAsync(priceUpdateData)
            }
         } else if (editingDiscount.type === 'CATEGORY') {
            // Xử lý thay đổi danh mục
            const originalCategoryIds = originalDiscount?.categoryIds || []
            const newCategoryIds = editingDiscount.categoryIds || []

            // Tìm danh mục cần thêm
            const categoriesToAdd = newCategoryIds.filter((id) => !originalCategoryIds.includes(id))
            // Tìm danh mục cần xóa
            const categoriesToRemove: number[] = (originalCategoryIds as number[]).filter(
               (id: number) => !(newCategoryIds as number[]).includes(id)
            )

            // Thêm danh mục mới
            if (categoriesToAdd.length > 0) {
               const addCategoriesData = {
                  discountId: editingDiscount.id,
                  categoryIds: categoriesToAdd
               }
               await assignDiscountToCategories.mutateAsync(addCategoriesData)
            }

            // Xóa danh mục
            if (categoriesToRemove.length > 0) {
               const removeCategoriesData = {
                  discountId: editingDiscount.id,
                  categoryIds: categoriesToRemove
               }
               await deleteDiscountToCategories.mutateAsync(removeCategoriesData)
            }
         }

         toast.success('Cập nhật mã giảm giá thành công!')
         setIsEditDialogOpen(false)
         setEditingDiscount(null)
      } catch (error) {
         toast.error('Có lỗi xảy ra khi cập nhật mã giảm giá')
         console.error('Update discount error:', error)
      }
   }

   const openEditDialog = (discount: DiscountType) => {
      setEditingDiscount({
         id: discount.id,
         name: discount.name || `Discount ${discount.id}`,
         type: discount.type || 'PRODUCT',
         value: discount.value,
         startDate: discount.startDate,
         endDate: discount.endDate,
         isActive: discount.isActive,
         productIds: discount.productIds || [],
         discountedPrices: discount.discountedPrices || {},
         categoryIds: discount.categoryIds || []
      })
      setIsEditDialogOpen(true)
   }

   // Auto-recalculate discounted prices when discount value changes (for Add Dialog)
   useEffect(() => {
   if (!editingDiscount || !editingDiscount.productIds || editingDiscount.value <= 0) return;

   const updatedPrices: Record<number, number> = { ...editingDiscount.discountedPrices }

   editingDiscount.productIds.forEach((productId) => {
      const isCustom = editingDiscount.discountedPrices?.[productId]
      const product = products.find((p) => p.id === productId)

      if (product && !isCustom) {
         // chỉ update nếu không phải giá người dùng nhập
         updatedPrices[productId] = Math.round(product.price * (1 - editingDiscount.value / 100))
      }
   })

   setEditingDiscount((prev) =>
      prev
         ? {
              ...prev,
              discountedPrices: updatedPrices
           }
         : null
   )
}, [editingDiscount?.value, editingDiscount?.productIds, products])


   // Auto-recalculate discounted prices when discount value changes (for Edit Dialog)
   useEffect(() => {
   if (!editingDiscount || !editingDiscount.productIds || editingDiscount.value <= 0) return;

   let hasChange = false;
   const updatedPrices: Record<number, number> = { ...editingDiscount.discountedPrices };

   editingDiscount.productIds.forEach((productId) => {
      const product = products.find((p) => p.id === productId);
      if (product) {
         const autoCalculatedPrice = Math.round(product.price * (1 - editingDiscount.value / 100));
         const currentPrice = editingDiscount.discountedPrices?.[productId];

         // Nếu người dùng chưa nhập giá hoặc giá không khớp giá tính tự động, thì cập nhật
         if (currentPrice === undefined || currentPrice === 0) {
            updatedPrices[productId] = autoCalculatedPrice;
            hasChange = true;
         }
      }
   });

   if (hasChange) {
      setEditingDiscount((prev) =>
         prev
            ? {
                 ...prev,
                 discountedPrices: updatedPrices
              }
            : null
      );
   }
}, [editingDiscount?.value, editingDiscount?.productIds, products]);


   return (
      <div className='container mx-auto p-6'>
         <div className='flex items-center justify-between flex-wrap gap-3'>
            <h1 className='text-2xl font-bold'>Quản lý mã giảm giá</h1>
            <Button onClick={() => setIsAddDialogOpen(true)}>
               <Plus className='mr-2 h-4 w-4' /> Thêm mã giảm giá
            </Button>
         </div>

         <div className='flex items-center flex-wrap gap-4 my-5'>
            <div className='flex items-center gap-2'>
               <Input
                  placeholder='Tìm kiếm mã giảm giá...'
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  onKeyDown={handleSearchKeyDown}
                  className='sm:w-[250px]'
               />
               <Button className='h-10 w-10 flex-shrink-0' onClick={handleSearch} size='icon' variant='outline'>
                  <Search />
               </Button>
               {queryParams.search && (
                  <Button onClick={handleClearSearch} variant='ghost' size='sm'>
                     Xóa
                  </Button>
               )}
            </div>
            <div className='flex items-center gap-2'>
               <span className='text-sm'>Hiển thị:</span>
               <Select value={(queryParams.size ?? 10).toString()} onValueChange={handlePageSizeChange}>
                  <SelectTrigger className='w-[80px]'>
                     <SelectValue placeholder='10' />
                  </SelectTrigger>
                  <SelectContent>
                     <SelectItem value='5'>5</SelectItem>
                     <SelectItem value='10'>10</SelectItem>
                     <SelectItem value='20'>20</SelectItem>
                     <SelectItem value='50'>50</SelectItem>
                  </SelectContent>
               </Select>
            </div>
            <div className='flex items-center gap-2'>
               <span className='text-sm'>Sắp xếp:</span>
               <Select value={`${queryParams.sortBy}-${queryParams.sortDir}`} onValueChange={handleSortChange}>
                  <SelectTrigger className='w-[180px]'>
                     <SelectValue placeholder='Mới nhất' />
                  </SelectTrigger>
                  <SelectContent>
                     <SelectItem value='id-desc'>Mới nhất</SelectItem>
                     <SelectItem value='id-asc'>Cũ nhất</SelectItem>
                     <SelectItem value='value-desc'>Giá trị cao nhất</SelectItem>
                     <SelectItem value='value-asc'>Giá trị thấp nhất</SelectItem>
                     <SelectItem value='priority-desc'>Ưu tiên cao</SelectItem>
                     <SelectItem value='priority-asc'>Ưu tiên thấp</SelectItem>
                  </SelectContent>
               </Select>
            </div>
         </div>

         {getAllDiscount.isLoading ? (
            <div className='text-center py-4'>Đang tải...</div>
         ) : (
            <Table>
               <TableHeader>
                  <TableRow>
                     <TableHead className='w-[80px]'>ID</TableHead>
                     <TableHead>Tên</TableHead>
                     <TableHead>Loại</TableHead>
                     <TableHead>Giá trị</TableHead>
                     <TableHead>Banner</TableHead>
                     <TableHead>Ngày giờ bắt đầu</TableHead>
                     <TableHead>Ngày giờ kết thúc</TableHead>
                     <TableHead>Trạng thái</TableHead>
                     <TableHead className='text-right'>Thao tác</TableHead>
                  </TableRow>
               </TableHeader>
               <TableBody>
                  {discounts.length === 0 ? (
                     <TableRow>
                        <TableCell colSpan={9} className='text-center'>
                           Không có mã giảm giá nào
                        </TableCell>
                     </TableRow>
                  ) : (
                     discounts.map((discount) => (
                        <TableRow key={discount.id}>
                           <TableCell>{discount.id}</TableCell>
                           <TableCell>{discount.name}</TableCell>
                           <TableCell>
                              <span
                                 className={`px-2 py-1 rounded-full text-xs ${
                                    discount.type === 'PRODUCT'
                                       ? 'bg-blue-100 text-blue-800'
                                       : 'bg-purple-100 text-purple-800'
                                 }`}
                              >
                                 {discount.type === 'PRODUCT' ? 'Sản phẩm' : 'Danh mục'}
                              </span>
                           </TableCell>
                           <TableCell className='font-medium'>{formatDiscountValue(discount.value)}</TableCell>
                           <TableCell>
                              {(discount.bannerUrl as any) ? (
                                 <Image
                                    src={discount.bannerUrl || '/placeholder.svg'}
                                    alt='Banner'
                                    width={60}
                                    height={40}
                                    className='rounded object-cover'
                                 />
                              ) : (
                                 <span className='text-muted-foreground text-sm'>Không có</span>
                              )}
                           </TableCell>
                           <TableCell className='text-sm'>{formatDateTime(discount.startDate)}</TableCell>
                           <TableCell className='text-sm'>{formatDateTime(discount.endDate)}</TableCell>
                           <TableCell>
                              {discount.isActive ? (
                                 <span className='px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs'>
                                    Hoạt động
                                 </span>
                              ) : (
                                 <span className='px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs'>
                                    Không hoạt động
                                 </span>
                              )}
                           </TableCell>
                           <TableCell className='text-right'>
                              <Button
                                 variant='outline'
                                 size='icon'
                                 onClick={() => openEditDialog(discount)}
                                 className='mr-2'
                              >
                                 <Edit className='h-4 w-4' />
                              </Button>
                           </TableCell>
                        </TableRow>
                     ))
                  )}
               </TableBody>
            </Table>
         )}

         {totalPages > 1 && (
            <div className='mt-4 flex justify-center'>
               <Paginate
                  totalPages={totalPages}
                  handlePageClick={handlePageClick}
                  currentPage={currentPage}
                  setCurrentPage={setCurrentPage}
               />
            </div>
         )}

         {/* Dialog thêm mã giảm giá */}
         <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
            <DialogContent className='max-w-md max-h-[90vh] overflow-y-auto'>
               <DialogHeader>
                  <DialogTitle>Thêm mã giảm giá mới</DialogTitle>
               </DialogHeader>
               <div className='grid gap-4 py-4'>
                  <div className='grid gap-2'>
                     <Label htmlFor='name'>Tên mã giảm giá</Label>
                     <Input
                        id='name'
                        value={newDiscount.name}
                        onChange={(e) => setNewDiscount({ ...newDiscount, name: e.target.value })}
                        placeholder='Nhập tên mã giảm giá'
                     />
                  </div>

                  <div className='grid gap-2'>
                     <Label htmlFor='banner'>Banner (tùy chọn)</Label>
                     <div className='space-y-2'>
                        <Input
                           id='banner'
                           type='file'
                           accept='image/*'
                           onChange={handleBannerFileChange}
                           className='cursor-pointer'
                        />
                        {bannerPreview && (
                           <div className='relative inline-block'>
                              <Image
                                 src={bannerPreview || '/placeholder.svg'}
                                 alt='Banner preview'
                                 width={200}
                                 height={120}
                                 className='rounded border object-cover'
                              />
                              <Button
                                 type='button'
                                 variant='destructive'
                                 size='icon'
                                 className='absolute -top-2 -right-2 h-6 w-6'
                                 onClick={removeBannerFile}
                              >
                                 <X className='h-4 w-4' />
                              </Button>
                           </div>
                        )}
                     </div>
                  </div>

                  <div className='grid gap-2'>
                     <Label htmlFor='type'>Loại áp dụng</Label>
                     <Select
                        value={newDiscount.type}
                        onValueChange={(value) =>
                           setNewDiscount({ ...newDiscount, type: value as 'PRODUCT' | 'CATEGORY' })
                        }
                     >
                        <SelectTrigger>
                           <SelectValue placeholder='Chọn loại áp dụng' />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='PRODUCT'>Sản phẩm</SelectItem>
                           <SelectItem value='CATEGORY'>Danh mục</SelectItem>
                        </SelectContent>
                     </Select>
                  </div>

                  <div className='grid gap-2'>
                     <Label htmlFor='value'>Giá trị (%)</Label>
                     <Input
                        id='value'
                        type='number'
                        min='0'
                        max='100'
                        value={newDiscount.value}
                        onChange={(e) => setNewDiscount({ ...newDiscount, value: Number(e.target.value) })}
                        placeholder='Nhập giá trị phần trăm'
                     />
                  </div>

                  {newDiscount.type === 'PRODUCT' && (
                     <div className='grid gap-2'>
                        <Label>Chọn sản phẩm áp dụng</Label>
                        <div className='space-y-2'>
                           <Input
                              placeholder='Tìm kiếm sản phẩm...'
                              value={productSearch}
                              onChange={(e) => setProductSearch(e.target.value)}
                              className='w-full'
                           />
                           <div className='border rounded-md p-2 max-h-40 overflow-y-auto space-y-2'>
                              {getAllAdminProduct.isLoading ? (
                                 <div className='text-sm text-muted-foreground'>Đang tải...</div>
                              ) : products.length === 0 ? (
                                 <div className='text-sm text-muted-foreground'>Không tìm thấy sản phẩm</div>
                              ) : (
                                 products.map((product) => {
                                    const isSelected = newDiscount?.productIds?.includes(product.id)
                                    const discountedPrice =
                                       newDiscount.discountedPrices?.[product.id] ||
                                       product.price * (1 - newDiscount.value / 100)
                                    return (
                                       <div
                                          key={product.id}
                                          className='flex items-center space-x-2 p-2 hover:bg-primary-foreground rounded'
                                       >
                                          <input
                                             type='checkbox'
                                             id={`product-${product.id}`}
                                             checked={isSelected}
                                             onChange={(e) => {
                                                if (e.target.checked) {
                                                   const updatedProductIds = [
                                                      ...(newDiscount.productIds || []),
                                                      product.id
                                                   ]
                                                   const updatedDiscountedPrices = {
                                                      ...newDiscount.discountedPrices,
                                                      [product.id]: discountedPrice
                                                   }
                                                   setNewDiscount({
                                                      ...newDiscount,
                                                      productIds: updatedProductIds,
                                                      discountedPrices: updatedDiscountedPrices
                                                   })
                                                } else {
                                                   const updatedProductIds = newDiscount?.productIds?.filter(
                                                      (id) => id !== product.id
                                                   )
                                                   const updatedDiscountedPrices = { ...newDiscount.discountedPrices }
                                                   delete updatedDiscountedPrices[product.id]
                                                   setNewDiscount({
                                                      ...newDiscount,
                                                      productIds: updatedProductIds,
                                                      discountedPrices: updatedDiscountedPrices
                                                   })
                                                }
                                             }}
                                             className='rounded'
                                          />
                                          <label htmlFor={`product-${product.id}`} className='flex-1 cursor-pointer'>
                                             <div className='flex gap-1 items-center'>
                                                <Image
                                                   src={product.image || '/placeholder.svg'}
                                                   alt=''
                                                   width={50}
                                                   height={50}
                                                   className='rounded w-10 h-10'
                                                />
                                                <div>
                                                   <div className='font-medium text-sm'>{decodeHTML(product.name)}</div>
                                                   <div className='text-xs text-muted-foreground'>
                                                      Giá gốc: {product.price?.toLocaleString('vi-VN')}đ
                                                   </div>
                                                </div>
                                                {isSelected && (
                                                   <div className='text-xs text-green-600 font-medium'>
                                                      Giá sau giảm: {discountedPrice.toLocaleString('vi-VN')}đ
                                                   </div>
                                                )}
                                             </div>
                                          </label>
                                       </div>
                                    )
                                 })
                              )}
                           </div>
                        </div>
                        <div className='text-xs text-muted-foreground'>
                           Đã chọn: {newDiscount?.productIds?.length} sản phẩm
                        </div>
                     </div>
                  )}

                  {newDiscount.type === 'CATEGORY' && (
                     <div className='grid gap-2'>
                        <Label>Chọn danh mục áp dụng</Label>
                        <div className='space-y-2'>
                           <Input
                              placeholder='Tìm kiếm danh mục...'
                              value={categorySearch}
                              onChange={(e) => setCategorySearch(e.target.value)}
                              className='w-full'
                           />
                           <div className='border rounded-md p-2 max-h-40 overflow-y-auto space-y-2'>
                              {getAllCategories.isLoading ? (
                                 <div className='text-sm text-muted-foreground'>Đang tải...</div>
                              ) : categories.length === 0 ? (
                                 <div className='text-sm text-muted-foreground'>Không tìm thấy danh mục</div>
                              ) : (
                                 categories.map((category) => {
                                    const isSelected = newDiscount.categoryIds?.includes(category.id) || false
                                    return (
                                       <div
                                          key={category.id}
                                          className='flex items-center space-x-2 p-2 hover:bg-primary-foreground rounded'
                                       >
                                          <input
                                             type='checkbox'
                                             id={`category-${category.id}`}
                                             checked={isSelected}
                                             onChange={(e) => {
                                                if (e.target.checked) {
                                                   const updatedCategoryIds = [
                                                      ...(newDiscount.categoryIds || []),
                                                      category.id
                                                   ]
                                                   setNewDiscount({
                                                      ...newDiscount,
                                                      categoryIds: updatedCategoryIds
                                                   })
                                                } else {
                                                   const updatedCategoryIds = (newDiscount.categoryIds || []).filter(
                                                      (id) => id !== category.id
                                                   )
                                                   setNewDiscount({
                                                      ...newDiscount,
                                                      categoryIds: updatedCategoryIds
                                                   })
                                                }
                                             }}
                                             className='rounded'
                                          />
                                          <label htmlFor={`category-${category.id}`} className='flex-1 cursor-pointer'>
                                             <div className='font-medium text-sm'>{category.categoryName}</div>
                                          </label>
                                       </div>
                                    )
                                 })
                              )}
                           </div>
                        </div>
                        <div className='text-xs text-muted-foreground'>
                           Đã chọn: {newDiscount.categoryIds?.length || 0} danh mục
                        </div>
                     </div>
                  )}

                  <div className='grid grid-cols-1 gap-4'>
                     <div className='grid gap-2'>
                        <Label htmlFor='startDate'>Ngày giờ bắt đầu</Label>
                        <Input
                           id='startDate'
                           type='datetime-local'
                           value={newDiscount.startDate}
                           onChange={(e) => setNewDiscount({ ...newDiscount, startDate: e.target.value })}
                        />
                     </div>
                     <div className='grid gap-2'>
                        <Label htmlFor='endDate'>Ngày giờ kết thúc</Label>
                        <Input
                           id='endDate'
                           type='datetime-local'
                           value={newDiscount.endDate}
                           onChange={(e) => setNewDiscount({ ...newDiscount, endDate: e.target.value })}
                        />
                     </div>
                  </div>

                  <div className='flex items-center space-x-2'>
                     <Switch
                        id='isActive'
                        checked={newDiscount.isActive}
                        onCheckedChange={(checked) => setNewDiscount({ ...newDiscount, isActive: checked })}
                     />
                     <Label htmlFor='isActive'>Kích hoạt ngay</Label>
                  </div>
               </div>
               <DialogFooter>
                  <Button variant='outline' onClick={() => setIsAddDialogOpen(false)}>
                     Hủy
                  </Button>
                  <Button onClick={handleAddDiscount} disabled={createDiscount.isPending}>
                     {createDiscount.isPending ? 'Đang xử lý...' : 'Thêm mã giảm giá'}
                  </Button>
               </DialogFooter>
            </DialogContent>
         </Dialog>

         {/* Dialog chỉnh sửa mã giảm giá */}
         <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
            <DialogContent className='max-w-lg max-h-[90vh] overflow-y-auto'>
               <DialogHeader>
                  <DialogTitle>Chỉnh sửa mã giảm giá</DialogTitle>
               </DialogHeader>
               {editingDiscount && (
                  <div className='grid gap-4 py-4'>
                     <div className='grid gap-2'>
                        <Label htmlFor='editName'>Tên mã giảm giá</Label>
                        <Input
                           id='editName'
                           value={editingDiscount.name}
                           onChange={(e) => setEditingDiscount({ ...editingDiscount, name: e.target.value })}
                           placeholder='Nhập tên mã giảm giá'
                        />
                     </div>
                     <div className='grid gap-2'>
                        <Label htmlFor='editValue'>Giá trị (%)</Label>
                        <Input
                           id='editValue'
                           type='number'
                           min='0'
                           max='100'
                           value={editingDiscount.value}
                           onChange={(e) => setEditingDiscount({ ...editingDiscount, value: Number(e.target.value) })}
                           placeholder='Nhập giá trị phần trăm'
                        />
                     </div>

                     {editingDiscount.type === 'PRODUCT' && (
                        <div className='grid gap-2'>
                           <Label>Chọn sản phẩm áp dụng</Label>
                           <div className='space-y-2'>
                              <Input
                                 placeholder='Tìm kiếm sản phẩm...'
                                 value={productSearch}
                                 onChange={(e) => setProductSearch(e.target.value)}
                                 className='w-full'
                              />
                              <div className='border rounded-md p-2 max-h-40 overflow-y-auto space-y-2'>
                                 {getAllAdminProduct.isLoading ? (
                                    <div className='text-sm text-muted-foreground'>Đang tải...</div>
                                 ) : products.length === 0 ? (
                                    <div className='text-sm text-muted-foreground'>Không tìm thấy sản phẩm</div>
                                 ) : (
                                    products.map((product) => {
                                       const isSelected = editingDiscount?.productIds?.includes(product.id)
                                       const discountedPrice =
                                          editingDiscount.discountedPrices?.[product.id] ||
                                          product.price * (1 - editingDiscount.value / 100)
                                       return (
                                          <div
                                             key={product.id}
                                             className='flex items-center space-x-2 p-2 hover:bg-primary-foreground rounded'
                                          >
                                             <input
                                                type='checkbox'
                                                id={`edit-product-${product.id}`}
                                                checked={isSelected}
                                                onChange={(e) => {
                                                   if (e.target.checked) {
                                                      const updatedProductIds = [
                                                         ...(editingDiscount.productIds || []),
                                                         product.id
                                                      ]
                                                      const updatedDiscountedPrices = {
                                                         ...editingDiscount.discountedPrices,
                                                         [product.id]: discountedPrice
                                                      }
                                                      setEditingDiscount({
                                                         ...editingDiscount,
                                                         productIds: updatedProductIds,
                                                         discountedPrices: updatedDiscountedPrices
                                                      })
                                                   } else {
                                                      const updatedProductIds = (
                                                         editingDiscount.productIds || []
                                                      ).filter((id) => id !== product.id)
                                                      const updatedDiscountedPrices = {
                                                         ...editingDiscount.discountedPrices
                                                      }
                                                      delete updatedDiscountedPrices[product.id]
                                                      setEditingDiscount({
                                                         ...editingDiscount,
                                                         productIds: updatedProductIds,
                                                         discountedPrices: updatedDiscountedPrices
                                                      })
                                                   }
                                                }}
                                                className='rounded'
                                             />
                                             <label
                                                htmlFor={`edit-product-${product.id}`}
                                                className='flex-1 cursor-pointer'
                                             >
                                                <div className='flex gap-1 items-center'>
                                                   <Image
                                                      src={product.image || '/placeholder.svg'}
                                                      alt=''
                                                      width={50}
                                                      height={50}
                                                      className='rounded w-10 h-10'
                                                   />
                                                   <div>
                                                      <div className='font-medium text-sm'>
                                                         {decodeHTML(product.name)}
                                                      </div>
                                                      <div className='text-xs text-muted-foreground'>
                                                         Giá gốc: {product.price?.toLocaleString('vi-VN')}đ
                                                      </div>
                                                   </div>
                                                   {isSelected && (
                                                      <div className='flex items-center space-x-2'>
                                                         <Input
                                                            type='number'
                                                            min='0'
                                                            value={editingDiscount.discountedPrices?.[product.id] || ''}
                                                            onChange={(e) => {
                                                               const newPrice = Number(e.target.value)

                                                               setEditingDiscount((prev) =>
                                                                  prev
                                                                     ? {
                                                                        ...prev,
                                                                        discountedPrices: {
                                                                           ...prev.discountedPrices,
                                                                           [product.id]: newPrice
                                                                        },
                                                                     }
                                                                     : null
                                                               )
                                                            }}

                                                            placeholder='Nhập giá giảm'
                                                            className='w-32 text-xs'
                                                         />
                                                         <div className='text-xs text-green-600 font-medium'>
                                                            Giá sau giảm:{' '}
                                                            {(
                                                               editingDiscount.discountedPrices?.[product.id] ||
                                                               discountedPrice
                                                            ).toLocaleString('vi-VN')}
                                                            đ
                                                         </div>
                                                      </div>
                                                   )}
                                                </div>
                                             </label>
                                          </div>
                                       )
                                    })
                                 )}
                              </div>
                           </div>
                           <div className='text-xs text-muted-foreground'>
                              Đã chọn: {editingDiscount?.productIds?.length} sản phẩm
                           </div>
                        </div>
                     )}

                     {editingDiscount.type === 'CATEGORY' && (
                        <div className='grid gap-2'>
                           <Label>Chọn danh mục áp dụng</Label>
                           <div className='space-y-2'>
                              <Input
                                 placeholder='Tìm kiếm danh mục...'
                                 value={categorySearch}
                                 onChange={(e) => setCategorySearch(e.target.value)}
                                 className='w-full'
                              />
                              <div className='border rounded-md p-2 max-h-40 overflow-y-auto space-y-2'>
                                 {getAllCategories.isLoading ? (
                                    <div className='text-sm text-muted-foreground'>Đang tải...</div>
                                 ) : categories.length === 0 ? (
                                    <div className='text-sm text-muted-foreground'>Không tìm thấy danh mục</div>
                                 ) : (
                                    categories.map((category) => {
                                       const isSelected = editingDiscount.categoryIds?.includes(category.id) || false
                                       return (
                                          <div
                                             key={category.id}
                                             className='flex items-center space-x-2 p-2 hover:bg-primary-foreground rounded'
                                          >
                                             <input
                                                type='checkbox'
                                                id={`edit-category-${category.id}`}
                                                checked={isSelected}
                                                onChange={(e) => {
                                                   if (e.target.checked) {
                                                      const updatedCategoryIds = [
                                                         ...(editingDiscount.categoryIds || []),
                                                         category.id
                                                      ]
                                                      setEditingDiscount({
                                                         ...editingDiscount,
                                                         categoryIds: updatedCategoryIds
                                                      })
                                                   } else {
                                                      const updatedCategoryIds = (
                                                         editingDiscount.categoryIds || []
                                                      ).filter((id) => id !== category.id)
                                                      setEditingDiscount({
                                                         ...editingDiscount,
                                                         categoryIds: updatedCategoryIds
                                                      })
                                                   }
                                                }}
                                                className='rounded'
                                             />
                                             <label
                                                htmlFor={`edit-category-${category.id}`}
                                                className='flex-1 cursor-pointer'
                                             >
                                                <div className='font-medium text-sm'>{category.categoryName}</div>
                                             </label>
                                          </div>
                                       )
                                    })
                                 )}
                              </div>
                           </div>
                           <div className='text-xs text-muted-foreground'>
                              Đã chọn: {editingDiscount.categoryIds?.length || 0} danh mục
                           </div>
                        </div>
                     )}

                     <div className='grid grid-cols-1 gap-4'>
                        <div className='grid gap-2'>
                           <Label htmlFor='editStartDate'>Ngày giờ bắt đầu</Label>
                           <Input
                              id='editStartDate'
                              type='datetime-local'
                              value={editingDiscount.startDate}
                              onChange={(e) => setEditingDiscount({ ...editingDiscount, startDate: e.target.value })}
                           />
                        </div>
                        <div className='grid gap-2'>
                           <Label htmlFor='editEndDate'>Ngày giờ kết thúc</Label>
                           <Input
                              id='editEndDate'
                              type='datetime-local'
                              value={editingDiscount.endDate}
                              onChange={(e) => setEditingDiscount({ ...editingDiscount, endDate: e.target.value })}
                           />
                        </div>
                     </div>

                     <div className='flex items-center space-x-2'>
                        <Switch
                           id='editIsActive'
                           checked={editingDiscount.isActive}
                           onCheckedChange={(checked) => setEditingDiscount({ ...editingDiscount, isActive: checked })}
                        />
                        <Label htmlFor='editIsActive'>Kích hoạt</Label>
                     </div>
                  </div>
               )}
               <DialogFooter>
                  <Button variant='outline' onClick={() => setIsEditDialogOpen(false)}>
                     Hủy
                  </Button>
                  <Button
                     onClick={handleEditDiscount}
                     disabled={
                        updateDiscount.isPending ||
                        assignDiscountToProducts.isPending ||
                        assignDiscountToCategories.isPending ||
                        deleteDiscountToProducts.isPending ||
                        deleteDiscountToCategories.isPending ||
                        editPriceDiscountToProducts.isPending
                     }
                  >
                     {updateDiscount.isPending ||
                     assignDiscountToProducts.isPending ||
                     assignDiscountToCategories.isPending ||
                     deleteDiscountToProducts.isPending ||
                     deleteDiscountToCategories.isPending ||
                     editPriceDiscountToProducts.isPending
                        ? 'Đang xử lý...'
                        : 'Cập nhật'}
                  </Button>
               </DialogFooter>
            </DialogContent>
         </Dialog>
      </div>
   )
}

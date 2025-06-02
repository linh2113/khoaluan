'use client'
import { useCreateDiscount, useGetAllDiscount, useUpdateDiscount } from '@/queries/useAdmin'
import type React from 'react'
import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Switch } from '@/components/ui/switch'
import { Edit, Plus, Search } from 'lucide-react'
import type {
   DiscountType,
   GetDiscountQueryParamsType,
   CreateDiscountType,
   UpdateDiscountType
} from '@/types/admin.type'
import { toast } from 'react-toastify'
import Paginate from '@/components/paginate'

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
   const getAllDiscount = useGetAllDiscount(queryParams)
   const discounts = getAllDiscount.data?.data.data.content || []
   const totalPages = getAllDiscount.data?.data.data.totalPages || 0

   const [isAddDialogOpen, setIsAddDialogOpen] = useState(false)
   const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
   const [newDiscount, setNewDiscount] = useState<CreateDiscountType>({
      name: '',
      type: 'PRODUCT',
      value: 0,
      startDate: '',
      endDate: '',
      isActive: true
   })
   const [editingDiscount, setEditingDiscount] = useState<UpdateDiscountType | null>(null)

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
      // Reset về trang 1 khi thay đổi filter
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
      // Reset về trang 1 khi thay đổi số lượng hiển thị
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

      createDiscount.mutate(newDiscount, {
         onSuccess: () => {
            setIsAddDialogOpen(false)
            setNewDiscount({
               name: '',
               type: 'PRODUCT',
               value: 0,
               startDate: '',
               endDate: '',
               isActive: true
            })
            getAllDiscount.refetch()
         }
      })
   }

   const handleEditDiscount = () => {
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

      updateDiscount.mutate(editingDiscount, {
         onSuccess: () => {
            setIsEditDialogOpen(false)
            setEditingDiscount(null)
            getAllDiscount.refetch()
         }
      })
   }

   const openEditDialog = (discount: DiscountType) => {
      setEditingDiscount({
         id: discount.id,
         name: discount.name || `Discount ${discount.id}`,
         type: discount.type || 'PRODUCT',
         value: discount.value,
         startDate: discount.startDate,
         endDate: discount.endDate,
         isActive: discount.isActive
      })
      setIsEditDialogOpen(true)
   }

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
                     <TableHead>Ngày giờ bắt đầu</TableHead>
                     <TableHead>Ngày giờ kết thúc</TableHead>
                     <TableHead>Trạng thái</TableHead>
                     <TableHead className='text-right'>Thao tác</TableHead>
                  </TableRow>
               </TableHeader>
               <TableBody>
                  {discounts.length === 0 ? (
                     <TableRow>
                        <TableCell colSpan={8} className='text-center'>
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
            <DialogContent className='max-w-md'>
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
            <DialogContent className='max-w-md'>
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
                        <Label htmlFor='editType'>Loại áp dụng</Label>
                        <Select
                           value={editingDiscount.type}
                           onValueChange={(value) =>
                              setEditingDiscount({ ...editingDiscount, type: value as 'PRODUCT' | 'CATEGORY' })
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
                  <Button onClick={handleEditDiscount} disabled={updateDiscount.isPending}>
                     {updateDiscount.isPending ? 'Đang xử lý...' : 'Cập nhật'}
                  </Button>
               </DialogFooter>
            </DialogContent>
         </Dialog>
      </div>
   )
}

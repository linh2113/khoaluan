'use client'
import { useCreateBrand, useGetAllBrand, useUpdateBrand } from '@/queries/useAdmin'
import React, { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Switch } from '@/components/ui/switch'
import { Edit, Plus, Search } from 'lucide-react'
import { BrandType, GetBrandQueryParamsType } from '@/types/admin.type'
import { toast } from 'react-toastify'
import { Textarea } from '@/components/ui/textarea'
import { format } from 'date-fns'
import { vi } from 'date-fns/locale'
import Paginate from '@/components/paginate'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

export default function BrandManage() {
   const [currentPage, setCurrentPage] = useState<number>(1)
   const [searchTerm, setSearchTerm] = useState('')
   const [queryParams, setQueryParams] = useState<GetBrandQueryParamsType>({
      page: currentPage - 1,
      size: 5,
      sortBy: 'id',
      sortDir: 'asc',
      search: ''
   })

   const getAllBrand = useGetAllBrand(queryParams)
   const brands = getAllBrand.data?.data.data.content || []
   const totalPages = getAllBrand.data?.data.data.totalPages || 0

   const createBrand = useCreateBrand()
   const updateBrand = useUpdateBrand()

   const [isAddDialogOpen, setIsAddDialogOpen] = useState(false)
   const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
   const [newBrand, setNewBrand] = useState<Partial<BrandType>>({
      brandName: '',
      description: '',
      status: true
   })
   const [editingBrand, setEditingBrand] = useState<BrandType | null>(null)

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

   const handleAddBrand = () => {
      if (!newBrand.brandName?.trim()) {
         toast.error('Tên thương hiệu không được để trống')
         return
      }

      createBrand.mutate(newBrand as BrandType, {
         onSuccess: () => {
            setIsAddDialogOpen(false)
            setNewBrand({
               brandName: '',
               description: '',
               status: true
            })
            // Refresh data sau khi thêm thành công
            getAllBrand.refetch()
         }
      })
   }

   const handleEditBrand = () => {
      if (!editingBrand || !editingBrand.brandName.trim()) {
         toast.error('Tên thương hiệu không được để trống')
         return
      }

      updateBrand.mutate(editingBrand, {
         onSuccess: () => {
            setIsEditDialogOpen(false)
            setEditingBrand(null)
            // Refresh data sau khi cập nhật thành công
            getAllBrand.refetch()
         }
      })
   }

   const openEditDialog = (brand: BrandType) => {
      setEditingBrand({ ...brand })
      setIsEditDialogOpen(true)
   }

   const formatDate = (dateString: string) => {
      try {
         return format(new Date(dateString), 'dd/MM/yyyy HH:mm', { locale: vi })
      } catch (error) {
         return 'Không xác định'
      }
   }

   return (
      <div className='container mx-auto p-6'>
         <div className='flex items-center justify-between flex-wrap gap-3'>
            <h1 className='text-2xl font-bold'>Quản lý thương hiệu</h1>
            <Button onClick={() => setIsAddDialogOpen(true)}>
               <Plus className='mr-2 h-4 w-4' /> Thêm thương hiệu
            </Button>
         </div>
         <div className='flex items-center flex-wrap gap-4 my-5'>
            <div className='flex items-center gap-2'>
               <Input
                  placeholder='Tìm kiếm thương hiệu...'
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
                     <SelectItem value='brandName-asc'>Tên A-Z</SelectItem>
                     <SelectItem value='brandName-desc'>Tên Z-A</SelectItem>
                  </SelectContent>
               </Select>
            </div>
         </div>

         {getAllBrand.isLoading ? (
            <div className='text-center py-4'>Đang tải...</div>
         ) : (
            <Table>
               <TableHeader>
                  <TableRow>
                     <TableHead className='w-[80px]'>ID</TableHead>
                     <TableHead>Tên thương hiệu</TableHead>
                     <TableHead>Mô tả</TableHead>
                     <TableHead>Trạng thái</TableHead>
                     <TableHead>Ngày tạo</TableHead>
                     <TableHead>Cập nhật lần cuối</TableHead>
                     <TableHead className='text-right'>Thao tác</TableHead>
                  </TableRow>
               </TableHeader>
               <TableBody>
                  {brands.length === 0 ? (
                     <TableRow>
                        <TableCell colSpan={7} className='text-center'>
                           Không có thương hiệu nào
                        </TableCell>
                     </TableRow>
                  ) : (
                     brands.map((brand) => (
                        <TableRow key={brand.id}>
                           <TableCell>{brand.id}</TableCell>
                           <TableCell className='font-medium'>{brand.brandName}</TableCell>
                           <TableCell title={brand.description} className='max-w-[300px] truncate'>
                              {brand.description}
                           </TableCell>
                           <TableCell>
                              {brand.status ? (
                                 <span className='px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs'>
                                    Hoạt động
                                 </span>
                              ) : (
                                 <span className='px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs'>
                                    Không hoạt động
                                 </span>
                              )}
                           </TableCell>
                           <TableCell>{formatDate(brand.createdAt)}</TableCell>
                           <TableCell>{formatDate(brand.updatedAt)}</TableCell>
                           <TableCell className='text-right'>
                              <Button
                                 variant='outline'
                                 size='icon'
                                 onClick={() => openEditDialog(brand)}
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

         {/* Dialog thêm thương hiệu */}
         <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
            <DialogContent>
               <DialogHeader>
                  <DialogTitle>Thêm thương hiệu mới</DialogTitle>
               </DialogHeader>
               <div className='grid gap-4 py-4'>
                  <div className='grid gap-2'>
                     <Label htmlFor='brandName'>Tên thương hiệu</Label>
                     <Input
                        id='brandName'
                        value={newBrand.brandName}
                        onChange={(e) => setNewBrand({ ...newBrand, brandName: e.target.value })}
                        placeholder='Nhập tên thương hiệu'
                     />
                  </div>
                  <div className='grid gap-2'>
                     <Label htmlFor='description'>Mô tả</Label>
                     <Textarea
                        id='description'
                        value={newBrand.description}
                        onChange={(e) => setNewBrand({ ...newBrand, description: e.target.value })}
                        placeholder='Nhập mô tả thương hiệu'
                        rows={3}
                     />
                  </div>
                  <div className='flex items-center gap-2'>
                     <Switch
                        id='status'
                        checked={newBrand.status}
                        onCheckedChange={(checked) => setNewBrand({ ...newBrand, status: checked })}
                     />
                     <Label htmlFor='status'>Kích hoạt</Label>
                  </div>
               </div>
               <DialogFooter>
                  <Button variant='outline' onClick={() => setIsAddDialogOpen(false)}>
                     Hủy
                  </Button>
                  <Button onClick={handleAddBrand} disabled={createBrand.isPending}>
                     {createBrand.isPending ? 'Đang xử lý...' : 'Thêm thương hiệu'}
                  </Button>
               </DialogFooter>
            </DialogContent>
         </Dialog>

         {/* Dialog chỉnh sửa thương hiệu */}
         <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
            <DialogContent>
               <DialogHeader>
                  <DialogTitle>Chỉnh sửa thương hiệu</DialogTitle>
               </DialogHeader>
               {editingBrand && (
                  <div className='grid gap-4 py-4'>
                     <div className='grid gap-2'>
                        <Label htmlFor='editBrandName'>Tên thương hiệu</Label>
                        <Input
                           id='editBrandName'
                           value={editingBrand.brandName}
                           onChange={(e) => setEditingBrand({ ...editingBrand, brandName: e.target.value })}
                           placeholder='Nhập tên thương hiệu'
                        />
                     </div>
                     <div className='grid gap-2'>
                        <Label htmlFor='editDescription'>Mô tả</Label>
                        <Textarea
                           id='editDescription'
                           value={editingBrand.description}
                           onChange={(e) => setEditingBrand({ ...editingBrand, description: e.target.value })}
                           placeholder='Nhập mô tả thương hiệu'
                           rows={3}
                        />
                     </div>
                     <div className='flex items-center gap-2'>
                        <Switch
                           id='editStatus'
                           checked={editingBrand.status}
                           onCheckedChange={(checked) => setEditingBrand({ ...editingBrand, status: checked })}
                        />
                        <Label htmlFor='editStatus'>Kích hoạt</Label>
                     </div>
                  </div>
               )}
               <DialogFooter>
                  <Button variant='outline' onClick={() => setIsEditDialogOpen(false)}>
                     Hủy
                  </Button>
                  <Button onClick={handleEditBrand} disabled={updateBrand.isPending}>
                     {updateBrand.isPending ? 'Đang xử lý...' : 'Cập nhật'}
                  </Button>
               </DialogFooter>
            </DialogContent>
         </Dialog>
      </div>
   )
}

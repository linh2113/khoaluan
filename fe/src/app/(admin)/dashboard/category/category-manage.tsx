'use client'
import { useCreateCategory, useGetAllCategories, useUpdateCategory } from '@/queries/useAdmin'
import React, { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Edit, Plus, Search } from 'lucide-react'
import { CategoryType, GetCategoryQueryParamsType } from '@/types/admin.type'
import { toast } from 'react-toastify'
import Paginate from '@/components/paginate'

export default function CategoryManage() {
   const [currentPage, setCurrentPage] = useState<number>(1)
   const [queryParams, setQueryParams] = useState<GetCategoryQueryParamsType>({
      page: currentPage - 1,
      size: 10,
      sortBy: 'id',
      sortDir: 'desc',
      search: ''
   })
   const [searchTerm, setSearchTerm] = useState('')

   const createCategory = useCreateCategory()
   const updateCategory = useUpdateCategory()
   const getAllCategories = useGetAllCategories(queryParams)
   const categories = getAllCategories.data?.data.data.content || []
   const totalPages = getAllCategories.data?.data.data.totalPages || 0

   const [isAddDialogOpen, setIsAddDialogOpen] = useState(false)
   const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
   const [newCategory, setNewCategory] = useState<CategoryType>({ id: 0, categoryName: '', status: 1 })
   const [editingCategory, setEditingCategory] = useState<CategoryType | null>(null)

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
         search: searchTerm
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

   const handleAddCategory = () => {
      if (!newCategory.categoryName.trim()) {
         toast.error('Tên danh mục không được để trống')
         return
      }

      createCategory.mutate(newCategory, {
         onSuccess: () => {
            setIsAddDialogOpen(false)
            setNewCategory({ id: 0, categoryName: '', status: 1 })
            getAllCategories.refetch()
         }
      })
   }

   const handleEditCategory = () => {
      if (!editingCategory || !editingCategory.categoryName.trim()) {
         toast.error('Tên danh mục không được để trống')
         return
      }

      updateCategory.mutate(editingCategory, {
         onSuccess: () => {
            setIsEditDialogOpen(false)
            setEditingCategory(null)
            getAllCategories.refetch()
         }
      })
   }

   const openEditDialog = (category: CategoryType) => {
      setEditingCategory({ ...category })
      setIsEditDialogOpen(true)
   }

   return (
      <div className='container mx-auto p-6'>
         <div className='flex items-center justify-between flex-wrap gap-3'>
            <h1 className='text-2xl font-bold'>Quản lý danh mục</h1>
            <Button onClick={() => setIsAddDialogOpen(true)}>
               <Plus className='mr-2 h-4 w-4' /> Thêm danh mục
            </Button>
         </div>
         <div className='flex items-center flex-wrap gap-4 my-5'>
            <div className='flex items-center gap-2'>
               <Input
                  placeholder='Tìm kiếm danh mục...'
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
                     <SelectItem value='categoryName-asc'>Tên A-Z</SelectItem>
                     <SelectItem value='categoryName-desc'>Tên Z-A</SelectItem>
                  </SelectContent>
               </Select>
            </div>
         </div>

         {getAllCategories.isLoading ? (
            <div className='text-center py-4'>Đang tải...</div>
         ) : (
            <Table>
               <TableHeader>
                  <TableRow>
                     <TableHead className='w-[100px]'>ID</TableHead>
                     <TableHead>Tên danh mục</TableHead>
                     <TableHead>Trạng thái</TableHead>
                     <TableHead className='text-right'>Thao tác</TableHead>
                  </TableRow>
               </TableHeader>
               <TableBody>
                  {categories.length === 0 ? (
                     <TableRow>
                        <TableCell colSpan={4} className='text-center'>
                           Không có danh mục nào
                        </TableCell>
                     </TableRow>
                  ) : (
                     categories.map((category) => (
                        <TableRow key={category.id}>
                           <TableCell>{category.id}</TableCell>
                           <TableCell>{category.categoryName}</TableCell>
                           <TableCell>
                              {category.status === 1 ? (
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
                                 onClick={() => openEditDialog(category)}
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

         {/* Dialog thêm danh mục */}
         <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
            <DialogContent>
               <DialogHeader>
                  <DialogTitle>Thêm danh mục mới</DialogTitle>
               </DialogHeader>
               <div className='grid gap-4 py-4'>
                  <div className='grid gap-2'>
                     <Label htmlFor='categoryName'>Tên danh mục</Label>
                     <Input
                        id='categoryName'
                        value={newCategory.categoryName}
                        onChange={(e) => setNewCategory({ ...newCategory, categoryName: e.target.value })}
                        placeholder='Nhập tên danh mục'
                     />
                  </div>
                  <div className='grid gap-2'>
                     <Label htmlFor='status'>Trạng thái</Label>
                     <Select
                        value={newCategory.status.toString()}
                        onValueChange={(value) => setNewCategory({ ...newCategory, status: parseInt(value) })}
                     >
                        <SelectTrigger>
                           <SelectValue placeholder='Chọn trạng thái' />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='1'>Hoạt động</SelectItem>
                           <SelectItem value='0'>Không hoạt động</SelectItem>
                        </SelectContent>
                     </Select>
                  </div>
               </div>
               <DialogFooter>
                  <Button variant='outline' onClick={() => setIsAddDialogOpen(false)}>
                     Hủy
                  </Button>
                  <Button onClick={handleAddCategory} disabled={createCategory.isPending}>
                     {createCategory.isPending ? 'Đang xử lý...' : 'Thêm danh mục'}
                  </Button>
               </DialogFooter>
            </DialogContent>
         </Dialog>

         {/* Dialog chỉnh sửa danh mục */}
         <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
            <DialogContent>
               <DialogHeader>
                  <DialogTitle>Chỉnh sửa danh mục</DialogTitle>
               </DialogHeader>
               {editingCategory && (
                  <div className='grid gap-4 py-4'>
                     <div className='grid gap-2'>
                        <Label htmlFor='editCategoryName'>Tên danh mục</Label>
                        <Input
                           id='editCategoryName'
                           value={editingCategory.categoryName}
                           onChange={(e) => setEditingCategory({ ...editingCategory, categoryName: e.target.value })}
                           placeholder='Nhập tên danh mục'
                        />
                     </div>
                     <div className='grid gap-2'>
                        <Label htmlFor='editStatus'>Trạng thái</Label>
                        <Select
                           value={editingCategory.status.toString()}
                           onValueChange={(value) =>
                              setEditingCategory({ ...editingCategory, status: parseInt(value) })
                           }
                        >
                           <SelectTrigger>
                              <SelectValue placeholder='Chọn trạng thái' />
                           </SelectTrigger>
                           <SelectContent>
                              <SelectItem value='1'>Hoạt động</SelectItem>
                              <SelectItem value='0'>Không hoạt động</SelectItem>
                           </SelectContent>
                        </Select>
                     </div>
                  </div>
               )}
               <DialogFooter>
                  <Button variant='outline' onClick={() => setIsEditDialogOpen(false)}>
                     Hủy
                  </Button>
                  <Button onClick={handleEditCategory} disabled={updateCategory.isPending}>
                     {updateCategory.isPending ? 'Đang xử lý...' : 'Cập nhật'}
                  </Button>
               </DialogFooter>
            </DialogContent>
         </Dialog>
      </div>
   )
}

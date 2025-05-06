'use client'
import { useCreateCategory, useGetAllCategories, useUpdateCategory } from '@/queries/useAdmin'
import React, { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Edit, Plus } from 'lucide-react'
import { CategoryType } from '@/types/admin.type'
import { toast } from 'react-toastify'

export default function CategoryManage() {
   const createCategory = useCreateCategory()
   const updateCategory = useUpdateCategory()
   const getAllCategories = useGetAllCategories()
   const categories = getAllCategories.data?.data.data || []

   const [isAddDialogOpen, setIsAddDialogOpen] = useState(false)
   const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
   const [newCategory, setNewCategory] = useState<CategoryType>({ id: 0, categoryName: '', status: 1 })
   const [editingCategory, setEditingCategory] = useState<CategoryType | null>(null)

   const handleAddCategory = () => {
      if (!newCategory.categoryName.trim()) {
         toast.error('Tên danh mục không được để trống')
         return
      }

      createCategory.mutate(newCategory, {
         onSuccess: () => {
            setIsAddDialogOpen(false)
            setNewCategory({ id: 0, categoryName: '', status: 1 })
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
         }
      })
   }

   const openEditDialog = (category: CategoryType) => {
      setEditingCategory({ ...category })
      setIsEditDialogOpen(true)
   }

   return (
      <div className='container mx-auto p-6'>
         <div className='flex justify-between items-center mb-6'>
            <h1 className='text-2xl font-bold'>Quản lý danh mục</h1>
            <Button onClick={() => setIsAddDialogOpen(true)}>
               <Plus className='mr-2 h-4 w-4' /> Thêm danh mục
            </Button>
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

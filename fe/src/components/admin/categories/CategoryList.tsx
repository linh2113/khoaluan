'use client'
import { useGetAllCategories, useUpdateCategory } from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { useState } from 'react'
import { Edit, Plus } from 'lucide-react'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { CategoryForm } from '@/components/admin/categories/CategoryForm'

export default function CategoryList() {
   const { data, isLoading } = useGetAllCategories()
   const updateCategory = useUpdateCategory()

   const handleToggleStatus = (id: number, currentStatus: boolean) => {
      updateCategory.mutate({
         id,
         data: { status: !currentStatus }
      })
   }

   if (isLoading) return <div>Đang tải...</div>

   const categories = data?.data.data || []

   return (
      <div className='space-y-4 p-4'>
         <div className='flex justify-between items-center'>
            <h1 className='text-2xl font-bold'>Quản lý danh mục</h1>
            <Dialog>
               <DialogTrigger asChild>
                  <Button>
                     <Plus className='mr-2 h-4 w-4' />
                     Thêm danh mục
                  </Button>
               </DialogTrigger>
               <DialogContent>
                  <DialogHeader>
                     <DialogTitle>Thêm danh mục mới</DialogTitle>
                  </DialogHeader>
                  <CategoryForm />
               </DialogContent>
            </Dialog>
         </div>

         <div className='rounded-md border'>
            <Table>
               <TableHeader>
                  <TableRow>
                     <TableHead>ID</TableHead>
                     <TableHead>Hình ảnh</TableHead>
                     <TableHead>Tên danh mục</TableHead>
                     <TableHead>Mô tả</TableHead>
                     <TableHead>Trạng thái</TableHead>
                     <TableHead>Thao tác</TableHead>
                  </TableRow>
               </TableHeader>
               <TableBody>
                  {categories.map((category) => (
                     <TableRow key={category.id}>
                        <TableCell>{category.id}</TableCell>
                        <TableCell>
                           {category.logo && (
                              <img
                                 src={category.logo || ''}
                                 alt={category.categoryName}
                                 className='w-12 h-12 object-contain'
                              />
                           )}
                        </TableCell>
                        <TableCell>{category.categoryName}</TableCell>
                        <TableCell className='max-w-xs truncate'>{category.description}</TableCell>
                        <TableCell>
                           <Button
                              variant={category.status ? 'default' : 'outline'}
                              size='sm'
                              onClick={() => handleToggleStatus(category.id, category.status)}
                           >
                              {category.status ? 'Hoạt động' : 'Không hoạt động'}
                           </Button>
                        </TableCell>
                        <TableCell>
                           <Dialog>
                              <DialogTrigger asChild>
                                 <Button variant='outline' size='icon'>
                                    <Edit className='h-4 w-4' />
                                 </Button>
                              </DialogTrigger>
                              <DialogContent>
                                 <DialogHeader>
                                    <DialogTitle>Chỉnh sửa danh mục</DialogTitle>
                                 </DialogHeader>
                                 <CategoryForm categoryId={category.id} />
                              </DialogContent>
                           </Dialog>
                        </TableCell>
                     </TableRow>
                  ))}
               </TableBody>
            </Table>
         </div>
      </div>
   )
}

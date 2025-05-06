'use client'
import { useGetAllBrand, useUpdateBrand } from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Pagination, PaginationContent, PaginationItem, PaginationLink } from '@/components/ui/pagination'
import { useState } from 'react'
import Link from 'next/link'
import { Edit, Plus } from 'lucide-react'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { BrandForm } from '@/components/admin/brands/BrandForm'

export default function BrandList() {
   const [queryParams, setQueryParams] = useState({
      page: 0,
      size: 10,
      sortBy: 'id',
      sortDir: 'desc'
   })

   const { data, isLoading } = useGetAllBrand(queryParams)
   const updateBrand = useUpdateBrand()

   const handleToggleStatus = (id: number, currentStatus: boolean) => {
      updateBrand.mutate({
         id,
         data: { status: !currentStatus }
      })
   }

   if (isLoading) return <div>Đang tải...</div>

   const brands = data?.data.content || []
   const totalPages = data?.data.totalPages || 0

   return (
      <div className='space-y-4 p-4'>
         <div className='flex justify-between items-center'>
            <h1 className='text-2xl font-bold'>Quản lý thương hiệu</h1>
            <Dialog>
               <DialogTrigger asChild>
                  <Button>
                     <Plus className='mr-2 h-4 w-4' />
                     Thêm thương hiệu
                  </Button>
               </DialogTrigger>
               <DialogContent>
                  <DialogHeader>
                     <DialogTitle>Thêm thương hiệu mới</DialogTitle>
                  </DialogHeader>
                  <BrandForm />
               </DialogContent>
            </Dialog>
         </div>

         <div className='rounded-md border'>
            <Table>
               <TableHeader>
                  <TableRow>
                     <TableHead>ID</TableHead>
                     <TableHead>Logo</TableHead>
                     <TableHead>Tên thương hiệu</TableHead>
                     <TableHead>Mô tả</TableHead>
                     <TableHead>Số sản phẩm</TableHead>
                     <TableHead>Trạng thái</TableHead>
                     <TableHead>Thao tác</TableHead>
                  </TableRow>
               </TableHeader>
               <TableBody>
                  {brands.map((brand) => (
                     <TableRow key={brand.id}>
                        <TableCell>{brand.id}</TableCell>
                        <TableCell>
                           {brand.logo && (
                              <img src={brand.logo} alt={brand.brandName} className='w-12 h-12 object-contain' />
                           )}
                        </TableCell>
                        <TableCell>{brand.brandName}</TableCell>
                        <TableCell className='max-w-xs truncate'>{brand.description}</TableCell>
                        <TableCell>{brand.productCount || 0}</TableCell>
                        <TableCell>
                           <Button
                              variant={brand.status ? 'default' : 'outline'}
                              size='sm'
                              onClick={() => handleToggleStatus(brand.id, brand.status)}
                           >
                              {brand.status ? 'Hoạt động' : 'Không hoạt động'}
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
                                    <DialogTitle>Chỉnh sửa thương hiệu</DialogTitle>
                                 </DialogHeader>
                                 <BrandForm brandId={brand.id} />
                              </DialogContent>
                           </Dialog>
                        </TableCell>
                     </TableRow>
                  ))}
               </TableBody>
            </Table>
         </div>

         <Pagination>
            <PaginationContent>
               <PaginationItem>
                  <PaginationLink
                     className={queryParams.page === 0 ? 'disabled-class' : ''}
                     onClick={(e) => {
                        if (queryParams.page === 0) {
                           e.preventDefault()
                           return
                        }
                        setQueryParams({ ...queryParams, page: Math.max(0, queryParams.page - 1) })
                     }}
                  >
                     Trước
                  </PaginationLink>
               </PaginationItem>

               {Array.from({ length: totalPages }).map((_, index) => (
                  <PaginationItem key={index}>
                     <PaginationLink
                        onClick={() => setQueryParams({ ...queryParams, page: index })}
                        isActive={queryParams.page === index}
                     >
                        {index + 1}
                     </PaginationLink>
                  </PaginationItem>
               ))}

               <PaginationItem>
                  <PaginationLink
                     className={queryParams.page === totalPages - 1 ? 'disabled-class' : ''}
                     onClick={(e) => {
                        if (queryParams.page === totalPages - 1) {
                           e.preventDefault()
                           return
                        }
                        setQueryParams({ ...queryParams, page: Math.min(totalPages - 1, queryParams.page + 1) })
                     }}
                  >
                     Sau
                  </PaginationLink>
               </PaginationItem>
            </PaginationContent>
         </Pagination>
      </div>
   )
}

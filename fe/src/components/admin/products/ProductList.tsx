'use client'
import { useGetAllProducts, useUpdateProduct } from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Pagination, PaginationContent, PaginationItem, PaginationLink } from '@/components/ui/pagination'
import { formatCurrency } from '@/lib/utils'
import { useState } from 'react'
import Link from 'next/link'
import { Edit, Plus, Trash } from 'lucide-react'

export default function ProductList() {
   const [page, setPage] = useState(0)
   const [size, setSize] = useState(10)
   const [sortBy, setSortBy] = useState('id')
   const [sortDir, setSortDir] = useState('desc')

   const { data, isLoading } = useGetAllProducts(page, size, sortBy, sortDir)
   const updateProduct = useUpdateProduct()

   const handleToggleStatus = (id: number, currentStatus: boolean) => {
      updateProduct.mutate({
         id,
         data: { status: !currentStatus }
      })
   }

   if (isLoading) return <div>Đang tải...</div>

   const products = data?.data.content || []
   const totalPages = data?.data.totalPages || 0

   return (
      <div className='space-y-4 p-4'>
         <div className='flex justify-between items-center'>
            <h1 className='text-2xl font-bold'>Quản lý sản phẩm</h1>
            <Link href='/admin/products/create'>
               <Button>
                  <Plus className='mr-2 h-4 w-4' />
                  Thêm sản phẩm
               </Button>
            </Link>
         </div>

         <div className='rounded-md border'>
            <Table>
               <TableHeader>
                  <TableRow>
                     <TableHead>ID</TableHead>
                     <TableHead>Hình ảnh</TableHead>
                     <TableHead>Tên sản phẩm</TableHead>
                     <TableHead>Giá</TableHead>
                     <TableHead>Tồn kho</TableHead>
                     <TableHead>Trạng thái</TableHead>
                     <TableHead>Thao tác</TableHead>
                  </TableRow>
               </TableHeader>
               <TableBody>
                  {products.map((product: any) => (
                     <TableRow key={product.id}>
                        <TableCell>{product.id}</TableCell>
                        <TableCell>
                           {product.primaryImage && (
                              <img
                                 src={product.primaryImage}
                                 alt={product.productName}
                                 className='w-16 h-16 object-cover rounded'
                              />
                           )}
                        </TableCell>
                        <TableCell>{product.productName}</TableCell>
                        <TableCell>{formatCurrency(product.price)}</TableCell>
                        <TableCell>{product.stockQuantity}</TableCell>
                        <TableCell>
                           <Button
                              variant={product.status ? 'default' : 'outline'}
                              size='sm'
                              onClick={() => handleToggleStatus(product.id, product.status)}
                           >
                              {product.status ? 'Đang bán' : 'Ngừng bán'}
                           </Button>
                        </TableCell>
                        <TableCell>
                           <div className='flex space-x-2'>
                              <Link href={`/admin/products/edit/${product.id}`}>
                                 <Button variant='outline' size='icon'>
                                    <Edit className='h-4 w-4' />
                                 </Button>
                              </Link>
                           </div>
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
                     onClick={page === 0 ? undefined : () => setPage(Math.max(0, page - 1))}
                     className={page === 0 ? 'cursor-not-allowed opacity-50' : ''}
                  >
                     Trước
                  </PaginationLink>
               </PaginationItem>

               {Array.from({ length: totalPages }).map((_, index) => (
                  <PaginationItem key={index}>
                     <PaginationLink onClick={() => setPage(index)} isActive={page === index}>
                        {index + 1}
                     </PaginationLink>
                  </PaginationItem>
               ))}

               <PaginationItem>
                  <PaginationLink
                     className={page === totalPages - 1 ? 'cursor-not-allowed opacity-50' : ''}
                     onClick={page === totalPages - 1 ? undefined : () => setPage(Math.min(totalPages - 1, page + 1))}
                  >
                     Sau
                  </PaginationLink>
               </PaginationItem>
            </PaginationContent>
         </Pagination>
      </div>
   )
}

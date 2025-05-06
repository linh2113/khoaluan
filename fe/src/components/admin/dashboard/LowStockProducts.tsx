'use client'
import { useGetLowStockProducts } from '@/queries/useAdmin'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { formatCurrency } from '@/lib/utils'
import { AlertTriangle } from 'lucide-react'

export default function LowStockProducts() {
   const { data, isLoading } = useGetLowStockProducts()

   if (isLoading) return <div>Đang tải...</div>

   const products = data?.data.data || []

   return (
      <Card>
         <CardHeader className='flex flex-row items-center'>
            <AlertTriangle className='mr-2 h-4 w-4 text-amber-500' />
            <CardTitle>Sản phẩm sắp hết hàng</CardTitle>
         </CardHeader>
         <CardContent>
            {products.length === 0 ? (
               <p className='text-center text-muted-foreground py-4'>Không có sản phẩm nào sắp hết hàng</p>
            ) : (
               <Table>
                  <TableHeader>
                     <TableRow>
                        <TableHead>Sản phẩm</TableHead>
                        <TableHead>Danh mục</TableHead>
                        <TableHead>Giá</TableHead>
                        <TableHead className='text-right'>Tồn kho</TableHead>
                     </TableRow>
                  </TableHeader>
                  <TableBody>
                     {products.map((product: any) => (
                        <TableRow key={product.id}>
                           <TableCell className='font-medium'>
                              <div className='flex items-center space-x-3'>
                                 {product.primaryImage && (
                                    <img
                                       src={product.primaryImage}
                                       alt={product.name}
                                       className='w-10 h-10 object-cover rounded'
                                    />
                                 )}
                                 <span className='truncate max-w-[200px]'>{product.name}</span>
                              </div>
                           </TableCell>
                           <TableCell>{product.categoryName}</TableCell>
                           <TableCell>{formatCurrency(product.price)}</TableCell>
                           <TableCell className='text-right'>
                              <span className={`font-medium ${product.stock <= 5 ? 'text-red-500' : 'text-amber-500'}`}>
                                 {product.stock}
                              </span>
                           </TableCell>
                        </TableRow>
                     ))}
                  </TableBody>
               </Table>
            )}
         </CardContent>
      </Card>
   )
}

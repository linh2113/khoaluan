'use client'
import { useGetAllOrders, useUpdateOrderStatus, useUpdatePaymentStatus } from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Pagination, PaginationContent, PaginationItem, PaginationLink } from '@/components/ui/pagination'
import { formatCurrency } from '@/lib/utils'
import { useState } from 'react'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { Eye } from 'lucide-react'
import { OrderDetails } from '@/components/admin/orders/OrderDetails'

export default function OrderList() {
   const [page, setPage] = useState(0)
   const [size, setSize] = useState(10)
   const [sortBy, setSortBy] = useState('id')
   const [sortDir, setSortDir] = useState('desc')

   const { data, isLoading } = useGetAllOrders(page, size, sortBy, sortDir)
   const updateOrderStatus = useUpdateOrderStatus()
   const updatePaymentStatus = useUpdatePaymentStatus()

   const handleUpdateOrderStatus = (id: number, status: number) => {
      updateOrderStatus.mutate({ id, status })
   }

   const handleUpdatePaymentStatus = (id: number, paymentStatus: string) => {
      updatePaymentStatus.mutate({ id, paymentStatus })
   }

   if (isLoading) return <div>Đang tải...</div>

   const orders = data?.data.content || []
   const totalPages = data?.data.totalPages || 0

   // Order status mapping
   const orderStatusMap = {
      0: { label: 'Chờ xác nhận', color: 'bg-yellow-100 text-yellow-800' },
      1: { label: 'Đã xác nhận', color: 'bg-blue-100 text-blue-800' },
      2: { label: 'Đang giao hàng', color: 'bg-purple-100 text-purple-800' },
      3: { label: 'Đã giao hàng', color: 'bg-green-100 text-green-800' },
      4: { label: 'Đã hủy', color: 'bg-red-100 text-red-800' }
   }

   // Payment status mapping
   const paymentStatusMap = {
      PENDING: { label: 'Chờ thanh toán', color: 'bg-yellow-100 text-yellow-800' },
      PAID: { label: 'Đã thanh toán', color: 'bg-green-100 text-green-800' },
      FAILED: { label: 'Thanh toán thất bại', color: 'bg-red-100 text-red-800' },
      REFUNDED: { label: 'Đã hoàn tiền', color: 'bg-blue-100 text-blue-800' }
   }

   return (
      <div className='space-y-4 p-4'>
         <div className='flex justify-between items-center'>
            <h1 className='text-2xl font-bold'>Quản lý đơn hàng</h1>
         </div>

         <div className='rounded-md border'>
            <Table>
               <TableHeader>
                  <TableRow>
                     <TableHead>ID</TableHead>
                     <TableHead>Khách hàng</TableHead>
                     <TableHead>Ngày đặt</TableHead>
                     <TableHead>Tổng tiền</TableHead>
                     <TableHead>Trạng thái đơn hàng</TableHead>
                     <TableHead>Trạng thái thanh toán</TableHead>
                     <TableHead>Thao tác</TableHead>
                  </TableRow>
               </TableHeader>
               <TableBody>
                  {orders.map((order) => (
                     <TableRow key={order.id}>
                        <TableCell>{order.id}</TableCell>
                        <TableCell>{order.customerName}</TableCell>
                        <TableCell>{new Date(order.orderDate).toLocaleDateString('vi-VN')}</TableCell>
                        <TableCell>{formatCurrency(order.totalAmount)}</TableCell>
                        <TableCell>
                           <Select
                              defaultValue={order.status.toString()}
                              onValueChange={(value) => handleUpdateOrderStatus(order.id, parseInt(value))}
                           >
                              <SelectTrigger className={`w-[180px] ${orderStatusMap[order.status]?.color}`}>
                                 <SelectValue placeholder='Trạng thái đơn hàng' />
                              </SelectTrigger>
                              <SelectContent>
                                 {Object.entries(orderStatusMap).map(([value, { label }]) => (
                                    <SelectItem key={value} value={value}>
                                       {label}
                                    </SelectItem>
                                 ))}
                              </SelectContent>
                           </Select>
                        </TableCell>
                        <TableCell>
                           <Select
                              defaultValue={order.paymentStatus}
                              onValueChange={(value) => handleUpdatePaymentStatus(order.id, value)}
                           >
                              <SelectTrigger className={`w-[180px] ${paymentStatusMap[order.paymentStatus]?.color}`}>
                                 <SelectValue placeholder='Trạng thái thanh toán' />
                              </SelectTrigger>
                              <SelectContent>
                                 {Object.entries(paymentStatusMap).map(([value, { label }]) => (
                                    <SelectItem key={value} value={value}>
                                       {label}
                                    </SelectItem>
                                 ))}
                              </SelectContent>
                           </Select>
                        </TableCell>
                        <TableCell>
                           <Dialog>
                              <DialogTrigger asChild>
                                 <Button variant='outline' size='icon'>
                                    <Eye className='h-4 w-4' />
                                 </Button>
                              </DialogTrigger>
                              <DialogContent className='max-w-3xl'>
                                 <DialogHeader>
                                    <DialogTitle>Chi tiết đơn hàng #{order.id}</DialogTitle>
                                 </DialogHeader>
                                 <OrderDetails order={order} />
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

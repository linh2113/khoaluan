'use client'
import { useGetAllOrders } from '@/queries/useAdmin'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { formatCurrency } from '@/lib/utils'
import { ShoppingBag } from 'lucide-react'
import { Badge } from '@/components/ui/badge'

export default function RecentOrders() {
   const { data, isLoading } = useGetAllOrders(0, 5)

   if (isLoading) return <div>Đang tải...</div>

   const orders = data?.data?.content || []

   // Map for order status colors
   const orderStatusMap: Record<string, string> = {
      PENDING: 'bg-yellow-100 text-yellow-800',
      PROCESSING: 'bg-blue-100 text-blue-800',
      SHIPPED: 'bg-purple-100 text-purple-800',
      DELIVERED: 'bg-green-100 text-green-800',
      CANCELLED: 'bg-red-100 text-red-800'
   }

   return (
      <Card>
         <CardHeader className='flex flex-row items-center'>
            <ShoppingBag className='mr-2 h-4 w-4' />
            <CardTitle>Đơn hàng gần đây</CardTitle>
         </CardHeader>
         <CardContent>
            {orders.length === 0 ? (
               <p className='text-center text-muted-foreground py-4'>Không có đơn hàng nào</p>
            ) : (
               <Table>
                  <TableHeader>
                     <TableRow>
                        <TableHead>Mã đơn</TableHead>
                        <TableHead>Khách hàng</TableHead>
                        <TableHead>Trạng thái</TableHead>
                        <TableHead className='text-right'>Tổng tiền</TableHead>
                     </TableRow>
                  </TableHeader>
                  <TableBody>
                     {orders.map((order: any) => (
                        <TableRow key={order.id}>
                           <TableCell className='font-medium'>#{order.id}</TableCell>
                           <TableCell>{order.customerName}</TableCell>
                           <TableCell>
                              <Badge variant='outline' className={orderStatusMap[order.status]}>
                                 {order.status}
                              </Badge>
                           </TableCell>
                           <TableCell className='text-right'>{formatCurrency(order.totalAmount)}</TableCell>
                        </TableRow>
                     ))}
                  </TableBody>
               </Table>
            )}
         </CardContent>
      </Card>
   )
}

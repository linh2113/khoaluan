'use client'
import { formatCurrency } from '@/lib/utils'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'

interface OrderDetailsProps {
   order: any
}

export function OrderDetails({ order }: OrderDetailsProps) {
   // Order status mapping
   const orderStatusMap = {
      0: { label: 'Chờ xác nhận', color: 'text-yellow-600' },
      1: { label: 'Đã xác nhận', color: 'text-blue-600' },
      2: { label: 'Đang giao hàng', color: 'text-purple-600' },
      3: { label: 'Đã giao hàng', color: 'text-green-600' },
      4: { label: 'Đã hủy', color: 'text-red-600' }
   }

   // Payment status mapping
   const paymentStatusMap = {
      PENDING: { label: 'Chờ thanh toán', color: 'text-yellow-600' },
      PAID: { label: 'Đã thanh toán', color: 'text-green-600' },
      FAILED: { label: 'Thanh toán thất bại', color: 'text-red-600' },
      REFUNDED: { label: 'Đã hoàn tiền', color: 'text-blue-600' }
   }

   return (
      <div className='space-y-6'>
         <div className='grid grid-cols-1 md:grid-cols-2 gap-4'>
            <Card>
               <CardHeader>
                  <CardTitle className='text-lg'>Thông tin khách hàng</CardTitle>
               </CardHeader>
               <CardContent>
                  <dl className='space-y-2'>
                     <div className='flex justify-between'>
                        <dt className='font-medium'>Tên khách hàng:</dt>
                        <dd>{order.customerName}</dd>
                     </div>
                     <div className='flex justify-between'>
                        <dt className='font-medium'>Email:</dt>
                        <dd>{order.customerEmail}</dd>
                     </div>
                     <div className='flex justify-between'>
                        <dt className='font-medium'>Số điện thoại:</dt>
                        <dd>{order.customerPhone}</dd>
                     </div>
                  </dl>
               </CardContent>
            </Card>

            <Card>
               <CardHeader>
                  <CardTitle className='text-lg'>Thông tin đơn hàng</CardTitle>
               </CardHeader>
               <CardContent>
                  <dl className='space-y-2'>
                     <div className='flex justify-between'>
                        <dt className='font-medium'>Mã đơn hàng:</dt>
                        <dd>#{order.id}</dd>
                     </div>
                     <div className='flex justify-between'>
                        <dt className='font-medium'>Ngày đặt:</dt>
                        <dd>{new Date(order.orderDate).toLocaleDateString('vi-VN')}</dd>
                     </div>
                     <div className='flex justify-between'>
                        <dt className='font-medium'>Trạng thái đơn hàng:</dt>
                        <dd className={orderStatusMap[order.status]?.color}>{orderStatusMap[order.status]?.label}</dd>
                     </div>
                     <div className='flex justify-between'>
                        <dt className='font-medium'>Trạng thái thanh toán:</dt>
                        <dd className={paymentStatusMap[order.paymentStatus]?.color}>
                           {paymentStatusMap[order.paymentStatus]?.label}
                        </dd>
                     </div>
                  </dl>
               </CardContent>
            </Card>
         </div>

         <Card>
            <CardHeader>
               <CardTitle className='text-lg'>Địa chỉ giao hàng</CardTitle>
            </CardHeader>
            <CardContent>
               <p>{order.shippingAddress}</p>
            </CardContent>
         </Card>

         <Card>
            <CardHeader>
               <CardTitle className='text-lg'>Sản phẩm</CardTitle>
            </CardHeader>
            <CardContent>
               <Table>
                  <TableHeader>
                     <TableRow>
                        <TableHead>Sản phẩm</TableHead>
                        <TableHead>Đơn giá</TableHead>
                        <TableHead>Số lượng</TableHead>
                        <TableHead className='text-right'>Thành tiền</TableHead>
                     </TableRow>
                  </TableHeader>
                  <TableBody>
                     {order.orderItems?.map((item, index) => (
                        <TableRow key={index}>
                           <TableCell>
                              <div className='flex items-center space-x-3'>
                                 {item.productImage && (
                                    <img
                                       src={item.productImage}
                                       alt={item.productName}
                                       className='w-12 h-12 object-cover rounded'
                                    />
                                 )}
                                 <div>
                                    <div className='font-medium'>{item.productName}</div>
                                    {item.productVariant && (
                                       <div className='text-sm text-muted-foreground'>{item.productVariant}</div>
                                    )}
                                 </div>
                              </div>
                           </TableCell>
                           <TableCell>{formatCurrency(item.price)}</TableCell>
                           <TableCell>{item.quantity}</TableCell>
                           <TableCell className='text-right'>{formatCurrency(item.price * item.quantity)}</TableCell>
                        </TableRow>
                     ))}
                  </TableBody>
               </Table>
            </CardContent>
         </Card>

         <Card>
            <CardHeader>
               <CardTitle className='text-lg'>Tổng cộng</CardTitle>
            </CardHeader>
            <CardContent>
               <dl className='space-y-2'>
                  <div className='flex justify-between'>
                     <dt className='font-medium'>Tạm tính:</dt>
                     <dd>{formatCurrency(order.subtotal || 0)}</dd>
                  </div>
                  <div className='flex justify-between'>
                     <dt className='font-medium'>Phí vận chuyển:</dt>
                     <dd>{formatCurrency(order.shippingFee || 0)}</dd>
                  </div>
                  {order.discount > 0 && (
                     <div className='flex justify-between'>
                        <dt className='font-medium'>Giảm giá:</dt>
                        <dd>-{formatCurrency(order.discount || 0)}</dd>
                     </div>
                  )}
                  <div className='flex justify-between border-t pt-2 font-bold'>
                     <dt>Tổng cộng:</dt>
                     <dd>{formatCurrency(order.totalAmount)}</dd>
                  </div>
               </dl>
            </CardContent>
         </Card>
      </div>
   )
}

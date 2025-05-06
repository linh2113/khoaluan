'use client'
import React, { useState } from 'react'
import { useAppContext } from '@/context/app.context'
import { useGetOrdersByUserId, useDeleteOrder, useUpdateOrderStatus } from '@/queries/useOrder'
import { useAddToCart } from '@/queries/useCart'
import { formatCurrency } from '@/lib/utils'
import { format } from 'date-fns'
import { vi } from 'date-fns/locale'
import { ORDER_STATUS } from '@/types/order.type'
import Image from 'next/image'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
   Dialog,
   DialogContent,
   DialogDescription,
   DialogFooter,
   DialogHeader,
   DialogTitle
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Loader2, ShoppingCart } from 'lucide-react'
import { toast } from 'react-toastify'
import { useRouter } from 'next/navigation'

export default function Purchase() {
   const router = useRouter()
   const { userId } = useAppContext()
   const { data: ordersData, isLoading } = useGetOrdersByUserId(userId!)
   const deleteOrderMutation = useDeleteOrder()
   const updateOrderStatusMutation = useUpdateOrderStatus()
   const addToCartMutation = useAddToCart()

   const [selectedOrderId, setSelectedOrderId] = useState<number | null>(null)
   const [isOpenCancelDialog, setIsOpenCancelDialog] = useState(false)
   const [isOpenDetailDialog, setIsOpenDetailDialog] = useState(false)
   const [isOpenDeleteDialog, setIsOpenDeleteDialog] = useState(false)
   const [isReordering, setIsReordering] = useState(false)

   const orders = ordersData?.data.data || []

   // Lọc đơn hàng theo trạng thái
   const pendingOrders = orders.filter((order) => order.orderStatus === ORDER_STATUS.PENDING)
   const processingOrders = orders.filter((order) => order.orderStatus === ORDER_STATUS.PROCESSING)
   const shippingOrders = orders.filter((order) => order.orderStatus === ORDER_STATUS.SHIPPED)
   const deliveredOrders = orders.filter((order) => order.orderStatus === ORDER_STATUS.DELIVERED)
   const completedOrders = orders.filter((order) => order.orderStatus === ORDER_STATUS.COMPLETED)
   const cancelledOrders = orders.filter((order) => order.orderStatus === ORDER_STATUS.CANCELLED)

   // Lấy thông tin đơn hàng được chọn
   const selectedOrder = orders.find((order) => order.id === selectedOrderId)

   // Xử lý hủy đơn hàng
   const handleCancelOrder = () => {
      if (selectedOrderId) {
         updateOrderStatusMutation.mutate(
            { id: selectedOrderId, status: ORDER_STATUS.CANCELLED },
            {
               onSuccess: () => {
                  setIsOpenCancelDialog(false)
                  toast.success('Hủy đơn hàng thành công')
               }
            }
         )
      }
   }

   // Xử lý xóa đơn hàng
   const handleDeleteOrder = () => {
      if (selectedOrderId) {
         deleteOrderMutation.mutate(selectedOrderId, {
            onSuccess: () => {
               setIsOpenDeleteDialog(false)
               toast.success('Xóa đơn hàng thành công')
            }
         })
      }
   }

   // Xử lý mua lại
   const handleReorder = (order: any) => {
      if (!userId) {
         toast.error('Vui lòng đăng nhập để thực hiện chức năng này')
         return
      }

      setIsReordering(true)

      // Thêm từng sản phẩm vào giỏ hàng
      const addProductsToCart = async () => {
         try {
            for (const item of order.orderDetails) {
               await addToCartMutation.mutateAsync({
                  userId: userId,
                  productId: item.productId,
                  quantity: item.quantity
               })
            }
            router.push('/cart')
         } catch (error) {
            toast.error('Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng')
         } finally {
            setIsReordering(false)
         }
      }

      addProductsToCart()
   }

   // Hiển thị trạng thái đơn hàng
   const renderOrderStatusBadge = (status: number) => {
      switch (status) {
         case ORDER_STATUS.PENDING:
            return (
               <Badge variant='outline' className='bg-yellow-50 text-yellow-600 border-yellow-300'>
                  Chờ xác nhận
               </Badge>
            )
         case ORDER_STATUS.PROCESSING:
            return (
               <Badge variant='outline' className='bg-blue-50 text-blue-600 border-blue-300'>
                  Đang xử lý
               </Badge>
            )
         case ORDER_STATUS.SHIPPED:
            return (
               <Badge variant='outline' className='bg-indigo-50 text-indigo-600 border-indigo-300'>
                  Đang giao hàng
               </Badge>
            )
         case ORDER_STATUS.DELIVERED:
            return (
               <Badge variant='outline' className='bg-green-50 text-green-600 border-green-300'>
                  Đã giao hàng
               </Badge>
            )
         case ORDER_STATUS.COMPLETED:
            return (
               <Badge variant='outline' className='bg-green-50 text-green-600 border-green-300'>
                  Hoàn thành
               </Badge>
            )
         case ORDER_STATUS.CANCELLED:
            return (
               <Badge variant='outline' className='bg-red-50 text-red-600 border-red-300'>
                  Đã hủy
               </Badge>
            )
         default:
            return <Badge variant='outline'>Không xác định</Badge>
      }
   }

   // Hiển thị trạng thái thanh toán
   const renderPaymentStatusBadge = (status: string) => {
      switch (status) {
         case 'Pending':
            return (
               <Badge variant='outline' className='bg-yellow-50 text-yellow-600 border-yellow-300'>
                  Chờ thanh toán
               </Badge>
            )
         case 'Paid':
            return (
               <Badge variant='outline' className='bg-green-50 text-green-600 border-green-300'>
                  Đã thanh toán
               </Badge>
            )
         case 'Failed':
            return (
               <Badge variant='outline' className='bg-red-50 text-red-600 border-red-300'>
                  Thanh toán thất bại
               </Badge>
            )
         case 'Refunded':
            return (
               <Badge variant='outline' className='bg-blue-50 text-blue-600 border-blue-300'>
                  Đã hoàn tiền
               </Badge>
            )
         default:
            return <Badge variant='outline'>Không xác định</Badge>
      }
   }

   // Hiển thị danh sách đơn hàng
   const renderOrderList = (orderList: typeof orders) => {
      if (orderList.length === 0) {
         return (
            <div className='text-center py-10'>
               <p className='text-gray-500'>Không có đơn hàng nào</p>
            </div>
         )
      }

      return orderList.map((order) => (
         <div key={order.id} className='border rounded-lg p-4 mb-4 shadow-sm'>
            <div className='flex justify-between items-center mb-3 pb-3 border-b'>
               <div>
                  <span className='text-gray-500 mr-2'>Mã đơn hàng:</span>
                  <span className='font-medium'>#{order.id}</span>
               </div>
               <div className='flex items-center gap-2'>
                  {renderOrderStatusBadge(order.orderStatus)}
                  {renderPaymentStatusBadge(order.paymentStatus)}
               </div>
            </div>

            {/* Hiển thị sản phẩm đầu tiên và số lượng sản phẩm còn lại */}
            {order.orderDetails.length > 0 && (
               <div className='mb-3'>
                  <div className='flex items-center gap-4 mb-2'>
                     <div className='w-16 h-16 flex-shrink-0'>
                        <Image
                           src={order.orderDetails[0].productImage}
                           alt={order.orderDetails[0].productName}
                           width={64}
                           height={64}
                           className='rounded object-cover'
                        />
                     </div>
                     <div className='flex-grow'>
                        <p className='font-medium line-clamp-1'>{order.orderDetails[0].productName}</p>
                        <p className='text-sm text-gray-500'>
                           {formatCurrency(order.orderDetails[0].price)} x {order.orderDetails[0].quantity}
                        </p>
                     </div>
                  </div>

                  {order.orderDetails.length > 1 && (
                     <p className='text-sm text-gray-500 ml-20'>và {order.orderDetails.length - 1} sản phẩm khác</p>
                  )}
               </div>
            )}

            <div className='flex justify-between items-center mb-3'>
               <div>
                  <span className='text-gray-500 mr-2'>Ngày đặt:</span>
                  <span>{format(new Date(order.createAt), 'dd/MM/yyyy HH:mm', { locale: vi })}</span>
               </div>
               <div>
                  <span className='text-gray-500 mr-2'>Tổng tiền:</span>
                  <span className='text-lg font-medium text-secondaryColor'>
                     {formatCurrency(order.totalPrice + order.shippingFee)}
                  </span>
               </div>
            </div>

            <div className='flex justify-end gap-2 mt-3'>
               <Button
                  variant='outline'
                  onClick={() => {
                     setSelectedOrderId(order.id)
                     setIsOpenDetailDialog(true)
                  }}
               >
                  Xem chi tiết
               </Button>

               {/* Chỉ hiển thị nút hủy đơn hàng khi đơn hàng đang ở trạng thái chờ xác nhận hoặc đang xử lý */}
               {(order.orderStatus === ORDER_STATUS.PENDING || order.orderStatus === ORDER_STATUS.PROCESSING) && (
                  <Button
                     variant='destructive'
                     onClick={() => {
                        setSelectedOrderId(order.id)
                        setIsOpenCancelDialog(true)
                     }}
                  >
                     Hủy đơn hàng
                  </Button>
               )}

               {/* Hiển thị nút mua lại khi đơn hàng đã hoàn thành hoặc đã hủy */}
               {(order.orderStatus === ORDER_STATUS.COMPLETED || order.orderStatus === ORDER_STATUS.CANCELLED) && (
                  <Button onClick={() => handleReorder(order)} disabled={isReordering}>
                     {isReordering ? (
                        <>
                           <Loader2 className='mr-2 h-4 w-4 animate-spin' />
                           Đang xử lý
                        </>
                     ) : (
                        <>
                           <ShoppingCart className='mr-2 h-4 w-4' />
                           Mua lại
                        </>
                     )}
                  </Button>
               )}

               {/* Hiển thị nút xóa đơn hàng cho đơn hàng đã hoàn thành hoặc đã hủy */}
               {(order.orderStatus === ORDER_STATUS.COMPLETED || order.orderStatus === ORDER_STATUS.CANCELLED) && (
                  <Button
                     variant='destructive'
                     onClick={() => {
                        setSelectedOrderId(order.id)
                        setIsOpenDeleteDialog(true)
                     }}
                  >
                     Xóa
                  </Button>
               )}
            </div>
         </div>
      ))
   }

   if (isLoading) {
      return (
         <div className='flex items-center justify-center min-h-[400px]'>
            <Loader2 className='h-8 w-8 animate-spin text-secondaryColor' />
         </div>
      )
   }

   return (
      <div className='container my-10'>
         <h1 className='text-2xl font-bold mb-6'>Đơn hàng của tôi</h1>

         <Tabs defaultValue='all'>
            <TabsList className='mb-6 flex flex-wrap'>
               <TabsTrigger value='all'>Tất cả ({orders.length})</TabsTrigger>
               <TabsTrigger value='pending'>Chờ xác nhận ({pendingOrders.length})</TabsTrigger>
               <TabsTrigger value='processing'>Đang xử lý ({processingOrders.length})</TabsTrigger>
               <TabsTrigger value='shipping'>Đang giao ({shippingOrders.length})</TabsTrigger>
               <TabsTrigger value='delivered'>Đã giao ({deliveredOrders.length})</TabsTrigger>
               <TabsTrigger value='completed'>Hoàn thành ({completedOrders.length})</TabsTrigger>
               <TabsTrigger value='cancelled'>Đã hủy ({cancelledOrders.length})</TabsTrigger>
            </TabsList>

            <TabsContent value='all'>{renderOrderList(orders)}</TabsContent>

            <TabsContent value='pending'>{renderOrderList(pendingOrders)}</TabsContent>

            <TabsContent value='processing'>{renderOrderList(processingOrders)}</TabsContent>

            <TabsContent value='shipping'>{renderOrderList(shippingOrders)}</TabsContent>

            <TabsContent value='delivered'>{renderOrderList(deliveredOrders)}</TabsContent>

            <TabsContent value='completed'>{renderOrderList(completedOrders)}</TabsContent>

            <TabsContent value='cancelled'>{renderOrderList(cancelledOrders)}</TabsContent>
         </Tabs>

         {/* Dialog xác nhận hủy đơn hàng */}
         <Dialog open={isOpenCancelDialog} onOpenChange={setIsOpenCancelDialog}>
            <DialogContent>
               <DialogHeader>
                  <DialogTitle>Xác nhận hủy đơn hàng</DialogTitle>
                  <DialogDescription>
                     Bạn có chắc chắn muốn hủy đơn hàng #{selectedOrderId} không? Hành động này không thể hoàn tác.
                  </DialogDescription>
               </DialogHeader>
               <DialogFooter>
                  <Button variant='outline' onClick={() => setIsOpenCancelDialog(false)}>
                     Hủy
                  </Button>
                  <Button
                     variant='destructive'
                     onClick={handleCancelOrder}
                     disabled={updateOrderStatusMutation.isPending}
                  >
                     {updateOrderStatusMutation.isPending ? (
                        <>
                           <Loader2 className='mr-2 h-4 w-4 animate-spin' />
                           Đang xử lý
                        </>
                     ) : (
                        'Xác nhận hủy'
                     )}
                  </Button>
               </DialogFooter>
            </DialogContent>
         </Dialog>

         {/* Dialog chi tiết đơn hàng */}
         <Dialog open={isOpenDetailDialog} onOpenChange={setIsOpenDetailDialog}>
            <DialogContent className='max-w-3xl'>
               <DialogHeader>
                  <DialogTitle>Chi tiết đơn hàng #{selectedOrderId}</DialogTitle>
               </DialogHeader>

               {selectedOrder && (
                  <div className='space-y-4 max-h-[70vh] overflow-y-auto pr-2'>
                     {/* Thông tin đơn hàng */}
                     <div className='grid grid-cols-2 gap-4'>
                        <div>
                           <h3 className='font-medium text-gray-500'>Trạng thái đơn hàng</h3>
                           <div>{renderOrderStatusBadge(selectedOrder.orderStatus)}</div>
                        </div>
                        <div>
                           <h3 className='font-medium text-gray-500'>Trạng thái thanh toán</h3>
                           <div>{renderPaymentStatusBadge(selectedOrder.paymentStatus)}</div>
                        </div>
                        <div>
                           <h3 className='font-medium text-gray-500'>Ngày đặt hàng</h3>
                           <p>{format(new Date(selectedOrder.createAt), 'dd/MM/yyyy HH:mm', { locale: vi })}</p>
                        </div>
                        <div>
                           <h3 className='font-medium text-gray-500'>Phương thức thanh toán</h3>
                           <p>{selectedOrder.paymentMethodName}</p>
                        </div>
                     </div>

                     {/* Thông tin giao hàng */}
                     <div className='border-t pt-4'>
                        <h3 className='font-medium mb-2'>Thông tin giao hàng</h3>
                        <div className='grid grid-cols-2 gap-4'>
                           <div>
                              <p className='text-gray-500'>Người nhận</p>
                              <p>{selectedOrder.userName}</p>
                           </div>
                           <div>
                              <p className='text-gray-500'>Số điện thoại</p>
                              <p>{selectedOrder.phoneNumber}</p>
                           </div>
                           <div className='col-span-2'>
                              <p className='text-gray-500'>Địa chỉ</p>
                              <p>{selectedOrder.address}</p>
                           </div>
                           <div>
                              <p className='text-gray-500'>Phương thức vận chuyển</p>
                              <p>{selectedOrder.shippingMethodName}</p>
                           </div>
                           {selectedOrder.trackingNumber && (
                              <div>
                                 <p className='text-gray-500'>Mã vận đơn</p>
                                 <p>{selectedOrder.trackingNumber}</p>
                              </div>
                           )}
                        </div>
                     </div>

                     {/* Danh sách sản phẩm */}
                     <div className='border-t pt-4'>
                        <h3 className='font-medium mb-2'>Sản phẩm</h3>
                        <div className='space-y-3'>
                           {selectedOrder.orderDetails.map((item) => (
                              <div key={item.id} className='flex items-center gap-4 border-b pb-3'>
                                 <div className='w-16 h-16 flex-shrink-0'>
                                    <Image
                                       src={item.productImage}
                                       alt={item.productName}
                                       width={64}
                                       height={64}
                                       className='rounded object-cover'
                                    />
                                 </div>
                                 <div className='flex-grow'>
                                    <p className='font-medium'>{item.productName}</p>
                                    <p className='text-sm text-gray-500'>
                                       {formatCurrency(item.price)} x {item.quantity}
                                    </p>
                                 </div>
                                 <div className='text-right'>
                                    <p className='font-medium text-secondaryColor'>{formatCurrency(item.totalPrice)}</p>
                                 </div>
                              </div>
                           ))}
                        </div>
                     </div>

                     {/* Tổng tiền */}
                     <div className='border-t pt-4'>
                        <div className='flex justify-between mb-1'>
                           <span>Tạm tính</span>
                           <span>{formatCurrency(selectedOrder.totalPrice)}</span>
                        </div>
                        <div className='flex justify-between mb-1'>
                           <span>Phí vận chuyển</span>
                           <span>{formatCurrency(selectedOrder.shippingFee)}</span>
                        </div>
                        <div className='flex justify-between font-medium text-lg'>
                           <span>Tổng cộng</span>
                           <span className='text-secondaryColor'>
                              {formatCurrency(selectedOrder.totalPrice + selectedOrder.shippingFee)}
                           </span>
                        </div>
                     </div>
                  </div>
               )}

               <DialogFooter>
                  <Button variant='outline' onClick={() => setIsOpenDetailDialog(false)}>
                     Đóng
                  </Button>

                  {/* Hiển thị các nút thao tác tùy theo trạng thái đơn hàng */}
                  {selectedOrder && (
                     <>
                        {(selectedOrder.orderStatus === ORDER_STATUS.PENDING ||
                           selectedOrder.orderStatus === ORDER_STATUS.PROCESSING) && (
                           <Button
                              variant='destructive'
                              onClick={() => {
                                 setIsOpenDetailDialog(false)
                                 setIsOpenCancelDialog(true)
                              }}
                           >
                              Hủy đơn hàng
                           </Button>
                        )}

                        {selectedOrder.orderStatus === ORDER_STATUS.DELIVERED && (
                           <Button
                              onClick={() => {
                                 updateOrderStatusMutation.mutate(
                                    { id: selectedOrder.id, status: ORDER_STATUS.COMPLETED },
                                    {
                                       onSuccess: () => {
                                          setIsOpenDetailDialog(false)
                                       }
                                    }
                                 )
                              }}
                              disabled={updateOrderStatusMutation.isPending}
                           >
                              {updateOrderStatusMutation.isPending ? (
                                 <>
                                    <Loader2 className='mr-2 h-4 w-4 animate-spin' />
                                    Đang xử lý
                                 </>
                              ) : (
                                 'Đã nhận hàng'
                              )}
                           </Button>
                        )}

                        {(selectedOrder.orderStatus === ORDER_STATUS.COMPLETED ||
                           selectedOrder.orderStatus === ORDER_STATUS.CANCELLED) && (
                           <Button onClick={() => handleReorder(selectedOrder)} disabled={isReordering}>
                              {isReordering ? (
                                 <>
                                    <Loader2 className='mr-2 h-4 w-4 animate-spin' />
                                    Đang xử lý
                                 </>
                              ) : (
                                 <>
                                    <ShoppingCart className='mr-2 h-4 w-4' />
                                    Mua lại
                                 </>
                              )}
                           </Button>
                        )}
                     </>
                  )}
               </DialogFooter>
            </DialogContent>
         </Dialog>

         {/* Dialog xác nhận xóa đơn hàng */}
         <Dialog open={isOpenDeleteDialog} onOpenChange={setIsOpenDeleteDialog}>
            <DialogContent>
               <DialogHeader>
                  <DialogTitle>Xác nhận xóa đơn hàng</DialogTitle>
                  <DialogDescription>
                     Bạn có chắc chắn muốn xóa đơn hàng #{selectedOrderId} khỏi lịch sử? Hành động này không thể hoàn
                     tác.
                  </DialogDescription>
               </DialogHeader>
               <DialogFooter>
                  <Button variant='outline' onClick={() => setIsOpenDeleteDialog(false)}>
                     Hủy
                  </Button>
                  <Button variant='destructive' onClick={handleDeleteOrder} disabled={deleteOrderMutation.isPending}>
                     {deleteOrderMutation.isPending ? (
                        <>
                           <Loader2 className='mr-2 h-4 w-4 animate-spin' />
                           Đang xử lý
                        </>
                     ) : (
                        'Xác nhận xóa'
                     )}
                  </Button>
               </DialogFooter>
            </DialogContent>
         </Dialog>
      </div>
   )
}

'use client'
import { useGetDashboardStatistics } from '@/queries/useAdmin'
import React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { formatCurrency } from '@/lib/utils'
import { format } from 'date-fns'
import { vi } from 'date-fns/locale'
import {
   BarChart,
   Bar,
   XAxis,
   YAxis,
   CartesianGrid,
   Tooltip,
   ResponsiveContainer,
   PieChart,
   Pie,
   Cell,
   Legend
} from 'recharts'
import { Users, Package, ShoppingCart, DollarSign, TrendingUp, Clock, Truck, CheckCircle, XCircle } from 'lucide-react'
import { DashboardStatisticsType } from '@/types/admin.type'

export default function Statistics() {
   const { data } = useGetDashboardStatistics()
   const statistics = data?.data.data as DashboardStatisticsType

   // Dữ liệu cho biểu đồ trạng thái đơn hàng
   const orderStatusData = [
      { name: 'Chờ xác nhận', value: statistics?.pendingOrders || 0, color: '#f59e0b' },
      { name: 'Đang xử lý', value: statistics?.processingOrders || 0, color: '#3b82f6' },
      { name: 'Đang giao', value: statistics?.shippedOrders || 0, color: '#10b981' },
      { name: 'Đã giao', value: statistics?.deliveredOrders || 0, color: '#16a34a' },
      { name: 'Đã hủy', value: statistics?.cancelledOrders || 0, color: '#ef4444' }
   ]

   // Format ngày tháng
   const formatDate = (dateString: string) => {
      try {
         return format(new Date(dateString), 'dd/MM/yyyy HH:mm', { locale: vi })
      } catch (error) {
         return 'Không xác định'
      }
   }

   // Lấy trạng thái đơn hàng
   const getOrderStatus = (status: number) => {
      switch (status) {
         case 0:
            return <span className='px-2 py-1 bg-amber-100 text-amber-800 rounded-full text-xs'>Chờ xác nhận</span>
         case 1:
            return <span className='px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs'>Đang xử lý</span>
         case 2:
            return <span className='px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs'>Đang giao</span>
         case 3:
            return <span className='px-2 py-1 bg-emerald-100 text-emerald-800 rounded-full text-xs'>Đã giao</span>
         case 4:
            return <span className='px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs'>Đã hủy</span>
         default:
            return <span className='px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-xs'>Không xác định</span>
      }
   }

   return (
      <div className='container p-6'>
         <h1 className='text-2xl font-bold mb-6'>Thống kê tổng quan</h1>

         {/* Thống kê tổng quan */}
         <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6'>
            <Card>
               <CardContent className='p-6 flex items-center justify-between'>
                  <div>
                     <p className='text-sm text-muted-foreground'>Tổng người dùng</p>
                     <h2 className='text-3xl font-bold'>{statistics?.totalUsers || 0}</h2>
                     <p className='text-xs text-muted-foreground mt-1'>+{statistics?.newUsersToday || 0} hôm nay</p>
                  </div>
                  <div className='p-3 bg-blue-100 rounded-full'>
                     <Users className='h-6 w-6 text-blue-600' />
                  </div>
               </CardContent>
            </Card>

            <Card>
               <CardContent className='p-6 flex items-center justify-between'>
                  <div>
                     <p className='text-sm text-muted-foreground'>Tổng sản phẩm</p>
                     <h2 className='text-3xl font-bold'>{statistics?.totalProducts || 0}</h2>
                     <p className='text-xs text-muted-foreground mt-1'>
                        {statistics?.lowStockProducts || 0} sản phẩm sắp hết hàng
                     </p>
                  </div>
                  <div className='p-3 bg-amber-100 rounded-full'>
                     <Package className='h-6 w-6 text-amber-600' />
                  </div>
               </CardContent>
            </Card>

            <Card>
               <CardContent className='p-6 flex items-center justify-between'>
                  <div>
                     <p className='text-sm text-muted-foreground'>Tổng đơn hàng</p>
                     <h2 className='text-3xl font-bold'>{statistics?.totalOrders || 0}</h2>
                     <p className='text-xs text-muted-foreground mt-1'>+{statistics?.newOrdersToday || 0} hôm nay</p>
                  </div>
                  <div className='p-3 bg-green-100 rounded-full'>
                     <ShoppingCart className='h-6 w-6 text-green-600' />
                  </div>
               </CardContent>
            </Card>

            <Card>
               <CardContent className='p-6 flex items-center justify-between'>
                  <div>
                     <p className='text-sm text-muted-foreground'>Tổng doanh thu</p>
                     <h2 className='text-3xl font-bold'>{formatCurrency(statistics?.totalRevenue || 0)}</h2>
                     <p className='text-xs text-muted-foreground mt-1'>
                        +{formatCurrency(statistics?.revenueToday || 0)} hôm nay
                     </p>
                  </div>
                  <div className='p-3 bg-purple-100 rounded-full'>
                     <DollarSign className='h-6 w-6 text-purple-600' />
                  </div>
               </CardContent>
            </Card>
         </div>

         {/* Thống kê trạng thái đơn hàng */}
         <div className='grid grid-cols-1 md:grid-cols-5 gap-4 mb-6'>
            <Card className='bg-amber-50 border-amber-200'>
               <CardContent className='p-4 flex flex-col items-center justify-center'>
                  <Clock className='h-6 w-6 text-amber-600 mb-2' />
                  <p className='text-sm text-muted-foreground'>Chờ xác nhận</p>
                  <h3 className='text-2xl font-bold text-amber-700'>{statistics?.pendingOrders || 0}</h3>
               </CardContent>
            </Card>

            <Card className='bg-blue-50 border-blue-200'>
               <CardContent className='p-4 flex flex-col items-center justify-center'>
                  <TrendingUp className='h-6 w-6 text-blue-600 mb-2' />
                  <p className='text-sm text-muted-foreground'>Đang xử lý</p>
                  <h3 className='text-2xl font-bold text-blue-700'>{statistics?.processingOrders || 0}</h3>
               </CardContent>
            </Card>

            <Card className='bg-green-50 border-green-200'>
               <CardContent className='p-4 flex flex-col items-center justify-center'>
                  <Truck className='h-6 w-6 text-green-600 mb-2' />
                  <p className='text-sm text-muted-foreground'>Đang giao</p>
                  <h3 className='text-2xl font-bold text-green-700'>{statistics?.shippedOrders || 0}</h3>
               </CardContent>
            </Card>

            <Card className='bg-emerald-50 border-emerald-200'>
               <CardContent className='p-4 flex flex-col items-center justify-center'>
                  <CheckCircle className='h-6 w-6 text-emerald-600 mb-2' />
                  <p className='text-sm text-muted-foreground'>Đã giao</p>
                  <h3 className='text-2xl font-bold text-emerald-700'>{statistics?.deliveredOrders || 0}</h3>
               </CardContent>
            </Card>

            <Card className='bg-red-50 border-red-200'>
               <CardContent className='p-4 flex flex-col items-center justify-center'>
                  <XCircle className='h-6 w-6 text-red-600 mb-2' />
                  <p className='text-sm text-muted-foreground'>Đã hủy</p>
                  <h3 className='text-2xl font-bold text-red-700'>{statistics?.cancelledOrders || 0}</h3>
               </CardContent>
            </Card>
         </div>

         <Tabs defaultValue='orders' className='w-full'>
            <TabsList className='mb-4 flex-wrap h-full'>
               <TabsTrigger value='orders'>Đơn hàng gần đây</TabsTrigger>
               <TabsTrigger value='products'>Sản phẩm bán chạy</TabsTrigger>
               <TabsTrigger value='customers'>Khách hàng hàng đầu</TabsTrigger>
               <TabsTrigger value='charts'>Biểu đồ</TabsTrigger>
            </TabsList>

            {/* Tab đơn hàng gần đây */}
            <TabsContent value='orders'>
               <Card>
                  <CardHeader>
                     <CardTitle>Đơn hàng gần đây</CardTitle>
                  </CardHeader>
                  <CardContent>
                     <Table>
                        <TableHeader>
                           <TableRow>
                              <TableHead>Mã đơn</TableHead>
                              <TableHead>Khách hàng</TableHead>
                              <TableHead>Ngày đặt</TableHead>
                              <TableHead>Tổng tiền</TableHead>
                              <TableHead>Trạng thái</TableHead>
                           </TableRow>
                        </TableHeader>
                        <TableBody>
                           {statistics?.recentOrders && statistics?.recentOrders.length > 0 ? (
                              statistics?.recentOrders.map((order) => (
                                 <TableRow key={order.id}>
                                    <TableCell className='font-medium'>#{order.id}</TableCell>
                                    <TableCell>{order.userName}</TableCell>
                                    <TableCell>{formatDate(order.createAt)}</TableCell>
                                    <TableCell>{formatCurrency(order.totalPrice)}</TableCell>
                                    <TableCell>{getOrderStatus(order.orderStatus)}</TableCell>
                                 </TableRow>
                              ))
                           ) : (
                              <TableRow>
                                 <TableCell colSpan={5} className='text-center py-4'>
                                    Không có đơn hàng nào
                                 </TableCell>
                              </TableRow>
                           )}
                        </TableBody>
                     </Table>
                  </CardContent>
               </Card>
            </TabsContent>

            {/* Tab sản phẩm bán chạy */}
            <TabsContent value='products'>
               <Card>
                  <CardHeader>
                     <CardTitle>Sản phẩm bán chạy</CardTitle>
                  </CardHeader>
                  <CardContent>
                     <Table>
                        <TableHeader>
                           <TableRow>
                              <TableHead>ID</TableHead>
                              <TableHead>Tên sản phẩm</TableHead>
                              <TableHead>Giá</TableHead>
                              <TableHead className='text-right'>Đã bán</TableHead>
                           </TableRow>
                        </TableHeader>
                        <TableBody>
                           {statistics?.topSellingProducts && statistics?.topSellingProducts.length > 0 ? (
                              statistics?.topSellingProducts.map((product) => (
                                 <TableRow key={product.id}>
                                    <TableCell className='font-medium'>{product.id}</TableCell>
                                    <TableCell>{product.name}</TableCell>
                                    <TableCell>{formatCurrency(product.price)}</TableCell>
                                    <TableCell className='text-right'>{product.soldQuantity}</TableCell>
                                 </TableRow>
                              ))
                           ) : (
                              <TableRow>
                                 <TableCell colSpan={4} className='text-center py-4'>
                                    Không có sản phẩm nào
                                 </TableCell>
                              </TableRow>
                           )}
                        </TableBody>
                     </Table>
                  </CardContent>
               </Card>
            </TabsContent>

            {/* Tab khách hàng hàng đầu */}
            <TabsContent value='customers'>
               <Card>
                  <CardHeader>
                     <CardTitle>Khách hàng hàng đầu</CardTitle>
                  </CardHeader>
                  <CardContent>
                     <Table>
                        <TableHeader>
                           <TableRow>
                              <TableHead>ID</TableHead>
                              <TableHead>Tên người dùng</TableHead>
                              <TableHead>Email</TableHead>
                              <TableHead className='text-right'>Số đơn hàng</TableHead>
                           </TableRow>
                        </TableHeader>
                        <TableBody>
                           {statistics?.topCustomers && statistics?.topCustomers.length > 0 ? (
                              statistics?.topCustomers.map((customer) => (
                                 <TableRow key={customer.id}>
                                    <TableCell className='font-medium'>{customer.id}</TableCell>
                                    <TableCell>{customer.userName}</TableCell>
                                    <TableCell>{customer.email}</TableCell>
                                    <TableCell className='text-right'>{customer.orderCount}</TableCell>
                                 </TableRow>
                              ))
                           ) : (
                              <TableRow>
                                 <TableCell colSpan={4} className='text-center py-4'>
                                    Không có khách hàng nào
                                 </TableCell>
                              </TableRow>
                           )}
                        </TableBody>
                     </Table>
                  </CardContent>
               </Card>
            </TabsContent>

            {/* Tab biểu đồ */}
            <TabsContent value='charts'>
               <div className='grid grid-cols-1 md:grid-cols-2 gap-6'>
                  <Card>
                     <CardHeader>
                        <CardTitle>Trạng thái đơn hàng</CardTitle>
                     </CardHeader>
                     <CardContent className='h-80'>
                        <ResponsiveContainer width='100%' height='100%'>
                           <PieChart>
                              <Pie
                                 data={orderStatusData.filter((item) => item.value > 0)}
                                 cx='50%'
                                 cy='50%'
                                 labelLine={false}
                                 outerRadius={80}
                                 fill='#8884d8'
                                 dataKey='value'
                                 label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                              >
                                 {orderStatusData.map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={entry.color} />
                                 ))}
                              </Pie>
                              <Tooltip formatter={(value) => [`${value} đơn hàng`, '']} />
                              <Legend />
                           </PieChart>
                        </ResponsiveContainer>
                     </CardContent>
                  </Card>

                  <Card>
                     <CardHeader>
                        <CardTitle>Sản phẩm bán chạy</CardTitle>
                     </CardHeader>
                     <CardContent className='h-80'>
                        <ResponsiveContainer width='100%' height='100%'>
                           <BarChart
                              data={statistics?.topSellingProducts || []}
                              layout='vertical'
                              margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                           >
                              <CartesianGrid strokeDasharray='3 3' />
                              <XAxis type='number' />
                              <YAxis type='category' dataKey='name' width={150} tick={{ fontSize: 12 }} />
                              <Tooltip formatter={(value, name) => [value, 'Đã bán']} />
                              <Bar dataKey='soldQuantity' fill='#8884d8' />
                           </BarChart>
                        </ResponsiveContainer>
                     </CardContent>
                  </Card>
               </div>
            </TabsContent>
         </Tabs>
      </div>
   )
}

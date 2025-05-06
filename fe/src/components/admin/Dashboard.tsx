'use client'
import { useGetDashboardStatistics, useGetSalesStatistics } from '@/queries/useAdmin'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'
import { formatCurrency } from '@/lib/utils'
import { useState } from 'react'
import { DatePicker } from '@/components/ui/date-picker'

export default function AdminDashboard() {
   const { data: dashboardData, isLoading } = useGetDashboardStatistics()
   const [startDate, setStartDate] = useState<string | undefined>(undefined)
   const [endDate, setEndDate] = useState<string | undefined>(undefined)

   const { data: salesData } = useGetSalesStatistics(startDate, endDate)

   if (isLoading) return <div>Đang tải...</div>

   const statistics = dashboardData?.data.data

   return (
      <div className='space-y-4 p-4'>
         <h1 className='text-2xl font-bold'>Bảng điều khiển</h1>

         <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4'>
            <Card>
               <CardHeader className='pb-2'>
                  <CardTitle className='text-sm font-medium'>Tổng doanh thu</CardTitle>
               </CardHeader>
               <CardContent>
                  <div className='text-2xl font-bold'>{formatCurrency(statistics?.totalRevenue || 0)}</div>
               </CardContent>
            </Card>

            <Card>
               <CardHeader className='pb-2'>
                  <CardTitle className='text-sm font-medium'>Tổng đơn hàng</CardTitle>
               </CardHeader>
               <CardContent>
                  <div className='text-2xl font-bold'>{statistics?.totalOrders || 0}</div>
               </CardContent>
            </Card>

            <Card>
               <CardHeader className='pb-2'>
                  <CardTitle className='text-sm font-medium'>Tổng sản phẩm</CardTitle>
               </CardHeader>
               <CardContent>
                  <div className='text-2xl font-bold'>{statistics?.totalProducts || 0}</div>
               </CardContent>
            </Card>

            <Card>
               <CardHeader className='pb-2'>
                  <CardTitle className='text-sm font-medium'>Tổng người dùng</CardTitle>
               </CardHeader>
               <CardContent>
                  <div className='text-2xl font-bold'>{statistics?.totalUsers || 0}</div>
               </CardContent>
            </Card>
         </div>

         <Tabs defaultValue='sales'>
            <TabsList>
               <TabsTrigger value='sales'>Thống kê doanh thu</TabsTrigger>
               <TabsTrigger value='products'>Sản phẩm bán chạy</TabsTrigger>
               <TabsTrigger value='orders'>Đơn hàng gần đây</TabsTrigger>
            </TabsList>

            <TabsContent value='sales' className='space-y-4'>
               <div className='flex gap-4 items-center'>
                  <div>
                     <label className='block text-sm mb-1'>Từ ngày</label>
                     <DatePicker onChange={(date) => setStartDate(date?.toISOString().split('T')[0])} />
                  </div>
                  <div>
                     <label className='block text-sm mb-1'>Đến ngày</label>
                     <DatePicker onChange={(date) => setEndDate(date?.toISOString().split('T')[0])} />
                  </div>
               </div>

               <Card>
                  <CardHeader>
                     <CardTitle>Doanh thu theo thời gian</CardTitle>
                  </CardHeader>
                  <CardContent>
                     <ResponsiveContainer width='100%' height={400}>
                        <BarChart data={Array.isArray(salesData?.data) ? salesData.data : []}>
                           <CartesianGrid strokeDasharray='3 3' />
                           <XAxis dataKey='date' />
                           <YAxis />
                           <Tooltip formatter={(value) => formatCurrency(Number(value))} />
                           <Bar dataKey='revenue' fill='#8884d8' name='Doanh thu' />
                        </BarChart>
                     </ResponsiveContainer>
                  </CardContent>
               </Card>
            </TabsContent>

            <TabsContent value='products'>
               <Card>
                  <CardHeader>
                     <CardTitle>Sản phẩm bán chạy</CardTitle>
                  </CardHeader>
                  <CardContent>
                     <div className='space-y-4'>
                        {statistics?.topSellingProducts?.map((product) => (
                           <div key={product.productId} className='flex justify-between items-center border-b pb-2'>
                              <div>
                                 <div className='font-medium'>{product.productName}</div>
                                 <div className='text-sm text-muted-foreground'>Đã bán: {product.totalSold}</div>
                              </div>
                              <div className='font-bold'>{formatCurrency(product.revenue)}</div>
                           </div>
                        ))}
                     </div>
                  </CardContent>
               </Card>
            </TabsContent>

            <TabsContent value='orders'>
               <Card>
                  <CardHeader>
                     <CardTitle>Đơn hàng gần đây</CardTitle>
                  </CardHeader>
                  <CardContent>
                     <div className='space-y-4'>
                        {statistics?.recentOrders?.map((order) => (
                           <div key={order.id} className='flex justify-between items-center border-b pb-2'>
                              <div>
                                 <div className='font-medium'>Đơn hàng #{order.id}</div>
                                 <div className='text-sm text-muted-foreground'>
                                    {new Date(order.orderDate).toLocaleDateString('vi-VN')}
                                 </div>
                              </div>
                              <div className='font-bold'>{formatCurrency(order.totalAmount)}</div>
                           </div>
                        ))}
                     </div>
                  </CardContent>
               </Card>
            </TabsContent>
         </Tabs>
      </div>
   )
}

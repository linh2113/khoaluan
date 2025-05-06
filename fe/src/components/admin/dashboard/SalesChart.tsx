'use client'
import { useGetSalesStatistics } from '@/queries/useAdmin'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { formatCurrency } from '@/lib/utils'
import { useState } from 'react'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Button } from '@/components/ui/button'
import { Calendar } from '@/components/ui/calendar'
import { CalendarIcon } from 'lucide-react'
import { format, subDays } from 'date-fns'
import { vi } from 'date-fns/locale'

export default function SalesChart() {
   const [dateRange, setDateRange] = useState<{
      from: Date
      to: Date
   }>({
      from: subDays(new Date(), 30),
      to: new Date()
   })

   const { data, isLoading } = useGetSalesStatistics(
      dateRange.from.toISOString().split('T')[0],
      dateRange.to.toISOString().split('T')[0]
   )

   if (isLoading) return <div>Đang tải...</div>

   const salesData = data?.data.data || []

   // Format data for chart
   const chartData = salesData.map((item: any) => ({
      date: item.date,
      revenue: item.totalSales,
      orders: item.orderCount
   }))

   // Calculate total revenue and orders
   const totalRevenue = chartData.reduce((sum: number, item: any) => sum + item.revenue, 0)
   const totalOrders = chartData.reduce((sum: number, item: any) => sum + item.orders, 0)

   return (
      <Card className='col-span-3'>
         <CardHeader className='flex flex-row items-center justify-between'>
            <div>
               <CardTitle>Doanh thu bán hàng</CardTitle>
               <CardDescription>
                  Thống kê doanh thu từ {format(dateRange.from, 'dd/MM/yyyy')} đến {format(dateRange.to, 'dd/MM/yyyy')}
               </CardDescription>
            </div>
            <Popover>
               <PopoverTrigger asChild>
                  <Button variant='outline'>
                     <CalendarIcon className='mr-2 h-4 w-4' />
                     Chọn khoảng thời gian
                  </Button>
               </PopoverTrigger>
               <PopoverContent className='w-auto p-0' align='end'>
                  <Calendar
                     mode='range'
                     selected={dateRange}
                     onSelect={(range) =>
                        range &&
                        setDateRange({
                           from: range.from ?? dateRange.from,
                           to: range.to ?? dateRange.to
                        })
                     }
                     initialFocus
                     locale={vi}
                  />
               </PopoverContent>
            </Popover>
         </CardHeader>
         <CardContent>
            <div className='grid grid-cols-2 gap-4 mb-6'>
               <Card>
                  <CardHeader className='pb-2'>
                     <CardTitle className='text-sm font-medium'>Tổng doanh thu</CardTitle>
                  </CardHeader>
                  <CardContent>
                     <div className='text-2xl font-bold'>{formatCurrency(totalRevenue)}</div>
                  </CardContent>
               </Card>
               <Card>
                  <CardHeader className='pb-2'>
                     <CardTitle className='text-sm font-medium'>Tổng đơn hàng</CardTitle>
                  </CardHeader>
                  <CardContent>
                     <div className='text-2xl font-bold'>{totalOrders}</div>
                  </CardContent>
               </Card>
            </div>

            <div className='h-[300px]'>
               <ResponsiveContainer width='100%' height='100%'>
                  <LineChart
                     data={chartData}
                     margin={{
                        top: 5,
                        right: 30,
                        left: 20,
                        bottom: 5
                     }}
                  >
                     <CartesianGrid strokeDasharray='3 3' />
                     <XAxis dataKey='date' />
                     <YAxis yAxisId='left' />
                     <YAxis yAxisId='right' orientation='right' />
                     <Tooltip
                        formatter={(value: any) => {
                           return typeof value === 'number'
                              ? value.toString().includes('.')
                                 ? formatCurrency(value)
                                 : value
                              : value
                        }}
                     />
                     <Legend />
                     <Line
                        yAxisId='left'
                        type='monotone'
                        dataKey='revenue'
                        name='Doanh thu'
                        stroke='hsl(var(--chart-1))'
                        activeDot={{ r: 8 }}
                     />
                     <Line
                        yAxisId='right'
                        type='monotone'
                        dataKey='orders'
                        name='Đơn hàng'
                        stroke='hsl(var(--chart-2))'
                     />
                  </LineChart>
               </ResponsiveContainer>
            </div>
         </CardContent>
      </Card>
   )
}

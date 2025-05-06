'use client'
import { useGetDashboardStatistics } from '@/queries/useAdmin'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { formatCurrency, formatNumberToK } from '@/lib/utils'
import { Users, ShoppingBag, Package, DollarSign } from 'lucide-react'

export default function DashboardStats() {
   const { data, isLoading } = useGetDashboardStatistics()

   if (isLoading) return <div>Đang tải...</div>

   const stats = data?.data.data || {
      totalUsers: 0,
      totalProducts: 0,
      totalOrders: 0,
      totalRevenue: 0,
      newUsersToday: 0,
      newOrdersToday: 0,
      revenueToday: 0
   }

   const statCards = [
      {
         title: 'Tổng người dùng',
         value: formatNumberToK(stats.totalUsers),
         change: `+${stats.newUsersToday} hôm nay`,
         icon: <Users className='h-4 w-4 text-muted-foreground' />
      },
      {
         title: 'Tổng sản phẩm',
         value: formatNumberToK(stats.totalProducts),
         change: `${stats.lowStockCount || 0} sản phẩm sắp hết hàng`,
         icon: <Package className='h-4 w-4 text-muted-foreground' />
      },
      {
         title: 'Tổng đơn hàng',
         value: formatNumberToK(stats.totalOrders),
         change: `+${stats.newOrdersToday} hôm nay`,
         icon: <ShoppingBag className='h-4 w-4 text-muted-foreground' />
      },
      {
         title: 'Tổng doanh thu',
         value: formatCurrency(stats.totalRevenue),
         change: `+${formatCurrency(stats.revenueToday)} hôm nay`,
         icon: <DollarSign className='h-4 w-4 text-muted-foreground' />
      }
   ]

   return (
      <div className='grid gap-4 md:grid-cols-2 lg:grid-cols-4'>
         {statCards.map((card, index) => (
            <Card key={index}>
               <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                  <CardTitle className='text-sm font-medium'>{card.title}</CardTitle>
                  {card.icon}
               </CardHeader>
               <CardContent>
                  <div className='text-2xl font-bold'>{card.value}</div>
                  <p className='text-xs text-muted-foreground'>{card.change}</p>
               </CardContent>
            </Card>
         ))}
      </div>
   )
}

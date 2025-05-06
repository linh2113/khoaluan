import DashboardStats from '@/components/admin/dashboard/DashboardStats'
import SalesChart from '@/components/admin/dashboard/SalesChart'
import LowStockProducts from '@/components/admin/dashboard/LowStockProducts'
import RecentOrders from '@/components/admin/dashboard/RecentOrders'

export default function DashboardPage() {
   return (
      <div className='space-y-6 p-6'>
         <h1 className='text-3xl font-bold'>Dashboard</h1>

         <DashboardStats />

         <div className='grid grid-cols-1 md:grid-cols-3 gap-6'>
            <SalesChart />
         </div>

         <div className='grid grid-cols-1 md:grid-cols-2 gap-6'>
            <LowStockProducts />
            <RecentOrders />
         </div>
      </div>
   )
}

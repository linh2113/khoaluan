import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { CalendarDateRangePicker } from '@/components/dashboard/date-range-picker'
import { Button } from '@/components/ui/button'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Overview } from '@/components/dashboard/overview'
import { RecentSales } from '@/components/dashboard/recent-sales'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { DollarSign, ShoppingCart, Package, TrendingUp, Users, ArrowUpRight, ArrowDownRight } from 'lucide-react'
import HeaderAdmin from '@/components/header-admin'

export default function StatisticsPage() {
   return (
      <div className='flex min-h-screen flex-col'>
         <HeaderAdmin />
         <div className='flex-1 space-y-4 p-8 pt-6'>
            <div className='flex items-center justify-between space-y-2'>
               <h2 className='text-3xl font-bold tracking-tight'>Thống kê</h2>
               <div className='flex items-center space-x-2'>
                  <CalendarDateRangePicker />
                  <Select defaultValue='month'>
                     <SelectTrigger className='w-[180px]'>
                        <SelectValue placeholder='Chọn khoảng thời gian' />
                     </SelectTrigger>
                     <SelectContent>
                        <SelectItem value='day'>Hôm nay</SelectItem>
                        <SelectItem value='week'>Tuần này</SelectItem>
                        <SelectItem value='month'>Tháng này</SelectItem>
                        <SelectItem value='quarter'>Quý này</SelectItem>
                        <SelectItem value='year'>Năm nay</SelectItem>
                     </SelectContent>
                  </Select>
                  <Button>Xuất báo cáo</Button>
               </div>
            </div>

            <Tabs defaultValue='revenue' className='space-y-4'>
               <TabsList>
                  <TabsTrigger value='revenue'>Doanh thu</TabsTrigger>
                  <TabsTrigger value='orders'>Đơn hàng</TabsTrigger>
                  <TabsTrigger value='products'>Sản phẩm</TabsTrigger>
                  <TabsTrigger value='customers'>Khách hàng</TabsTrigger>
               </TabsList>

               <TabsContent value='revenue' className='space-y-4'>
                  <div className='grid gap-4 md:grid-cols-2 lg:grid-cols-4'>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Tổng doanh thu</CardTitle>
                           <DollarSign className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>132.500.000 ₫</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+20.1% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Doanh thu trung bình mỗi đơn</CardTitle>
                           <TrendingUp className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>231.240 ₫</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+5.4% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Tỷ lệ chuyển đổi</CardTitle>
                           <TrendingUp className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>3.2%</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+0.5% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Tỷ lệ hoàn đơn</CardTitle>
                           <TrendingUp className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>1.5%</div>
                           <div className='flex items-center pt-1 text-xs text-red-500'>
                              <ArrowDownRight className='h-4 w-4 mr-1' />
                              <span>-0.3% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                  </div>
                  <div className='grid gap-4 md:grid-cols-2 lg:grid-cols-7'>
                     <Card className='col-span-4'>
                        <CardHeader>
                           <CardTitle>Doanh thu theo thời gian</CardTitle>
                           <CardDescription>Biểu đồ doanh thu theo tháng trong năm nay</CardDescription>
                        </CardHeader>
                        <CardContent className='pl-2'>
                           <Overview />
                        </CardContent>
                     </Card>
                     <Card className='col-span-3'>
                        <CardHeader>
                           <CardTitle>Top 5 sản phẩm bán chạy</CardTitle>
                           <CardDescription>Các sản phẩm có doanh thu cao nhất</CardDescription>
                        </CardHeader>
                        <CardContent>
                           <RecentSales />
                        </CardContent>
                     </Card>
                  </div>
               </TabsContent>

               <TabsContent value='orders' className='space-y-4'>
                  <div className='grid gap-4 md:grid-cols-2 lg:grid-cols-4'>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Tổng đơn hàng</CardTitle>
                           <ShoppingCart className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>573</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+12.2% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Đơn hàng hoàn thành</CardTitle>
                           <ShoppingCart className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>498</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+10.5% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Đơn hàng đang xử lý</CardTitle>
                           <ShoppingCart className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>67</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+15.3% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Đơn hàng bị hủy</CardTitle>
                           <ShoppingCart className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>8</div>
                           <div className='flex items-center pt-1 text-xs text-red-500'>
                              <ArrowDownRight className='h-4 w-4 mr-1' />
                              <span>-2.1% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                  </div>
                  <div className='grid gap-4 md:grid-cols-2 lg:grid-cols-7'>
                     <Card className='col-span-4'>
                        <CardHeader>
                           <CardTitle>Đơn hàng theo thời gian</CardTitle>
                           <CardDescription>Biểu đồ số lượng đơn hàng theo tháng trong năm nay</CardDescription>
                        </CardHeader>
                        <CardContent className='pl-2'>
                           <Overview />
                        </CardContent>
                     </Card>
                     <Card className='col-span-3'>
                        <CardHeader>
                           <CardTitle>Phân bố trạng thái đơn hàng</CardTitle>
                           <CardDescription>Tỷ lệ các trạng thái đơn hàng</CardDescription>
                        </CardHeader>
                        <CardContent>
                           <div className='space-y-4'>
                              <div className='flex items-center'>
                                 <div className='w-full'>
                                    <div className='flex items-center justify-between'>
                                       <span className='text-sm font-medium'>Đã giao hàng</span>
                                       <span className='text-sm font-medium'>87%</span>
                                    </div>
                                    <div className='mt-2 h-2 w-full rounded-full bg-muted'>
                                       <div className='h-2 rounded-full bg-green-500' style={{ width: '87%' }}></div>
                                    </div>
                                 </div>
                              </div>
                              <div className='flex items-center'>
                                 <div className='w-full'>
                                    <div className='flex items-center justify-between'>
                                       <span className='text-sm font-medium'>Đang xử lý</span>
                                       <span className='text-sm font-medium'>11.7%</span>
                                    </div>
                                    <div className='mt-2 h-2 w-full rounded-full bg-muted'>
                                       <div className='h-2 rounded-full bg-yellow-500' style={{ width: '11.7%' }}></div>
                                    </div>
                                 </div>
                              </div>
                              <div className='flex items-center'>
                                 <div className='w-full'>
                                    <div className='flex items-center justify-between'>
                                       <span className='text-sm font-medium'>Đã hủy</span>
                                       <span className='text-sm font-medium'>1.3%</span>
                                    </div>
                                    <div className='mt-2 h-2 w-full rounded-full bg-muted'>
                                       <div className='h-2 rounded-full bg-red-500' style={{ width: '1.3%' }}></div>
                                    </div>
                                 </div>
                              </div>
                           </div>
                        </CardContent>
                     </Card>
                  </div>
               </TabsContent>

               <TabsContent value='products' className='space-y-4'>
                  <div className='grid gap-4 md:grid-cols-2 lg:grid-cols-4'>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Tổng sản phẩm</CardTitle>
                           <Package className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>249</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+4.3% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Sản phẩm còn hàng</CardTitle>
                           <Package className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>215</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+3.8% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Sản phẩm sắp hết hàng</CardTitle>
                           <Package className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>24</div>
                           <div className='flex items-center pt-1 text-xs text-red-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+8.2% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Sản phẩm hết hàng</CardTitle>
                           <Package className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>10</div>
                           <div className='flex items-center pt-1 text-xs text-red-500'>
                              <ArrowDownRight className='h-4 w-4 mr-1' />
                              <span>-2.5% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                  </div>
                  <div className='grid gap-4 md:grid-cols-2 lg:grid-cols-7'>
                     <Card className='col-span-4'>
                        <CardHeader>
                           <CardTitle>Top danh mục sản phẩm</CardTitle>
                           <CardDescription>Phân bố sản phẩm theo danh mục</CardDescription>
                        </CardHeader>
                        <CardContent className='pl-2'>
                           <div className='space-y-4'>
                              <div className='flex items-center'>
                                 <div className='w-full'>
                                    <div className='flex items-center justify-between'>
                                       <span className='text-sm font-medium'>Điện tử</span>
                                       <span className='text-sm font-medium'>35%</span>
                                    </div>
                                    <div className='mt-2 h-2 w-full rounded-full bg-muted'>
                                       <div className='h-2 rounded-full bg-primary' style={{ width: '35%' }}></div>
                                    </div>
                                 </div>
                              </div>
                              <div className='flex items-center'>
                                 <div className='w-full'>
                                    <div className='flex items-center justify-between'>
                                       <span className='text-sm font-medium'>Quần áo</span>
                                       <span className='text-sm font-medium'>28%</span>
                                    </div>
                                    <div className='mt-2 h-2 w-full rounded-full bg-muted'>
                                       <div className='h-2 rounded-full bg-primary' style={{ width: '28%' }}></div>
                                    </div>
                                 </div>
                              </div>
                              <div className='flex items-center'>
                                 <div className='w-full'>
                                    <div className='flex items-center justify-between'>
                                       <span className='text-sm font-medium'>Nội thất</span>
                                       <span className='text-sm font-medium'>20%</span>
                                    </div>
                                    <div className='mt-2 h-2 w-full rounded-full bg-muted'>
                                       <div className='h-2 rounded-full bg-primary' style={{ width: '20%' }}></div>
                                    </div>
                                 </div>
                              </div>
                              <div className='flex items-center'>
                                 <div className='w-full'>
                                    <div className='flex items-center justify-between'>
                                       <span className='text-sm font-medium'>Sách</span>
                                       <span className='text-sm font-medium'>12%</span>
                                    </div>
                                    <div className='mt-2 h-2 w-full rounded-full bg-muted'>
                                       <div className='h-2 rounded-full bg-primary' style={{ width: '12%' }}></div>
                                    </div>
                                 </div>
                              </div>
                              <div className='flex items-center'>
                                 <div className='w-full'>
                                    <div className='flex items-center justify-between'>
                                       <span className='text-sm font-medium'>Khác</span>
                                       <span className='text-sm font-medium'>5%</span>
                                    </div>
                                    <div className='mt-2 h-2 w-full rounded-full bg-muted'>
                                       <div className='h-2 rounded-full bg-primary' style={{ width: '5%' }}></div>
                                    </div>
                                 </div>
                              </div>
                           </div>
                        </CardContent>
                     </Card>
                     <Card className='col-span-3'>
                        <CardHeader>
                           <CardTitle>Top 5 sản phẩm bán chạy</CardTitle>
                           <CardDescription>Các sản phẩm có số lượng bán cao nhất</CardDescription>
                        </CardHeader>
                        <CardContent>
                           <RecentSales />
                        </CardContent>
                     </Card>
                  </div>
               </TabsContent>

               <TabsContent value='customers' className='space-y-4'>
                  <div className='grid gap-4 md:grid-cols-2 lg:grid-cols-4'>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Tổng khách hàng</CardTitle>
                           <Users className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>2350</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+10.1% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Khách hàng mới</CardTitle>
                           <Users className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>215</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+12.3% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Tỷ lệ khách hàng quay lại</CardTitle>
                           <Users className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>42.5%</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+3.2% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                     <Card>
                        <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                           <CardTitle className='text-sm font-medium'>Giá trị trung bình mỗi khách hàng</CardTitle>
                           <Users className='h-4 w-4 text-muted-foreground' />
                        </CardHeader>
                        <CardContent>
                           <div className='text-2xl font-bold'>1.250.000 ₫</div>
                           <div className='flex items-center pt-1 text-xs text-green-500'>
                              <ArrowUpRight className='h-4 w-4 mr-1' />
                              <span>+5.7% so với tháng trước</span>
                           </div>
                        </CardContent>
                     </Card>
                  </div>
                  <div className='grid gap-4 md:grid-cols-2 lg:grid-cols-7'>
                     <Card className='col-span-4'>
                        <CardHeader>
                           <CardTitle>Khách hàng mới theo thời gian</CardTitle>
                           <CardDescription>Biểu đồ số lượng khách hàng mới theo tháng trong năm nay</CardDescription>
                        </CardHeader>
                        <CardContent className='pl-2'>
                           <Overview />
                        </CardContent>
                     </Card>
                     <Card className='col-span-3'>
                        <CardHeader>
                           <CardTitle>Top 5 khách hàng</CardTitle>
                           <CardDescription>Khách hàng có giá trị đơn hàng cao nhất</CardDescription>
                        </CardHeader>
                        <CardContent>
                           <RecentSales />
                        </CardContent>
                     </Card>
                  </div>
               </TabsContent>
            </Tabs>
         </div>
      </div>
   )
}

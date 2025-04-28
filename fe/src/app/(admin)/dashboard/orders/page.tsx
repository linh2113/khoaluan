import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import {
   DropdownMenu,
   DropdownMenuContent,
   DropdownMenuItem,
   DropdownMenuLabel,
   DropdownMenuSeparator,
   DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import { MoreHorizontal, Download, Eye } from 'lucide-react'
import HeaderAdmin from '@/components/header-admin'

export default function OrdersPage() {
   return (
      <div className='flex min-h-screen flex-col'>
         <HeaderAdmin />
         <div className='flex-1 space-y-4 p-8 pt-6'>
            <div className='flex items-center justify-between space-y-2'>
               <h2 className='text-3xl font-bold tracking-tight'>Quản lý đơn hàng</h2>
               <div className='flex items-center space-x-2'>
                  <Button>
                     <Download className='mr-2 h-4 w-4' />
                     Xuất Excel
                  </Button>
               </div>
            </div>
            <div className='flex flex-col space-y-4'>
               <div className='flex items-center justify-between'>
                  <div className='flex flex-1 items-center space-x-2'>
                     <Input placeholder='Tìm kiếm theo mã đơn hàng hoặc khách hàng...' className='w-[300px]' />
                     <Select defaultValue='all'>
                        <SelectTrigger className='w-[180px]'>
                           <SelectValue placeholder='Trạng thái' />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='all'>Tất cả trạng thái</SelectItem>
                           <SelectItem value='pending'>Chờ xác nhận</SelectItem>
                           <SelectItem value='processing'>Đang xử lý</SelectItem>
                           <SelectItem value='shipped'>Đang giao hàng</SelectItem>
                           <SelectItem value='delivered'>Đã giao hàng</SelectItem>
                           <SelectItem value='cancelled'>Đã hủy</SelectItem>
                        </SelectContent>
                     </Select>
                     <Select defaultValue='all'>
                        <SelectTrigger className='w-[180px]'>
                           <SelectValue placeholder='Thanh toán' />
                        </SelectTrigger>
                        <SelectContent>
                           <SelectItem value='all'>Tất cả phương thức</SelectItem>
                           <SelectItem value='cod'>Tiền mặt (COD)</SelectItem>
                           <SelectItem value='bank'>Chuyển khoản</SelectItem>
                           <SelectItem value='credit'>Thẻ tín dụng</SelectItem>
                           <SelectItem value='momo'>Ví MoMo</SelectItem>
                        </SelectContent>
                     </Select>
                     <Button variant='outline'>Lọc</Button>
                  </div>
               </div>
               <div className='rounded-md border'>
                  <Table>
                     <TableHeader>
                        <TableRow>
                           <TableHead className='w-[100px]'>Mã đơn</TableHead>
                           <TableHead>Khách hàng</TableHead>
                           <TableHead>Ngày đặt</TableHead>
                           <TableHead>Tổng tiền</TableHead>
                           <TableHead>Thanh toán</TableHead>
                           <TableHead>Trạng thái</TableHead>
                           <TableHead className='text-right'>Thao tác</TableHead>
                        </TableRow>
                     </TableHeader>
                     <TableBody>
                        <TableRow>
                           <TableCell className='font-medium'>#ORD-001</TableCell>
                           <TableCell>Nguyễn Văn A</TableCell>
                           <TableCell>15/04/2023</TableCell>
                           <TableCell>1.250.000 ₫</TableCell>
                           <TableCell>
                              <Badge variant='outline'>Tiền mặt (COD)</Badge>
                           </TableCell>
                           <TableCell>
                              <Badge>Đã giao hàng</Badge>
                           </TableCell>
                           <TableCell className='text-right'>
                              <DropdownMenu>
                                 <DropdownMenuTrigger asChild>
                                    <Button variant='ghost' className='h-8 w-8 p-0'>
                                       <span className='sr-only'>Mở menu</span>
                                       <MoreHorizontal className='h-4 w-4' />
                                    </Button>
                                 </DropdownMenuTrigger>
                                 <DropdownMenuContent align='end'>
                                    <DropdownMenuLabel>Thao tác</DropdownMenuLabel>
                                    <DropdownMenuItem>
                                       <Eye className='mr-2 h-4 w-4' />
                                       Xem chi tiết
                                    </DropdownMenuItem>
                                    <DropdownMenuSeparator />
                                    <DropdownMenuItem>Cập nhật trạng thái</DropdownMenuItem>
                                    <DropdownMenuItem>In hóa đơn</DropdownMenuItem>
                                 </DropdownMenuContent>
                              </DropdownMenu>
                           </TableCell>
                        </TableRow>
                        <TableRow>
                           <TableCell className='font-medium'>#ORD-002</TableCell>
                           <TableCell>Trần Thị B</TableCell>
                           <TableCell>16/04/2023</TableCell>
                           <TableCell>2.450.000 ₫</TableCell>
                           <TableCell>
                              <Badge variant='outline'>Chuyển khoản</Badge>
                           </TableCell>
                           <TableCell>
                              <Badge variant='secondary'>Đang xử lý</Badge>
                           </TableCell>
                           <TableCell className='text-right'>
                              <DropdownMenu>
                                 <DropdownMenuTrigger asChild>
                                    <Button variant='ghost' className='h-8 w-8 p-0'>
                                       <span className='sr-only'>Mở menu</span>
                                       <MoreHorizontal className='h-4 w-4' />
                                    </Button>
                                 </DropdownMenuTrigger>
                                 <DropdownMenuContent align='end'>
                                    <DropdownMenuLabel>Thao tác</DropdownMenuLabel>
                                    <DropdownMenuItem>
                                       <Eye className='mr-2 h-4 w-4' />
                                       Xem chi tiết
                                    </DropdownMenuItem>
                                    <DropdownMenuSeparator />
                                    <DropdownMenuItem>Cập nhật trạng thái</DropdownMenuItem>
                                    <DropdownMenuItem>In hóa đơn</DropdownMenuItem>
                                 </DropdownMenuContent>
                              </DropdownMenu>
                           </TableCell>
                        </TableRow>
                        <TableRow>
                           <TableCell className='font-medium'>#ORD-003</TableCell>
                           <TableCell>Lê Văn C</TableCell>
                           <TableCell>17/04/2023</TableCell>
                           <TableCell>850.000 ₫</TableCell>
                           <TableCell>
                              <Badge variant='outline'>Ví MoMo</Badge>
                           </TableCell>
                           <TableCell>
                              <Badge variant='destructive'>Đã hủy</Badge>
                           </TableCell>
                           <TableCell className='text-right'>
                              <DropdownMenu>
                                 <DropdownMenuTrigger asChild>
                                    <Button variant='ghost' className='h-8 w-8 p-0'>
                                       <span className='sr-only'>Mở menu</span>
                                       <MoreHorizontal className='h-4 w-4' />
                                    </Button>
                                 </DropdownMenuTrigger>
                                 <DropdownMenuContent align='end'>
                                    <DropdownMenuLabel>Thao tác</DropdownMenuLabel>
                                    <DropdownMenuItem>
                                       <Eye className='mr-2 h-4 w-4' />
                                       Xem chi tiết
                                    </DropdownMenuItem>
                                    <DropdownMenuSeparator />
                                    <DropdownMenuItem>Cập nhật trạng thái</DropdownMenuItem>
                                    <DropdownMenuItem>In hóa đơn</DropdownMenuItem>
                                 </DropdownMenuContent>
                              </DropdownMenu>
                           </TableCell>
                        </TableRow>
                        <TableRow>
                           <TableCell className='font-medium'>#ORD-004</TableCell>
                           <TableCell>Phạm Thị D</TableCell>
                           <TableCell>18/04/2023</TableCell>
                           <TableCell>3.150.000 ₫</TableCell>
                           <TableCell>
                              <Badge variant='outline'>Thẻ tín dụng</Badge>
                           </TableCell>
                           <TableCell>
                              <Badge variant='outline'>Đang giao hàng</Badge>
                           </TableCell>
                           <TableCell className='text-right'>
                              <DropdownMenu>
                                 <DropdownMenuTrigger asChild>
                                    <Button variant='ghost' className='h-8 w-8 p-0'>
                                       <span className='sr-only'>Mở menu</span>
                                       <MoreHorizontal className='h-4 w-4' />
                                    </Button>
                                 </DropdownMenuTrigger>
                                 <DropdownMenuContent align='end'>
                                    <DropdownMenuLabel>Thao tác</DropdownMenuLabel>
                                    <DropdownMenuItem>
                                       <Eye className='mr-2 h-4 w-4' />
                                       Xem chi tiết
                                    </DropdownMenuItem>
                                    <DropdownMenuSeparator />
                                    <DropdownMenuItem>Cập nhật trạng thái</DropdownMenuItem>
                                    <DropdownMenuItem>In hóa đơn</DropdownMenuItem>
                                 </DropdownMenuContent>
                              </DropdownMenu>
                           </TableCell>
                        </TableRow>
                        <TableRow>
                           <TableCell className='font-medium'>#ORD-005</TableCell>
                           <TableCell>Hoàng Văn E</TableCell>
                           <TableCell>19/04/2023</TableCell>
                           <TableCell>1.750.000 ₫</TableCell>
                           <TableCell>
                              <Badge variant='outline'>Tiền mặt (COD)</Badge>
                           </TableCell>
                           <TableCell>
                              <Badge variant='secondary'>Chờ xác nhận</Badge>
                           </TableCell>
                           <TableCell className='text-right'>
                              <DropdownMenu>
                                 <DropdownMenuTrigger asChild>
                                    <Button variant='ghost' className='h-8 w-8 p-0'>
                                       <span className='sr-only'>Mở menu</span>
                                       <MoreHorizontal className='h-4 w-4' />
                                    </Button>
                                 </DropdownMenuTrigger>
                                 <DropdownMenuContent align='end'>
                                    <DropdownMenuLabel>Thao tác</DropdownMenuLabel>
                                    <DropdownMenuItem>
                                       <Eye className='mr-2 h-4 w-4' />
                                       Xem chi tiết
                                    </DropdownMenuItem>
                                    <DropdownMenuSeparator />
                                    <DropdownMenuItem>Cập nhật trạng thái</DropdownMenuItem>
                                    <DropdownMenuItem>In hóa đơn</DropdownMenuItem>
                                 </DropdownMenuContent>
                              </DropdownMenu>
                           </TableCell>
                        </TableRow>
                     </TableBody>
                  </Table>
               </div>
            </div>
         </div>
      </div>
   )
}

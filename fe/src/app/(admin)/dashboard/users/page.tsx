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
import { MoreHorizontal, Plus, Eye } from 'lucide-react'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import Image from 'next/image'
import HeaderAdmin from '@/components/header-admin'

export default function UsersPage() {
   return (
      <div className='flex min-h-screen flex-col'>
         <HeaderAdmin />
         <div className='flex-1 space-y-4 p-8 pt-6'>
            <div className='flex items-center justify-between space-y-2'>
               <h2 className='text-3xl font-bold tracking-tight'>Quản lý tài khoản</h2>
               <div className='flex items-center space-x-2'>
                  <Button>
                     <Plus className='mr-2 h-4 w-4' />
                     Thêm tài khoản
                  </Button>
               </div>
            </div>

            <Tabs defaultValue='customers' className='space-y-4'>
               <TabsList>
                  <TabsTrigger value='customers'>Khách hàng</TabsTrigger>
                  <TabsTrigger value='admins'>Quản trị viên</TabsTrigger>
               </TabsList>

               <TabsContent value='customers' className='space-y-4'>
                  <div className='flex items-center justify-between'>
                     <div className='flex flex-1 items-center space-x-2'>
                        <Input placeholder='Tìm kiếm theo tên hoặc email...' className='w-[300px]' />
                        <Select defaultValue='all'>
                           <SelectTrigger className='w-[180px]'>
                              <SelectValue placeholder='Trạng thái' />
                           </SelectTrigger>
                           <SelectContent>
                              <SelectItem value='all'>Tất cả trạng thái</SelectItem>
                              <SelectItem value='active'>Đang hoạt động</SelectItem>
                              <SelectItem value='inactive'>Không hoạt động</SelectItem>
                           </SelectContent>
                        </Select>
                        <Button variant='outline'>Lọc</Button>
                     </div>
                  </div>
                  <div className='rounded-md border'>
                     <Table>
                        <TableHeader>
                           <TableRow>
                              <TableHead className='w-[50px]'>Avatar</TableHead>
                              <TableHead>Tên</TableHead>
                              <TableHead>Email</TableHead>
                              <TableHead>Số điện thoại</TableHead>
                              <TableHead>Ngày đăng ký</TableHead>
                              <TableHead>Đơn hàng</TableHead>
                              <TableHead>Trạng thái</TableHead>
                              <TableHead className='text-right'>Thao tác</TableHead>
                           </TableRow>
                        </TableHeader>
                        <TableBody>
                           <TableRow>
                              <TableCell>
                                 <Image
                                    src='/placeholder.svg?height=40&width=40'
                                    width={40}
                                    height={40}
                                    alt='User avatar'
                                    className='rounded-full'
                                 />
                              </TableCell>
                              <TableCell className='font-medium'>Nguyễn Văn A</TableCell>
                              <TableCell>nguyenvana@example.com</TableCell>
                              <TableCell>0912345678</TableCell>
                              <TableCell>15/01/2023</TableCell>
                              <TableCell>12</TableCell>
                              <TableCell>
                                 <Badge>Đang hoạt động</Badge>
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
                                       <DropdownMenuItem>Chỉnh sửa thông tin</DropdownMenuItem>
                                       <DropdownMenuSeparator />
                                       <DropdownMenuItem className='text-destructive'>
                                          Vô hiệu hóa tài khoản
                                       </DropdownMenuItem>
                                    </DropdownMenuContent>
                                 </DropdownMenu>
                              </TableCell>
                           </TableRow>
                           <TableRow>
                              <TableCell>
                                 <Image
                                    src='/placeholder.svg?height=40&width=40'
                                    width={40}
                                    height={40}
                                    alt='User avatar'
                                    className='rounded-full'
                                 />
                              </TableCell>
                              <TableCell className='font-medium'>Trần Thị B</TableCell>
                              <TableCell>tranthib@example.com</TableCell>
                              <TableCell>0923456789</TableCell>
                              <TableCell>20/02/2023</TableCell>
                              <TableCell>5</TableCell>
                              <TableCell>
                                 <Badge>Đang hoạt động</Badge>
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
                                       <DropdownMenuItem>Chỉnh sửa thông tin</DropdownMenuItem>
                                       <DropdownMenuSeparator />
                                       <DropdownMenuItem className='text-destructive'>
                                          Vô hiệu hóa tài khoản
                                       </DropdownMenuItem>
                                    </DropdownMenuContent>
                                 </DropdownMenu>
                              </TableCell>
                           </TableRow>
                           <TableRow>
                              <TableCell>
                                 <Image
                                    src='/placeholder.svg?height=40&width=40'
                                    width={40}
                                    height={40}
                                    alt='User avatar'
                                    className='rounded-full'
                                 />
                              </TableCell>
                              <TableCell className='font-medium'>Lê Văn C</TableCell>
                              <TableCell>levanc@example.com</TableCell>
                              <TableCell>0934567890</TableCell>
                              <TableCell>05/03/2023</TableCell>
                              <TableCell>0</TableCell>
                              <TableCell>
                                 <Badge variant='secondary'>Không hoạt động</Badge>
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
                                       <DropdownMenuItem>Chỉnh sửa thông tin</DropdownMenuItem>
                                       <DropdownMenuSeparator />
                                       <DropdownMenuItem>Kích hoạt tài khoản</DropdownMenuItem>
                                    </DropdownMenuContent>
                                 </DropdownMenu>
                              </TableCell>
                           </TableRow>
                        </TableBody>
                     </Table>
                  </div>
               </TabsContent>

               <TabsContent value='admins' className='space-y-4'>
                  <div className='flex items-center justify-between'>
                     <div className='flex flex-1 items-center space-x-2'>
                        <Input placeholder='Tìm kiếm theo tên hoặc email...' className='w-[300px]' />
                        <Select defaultValue='all'>
                           <SelectTrigger className='w-[180px]'>
                              <SelectValue placeholder='Vai trò' />
                           </SelectTrigger>
                           <SelectContent>
                              <SelectItem value='all'>Tất cả vai trò</SelectItem>
                              <SelectItem value='super-admin'>Super Admin</SelectItem>
                              <SelectItem value='admin'>Admin</SelectItem>
                              <SelectItem value='editor'>Editor</SelectItem>
                           </SelectContent>
                        </Select>
                        <Button variant='outline'>Lọc</Button>
                     </div>
                  </div>
                  <div className='rounded-md border'>
                     <Table>
                        <TableHeader>
                           <TableRow>
                              <TableHead className='w-[50px]'>Avatar</TableHead>
                              <TableHead>Tên</TableHead>
                              <TableHead>Email</TableHead>
                              <TableHead>Vai trò</TableHead>
                              <TableHead>Ngày tạo</TableHead>
                              <TableHead>Trạng thái</TableHead>
                              <TableHead className='text-right'>Thao tác</TableHead>
                           </TableRow>
                        </TableHeader>
                        <TableBody>
                           <TableRow>
                              <TableCell>
                                 <Image
                                    src='/placeholder.svg?height=40&width=40'
                                    width={40}
                                    height={40}
                                    alt='Admin avatar'
                                    className='rounded-full'
                                 />
                              </TableCell>
                              <TableCell className='font-medium'>Admin System</TableCell>
                              <TableCell>admin@example.com</TableCell>
                              <TableCell>
                                 <Badge variant='outline'>Super Admin</Badge>
                              </TableCell>
                              <TableCell>01/01/2023</TableCell>
                              <TableCell>
                                 <Badge>Đang hoạt động</Badge>
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
                                       <DropdownMenuItem>Chỉnh sửa thông tin</DropdownMenuItem>
                                    </DropdownMenuContent>
                                 </DropdownMenu>
                              </TableCell>
                           </TableRow>
                           <TableRow>
                              <TableCell>
                                 <Image
                                    src='/placeholder.svg?height=40&width=40'
                                    width={40}
                                    height={40}
                                    alt='Admin avatar'
                                    className='rounded-full'
                                 />
                              </TableCell>
                              <TableCell className='font-medium'>Hoàng Văn D</TableCell>
                              <TableCell>hoangvand@example.com</TableCell>
                              <TableCell>
                                 <Badge variant='outline'>Admin</Badge>
                              </TableCell>
                              <TableCell>10/02/2023</TableCell>
                              <TableCell>
                                 <Badge>Đang hoạt động</Badge>
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
                                       <DropdownMenuItem>Chỉnh sửa thông tin</DropdownMenuItem>
                                       <DropdownMenuSeparator />
                                       <DropdownMenuItem className='text-destructive'>
                                          Vô hiệu hóa tài khoản
                                       </DropdownMenuItem>
                                    </DropdownMenuContent>
                                 </DropdownMenu>
                              </TableCell>
                           </TableRow>
                           <TableRow>
                              <TableCell>
                                 <Image
                                    src='/placeholder.svg?height=40&width=40'
                                    width={40}
                                    height={40}
                                    alt='Admin avatar'
                                    className='rounded-full'
                                 />
                              </TableCell>
                              <TableCell className='font-medium'>Phạm Thị E</TableCell>
                              <TableCell>phamthie@example.com</TableCell>
                              <TableCell>
                                 <Badge variant='outline'>Editor</Badge>
                              </TableCell>
                              <TableCell>15/03/2023</TableCell>
                              <TableCell>
                                 <Badge variant='secondary'>Không hoạt động</Badge>
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
                                       <DropdownMenuItem>Chỉnh sửa thông tin</DropdownMenuItem>
                                       <DropdownMenuSeparator />
                                       <DropdownMenuItem>Kích hoạt tài khoản</DropdownMenuItem>
                                    </DropdownMenuContent>
                                 </DropdownMenu>
                              </TableCell>
                           </TableRow>
                        </TableBody>
                     </Table>
                  </div>
               </TabsContent>
            </Tabs>
         </div>
      </div>
   )
}

'use client'
import { ModeToggle } from '@/components/mode-toggle'
import { Input } from '@/components/ui/input'
import { Bell, LayoutDashboard, LogOut, Search, ShoppingCart, User } from 'lucide-react'
import Image from 'next/image'
import Link from 'next/link'
import React from 'react'
import {
   Dialog,
   DialogContent,
   DialogDescription,
   DialogFooter,
   DialogHeader,
   DialogTitle
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import {
   DropdownMenu,
   DropdownMenuContent,
   DropdownMenuGroup,
   DropdownMenuItem,
   DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import { formatCurrency } from '@/lib/utils'
import { useAppContext } from '@/context/app.context'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { useGetUserInfo } from '@/queries/useUser'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { toast } from 'react-toastify'

export default function Header() {
   const { userId, logout } = useAppContext()
   const { data } = useGetUserInfo(userId!)
   const userInfo = data?.data.data
   const [showLogoutDialog, setShowLogoutDialog] = React.useState(false)

   const handleLogout = () => {
      logout()
      setShowLogoutDialog(false)
      toast.success('Đăng xuất thành công')
   }

   return (
      <>
         <header className='bg-primaryColor py-3'>
            <div className='container flex justify-end gap-4 items-center mb-2'>
               <ModeToggle />
               <div className='flex items-center text-white gap-1 relative'>
                  <Bell size={20} strokeWidth={1.5} />
                  Thông báo
                  <div className='absolute top-[-7px] left-[5px] bg-secondaryColor px-2 text-xs rounded-lg'>0</div>
               </div>

               <div className='flex items-center gap-1 text-white'>
                  {userInfo ? (
                     <>
                        <Popover>
                           <PopoverTrigger asChild>
                              <button className='flex items-center gap-1 text-white'>
                                 <Avatar>
                                    <AvatarImage src={userInfo.picture || 'https://github.com/shadcn.png'} />
                                    <AvatarFallback>
                                       {userInfo.surName?.[0]?.toUpperCase()}
                                       {userInfo.lastName?.[0]?.toUpperCase()}
                                    </AvatarFallback>
                                 </Avatar>
                                 <span>
                                    {userInfo.surName} {userInfo.lastName}
                                 </span>
                              </button>
                           </PopoverTrigger>
                           <PopoverContent className='w-40 p-0'>
                              <div className='flex flex-col'>
                                 <Link
                                    className='flex items-center hover:bg-primary/10 px-4 py-2 transition-colors gap-2'
                                    href={'/profile'}
                                 >
                                    <User size={20} />
                                    Hồ sơ
                                 </Link>
                                 <Link
                                    className='flex items-center hover:bg-primary/10 px-4 py-2 transition-colors gap-2'
                                    href={'/dashboard'}
                                 >
                                    <LayoutDashboard size={20} />
                                    Quản lý
                                 </Link>
                                 <button
                                    onClick={() => setShowLogoutDialog(true)}
                                    className='flex items-center hover:bg-primary/10 px-4 py-2 transition-colors gap-2 text-red-500'
                                 >
                                    <LogOut size={20} />
                                    Đăng xuất
                                 </button>
                              </div>
                           </PopoverContent>
                        </Popover>
                     </>
                  ) : (
                     <>
                        <Link href={'/login'}>Đăng nhập</Link>/<Link href={'/register'}>Đăng ký</Link>
                     </>
                  )}
               </div>
            </div>
            <div className='container flex items-center'>
               <Link href={'/'} className='w-1/5'>
                  <Image
                     src={
                        'https://cdn2.fptshop.com.vn/unsafe/360x0/filters:quality(100)/small/fptshop_logo_c5ac91ae46.png'
                     }
                     priority
                     alt='fptshop'
                     width={150}
                     height={40}
                     className='w-[150px] h-10 flex-shrink-0'
                  />
               </Link>
               <form className='w-3/5 flex-1 flex items-center'>
                  <Input
                     placeholder='Bạn muốn tìm gì...'
                     required
                     className='bg-white text-black rounded-tr-none rounded-br-none border-none outline-none ring-offset-0'
                  />
                  <button className='flex h-9 items-center gap-1 bg-[#097345] font-medium px-3 text-white whitespace-nowrap rounded-tr-md rounded-br-md'>
                     <Search size={20} strokeWidth={1.5} />
                     Tìm kiếm
                  </button>
               </form>
               <div className='w-1/5 flex items-center justify-center text-white'>
                  <DropdownMenu>
                     <DropdownMenuTrigger asChild>
                        <div className='relative cursor-pointer'>
                           <ShoppingCart />
                           <div className='absolute top-[-4px] right-[-8px] bg-secondaryColor px-2 text-xs rounded-lg'>
                              1
                           </div>
                        </div>
                     </DropdownMenuTrigger>
                     <DropdownMenuContent className='w-96'>
                        <DropdownMenuGroup className='p-0'>
                           {/* <div className='flex flex-col justify-center items-center gap-3'>
                              <Image
                                 src={'/no-product.png'}
                                 alt='no-product'
                                 width={100}
                                 height={100}
                                 className='w-[100px] h-[100px]'
                              />
                              Chưa có sản phẩm
                           </div> */}
                           <p className='p-3'>Sản phẩm mới thêm</p>
                           <DropdownMenuItem className='flex items-center gap-2'>
                              <Image
                                 src={'https://cdn.tgdd.vn/Products/Images/42/329143/iphone-16-pro-titan-sa-mac.png'}
                                 alt=''
                                 width={40}
                                 height={40}
                                 className='aspect-square w-10 h-10 flex-shrink-0'
                              />
                              <h3 className='truncate'>Điện thoại Apple Iphone 12 64GB - Hàng chính hãng VNA</h3>
                              <span className='text-secondaryColor'>{formatCurrency(2000000)}</span>
                           </DropdownMenuItem>
                           <div className='flex items-center justify-between p-3'>
                              <span>Thêm hàng vào giỏ</span>
                              <Link
                                 href={'/cart'}
                                 className='px-5 py-2 bg-secondaryColor text-white button-primary rounded-sm'
                              >
                                 Xem giỏ hàng
                              </Link>
                           </div>
                        </DropdownMenuGroup>
                     </DropdownMenuContent>
                  </DropdownMenu>
               </div>
            </div>
         </header>

         <Dialog open={showLogoutDialog} onOpenChange={setShowLogoutDialog}>
            <DialogContent>
               <DialogHeader>
                  <DialogTitle>Xác nhận đăng xuất</DialogTitle>
                  <DialogDescription>Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?</DialogDescription>
               </DialogHeader>
               <DialogFooter>
                  <Button variant='outline' onClick={() => setShowLogoutDialog(false)}>
                     Hủy
                  </Button>
                  <Button variant='destructive' onClick={handleLogout}>
                     Đăng xuất
                  </Button>
               </DialogFooter>
            </DialogContent>
         </Dialog>
      </>
   )
}

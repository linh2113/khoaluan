'use client'
import { ModeToggle } from '@/components/mode-toggle'
import { Input } from '@/components/ui/input'
import { Bell, BookHeart, LayoutDashboard, LogOut, Search, ShoppingCart, Store, User, WalletCards } from 'lucide-react'
import Image from 'next/image'
import Link from 'next/link'
import React, { Fragment } from 'react'
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
import { formatCurrency, generateNameId } from '@/lib/utils'
import { useAppContext } from '@/context/app.context'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { useGetUserInfo } from '@/queries/useUser'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { toast } from 'react-toastify'
import { useGetAllCart } from '@/queries/useCart'
import SwitchLanguage from '@/components/switch-language'
import { useTranslations } from 'next-intl'

export default function Header() {
   const { userId, logout } = useAppContext()
   const { data } = useGetUserInfo(userId!)
   const userInfo = data?.data.data
   const [showLogoutDialog, setShowLogoutDialog] = React.useState(false)
   const getAllCart = useGetAllCart(userId!)
   const cartData = getAllCart.data?.data.data.items || []
   const t = useTranslations('Header')

   const handleLogout = () => {
      logout()
      setShowLogoutDialog(false)
      toast.success(t('logoutSuccess'))
   }

   return (
      <>
         <header className='bg-primaryColor py-3'>
            <div className='container flex justify-end gap-4 items-center mb-2'>
               <SwitchLanguage />
               <ModeToggle />
               <div className='flex items-center text-white gap-1 relative'>
                  <Bell size={20} strokeWidth={1.5} />
                  <span className='sm:block hidden'>{t('notifications')}</span>
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
                                 <span className='sm:block hidden'>
                                    {userInfo.surName} {userInfo.lastName}
                                 </span>
                              </button>
                           </PopoverTrigger>
                           <PopoverContent className='w-48 p-0'>
                              <div className='flex flex-col'>
                                 <span className='px-4 py-2 border-b sm:hidden'>
                                    {userInfo.surName} {userInfo.lastName}
                                 </span>
                                 <Link
                                    className='flex items-center hover:bg-primary/10 px-4 py-2 transition-colors gap-2'
                                    href={'/profile'}
                                 >
                                    <User size={20} />
                                    {t('profile')}
                                 </Link>
                                 {userInfo.role && (
                                    <Link
                                       className='flex items-center hover:bg-primary/10 px-4 py-2 transition-colors gap-2'
                                       href={'/dashboard/statistics'}
                                    >
                                       <LayoutDashboard size={20} />
                                       {t('dashboard')}
                                    </Link>
                                 )}
                                 <Link
                                    className='flex items-center hover:bg-primary/10 px-4 py-2 transition-colors gap-2'
                                    href={'/purchase'}
                                 >
                                    <WalletCards size={20} />
                                    {t('purchases')}
                                 </Link>
                                 <Link
                                    className='flex items-center hover:bg-primary/10 px-4 py-2 transition-colors gap-2'
                                    href={'/wishlist'}
                                 >
                                    <BookHeart size={20} />
                                    {t('wishlist')}
                                 </Link>
                                 <button
                                    onClick={() => setShowLogoutDialog(true)}
                                    className='flex items-center hover:bg-primary/10 px-4 py-2 transition-colors gap-2 text-red-500'
                                 >
                                    <LogOut size={20} />
                                    {t('logout')}
                                 </button>
                              </div>
                           </PopoverContent>
                        </Popover>
                     </>
                  ) : (
                     <>
                        <Link href={'/login'}>{t('login')}</Link>/<Link href={'/register'}>{t('register')}</Link>
                     </>
                  )}
               </div>
            </div>
            <div className='container flex items-center gap-2'>
               <Link href={'/'} className='sm:w-1/5'>
                  <Image
                     src={
                        'https://cdn2.fptshop.com.vn/unsafe/360x0/filters:quality(100)/small/fptshop_logo_c5ac91ae46.png'
                     }
                     priority
                     alt='fptshop'
                     width={150}
                     height={40}
                     className='w-[150px] h-10 flex-shrink-0 md:block hidden'
                  />
                  <Store size={30} className='md:hidden block' />
               </Link>
               <form className='sm:w-3/5 flex-1 flex items-center'>
                  <Input
                     placeholder={t('search.placeholder')}
                     required
                     className='bg-white text-black rounded-tr-none rounded-br-none border-none outline-none ring-offset-0'
                  />
                  <button className='flex h-9 items-center gap-1 bg-[#097345] font-medium px-3 text-white whitespace-nowrap rounded-tr-md rounded-br-md'>
                     <Search size={20} strokeWidth={1.5} />
                     <span className='sm:block hidden'> {t('search.button')}</span>
                  </button>
               </form>
               <div className='sm:w-1/5 flex items-center justify-center text-white'>
                  <DropdownMenu>
                     <DropdownMenuTrigger asChild>
                        <div className='relative cursor-pointer'>
                           <ShoppingCart size={30} />
                           <div className='absolute top-[-4px] right-[-8px] bg-secondaryColor px-2 text-xs rounded-lg'>
                              {cartData.length}
                           </div>
                        </div>
                     </DropdownMenuTrigger>
                     <DropdownMenuContent className='w-[300px] sm:w-96'>
                        <DropdownMenuGroup className='p-0'>
                           {cartData?.length === 0 && (
                              <div className='flex flex-col justify-center items-center gap-3 my-5'>
                                 <Image
                                    src={'/no-product.png'}
                                    alt='no-product'
                                    width={100}
                                    height={100}
                                    className='w-[100px] h-[100px]'
                                 />
                                 {t('cart.empty')}
                              </div>
                           )}
                           {cartData?.length > 0 && (
                              <>
                                 <p className='p-3'>{t('cart.title')}</p>
                                 {cartData.slice(0, 5)?.map((cart) => (
                                    <Link
                                       href={`/${generateNameId({ name: cart.productName, id: cart.productId })}`}
                                       key={cart.id}
                                    >
                                       <DropdownMenuItem className='flex items-center gap-2 cursor-pointer'>
                                          <Image
                                             src={cart.productImage}
                                             alt={cart.productName}
                                             width={40}
                                             height={40}
                                             className='aspect-square w-10 h-10 flex-shrink-0'
                                          />
                                          <h3 className='truncate font-semibold'>{cart.productName}</h3>
                                          <span className='text-secondaryColor'>{formatCurrency(cart.price)}</span>
                                          <span className='ml-auto'>x{cart.quantity}</span>
                                       </DropdownMenuItem>
                                    </Link>
                                 ))}
                                 <div className='flex items-center justify-between p-3'>
                                    <span>
                                       {cartData.length > 5 && cartData.slice(5).length} {t('cart.more')}
                                    </span>
                                    <Link
                                       href={'/cart'}
                                       className='px-5 py-2 bg-secondaryColor text-white button-primary rounded-sm'
                                    >
                                       {t('cart.viewCart')}
                                    </Link>
                                 </div>
                              </>
                           )}
                        </DropdownMenuGroup>
                     </DropdownMenuContent>
                  </DropdownMenu>
               </div>
            </div>
         </header>

         <Dialog open={showLogoutDialog} onOpenChange={setShowLogoutDialog}>
            <DialogContent>
               <DialogHeader>
                  <DialogTitle>{t('logoutDialog.title')}</DialogTitle>
                  <DialogDescription>{t('logoutDialog.description')}</DialogDescription>
               </DialogHeader>
               <DialogFooter>
                  <Button variant='outline' onClick={() => setShowLogoutDialog(false)}>
                     {t('logoutDialog.cancel')}
                  </Button>
                  <Button variant='destructive' onClick={handleLogout}>
                     {t('logoutDialog.confirm')}
                  </Button>
               </DialogFooter>
            </DialogContent>
         </Dialog>
      </>
   )
}

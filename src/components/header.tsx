import { ModeToggle } from '@/components/mode-toggle'
import { Input } from '@/components/ui/input'
import { Bell, Search, ShoppingCart } from 'lucide-react'
import Image from 'next/image'
import Link from 'next/link'
import React from 'react'

export default function Header() {
   return (
      <header className='bg-primaryColor py-3'>
         <div className='container flex justify-end gap-4 items-center mb-2'>
            <ModeToggle />
            <div className='flex items-center text-white gap-1 relative'>
               <Bell size={20} strokeWidth={1.5} />
               Thông báo
               <div className='absolute top-[-7px] left-[5px] bg-secondaryColor px-2 text-xs rounded-lg'>0</div>
            </div>
            <div className='flex items-center gap-1 text-white'>
               <Link href={'/login'}>Đăng nhập</Link>/<Link href={'/register'}>Đăng ký</Link>
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
               <div className='relative'>
                  <ShoppingCart />
                  <div className='absolute top-[-4px] right-[-8px] bg-secondaryColor px-2 text-xs rounded-lg'>1</div>
               </div>
            </div>
         </div>
      </header>
   )
}

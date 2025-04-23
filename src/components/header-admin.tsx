import { MainNav } from '@/components/dashboard/main-nav'
import { UserNav } from '@/components/dashboard/user-nav'
import { ModeToggle } from '@/components/mode-toggle'
import React from 'react'

export default function HeaderAdmin() {
   return (
      <header className='border-b bg-primaryColor text-white'>
         <div className='flex h-16 items-center px-4'>
            <MainNav className='mx-6' />
            <div className='ml-auto flex items-center space-x-4'>
               <ModeToggle />
               {/* <Search /> */}
               <UserNav />
            </div>
         </div>
      </header>
   )
}

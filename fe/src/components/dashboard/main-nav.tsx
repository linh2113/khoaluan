'use client'

import type React from 'react'

import Link from 'next/link'
import { usePathname } from 'next/navigation'

import { cn } from '@/lib/utils'

export function MainNav({ className, ...props }: React.HTMLAttributes<HTMLElement>) {
   const pathname = usePathname()

   return (
      <nav className={cn('flex items-center space-x-4 lg:space-x-6', className)} {...props}>
         <Link
            href='/dashboard'
            className={cn('text-sm font-medium transition-colors', pathname === '/dashboard' ? 'text-black' : '')}
         >
            Tổng quan
         </Link>
         <Link
            href='/dashboard/orders'
            className={cn(
               'text-sm font-medium transition-colors',
               pathname === '/dashboard/orders' ? 'text-black' : ''
            )}
         >
            Đơn hàng
         </Link>
         <Link
            href='/dashboard/products'
            className={cn(
               'text-sm font-medium transition-colors',
               pathname === '/dashboard/products' ? 'text-black' : ''
            )}
         >
            Sản phẩm
         </Link>
         <Link
            href='/dashboard/users'
            className={cn('text-sm font-medium transition-colors', pathname === '/dashboard/users' ? 'text-black' : '')}
         >
            Tài khoản
         </Link>
         <Link
            href='/dashboard/reviews'
            className={cn(
               'text-sm font-medium transition-colors',
               pathname === '/dashboard/reviews' ? 'text-black' : ''
            )}
         >
            Đánh giá
         </Link>
         <Link
            href='/dashboard/promotions'
            className={cn(
               'text-sm font-medium transition-colors',
               pathname === '/dashboard/promotions' ? 'text-black' : ''
            )}
         >
            Khuyến mãi
         </Link>
         <Link
            href='/dashboard/statistics'
            className={cn(
               'text-sm font-medium transition-colors',
               pathname === '/dashboard/statistics' ? 'text-black' : ''
            )}
         >
            Thống kê
         </Link>
      </nav>
   )
}

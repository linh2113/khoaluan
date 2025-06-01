import FlashSale from '@/app/(admin)/dashboard/flash-sale/flash-sale'
import { Metadata } from 'next'
import React from 'react'
export const metadata: Metadata = {
   title: 'Quản lý Flash Sale | TechShop',
   description: 'Quản lý Flash Sale | TechShop'
}
export default function page() {
   return <FlashSale />
}

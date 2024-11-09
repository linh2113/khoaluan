import ProductRating from '@/components/product-rating'
import { formatCurrency, formatNumberToK } from '@/lib/utils'
import Image from 'next/image'
import Link from 'next/link'
import React from 'react'

export default function ProductItem() {
   return (
      <Link href={'/q'} className='flex flex-col gap-2 p-3 hover:-translate-y-0.5 transition-all duration-300'>
         <Image
            src={'https://cdn.tgdd.vn/Products/Images/42/329143/iphone-16-pro-titan-sa-mac.png'}
            alt=''
            width={100}
            height={100}
            className='aspect-square'
         />
         <p>Điện thoại, smartphone chính hãng giá rẻ</p>
         <div className='flex items-center gap-3'>
            <span className='line-through text-gray-500'>{formatCurrency(1000000)}</span>
            <span className='text-secondaryColor'>{formatCurrency(2000000)}</span>
         </div>
         <div className='flex items-center gap-3'>
            <ProductRating rating={3.5} />
            <span>{formatNumberToK(1245)} đã bán</span>
         </div>
      </Link>
   )
}

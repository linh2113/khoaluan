'use client'
import QuantityController from '@/components/quantity-controller'
import { formatCurrency } from '@/lib/utils'
import Image from 'next/image'
import Link from 'next/link'
import React, { useState } from 'react'

export default function Cart() {
   const [buyCount, setBuyCount] = useState<number>(1)
   const handleBuyCount = (value: number) => {
      setBuyCount(value)
   }
   return (
      <>
         <div className='overflow-auto'>
            <div className='min-w-[1200px]'>
               <div className='grid grid-cols-12 rounded-sm bg-secondary py-5 px-9 capitalize shadow'>
                  <div className='col-span-6'>
                     <div className='flex items-center'>
                        <div className='flex flex-shrink-0 items-center justify-center pr-3'>
                           <input type='checkbox' className='h-5 w-5 accent-secondaryColor cursor-pointer' />
                        </div>
                        <div className='flex-grow'>sản phẩm</div>
                     </div>
                  </div>
                  <div className='col-span-6'>
                     <div className='grid grid-cols-5 text-center'>
                        <div className='col-span-2'>đơn giá</div>
                        <div className='col-span-1'>Số lượng</div>
                        <div className='col-span-1'>thành tiền</div>
                        <div className='col-span-1'>thao tác</div>
                     </div>
                  </div>
               </div>
               <div className='my-3 rounded-sm bg-secondary p-5 shadow flex flex-col gap-y-5'>
                  <div className='grid grid-cols-12 items-center rounded-sm border border-gray-200 py-5 px-4 text-center'>
                     <div className='col-span-6 flex'>
                        <div className='flex flex-shrink-0 items-center justify-center pr-3'>
                           <input type='checkbox' className='h-5 w-5 accent-secondaryColor cursor-pointer' />
                        </div>
                        <Link
                           className='flex flex-grow items-center'
                           href='/KHUYẾN-MÃI-35-Áo-Thun-POLO-Nam-Tay-Ngắn-Áo-Cổ-Sọc-Chất-Liệu-Cá-Sấu-Cao-Cấp--Nhiều-màu-Đủ-Size-i-60af722af1a3041b289d8ba1'
                        >
                           <div className='h-20 w-20 flex-shrink-0'>
                              <Image
                                 width={80}
                                 height={80}
                                 className='w-[80px] h-[80px]'
                                 src={'https://cdn.tgdd.vn/Products/Images/42/329143/iphone-16-pro-titan-sa-mac.png'}
                                 alt='[KHUYẾN MÃI 35%] Áo Thun POLO Nam, Tay Ngắn Áo Cổ Sọc, Chất Liệu Cá Sấu Cao Cấp - Nhiều màu- Đủ Size'
                              />
                           </div>
                           <div className='flex-grow px-2 text-left line-clamp-2'>
                              [KHUYẾN MÃI 35%] Áo Thun POLO Nam, Tay Ngắn Áo Cổ Sọc, Chất Liệu Cá Sấu Cao Cấp - Nhiều
                              màu- Đủ Size
                           </div>
                        </Link>
                     </div>
                     <div className='col-span-6 grid grid-cols-5 items-center'>
                        <div className='col-span-2'>
                           <div className='flex items-center gap-x-3 justify-center'>
                              <span className='text-gray-400 line-through'>{formatCurrency(299.999)}</span>
                              <span>{formatCurrency(194.555)}</span>
                           </div>
                        </div>
                        <div className='col-span-1'>
                           <QuantityController
                              value={buyCount}
                              max={100}
                              onIncrease={handleBuyCount}
                              onDecrease={handleBuyCount}
                              onType={handleBuyCount}
                           />
                        </div>
                        <div className='col-span-1'>
                           <span className='text-secondaryColor'>{formatCurrency(194.555)}</span>
                        </div>
                        <div className='col-span-1'>
                           <button className='transition-colors hover:text-secondaryColor'>Xoá</button>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
         </div>
         <div className='sticky bottom-0 z-10 mt-5 flex flex-col rounded-sm bg-secondary p-5 shadow md:flex-row md:items-center'>
            <div className='flex gap-x-5 items-center text-base'>
               <div className='flex flex-shrink-0 items-center justify-center'>
                  <input type='checkbox' className='h-5 w-5 accent-secondaryColor cursor-pointer' />
               </div>
               <button>Chọn tất cả (7)</button>
               <button className='hover:text-secondaryColor transition-colors'>Xoá</button>
            </div>
            <div className='mt-5 flex flex-col sm:ml-auto sm:mt-0 sm:flex-row sm:items-center'>
               <div>
                  <div className='flex items-center'>
                     <div className='text-base'>Tổng thanh toán (0 sản phẩm):</div>
                     <div className='ml-2 text-2xl text-secondaryColor'>{formatCurrency(0)}</div>
                  </div>
                  <div className='flex items-center text-sm gap-x-5 sm:justify-end'>
                     <div>Tiết kiệm:</div>
                     <div className='text-secondaryColor'>{formatCurrency(0)}</div>
                  </div>
               </div>
               <Link
                  href={'/checkout'}
                  className='disabled:cursor-not-allowed mt-5 flex h-10 disabled:opacity-70 w-52 items-center justify-center bg-secondaryColor uppercase text-white hover:bg-secondaryColor/90 sm:ml-4 sm:mt-0'
               >
                  mua hàng
               </Link>
            </div>
         </div>
      </>
   )
}

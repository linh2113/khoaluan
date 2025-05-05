'use client'
import { formatCurrency } from '@/lib/utils'
import React from 'react'
import Link from 'next/link'
import Image from 'next/image'

export default function Checkout() {
   return (
      <div className='flex items-start gap-5 my-10'>
         {/* Cột thông tin sản phẩm và địa chỉ */}
         <div className='w-2/3 bg-secondary rounded-lg shadow-lg p-5'>
            <h1 className='text-2xl font-medium mb-5'>Xác nhận đơn hàng</h1>

            {/* Thông tin người nhận */}
            <div className='border-b border-gray-200 pb-5 mb-5'>
               <h2 className='font-semibold text-lg mb-3'>Địa chỉ nhận hàng</h2>
               <div className='space-y-1'>
                  <div className='font-medium'>Nguyễn Văn A</div>
                  <div>0123456789</div>
                  <div>Số 1, Đường ABC, Phường XYZ, Quận 1, TP. HCM</div>
               </div>
               <button className='text-secondaryColor font-medium hover:underline mt-3'>Thay đổi</button>
            </div>

            {/* Danh sách sản phẩm */}
            <div>
               <h2 className='font-semibold text-lg mb-3'>Sản phẩm</h2>
               <div className='grid gap-5'>
                  <div className='grid grid-cols-12 items-center gap-3 p-3 border border-gray-200 rounded'>
                     <div className='col-span-2'>
                        <Image
                           width={80}
                           height={80}
                           src='https://cdn.tgdd.vn/Products/Images/42/329143/iphone-16-pro-titan-sa-mac.png'
                           alt='Product Image'
                           className='w-full rounded'
                        />
                     </div>
                     <div className='col-span-6'>
                        <h3 className='text-base font-medium line-clamp-2'>
                           [KHUYẾN MÃI 35%] Áo Thun POLO Nam, Tay Ngắn Áo Cổ Sọc, Chất Liệu Cá Sấu Cao Cấp - Nhiều màu-
                           Đủ Size
                        </h3>
                     </div>
                     <div className='col-span-2 text-center'>
                        <span className='text-gray-400 line-through'>{formatCurrency(299999)}</span>
                        <div>{formatCurrency(194555)}</div>
                     </div>
                     <div className='col-span-1 text-center'>x1</div>
                     <div className='col-span-1 font-medium text-secondaryColor text-center'>
                        {formatCurrency(194555)}
                     </div>
                  </div>
               </div>
            </div>
         </div>

         {/* Cột phương thức thanh toán */}
         <div className='w-1/3 bg-secondary rounded-lg shadow-lg p-5'>
            <h2 className='text-xl font-semibold mb-5'>Phương thức thanh toán</h2>
            <div className='space-y-3 mb-5'>
               <label className='flex items-center gap-3'>
                  <input type='radio' name='paymentMethod' className='cursor-pointer w-4 h-4 accent-secondaryColor' />
                  <span className='cursor-pointer'>Thanh toán khi nhận hàng (COD)</span>
               </label>
               <label className='flex items-center gap-3'>
                  <input type='radio' name='paymentMethod' className='cursor-pointer w-4 h-4 accent-secondaryColor' />
                  <span className='cursor-pointer'>Thẻ tín dụng / Thẻ ghi nợ</span>
               </label>
               <label className='flex items-center gap-3'>
                  <input type='radio' name='paymentMethod' className='cursor-pointer w-4 h-4 accent-secondaryColor' />
                  <span className='cursor-pointer'>Ví điện tử</span>
               </label>
            </div>

            {/* Tổng thanh toán */}
            <div className='space-y-3'>
               <div className='flex justify-between'>
                  <span>Tạm tính</span>
                  <span>{formatCurrency(194555)}</span>
               </div>
               <div className='flex justify-between'>
                  <span>Phí vận chuyển</span>
                  <span>{formatCurrency(15000)}</span>
               </div>
               <div className='flex justify-between font-medium text-lg'>
                  <span>Tổng cộng</span>
                  <span className='text-secondaryColor'>{formatCurrency(209555)}</span>
               </div>
            </div>

            {/* Nút đặt hàng */}
            <div className='mt-5'>
               <Link href='/cart' className='text-secondaryColor hover:underline'>
                  &lt; Quay lại giỏ hàng
               </Link>
               <button className='w-full mt-3 bg-secondaryColor text-white py-3 rounded hover:bg-secondaryColor/90'>
                  Đặt hàng
               </button>
            </div>
         </div>
      </div>
   )
}

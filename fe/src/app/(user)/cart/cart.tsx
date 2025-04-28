'use client'
import {
   Dialog,
   DialogContent,
   DialogDescription,
   DialogFooter,
   DialogHeader,
   DialogTitle
} from '@/components/ui/dialog'
import QuantityController from '@/components/quantity-controller'
import { useAppContext } from '@/context/app.context'
import { formatCurrency, generateNameId } from '@/lib/utils'
import { useClearCart, useDeleteCartItem, useGetAllCart, useUpdateCart } from '@/queries/useCart'
import Image from 'next/image'
import Link from 'next/link'
import React, { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Trash2 } from 'lucide-react'

export default function Cart() {
   const { userId } = useAppContext()
   const getAllCart = useGetAllCart(userId!)
   const cartData = getAllCart.data?.data.data.items || []
   const updateCart = useUpdateCart()
   const deleteCartItem = useDeleteCartItem()
   const clearCart = useClearCart()
   const [isOpenDialog, setIsOpenDialog] = useState(false)
   const [checkedItems, setCheckedItems] = useState<number[]>([])
   const handleQuantityChange = (cartItemId: number, value: number) => {
      if (userId) {
         updateCart.mutate({
            userId: userId,
            cartItemId: cartItemId,
            quantity: value
         })
      }
   }
   const handleDeleteCartItem = (cartItemId: number) => {
      if (userId) {
         deleteCartItem.mutate({
            userId: userId,
            cartItemId: cartItemId
         })
      }
   }

   const handleClearCart = () => {
      if (userId) {
         clearCart.mutate(
            { userId: userId },
            {
               onSuccess: () => {
                  setIsOpenDialog(false)
                  setCheckedItems([])
               }
            }
         )
      }
   }

   const handleCheckAll = (checked: boolean) => {
      if (checked) {
         setCheckedItems(cartData.map((item) => item.id))
      } else {
         setCheckedItems([])
      }
   }

   const handleCheckItem = (checked: boolean, itemId: number) => {
      if (checked) {
         setCheckedItems((prev) => [...prev, itemId])
      } else {
         setCheckedItems((prev) => prev.filter((id) => id !== itemId))
      }
   }

   const isAllChecked = cartData.length > 0 && checkedItems.length === cartData.length

   const checkedCartItems = cartData.filter((item) => checkedItems.includes(item.id))

   const totalCheckedAmount = checkedCartItems.reduce((sum, item) => sum + item.totalPrice, 0)

   const totalSavedAmount = checkedCartItems.reduce((sum, item) => {
      const originalPrice = item.price + 300000 // Giá gốc (price + 300000 như trong UI)
      const savedPerItem = (originalPrice - item.price) * item.quantity
      return sum + savedPerItem
   }, 0)
   return (
      <div className='my-10'>
         <div className='overflow-auto'>
            <div className='min-w-[1200px]'>
               <div className='grid grid-cols-12 rounded-sm bg-secondary py-5 px-9 capitalize shadow'>
                  <div className='col-span-6'>
                     <div className='flex items-center'>
                        <div className='flex flex-shrink-0 items-center justify-center pr-3'>
                           <input
                              type='checkbox'
                              className='h-5 w-5 accent-secondaryColor cursor-pointer'
                              checked={isAllChecked}
                              onChange={(e) => handleCheckAll(e.target.checked)}
                           />
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
                  {cartData.map((cart) => (
                     <div
                        key={cart.id}
                        className='grid grid-cols-12 items-center rounded-sm border border-gray-200 py-5 px-4 text-center'
                     >
                        <div className='col-span-6 flex'>
                           <div className='flex flex-shrink-0 items-center justify-center pr-3'>
                              <input
                                 type='checkbox'
                                 className='h-5 w-5 accent-secondaryColor cursor-pointer'
                                 checked={checkedItems.includes(cart.id)}
                                 onChange={(e) => handleCheckItem(e.target.checked, cart.id)}
                              />
                           </div>
                           <Link
                              className='flex flex-grow items-center'
                              href={`/${generateNameId({ name: cart.productName, id: cart.productId })}`}
                           >
                              <div className='h-20 w-20 flex-shrink-0'>
                                 <Image
                                    width={80}
                                    height={80}
                                    className='w-[80px] h-[80px]'
                                    src={'https://cdn.tgdd.vn/Products/Images/42/329143/iphone-16-pro-titan-sa-mac.png'}
                                    alt={cart.productName}
                                 />
                              </div>
                              <div className='flex-grow px-2 text-left line-clamp-2'>{cart.productName}</div>
                           </Link>
                        </div>
                        <div className='col-span-6 grid grid-cols-5 items-center'>
                           <div className='col-span-2'>
                              <div className='flex items-center gap-x-3 justify-center'>
                                 <span className='text-gray-400 line-through'>
                                    {formatCurrency(cart.price + 300000)}
                                 </span>
                                 <span>{formatCurrency(cart.price)}</span>
                              </div>
                           </div>
                           <div className='col-span-1'>
                              <QuantityController
                                 value={cart.quantity}
                                 max={20}
                                 onIncrease={(value) => handleQuantityChange(cart.id, value)}
                                 onDecrease={(value) => handleQuantityChange(cart.id, value)}
                                 onType={(value) => handleQuantityChange(cart.id, value)}
                                 onFocusOut={(value) => handleQuantityChange(cart.id, value)}
                              />
                           </div>
                           <div className='col-span-1'>
                              <span className='text-secondaryColor'>{formatCurrency(cart.totalPrice)}</span>
                           </div>
                           <div className='col-span-1'>
                              <button
                                 onClick={() => handleDeleteCartItem(cart.id)}
                                 className='transition-colors hover:text-secondaryColor'
                              >
                                 <Trash2 />
                              </button>
                           </div>
                        </div>
                     </div>
                  ))}
               </div>
            </div>
         </div>
         <div className='sticky bottom-0 z-10 mt-5 flex flex-col rounded-sm bg-secondary p-5 shadow md:flex-row md:items-center'>
            <div className='flex gap-x-5 items-center text-base'>
               <div className='flex flex-shrink-0 items-center justify-center'>
                  <input
                     id='selectAll'
                     type='checkbox'
                     className='h-5 w-5 accent-secondaryColor cursor-pointer'
                     checked={isAllChecked}
                     onChange={(e) => handleCheckAll(e.target.checked)}
                  />
               </div>
               <label htmlFor='selectAll' className='cursor-pointer'>
                  Chọn tất cả ({cartData.length})
               </label>
               <button
                  disabled={checkedItems.length === 0}
                  onClick={() => setIsOpenDialog(true)}
                  className={`${
                     checkedItems.length === 0
                        ? 'cursor-not-allowed opacity-70'
                        : 'hover:text-secondaryColor transition-colors'
                  }`}
               >
                  Xoá
               </button>
            </div>
            <div className='mt-5 flex flex-col sm:ml-auto sm:mt-0 sm:flex-row sm:items-center'>
               <div>
                  <div className='flex items-center'>
                     <div className='text-base'>Tổng thanh toán ({checkedCartItems.length} sản phẩm):</div>
                     <div className='ml-2 text-2xl text-secondaryColor'>{formatCurrency(totalCheckedAmount)}</div>
                  </div>
                  <div className='flex items-center text-sm gap-x-5 sm:justify-end'>
                     <div>Tiết kiệm:</div>
                     <div className='text-secondaryColor'>{formatCurrency(totalSavedAmount)}</div>
                  </div>
               </div>
               <Link
                  href={checkedItems.length > 0 ? '/checkout' : '#'}
                  className={`mt-5 flex h-10 w-52 items-center justify-center bg-secondaryColor uppercase text-white hover:bg-secondaryColor/90 sm:ml-4 sm:mt-0 ${
                     checkedItems.length === 0 ? 'cursor-not-allowed opacity-70' : ''
                  }`}
               >
                  mua hàng
               </Link>
               <Dialog open={isOpenDialog} onOpenChange={setIsOpenDialog}>
                  <DialogContent>
                     <DialogHeader>
                        <DialogTitle>Xác nhận xóa</DialogTitle>
                        <DialogDescription>Bạn có chắc chắn muốn xóa tất cả sản phẩm trong giỏ hàng?</DialogDescription>
                     </DialogHeader>
                     <DialogFooter>
                        <Button variant='outline' onClick={() => setIsOpenDialog(false)}>
                           Hủy
                        </Button>
                        <Button variant='destructive' onClick={handleClearCart}>
                           Xóa
                        </Button>
                     </DialogFooter>
                  </DialogContent>
               </Dialog>
            </div>
         </div>
      </div>
   )
}

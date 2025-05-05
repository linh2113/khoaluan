'use client'
import React, { useState } from 'react'
import ReactPaginate from 'react-paginate'
import Tippy from '@tippyjs/react'
import 'tippy.js/animations/perspective-extreme.css'
import { ChevronLeft, ChevronRight, MoveHorizontal } from 'lucide-react'
interface Props {
   totalPages: number
   currentPage: number
   handlePageClick: (e: { selected: number }) => void
   setCurrentPage: React.Dispatch<React.SetStateAction<number>>
}
export default function Paginate({ currentPage, handlePageClick, totalPages, setCurrentPage }: Props) {
   const [value, setValue] = useState<number>(1)
   const [openPaginate, setOpenPaginate] = useState<boolean>(false)
   // Di chuyển đến trang đã nhập
   const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const newValue = parseInt(e.target.value)
      if (newValue >= 1 && newValue <= totalPages) {
         setValue(newValue)
      }
   }
   return (
      <div className='my-5 flex items-center justify-center gap-x-3'>
         <ReactPaginate
            pageCount={totalPages} // Tổng số trang
            marginPagesDisplayed={1} // Số lượng trang được hiển thị trước và sau trang hiện tại
            pageRangeDisplayed={2} // Số lượng trang được hiển thị trong phân đoạn paginate
            breakLabel='...'
            nextLabel={
               currentPage < totalPages ? (
                  <div className='flex items-center justify-center w-9 h-9 rounded-md border  transition-colors'>
                     <ChevronRight className='h-4 w-4' />
                  </div>
               ) : null
            }
            onPageChange={handlePageClick}
            forcePage={currentPage - 1} // Đặt trang hiện tại là trang active
            previousLabel={
               currentPage > 1 ? (
                  <div className='flex items-center justify-center w-9 h-9 rounded-md border transition-colors'>
                     <ChevronLeft className='h-4 w-4' />
                  </div>
               ) : null
            }
            renderOnZeroPageCount={null}
            containerClassName={'flex items-center gap-x-2 justify-center text-sm font-medium list-none'}
            pageClassName={''}
            pageLinkClassName={'flex items-center justify-center w-9 h-9 rounded-md border transition-colors'}
            activeClassName={''}
            activeLinkClassName={'!bg-secondaryColor  !border-secondaryColor hover:!bg-secondaryColor/90'}
            breakClassName={'flex items-center justify-center'}
            breakLinkClassName={'flex items-center justify-center w-9 h-9 text-gray-500'}
            disabledClassName={'opacity-50 cursor-not-allowed'}
            disabledLinkClassName={'cursor-not-allowed'}
         />
         <Tippy
            animation={'perspective-extreme'}
            visible={openPaginate}
            onClickOutside={() => setOpenPaginate(false)}
            content={
               <form
                  onSubmit={(e) => {
                     e.preventDefault()
                     setCurrentPage(value)
                     handlePageClick({ selected: value - 1 })
                     setOpenPaginate(false)
                  }}
                  className='shadow-md rounded-lg p-3 flex items-center gap-x-2'
               >
                  <div className='flex flex-col gap-1 w-full'>
                     <label htmlFor='page-input' className='text-xs text-gray-500'>
                        Đi đến trang:
                     </label>
                     <div className='flex gap-2'>
                        <input
                           id='page-input'
                           type='number'
                           value={value}
                           min={1}
                           max={totalPages}
                           onChange={handleChange}
                           className='w-16 outline-none p-2 text-sm border border-gray-300 rounded-md focus:border-secondaryColor focus:ring-1 focus:ring-secondaryColor'
                        />
                        <button
                           className='bg-secondaryColor px-3 py-2 rounded-md hover:bg-secondaryColor/90 transition-colors text-sm font-medium'
                           type='submit'
                        >
                           Đi
                        </button>
                     </div>
                  </div>
               </form>
            }
            interactive={true}
            arrow={true}
            offset={[0, 8]}
            placement={'bottom'}
         >
            <button
               onClick={() => setOpenPaginate((prev) => !prev)}
               className={`flex items-center justify-center w-9 h-9 rounded-md border ${
                  openPaginate ? 'border-secondaryColor bg-secondaryColor/10 text-secondaryColor' : 'border-gray-200 '
               } transition-colors`}
               aria-label='Đi đến trang'
               title='Đi đến trang'
            >
               <MoveHorizontal className='h-4 w-4' />
            </button>
         </Tippy>
      </div>
   )
}

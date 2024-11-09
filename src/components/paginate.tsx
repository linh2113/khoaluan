'use client'
import React, { useState } from 'react'
import ReactPaginate from 'react-paginate'
import Tippy from '@tippyjs/react'
import 'tippy.js/animations/perspective-extreme.css'
import { ChevronLeft, ChevronRight } from 'lucide-react'
interface Props {
   totalPages: number
   currentPage: number
   handlePageClick: (e: { selected: number }) => void
   setCurrentPage: React.Dispatch<React.SetStateAction<number>>
}
export default function Paginate({ currentPage, handlePageClick, totalPages, setCurrentPage }: Props) {
   const [value, setValue] = useState<number>(1)
   const [openPaginate, setOpenPaginate] = useState<boolean>(false)
   //di chuyển đến trang đã nhập
   const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const newValue = parseInt(e.target.value)
      if (newValue >= 1 && newValue <= totalPages) {
         setValue(newValue)
      }
   }
   return (
      <div className='my-3 flex items-center justify-center gap-x-1'>
         <ReactPaginate
            pageCount={totalPages} // Tổng số trang
            marginPagesDisplayed={1} // Số lượng trang được hiển thị trước và sau trang hiện tại
            pageRangeDisplayed={1} // Số lượng trang được hiển thị trong phân đoạn paginate
            breakLabel='...'
            nextLabel={currentPage < totalPages && <ChevronRight />}
            onPageChange={handlePageClick}
            forcePage={currentPage - 1} // Đặt trang hiện tại là trang active
            previousLabel={currentPage > 1 && <ChevronLeft />}
            renderOnZeroPageCount={null}
            containerClassName={'flex items-center gap-x-5 justify-center text-xl font-bold list-none'} // Class cho container của paginate
            activeClassName={'text-primaryColor'} // Class cho trang hiện tại
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
                  className='bg-blue-500 text-white shadow rounded p-2 flex items-center gap-x-2'
               >
                  <input
                     type='number'
                     value={value}
                     min={1}
                     max={totalPages}
                     onChange={handleChange}
                     className='outline-none p-1 bg-transparent border border-white'
                  />
                  <button className='border border-white text-white p-1' type='submit'>
                     Go
                  </button>
               </form>
            }
            interactive={true}
            arrow={false}
            offset={[0, 6]}
            placement={'top'}
         >
            <button onClick={() => setOpenPaginate((prev) => !prev)}>
               <svg
                  stroke='currentColor'
                  fill='currentColor'
                  strokeWidth={0}
                  viewBox='0 0 24 24'
                  height='22px'
                  width='22px'
                  xmlns='http://www.w3.org/2000/svg'
               >
                  <path fill='none' d='M0 0h24v24H0z' />
                  <path d='M3 5v6h5L7 7l4 1V3H5c-1.1 0-2 .9-2 2zm5 8H3v6c0 1.1.9 2 2 2h6v-5l-4 1 1-4zm9 4l-4-1v5h6c1.1 0 2-.9 2-2v-6h-5l1 4zm2-14h-6v5l4-1-1 4h5V5c0-1.1-.9-2-2-2z' />
               </svg>
            </button>
         </Tippy>
      </div>
   )
}

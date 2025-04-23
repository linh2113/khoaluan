'use client'

import { Input } from '@/components/ui/input'
import { SearchIcon } from 'lucide-react'

export function Search() {
   return (
      <div className='relative w-full max-w-sm bg-white text-black'>
         <SearchIcon className='absolute left-2 top-2.5 h-4 w-4 text-black' />
         <Input placeholder='Tìm kiếm...' className='pl-8' />
      </div>
   )
}

'use client'
import {
   useGetAllShippingMethod,
   useCreateShippingMethod,
   useUpdateShippingMethod,
   useToggleShippingMethodStatus
} from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { formatCurrency } from '@/lib/utils'
import { useState } from 'react'
import { Edit, Plus, Power } from 'lucide-react'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { ShippingMethodForm } from '@/components/admin/shipping/ShippingMethodForm'

export default function ShippingMethodList() {
   const { data, isLoading } = useGetAllShippingMethod()
   const toggleStatus = useToggleShippingMethodStatus()

   const handleToggleStatus = (id: number) => {
      toggleStatus.mutate(id)
   }

   if (isLoading) return <div>Đang tải...</div>

   const shippingMethods = data?.data.data || []

   return (
      <div className='space-y-4 p-4'>
         <div className='flex justify-between items-center'>
            <h1 className='text-2xl font-bold'>Quản lý phương thức vận chuyển</h1>
            <Dialog>
               <DialogTrigger asChild>
                  <Button>
                     <Plus className='mr-2 h-4 w-4' />
                     Thêm phương thức vận chuyển
                  </Button>
               </DialogTrigger>
               <DialogContent>
                  <DialogHeader>
                     <DialogTitle>Thêm phương thức vận chuyển mới</DialogTitle>
                  </DialogHeader>
                  <ShippingMethodForm />
               </DialogContent>
            </Dialog>
         </div>

         <div className='rounded-md border'>
            <Table>
               <TableHeader>
                  <TableRow>
                     <TableHead>ID</TableHead>
                     <TableHead>Tên phương thức</TableHead>
                     <TableHead>Mô tả</TableHead>
                     <TableHead>Phí vận chuyển</TableHead>
                     <TableHead>Trạng thái</TableHead>
                     <TableHead>Thao tác</TableHead>
                  </TableRow>
               </TableHeader>
               <TableBody>
                  {shippingMethods.map((method) => (
                     <TableRow key={method.id}>
                        <TableCell>{method.id}</TableCell>
                        <TableCell>{method.name}</TableCell>
                        <TableCell className='max-w-xs truncate'>{method.description}</TableCell>
                        <TableCell>{formatCurrency(method.price)}</TableCell>
                        <TableCell>
                           <span
                              className={`px-2 py-1 rounded-full text-xs ${
                                 method.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                              }`}
                           >
                              {method.status ? 'Hoạt động' : 'Không hoạt động'}
                           </span>
                        </TableCell>
                        <TableCell>
                           <div className='flex space-x-2'>
                              <Dialog>
                                 <DialogTrigger asChild>
                                    <Button variant='outline' size='icon'>
                                       <Edit className='h-4 w-4' />
                                    </Button>
                                 </DialogTrigger>
                                 <DialogContent>
                                    <DialogHeader>
                                       <DialogTitle>Chỉnh sửa phương thức vận chuyển</DialogTitle>
                                    </DialogHeader>
                                    <ShippingMethodForm methodId={method.id} />
                                 </DialogContent>
                              </Dialog>

                              <Button variant='outline' size='icon' onClick={() => handleToggleStatus(method.id)}>
                                 <Power className='h-4 w-4' />
                              </Button>
                           </div>
                        </TableCell>
                     </TableRow>
                  ))}
               </TableBody>
            </Table>
         </div>
      </div>
   )
}

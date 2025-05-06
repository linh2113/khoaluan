'use client'
import {
   useGetAllPaymentMethod,
   useCreatePaymentMethod,
   useUpdatePaymentMethod,
   useTogglePaymentMethodStatus
} from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { useState } from 'react'
import { Edit, Plus, Power } from 'lucide-react'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { PaymentMethodForm } from '@/components/admin/payment/PaymentMethodForm'

export default function PaymentMethodList() {
   const { data, isLoading } = useGetAllPaymentMethod()
   const toggleStatus = useTogglePaymentMethodStatus()

   const handleToggleStatus = (id: number) => {
      toggleStatus.mutate(id)
   }

   if (isLoading) return <div>Đang tải...</div>

   const paymentMethods = data?.data.data || []

   return (
      <div className='space-y-4 p-4'>
         <div className='flex justify-between items-center'>
            <h1 className='text-2xl font-bold'>Quản lý phương thức thanh toán</h1>
            <Dialog>
               <DialogTrigger asChild>
                  <Button>
                     <Plus className='mr-2 h-4 w-4' />
                     Thêm phương thức thanh toán
                  </Button>
               </DialogTrigger>
               <DialogContent>
                  <DialogHeader>
                     <DialogTitle>Thêm phương thức thanh toán mới</DialogTitle>
                  </DialogHeader>
                  <PaymentMethodForm />
               </DialogContent>
            </Dialog>
         </div>

         <div className='rounded-md border'>
            <Table>
               <TableHeader>
                  <TableRow>
                     <TableHead>ID</TableHead>
                     <TableHead>Hình ảnh</TableHead>
                     <TableHead>Tên phương thức</TableHead>
                     <TableHead>Mô tả</TableHead>
                     <TableHead>Trạng thái</TableHead>
                     <TableHead>Thao tác</TableHead>
                  </TableRow>
               </TableHeader>
               <TableBody>
                  {paymentMethods.map((method) => (
                     <TableRow key={method.id}>
                        <TableCell>{method.id}</TableCell>
                        <TableCell>{method.methodName}</TableCell>
                        <TableCell className='max-w-xs truncate'>{method.description}</TableCell>
                        <TableCell>
                           <span
                              className={`px-2 py-1 rounded-full text-xs ${
                                 method.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                              }`}
                           >
                              {method.isActive ? 'Hoạt động' : 'Không hoạt động'}
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
                                       <DialogTitle>Chỉnh sửa phương thức thanh toán</DialogTitle>
                                    </DialogHeader>
                                    <PaymentMethodForm methodId={method.id} />
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

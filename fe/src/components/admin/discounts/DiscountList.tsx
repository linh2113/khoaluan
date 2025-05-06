'use client'
import { useGetAllDiscounts } from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { formatCurrency } from '@/lib/utils'
import { Edit, Plus } from 'lucide-react'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { DiscountForm } from '@/components/admin/discounts/DiscountForm'

export default function DiscountList() {
   const { data, isLoading } = useGetAllDiscounts()

   if (isLoading) return <div>Đang tải...</div>

   const discounts = data?.data.data || []

   return (
      <div className='space-y-4 p-4'>
         <div className='flex justify-between items-center'>
            <h1 className='text-2xl font-bold'>Quản lý mã giảm giá</h1>
            <Dialog>
               <DialogTrigger asChild>
                  <Button>
                     <Plus className='mr-2 h-4 w-4' />
                     Thêm mã giảm giá
                  </Button>
               </DialogTrigger>
               <DialogContent>
                  <DialogHeader>
                     <DialogTitle>Thêm mã giảm giá mới</DialogTitle>
                  </DialogHeader>
                  <DiscountForm />
               </DialogContent>
            </Dialog>
         </div>

         <div className='rounded-md border'>
            <Table>
               <TableHeader>
                  <TableRow>
                     <TableHead>ID</TableHead>
                     <TableHead>Mã</TableHead>
                     <TableHead>Loại</TableHead>
                     <TableHead>Giá trị</TableHead>
                     <TableHead>Giảm tối đa</TableHead>
                     <TableHead>Đơn hàng tối thiểu</TableHead>
                     <TableHead>Ngày bắt đầu</TableHead>
                     <TableHead>Ngày kết thúc</TableHead>
                     <TableHead>Trạng thái</TableHead>
                     <TableHead>Thao tác</TableHead>
                  </TableRow>
               </TableHeader>
               <TableBody>
                  {discounts.map((discount) => (
                     <TableRow key={discount.id}>
                        <TableCell>{discount.id}</TableCell>
                        <TableCell>{discount.code}</TableCell>

                        <TableCell>{discount.discountPercent}%</TableCell>
                        <TableCell>
                           {discount.maxAmount ? formatCurrency(discount.maxAmount) : 'Không giới hạn'}
                        </TableCell>

                        <TableCell>{new Date(discount.startDate).toLocaleDateString('vi-VN')}</TableCell>
                        <TableCell>{new Date(discount.endDate).toLocaleDateString('vi-VN')}</TableCell>
                        <TableCell>
                           <span
                              className={`px-2 py-1 rounded-full text-xs ${
                                 isDiscountActive(discount) ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                              }`}
                           >
                              {isDiscountActive(discount) ? 'Hoạt động' : 'Hết hạn'}
                           </span>
                        </TableCell>
                        <TableCell>
                           <Dialog>
                              <DialogTrigger asChild>
                                 <Button variant='outline' size='icon'>
                                    <Edit className='h-4 w-4' />
                                 </Button>
                              </DialogTrigger>
                              <DialogContent>
                                 <DialogHeader>
                                    <DialogTitle>Chỉnh sửa mã giảm giá</DialogTitle>
                                 </DialogHeader>
                                 <DiscountForm discountId={discount.id} />
                              </DialogContent>
                           </Dialog>
                        </TableCell>
                     </TableRow>
                  ))}
               </TableBody>
            </Table>
         </div>
      </div>
   )
}

// Helper function to check if discount is active
function isDiscountActive(discount: any) {
   const now = new Date()
   const startDate = new Date(discount.startDate)
   const endDate = new Date(discount.endDate)

   return startDate <= now && now <= endDate
}

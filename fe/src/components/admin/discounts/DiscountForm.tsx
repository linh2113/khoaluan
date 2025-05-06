'use client'
import { useCreateDiscount, useUpdateDiscount } from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useForm, Controller } from 'react-hook-form'
import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Calendar } from '@/components/ui/calendar'
import { CalendarIcon } from 'lucide-react'
import { format } from 'date-fns'
import { vi } from 'date-fns/locale'

interface DiscountFormProps {
   discountId?: number
}

export function DiscountForm({ discountId }: DiscountFormProps) {
   const createDiscount = useCreateDiscount()
   const updateDiscount = useUpdateDiscount()

   const { register, handleSubmit, setValue, watch, control, reset } = useForm({
      defaultValues: {
         code: '',
         discountType: 'PERCENTAGE',
         discountValue: 0,
         maxDiscount: 0,
         minOrderValue: 0,
         startDate: new Date(),
         endDate: new Date(new Date().setMonth(new Date().getMonth() + 1))
      }
   })

   const discountType = watch('discountType')

   useEffect(() => {
      if (discountId) {
         // Fetch discount data and set form values
         // This would typically be done with a query hook like useGetDiscountById
         // For now, we'll leave this as a placeholder
      }
   }, [discountId])

   const onSubmit = (data: any) => {
      // Format dates to ISO string
      const formattedData = {
         ...data,
         startDate: data.startDate.toISOString(),
         endDate: data.endDate.toISOString()
      }

      if (discountId) {
         updateDiscount.mutate(
            {
               id: discountId,
               data: formattedData
            },
            {
               onSuccess: () => {
                  toast.success('Cập nhật mã giảm giá thành công')
               }
            }
         )
      } else {
         createDiscount.mutate(formattedData, {
            onSuccess: () => {
               toast.success('Thêm mã giảm giá thành công')
               reset()
            }
         })
      }
   }

   return (
      <form onSubmit={handleSubmit(onSubmit)} className='space-y-4'>
         <div className='space-y-2'>
            <Label htmlFor='code'>Mã giảm giá</Label>
            <Input
               id='code'
               {...register('code', { required: true })}
               placeholder='Nhập mã giảm giá (VD: SUMMER2023)'
            />
         </div>

         <div className='space-y-2'>
            <Label htmlFor='discountType'>Loại giảm giá</Label>
            <Controller
               control={control}
               name='discountType'
               render={({ field }) => (
                  <Select onValueChange={field.onChange} defaultValue={field.value}>
                     <SelectTrigger>
                        <SelectValue placeholder='Chọn loại giảm giá' />
                     </SelectTrigger>
                     <SelectContent>
                        <SelectItem value='PERCENTAGE'>Phần trăm (%)</SelectItem>
                        <SelectItem value='FIXED_AMOUNT'>Số tiền cố định (VNĐ)</SelectItem>
                     </SelectContent>
                  </Select>
               )}
            />
         </div>

         <div className='space-y-2'>
            <Label htmlFor='discountValue'>Giá trị giảm giá</Label>
            <Input
               id='discountValue'
               type='number'
               {...register('discountValue', {
                  required: true,
                  min: 0,
                  max: discountType === 'PERCENTAGE' ? 100 : undefined
               })}
               placeholder={discountType === 'PERCENTAGE' ? 'Nhập % giảm giá' : 'Nhập số tiền giảm giá'}
            />
            {discountType === 'PERCENTAGE' && <p className='text-xs text-muted-foreground'>Giá trị từ 0-100%</p>}
         </div>

         {discountType === 'PERCENTAGE' && (
            <div className='space-y-2'>
               <Label htmlFor='maxDiscount'>Giảm tối đa (VNĐ)</Label>
               <Input
                  id='maxDiscount'
                  type='number'
                  {...register('maxDiscount', { min: 0 })}
                  placeholder='Nhập số tiền giảm tối đa (0 = không giới hạn)'
               />
            </div>
         )}

         <div className='space-y-2'>
            <Label htmlFor='minOrderValue'>Giá trị đơn hàng tối thiểu (VNĐ)</Label>
            <Input
               id='minOrderValue'
               type='number'
               {...register('minOrderValue', { min: 0 })}
               placeholder='Nhập giá trị đơn hàng tối thiểu (0 = không giới hạn)'
            />
         </div>

         <div className='grid grid-cols-2 gap-4'>
            <div className='space-y-2'>
               <Label>Ngày bắt đầu</Label>
               <Controller
                  control={control}
                  name='startDate'
                  render={({ field }) => (
                     <Popover>
                        <PopoverTrigger asChild>
                           <Button variant='outline' className='w-full justify-start text-left font-normal'>
                              <CalendarIcon className='mr-2 h-4 w-4' />
                              {field.value ? format(field.value, 'dd/MM/yyyy', { locale: vi }) : 'Chọn ngày'}
                           </Button>
                        </PopoverTrigger>
                        <PopoverContent className='w-auto p-0'>
                           <Calendar mode='single' selected={field.value} onSelect={field.onChange} initialFocus />
                        </PopoverContent>
                     </Popover>
                  )}
               />
            </div>

            <div className='space-y-2'>
               <Label>Ngày kết thúc</Label>
               <Controller
                  control={control}
                  name='endDate'
                  render={({ field }) => (
                     <Popover>
                        <PopoverTrigger asChild>
                           <Button variant='outline' className='w-full justify-start text-left font-normal'>
                              <CalendarIcon className='mr-2 h-4 w-4' />
                              {field.value ? format(field.value, 'dd/MM/yyyy', { locale: vi }) : 'Chọn ngày'}
                           </Button>
                        </PopoverTrigger>
                        <PopoverContent className='w-auto p-0'>
                           <Calendar
                              mode='single'
                              selected={field.value}
                              onSelect={field.onChange}
                              initialFocus
                              disabled={(date) => date < new Date(watch('startDate'))}
                           />
                        </PopoverContent>
                     </Popover>
                  )}
               />
            </div>
         </div>

         <Button type='submit' className='w-full' disabled={createDiscount.isPending || updateDiscount.isPending}>
            {discountId ? 'Cập nhật' : 'Thêm mới'}
         </Button>
      </form>
   )
}

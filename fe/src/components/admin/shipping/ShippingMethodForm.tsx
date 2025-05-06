'use client'
import { useCreateShippingMethod, useGetShippingMethodById, useUpdateShippingMethod } from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { Switch } from '@/components/ui/switch'
import { useForm } from 'react-hook-form'
import { useEffect } from 'react'
import { toast } from 'react-toastify'

interface ShippingMethodFormProps {
   methodId?: number
}

export function ShippingMethodForm({ methodId }: ShippingMethodFormProps) {
   const { data: methodData } = useGetShippingMethodById(methodId || 0)
   const createMethod = useCreateShippingMethod()
   const updateMethod = useUpdateShippingMethod()

   const { register, handleSubmit, setValue, watch, reset } = useForm({
      defaultValues: {
         name: '',
         description: '',
         price: 0,
         status: true
      }
   })

   useEffect(() => {
      if (methodData?.data) {
         const method = methodData.data.data
         setValue('name', method.methodName)
         setValue('description', method.description)
         setValue('price', method.baseCost)
         setValue('status', method.isActive)
      }
   }, [methodData, setValue])

   const onSubmit = (data: any) => {
      if (methodId) {
         updateMethod.mutate(
            {
               id: methodId,
               data: data
            },
            {
               onSuccess: () => {
                  toast.success('Cập nhật phương thức vận chuyển thành công')
               }
            }
         )
      } else {
         createMethod.mutate(data, {
            onSuccess: () => {
               toast.success('Thêm phương thức vận chuyển thành công')
               reset()
            }
         })
      }
   }

   return (
      <form onSubmit={handleSubmit(onSubmit)} className='space-y-4'>
         <div className='space-y-2'>
            <Label htmlFor='name'>Tên phương thức</Label>
            <Input id='name' {...register('name', { required: true })} placeholder='Nhập tên phương thức vận chuyển' />
         </div>

         <div className='space-y-2'>
            <Label htmlFor='description'>Mô tả</Label>
            <Textarea
               id='description'
               {...register('description')}
               placeholder='Nhập mô tả phương thức vận chuyển'
               rows={4}
            />
         </div>

         <div className='space-y-2'>
            <Label htmlFor='price'>Phí vận chuyển</Label>
            <Input
               id='price'
               type='number'
               {...register('price', { required: true, min: 0 })}
               placeholder='Nhập phí vận chuyển'
            />
         </div>

         <div className='flex items-center space-x-2'>
            <Switch id='status' checked={watch('status')} onCheckedChange={(checked) => setValue('status', checked)} />
            <Label htmlFor='status'>Hoạt động</Label>
         </div>

         <Button type='submit' className='w-full' disabled={createMethod.isPending || updateMethod.isPending}>
            {methodId ? 'Cập nhật' : 'Thêm mới'}
         </Button>
      </form>
   )
}

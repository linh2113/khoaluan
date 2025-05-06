'use client'
import { useCreatePaymentMethod, useGetPaymentMethodById, useUpdatePaymentMethod } from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { Switch } from '@/components/ui/switch'
import { useForm } from 'react-hook-form'
import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'

interface PaymentMethodFormProps {
   methodId?: number
}

export function PaymentMethodForm({ methodId }: PaymentMethodFormProps) {
   const { data: methodData } = useGetPaymentMethodById(methodId || 0)
   const createMethod = useCreatePaymentMethod()
   const updateMethod = useUpdatePaymentMethod()
   const [file, setFile] = useState<File | null>(null)
   const [previewUrl, setPreviewUrl] = useState<string>('')

   const { register, handleSubmit, setValue, watch, reset } = useForm({
      defaultValues: {
         name: '',
         description: '',
         status: true
      }
   })

   useEffect(() => {
      if (methodData?.data) {
         const method = methodData.data.data
         setValue('name', method.methodName)
         setValue('description', method.description)
         setValue('status', method.isActive)
      }
   }, [methodData, setValue])

   const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      if (e.target.files && e.target.files[0]) {
         const selectedFile = e.target.files[0]
         setFile(selectedFile)

         // Create preview URL
         const reader = new FileReader()
         reader.onload = () => {
            setPreviewUrl(reader.result as string)
         }
         reader.readAsDataURL(selectedFile)
      }
   }

   const onSubmit = (data: any) => {
      const formData = new FormData()
      formData.append('name', data.name)
      formData.append('description', data.description)
      formData.append('status', data.status.toString())

      if (file) {
         formData.append('image', file)
      }

      if (methodId) {
         updateMethod.mutate(
            {
               id: methodId,
               data: formData
            },
            {
               onSuccess: () => {
                  toast.success('Cập nhật phương thức thanh toán thành công')
               }
            }
         )
      } else {
         createMethod.mutate(formData, {
            onSuccess: () => {
               toast.success('Thêm phương thức thanh toán thành công')
               reset()
               setFile(null)
               setPreviewUrl('')
            }
         })
      }
   }

   return (
      <form onSubmit={handleSubmit(onSubmit)} className='space-y-4'>
         <div className='space-y-2'>
            <Label htmlFor='name'>Tên phương thức</Label>
            <Input id='name' {...register('name', { required: true })} placeholder='Nhập tên phương thức thanh toán' />
         </div>

         <div className='space-y-2'>
            <Label htmlFor='description'>Mô tả</Label>
            <Textarea
               id='description'
               {...register('description')}
               placeholder='Nhập mô tả phương thức thanh toán'
               rows={4}
            />
         </div>

         <div className='space-y-2'>
            <Label htmlFor='image'>Hình ảnh</Label>
            <Input id='image' type='file' accept='image/*' onChange={handleFileChange} />
            {previewUrl && (
               <div className='mt-2'>
                  <img
                     src={previewUrl}
                     alt='Payment method preview'
                     className='w-32 h-32 object-contain border rounded'
                  />
               </div>
            )}
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

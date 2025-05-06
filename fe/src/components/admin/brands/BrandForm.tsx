'use client'
import { useCreateBrand, useGetBrandById, useUpdateBrand } from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { Switch } from '@/components/ui/switch'
import { useForm } from 'react-hook-form'
import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'

interface BrandFormProps {
   brandId?: number
}

export function BrandForm({ brandId }: BrandFormProps) {
   const { data: brandData } = useGetBrandById(brandId || 0)
   const createBrand = useCreateBrand()
   const updateBrand = useUpdateBrand()
   const [file, setFile] = useState<File | null>(null)
   const [previewUrl, setPreviewUrl] = useState<string>('')

   const { register, handleSubmit, setValue, watch, reset } = useForm({
      defaultValues: {
         brandName: '',
         description: '',
         status: true
      }
   })

   useEffect(() => {
      if (brandData?.data) {
         const brand = brandData.data.data
         setValue('brandName', brand.brandName)
         setValue('description', brand.description)
         setValue('status', brand.status)
         setPreviewUrl(brand.logo)
      }
   }, [brandData, setValue])

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
      formData.append('brandName', data.brandName)
      formData.append('description', data.description)
      formData.append('status', data.status.toString())

      if (file) {
         formData.append('logo', file)
      }

      if (brandId) {
         updateBrand.mutate(
            {
               id: brandId,
               data: formData
            },
            {
               onSuccess: () => {
                  toast.success('Cập nhật thương hiệu thành công')
                  reset()
               }
            }
         )
      } else {
         createBrand.mutate(
            {
               brandName: data.brandName,
               description: data.description,
               status: data.status,
               logo: file ? URL.createObjectURL(file) : ''
            },
            {
               onSuccess: () => {
                  toast.success('Thêm thương hiệu thành công')
                  reset()
                  setFile(null)
                  setPreviewUrl('')
               }
            }
         )
      }
   }

   return (
      <form onSubmit={handleSubmit(onSubmit)} className='space-y-4'>
         <div className='space-y-2'>
            <Label htmlFor='brandName'>Tên thương hiệu</Label>
            <Input id='brandName' {...register('brandName', { required: true })} placeholder='Nhập tên thương hiệu' />
         </div>

         <div className='space-y-2'>
            <Label htmlFor='description'>Mô tả</Label>
            <Textarea id='description' {...register('description')} placeholder='Nhập mô tả thương hiệu' rows={4} />
         </div>

         <div className='space-y-2'>
            <Label htmlFor='logo'>Logo</Label>
            <Input id='logo' type='file' accept='image/*' onChange={handleFileChange} />
            {previewUrl && (
               <div className='mt-2'>
                  <img src={previewUrl} alt='Logo preview' className='w-32 h-32 object-contain border rounded' />
               </div>
            )}
         </div>

         <div className='flex items-center space-x-2'>
            <Switch id='status' checked={watch('status')} onCheckedChange={(checked) => setValue('status', checked)} />
            <Label htmlFor='status'>Hoạt động</Label>
         </div>

         <Button type='submit' className='w-full' disabled={createBrand.isPending || updateBrand.isPending}>
            {brandId ? 'Cập nhật' : 'Thêm mới'}
         </Button>
      </form>
   )
}

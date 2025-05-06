'use client'
import { useCreateCategory, useGetCategoryById, useUpdateCategory } from '@/queries/useAdmin'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { Switch } from '@/components/ui/switch'
import { useForm } from 'react-hook-form'
import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'

interface CategoryFormProps {
   categoryId?: number
}

export function CategoryForm({ categoryId }: CategoryFormProps) {
   const { data: categoryData } = useGetCategoryById(categoryId || 0)
   const createCategory = useCreateCategory()
   const updateCategory = useUpdateCategory()
   const [file, setFile] = useState<File | null>(null)
   const [previewUrl, setPreviewUrl] = useState<string>('')

   const { register, handleSubmit, setValue, watch, reset } = useForm({
      defaultValues: {
         categoryName: '',
         description: '',
         status: true
      }
   })

   useEffect(() => {
      if (categoryData?.data) {
         const category = categoryData.data.data
         setValue('categoryName', category.categoryName)
         setValue('description', category.description)
         setValue('status', category.status)
         setPreviewUrl(category.image)
      }
   }, [categoryData, setValue])

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
      formData.append('categoryName', data.categoryName)
      formData.append('description', data.description)
      formData.append('status', data.status.toString())

      if (file) {
         formData.append('image', file)
      }

      if (categoryId) {
         updateCategory.mutate(
            {
               id: categoryId,
               data: formData
            },
            {
               onSuccess: () => {
                  toast.success('Cập nhật danh mục thành công')
                  reset()
               }
            }
         )
      } else {
         createCategory.mutate(formData, {
            onSuccess: () => {
               toast.success('Thêm danh mục thành công')
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
            <Label htmlFor='categoryName'>Tên danh mục</Label>
            <Input
               id='categoryName'
               {...register('categoryName', { required: true })}
               placeholder='Nhập tên danh mục'
            />
         </div>

         <div className='space-y-2'>
            <Label htmlFor='description'>Mô tả</Label>
            <Textarea id='description' {...register('description')} placeholder='Nhập mô tả danh mục' rows={4} />
         </div>

         <div className='space-y-2'>
            <Label htmlFor='image'>Hình ảnh</Label>
            <Input id='image' type='file' accept='image/*' onChange={handleFileChange} />
            {previewUrl && (
               <div className='mt-2'>
                  <img src={previewUrl} alt='Category preview' className='w-32 h-32 object-contain border rounded' />
               </div>
            )}
         </div>

         <div className='flex items-center space-x-2'>
            <Switch id='status' checked={watch('status')} onCheckedChange={(checked) => setValue('status', checked)} />
            <Label htmlFor='status'>Hoạt động</Label>
         </div>

         <Button type='submit' className='w-full' disabled={createCategory.isPending || updateCategory.isPending}>
            {categoryId ? 'Cập nhật' : 'Thêm mới'}
         </Button>
      </form>
   )
}

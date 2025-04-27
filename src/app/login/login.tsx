'use client'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useForm } from 'react-hook-form'
import { Form, FormField, FormItem, FormMessage } from '@/components/ui/form'
import { zodResolver } from '@hookform/resolvers/zod'
import { LoginBody, LoginBodyType } from '@/schemaValidations/auth.schema'
import Link from 'next/link'
import { useLogin } from '@/queries/useAuth'
import { useAppContext } from '@/context/app.context'

export default function Login() {
   const { setUserId } = useAppContext()
   const { mutate, isPending } = useLogin()
   const form = useForm<LoginBodyType>({
      resolver: zodResolver(LoginBody),
      defaultValues: {
         userName: '',
         password: ''
      }
   })

   const onSubmit = (data: LoginBodyType) => {
      mutate(data, {
         onSuccess: (res) => {
            setUserId(res.data.data.user.id)
         }
      })
   }

   return (
      <div className='flex items-center justify-center h-screen'>
         <Card className='max-w-sm'>
            <CardHeader>
               <CardTitle className='text-2xl'>Đăng nhập</CardTitle>
               <CardDescription>Nhập tên đăng nhập và mật khẩu của bạn để đăng nhập vào hệ thống</CardDescription>
            </CardHeader>
            <CardContent>
               <Form {...form}>
                  <form className='space-y-2' noValidate onSubmit={form.handleSubmit(onSubmit)}>
                     <FormField
                        control={form.control}
                        name='userName'
                        render={({ field }) => (
                           <FormItem>
                              <div className='grid gap-2'>
                                 <Label htmlFor='userName'>Tên đăng nhập</Label>
                                 <Input id='userName' type='text' required {...field} />
                                 <FormMessage />
                              </div>
                           </FormItem>
                        )}
                     />
                     <FormField
                        control={form.control}
                        name='password'
                        render={({ field }) => (
                           <FormItem>
                              <div className='grid gap-2'>
                                 <Label htmlFor='password'>Mật khẩu</Label>
                                 <Input id='password' type='password' isPassword required {...field} />
                                 <FormMessage />
                              </div>
                           </FormItem>
                        )}
                     />
                     <button
                        disabled={isPending}
                        type='submit'
                        className='w-full bg-primaryColor text-white h-9 rounded button-primary'
                     >
                        {isPending ? (
                           <div className='flex items-center justify-center gap-2'>
                              <div className='w-5 h-5 border-2 border-white rounded-full animate-spin border-t-transparent'></div>
                              Đang đăng nhập...
                           </div>
                        ) : (
                           'Đăng nhập'
                        )}
                     </button>
                     <div className='flex justify-between'>
                        <Link className='text-primaryColor hover:underline' href='/forgot-password'>
                           Quên mật khẩu?
                        </Link>
                        <Link className='text-primaryColor hover:underline' href='/register'>
                           Đăng ký
                        </Link>
                     </div>
                  </form>
               </Form>
            </CardContent>
         </Card>
      </div>
   )
}

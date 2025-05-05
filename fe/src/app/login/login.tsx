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
import { Button } from '@/components/ui/button'

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
         <Card className='w-[400px]'>
            <CardHeader>
               <CardTitle className='text-2xl'>Đăng nhập</CardTitle>
               <CardDescription>Nhập tên đăng nhập và mật khẩu của bạn để đăng nhập vào hệ thống</CardDescription>
            </CardHeader>
            <CardContent>
               <Form {...form}>
                  <form className='space-y-3' noValidate onSubmit={form.handleSubmit(onSubmit)}>
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

                     <div className='relative'>
                        <div className='absolute inset-0 flex items-center'>
                           <span className='w-full border-t' />
                        </div>
                        <div className='relative flex justify-center text-xs uppercase'>
                           <span className='bg-background px-2 text-muted-foreground'>Hoặc đăng nhập với</span>
                        </div>
                     </div>

                     <div className='grid grid-cols-2 gap-3'>
                        <Button
                           type='button'
                           variant={'outline'}
                           className='flex items-center justify-center gap-2 h-9 px-4 border rounded '
                        >
                           <svg className='w-4 h-4' viewBox='0 0 48 48'>
                              <path
                                 fill='#FFC107'
                                 d='M43.611,20.083H42V20H24v8h11.303c-1.649,4.657-6.08,8-11.303,8c-6.627,0-12-5.373-12-12c0-6.627,5.373-12,12-12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C12.955,4,4,12.955,4,24c0,11.045,8.955,20,20,20c11.045,0,20-8.955,20-20C44,22.659,43.862,21.35,43.611,20.083z'
                              />
                              <path
                                 fill='#FF3D00'
                                 d='M6.306,14.691l6.571,4.819C14.655,15.108,18.961,12,24,12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C16.318,4,9.656,8.337,6.306,14.691z'
                              />
                              <path
                                 fill='#4CAF50'
                                 d='M24,44c5.166,0,9.86-1.977,13.409-5.192l-6.19-5.238C29.211,35.091,26.715,36,24,36c-5.202,0-9.619-3.317-11.283-7.946l-6.522,5.025C9.505,39.556,16.227,44,24,44z'
                              />
                              <path
                                 fill='#1976D2'
                                 d='M43.611,20.083H42V20H24v8h11.303c-0.792,2.237-2.231,4.166-4.087,5.571c0.001-0.001,0.002-0.001,0.003-0.002l6.19,5.238C36.971,39.205,44,34,44,24C44,22.659,43.862,21.35,43.611,20.083z'
                              />
                           </svg>
                           Google
                        </Button>

                        <Button
                           type='button'
                           variant={'outline'}
                           className='flex items-center justify-center gap-2 h-9 px-4 border rounded'
                        >
                           <svg className='w-4 h-4' viewBox='0 0 24 24'>
                              <path
                                 fill='#1877F2'
                                 d='M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z'
                              />
                           </svg>
                           Facebook
                        </Button>
                     </div>

                     <div className='text-center text-sm'>
                        Chưa có tài khoản?{' '}
                        <Link className='text-primaryColor hover:underline' href={'/register'}>
                           Đăng ký
                        </Link>{' '}
                        |{' '}
                        <Link className='text-primaryColor hover:underline' href={'/forgot-password'}>
                           Quên mật khẩu
                        </Link>
                     </div>
                  </form>
               </Form>
            </CardContent>
         </Card>
      </div>
   )
}

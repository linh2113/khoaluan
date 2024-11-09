'use client'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useForm } from 'react-hook-form'
import { Form, FormField, FormItem, FormMessage } from '@/components/ui/form'
import { zodResolver } from '@hookform/resolvers/zod'
import { LoginBody, LoginBodyType } from '@/schemaValidations/auth.schema'
import Link from 'next/link'

export default function Register() {
   const form = useForm<LoginBodyType>({
      resolver: zodResolver(LoginBody),
      defaultValues: {
         email: '',
         password: ''
      }
   })
   // eslint-disable-next-line @typescript-eslint/no-unused-vars
   const onSubmit = async (data: LoginBodyType) => {}
   return (
      <Card className='mx-auto max-w-sm mt-10'>
         <CardHeader>
            <CardTitle className='text-2xl'>Đăng ký</CardTitle>
            <CardDescription>Nhập email và mật khẩu của bạn để đăng ký vào hệ thống</CardDescription>
         </CardHeader>
         <CardContent>
            <Form {...form}>
               <form
                  className='space-y-2 max-w-[600px] flex-shrink-0 w-full'
                  noValidate
                  onSubmit={form.handleSubmit(onSubmit, (err) => {
                     console.log(err)
                  })}
               >
                  <div className='grid gap-4'>
                     <FormField
                        control={form.control}
                        name='email'
                        render={({ field }) => (
                           <FormItem>
                              <div className='grid gap-2'>
                                 <Label htmlFor='email'>Email</Label>
                                 <Input id='email' type='email' placeholder='m@example.com' required {...field} />
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
                                 <div className='flex items-center'>
                                    <Label htmlFor='password'>Password</Label>
                                 </div>
                                 <Input isPassword id='password' type='password' required {...field} />
                                 <FormMessage />
                              </div>
                           </FormItem>
                        )}
                     />
                     <button type='submit' className='w-full bg-primaryColor text-white h-9 rounded button-primary'>
                        Đăng nhập
                     </button>
                     <p className='text-center'>
                        Bạn đã có tài khoản?{' '}
                        <Link className='text-secondaryColor hover:underline' href={'/login'}>
                           Đăng nhậpp
                        </Link>
                     </p>
                  </div>
               </form>
            </Form>
         </CardContent>
      </Card>
   )
}

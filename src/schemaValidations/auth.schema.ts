import z from 'zod'

export const LoginBody = z
   .object({
      email: z.string().email('Email không hợp lệ'),
      password: z.string().min(6, { message: 'Mật khẩu phải có ít nhất 6 kí tự' }).max(100)
   })
   .strict()

export type LoginBodyType = z.TypeOf<typeof LoginBody>

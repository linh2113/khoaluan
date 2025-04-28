import http from '@/lib/http'
import { LoginBodyType, RegisterBodyType } from '@/schemaValidations/auth.schema'
import { LoginResType } from '@/types/auth.type'

export const registerAccount = (body: RegisterBodyType) => http.post('/auth/register', body)

export const loginAccount = (body: LoginBodyType) => http.post<LoginResType>('/auth/login', body)

export const forgotPassword = (email: string) => http.post('/auth/forgot-password', { email })

export const resetPassword = (body: { token: string; newPassword: string }) => http.post('/auth/reset-password', body)

export const refreshToken = (refreshToken: string) => http.post<LoginResType>('/auth/refresh-token', { refreshToken })

export const verifyEmail = (token: string) => http.get('/auth/verify-email?token=' + token)

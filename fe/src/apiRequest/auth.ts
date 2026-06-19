import http from '@/lib/http'
import { LoginBodyType, RegisterBodyType } from '@/schemaValidations/auth.schema'
import { LoginResType } from '@/types/auth.type'

export const registerAccount = (body: RegisterBodyType) => http.post('/auth/register', body)

export const loginAccount = (body: LoginBodyType) => http.post<LoginResType>('/auth/login', body)

export const forgotPassword = (email: string) => http.post(`/auth/forgot-password?email=${email}`)

export const resetPassword = (body: { token: string; newPassword: string }) => http.post('/auth/reset-password', body)

export const refreshToken = (refreshToken: string) => http.post<LoginResType>('/auth/refresh-token', { refreshToken })

export const verifyEmail = (token: string) => http.get('/auth/verify-email?token=' + token)
//OAuth2
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api/v1'
const APP_URL = process.env.NEXT_PUBLIC_APP_URL || 'http://localhost:3000'

export const getGoogleAuthUrl = () =>
   `${API_BASE_URL}/oauth2/authorize/google?redirect_uri=${APP_URL}/oauth2/callback`

export const getDiscordAuthUrl = () =>
   `${API_BASE_URL}/oauth2/authorize/discord?redirect_uri=${APP_URL}/oauth2/callback`

export const handleOAuth2Redirect = (token: string, refreshToken: string) =>
   http.get<LoginResType>(`/auth/oauth2/redirect?token=${token}&refreshToken=${refreshToken}`)

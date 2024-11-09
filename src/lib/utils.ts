import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
   return twMerge(clsx(inputs))
}
export const formatCurrency = (number: number) => {
   return number?.toLocaleString() + 'đ'
}
export const formatNumberToK = (number: number) => {
   if (number < 1000) return number
   return `${(number / 1000).toFixed(1)}k`
}

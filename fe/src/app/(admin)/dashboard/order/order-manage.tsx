'use client'
import { useGetAllAdminOrder } from '@/queries/useAdmin'
import React from 'react'

export default function OrderManage() {
   const { data: orders, isLoading } = useGetAllAdminOrder({
      page: 0,
      size: 10,
      sortBy: 'id',
      sortDir: 'desc'
   })

   return <div>OrderManage</div>
}

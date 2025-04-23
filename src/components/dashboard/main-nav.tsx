"use client"

import type React from "react"

import Link from "next/link"
import { usePathname } from "next/navigation"

import { cn } from "@/lib/utils"

export function MainNav({ className, ...props }: React.HTMLAttributes<HTMLElement>) {
  const pathname = usePathname()

  return (
    <nav className={cn("flex items-center space-x-4 lg:space-x-6", className)} {...props}>
      <Link
        href="/dashboard"
        className={cn(
          "text-sm font-medium transition-colors hover:text-primary",
          pathname === "/dashboard" ? "text-primary" : "text-muted-foreground",
        )}
      >
        Tổng quan
      </Link>
      <Link
        href="/dashboard/orders"
        className={cn(
          "text-sm font-medium transition-colors hover:text-primary",
          pathname === "/dashboard/orders" ? "text-primary" : "text-muted-foreground",
        )}
      >
        Đơn hàng
      </Link>
      <Link
        href="/dashboard/products"
        className={cn(
          "text-sm font-medium transition-colors hover:text-primary",
          pathname === "/dashboard/products" ? "text-primary" : "text-muted-foreground",
        )}
      >
        Sản phẩm
      </Link>
      <Link
        href="/dashboard/users"
        className={cn(
          "text-sm font-medium transition-colors hover:text-primary",
          pathname === "/dashboard/users" ? "text-primary" : "text-muted-foreground",
        )}
      >
        Tài khoản
      </Link>
      <Link
        href="/dashboard/reviews"
        className={cn(
          "text-sm font-medium transition-colors hover:text-primary",
          pathname === "/dashboard/reviews" ? "text-primary" : "text-muted-foreground",
        )}
      >
        Đánh giá
      </Link>
      <Link
        href="/dashboard/promotions"
        className={cn(
          "text-sm font-medium transition-colors hover:text-primary",
          pathname === "/dashboard/promotions" ? "text-primary" : "text-muted-foreground",
        )}
      >
        Khuyến mãi
      </Link>
      <Link
        href="/dashboard/statistics"
        className={cn(
          "text-sm font-medium transition-colors hover:text-primary",
          pathname === "/dashboard/statistics" ? "text-primary" : "text-muted-foreground",
        )}
      >
        Thống kê
      </Link>
    </nav>
  )
}

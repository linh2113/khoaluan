"use client"

import { Bar, BarChart, ResponsiveContainer, XAxis, YAxis } from "recharts"

const data = [
  {
    name: "Tháng 1",
    total: 5500000,
  },
  {
    name: "Tháng 2",
    total: 6200000,
  },
  {
    name: "Tháng 3",
    total: 7800000,
  },
  {
    name: "Tháng 4",
    total: 8900000,
  },
  {
    name: "Tháng 5",
    total: 9100000,
  },
  {
    name: "Tháng 6",
    total: 10200000,
  },
  {
    name: "Tháng 7",
    total: 11500000,
  },
  {
    name: "Tháng 8",
    total: 12300000,
  },
  {
    name: "Tháng 9",
    total: 13100000,
  },
  {
    name: "Tháng 10",
    total: 14200000,
  },
  {
    name: "Tháng 11",
    total: 15500000,
  },
  {
    name: "Tháng 12",
    total: 16800000,
  },
]

export function Overview() {
  return (
    <ResponsiveContainer width="100%" height={350}>
      <BarChart data={data}>
        <XAxis dataKey="name" stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
        <YAxis
          stroke="#888888"
          fontSize={12}
          tickLine={false}
          axisLine={false}
          tickFormatter={(value) => `${value / 1000000}M`}
        />
        <Bar dataKey="total" fill="currentColor" radius={[4, 4, 0, 0]} className="fill-primary" />
      </BarChart>
    </ResponsiveContainer>
  )
}

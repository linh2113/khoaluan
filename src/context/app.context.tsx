'use client'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { createContext, useContext } from 'react'

const queryClient = new QueryClient({
   defaultOptions: {
      queries: {
         refetchOnWindowFocus: false,
         retry: 0
      }
   }
})
const AppContext = createContext({})
export const useAppContext = () => {
   return useContext(AppContext)
}
export default function AppProvider({ children }: { children: React.ReactNode }) {
   return (
      <AppContext.Provider value={{}}>
         <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
      </AppContext.Provider>
   )
}

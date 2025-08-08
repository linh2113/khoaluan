import { Card, CardContent, CardHeader } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'

export default function SectionSkeleton() {
   return (
      <Card className='w-full shadow-lg border-0 overflow-hidden'>
         <CardHeader className='p-6 bg-gradient-to-r from-muted/50 to-muted/30'>
            <div className='flex items-center justify-between'>
               <div className='flex items-center gap-3'>
                  <Skeleton className='h-8 w-8 rounded' />
                  <div>
                     <Skeleton className='h-6 w-48 mb-2' />
                     <Skeleton className='h-4 w-64' />
                  </div>
                  <Skeleton className='h-6 w-20 rounded-full' />
               </div>
               <div className='flex items-center gap-2'>
                  <Skeleton className='h-8 w-20' />
                  <Skeleton className='h-8 w-24' />
               </div>
            </div>
         </CardHeader>
         <CardContent className='p-6'>
            <div className='grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4'>
               {Array(4)
                  .fill(0)
                  .map((_, index) => (
                     <div key={index} className='border rounded-xl p-4 shadow-sm'>
                        <Skeleton className='h-48 w-full mb-4 rounded-lg' />
                        <Skeleton className='h-4 w-1/3 mb-2' />
                        <Skeleton className='h-5 w-3/4 mb-4' />
                        <Skeleton className='h-4 w-1/2 mb-2' />
                        <Skeleton className='h-8 w-full mt-4' />
                     </div>
                  ))}
            </div>
         </CardContent>
      </Card>
   )
}

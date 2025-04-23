import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"

export function RecentSales() {
  return (
    <div className="space-y-8">
      <div className="flex items-center">
        <Avatar className="h-9 w-9">
          <AvatarImage src="/placeholder.svg" alt="Avatar" />
          <AvatarFallback>NV</AvatarFallback>
        </Avatar>
        <div className="ml-4 space-y-1">
          <p className="text-sm font-medium leading-none">Nguyễn Văn A</p>
          <p className="text-sm text-muted-foreground">nguyenvana@example.com</p>
        </div>
        <div className="ml-auto font-medium">+1.250.000 ₫</div>
      </div>
      <div className="flex items-center">
        <Avatar className="flex h-9 w-9 items-center justify-center space-y-0 border">
          <AvatarImage src="/placeholder.svg" alt="Avatar" />
          <AvatarFallback>TT</AvatarFallback>
        </Avatar>
        <div className="ml-4 space-y-1">
          <p className="text-sm font-medium leading-none">Trần Thị B</p>
          <p className="text-sm text-muted-foreground">tranthib@example.com</p>
        </div>
        <div className="ml-auto font-medium">+950.000 ₫</div>
      </div>
      <div className="flex items-center">
        <Avatar className="h-9 w-9">
          <AvatarImage src="/placeholder.svg" alt="Avatar" />
          <AvatarFallback>LV</AvatarFallback>
        </Avatar>
        <div className="ml-4 space-y-1">
          <p className="text-sm font-medium leading-none">Lê Văn C</p>
          <p className="text-sm text-muted-foreground">levanc@example.com</p>
        </div>
        <div className="ml-auto font-medium">+850.000 ₫</div>
      </div>
      <div className="flex items-center">
        <Avatar className="h-9 w-9">
          <AvatarImage src="/placeholder.svg" alt="Avatar" />
          <AvatarFallback>PT</AvatarFallback>
        </Avatar>
        <div className="ml-4 space-y-1">
          <p className="text-sm font-medium leading-none">Phạm Thị D</p>
          <p className="text-sm text-muted-foreground">phamthid@example.com</p>
        </div>
        <div className="ml-auto font-medium">+750.000 ₫</div>
      </div>
      <div className="flex items-center">
        <Avatar className="h-9 w-9">
          <AvatarImage src="/placeholder.svg" alt="Avatar" />
          <AvatarFallback>HV</AvatarFallback>
        </Avatar>
        <div className="ml-4 space-y-1">
          <p className="text-sm font-medium leading-none">Hoàng Văn E</p>
          <p className="text-sm text-muted-foreground">hoangvane@example.com</p>
        </div>
        <div className="ml-auto font-medium">+650.000 ₫</div>
      </div>
    </div>
  )
}

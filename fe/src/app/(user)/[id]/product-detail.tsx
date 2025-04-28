'use client'
import ProductRating from '@/components/product-rating'
import QuantityController from '@/components/quantity-controller'
import { formatCurrency, formatNumberToK } from '@/lib/utils'
import { ShoppingBasket } from 'lucide-react'
import Image from 'next/image'
import React, { useState } from 'react'
import Autoplay from 'embla-carousel-autoplay'
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from '@/components/ui/carousel'
import ProductItem from '@/components/product-item'
import { Textarea } from '@/components/ui/textarea'
export default function ProductDetail({ id }: { id: string }) {
   const [buyCount, setBuyCount] = useState<number>(1)
   const handleBuyCount = (value: number) => {
      setBuyCount(value)
   }

   return (
      <>
         <div className='bg-secondary rounded-lg p-5 flex gap-10'>
            <div className='w-[40%] flex flex-col gap-4'>
               <Image
                  src={'https://cdn.tgdd.vn/Products/Images/42/329143/iphone-16-pro-titan-sa-mac.png'}
                  alt=''
                  width={500}
                  height={500}
                  className='aspect-square'
               />
               <div className='grid grid-cols-5 gap-3'>
                  {Array(5)
                     .fill(0)
                     .map((item, index) => (
                        <Image
                           key={index}
                           src={'https://cdn.tgdd.vn/Products/Images/42/329143/iphone-16-pro-titan-sa-mac.png'}
                           alt=''
                           width={50}
                           height={50}
                           className='aspect-square flex-1'
                        />
                     ))}
               </div>
            </div>
            <div className='w-[60%] flex flex-col gap-5'>
               <h1 className='font-medium text-2xl'>
                  [KHUYẾN MÃI 35%] Áo Thun POLO Nam, Tay Ngắn Áo Cổ Sọc, Chất Liệu Cá Sấu Cao Cấp - Nhiều màu- Đủ Size
               </h1>
               <div className='flex items-center gap-3 text-base'>
                  <ProductRating
                     classNameStar1='w-5 h-5 fill-yellow-300 text-yellow-300'
                     classNameStar2='w-5 h-5 fill-current text-gray-300'
                     rating={3.5}
                  />
                  |<span>{formatNumberToK(1245)} đã bán</span>
               </div>
               <div className='bg-background p-5 rounded flex items-center gap-5'>
                  <span className='line-through text-base text-gray-500'>{formatCurrency(1000000)}</span>
                  <span className='text-secondaryColor text-2xl font-medium'>{formatCurrency(2000000)}</span>
                  <span className='rounded-sm bg-secondaryColor px-1 py-[2px] text-xs font-semibold text-white'>
                     35% GIẢM
                  </span>
               </div>
               <div className='flex items-center gap-4 text-base'>
                  <span>Số lượng</span>
                  <QuantityController
                     value={buyCount}
                     max={100}
                     onIncrease={handleBuyCount}
                     onDecrease={handleBuyCount}
                     onType={handleBuyCount}
                  />
                  <span>75 Sản phẩm có sẵn</span>
               </div>
               <div className='flex items-center gap-4 text-base'>
                  <button className='px-5 py-3 flex items-center gap-2 rounded border border-secondaryColor bg-secondaryColor/10 hover:bg-secondaryColor/0 text-secondaryColor'>
                     <ShoppingBasket />
                     Thêm vào giỏ hàng
                  </button>
                  <button className='px-5 py-3 bg-secondaryColor text-white rounded hover:bg-secondaryColor/90'>
                     Mua ngay
                  </button>
               </div>
            </div>
         </div>
         <div className='bg-secondary rounded-lg p-5 mt-5'>
            <h2 className='font-medium text-xl mb-5 p-3 rounded-lg bg-background'>Mô Tả Sản Phẩm</h2>
            <p>
               Áo Polo nam tay ngắn phù hợp với nhiều hoàn cảnh: công sở, mặc nhà, đi học, đi chơi, du lịch, thể thao,
               Gym, làm quà tặng…… Tạo cảm giác trẻ trung cho người mặc, phối hợp được với nhiều loại quần như quần
               kaki, quần tây, quần jeans, quần short….. Thông tin sản phẩm Tên sản phẩm: Áo Thun POLO Nam, Tay Ngắn Áo
               Cổ Sọc, áo thun nam, áo cá sấu Xuất xứ: Việt Nam Màu sắc: 10 Màu: Trắng, Đen, Xanh lá, Xanh Rêu, Cam
               Đất, Đỏ Đô, Xanh Biển, Xám Khói, Coffee, Xanh Đen Size: S, M, L, XL
               -------------------------------------- Hướng dẫn chọn size theo chiều cao cân nặng Thông số: Size S: Cân
               nặng từ 53 - 60kg Size M: Cân nặng từ 60 - 68kg Size L: Cân nặng từ 68 - 78kg Size XL: Cân nặng từ 78
               - 85kg -------------------------------------- Vì sao nên mua hàng tại Lozano Chất lượng vải: Chất liệu
               thun Cá Sấu 100% cotton, bề mặt mềm mịn, thông thoáng, co dãn giúp giảm nhiệt cực nhanh. (có thể thêm đặc
               điểm màu sắc vào nếu áo có những đặc điểm riêng biệt)Độ dày vừa phải đảm bảo giữ form dáng, bền màu sau
               nhiều lần giặt. Những đường may tỉ mỉ cũng là một đặc điểm đáng chú ý của áo Polo bên Lozano, chất liệu
               thoáng mát thấm hút mồ hôi tốt giúp hoạt động thoải mái trong công việc hàng ngày Giá cả áo Polo bên
               shop Lozano “hời” nhưng chất lượng áo tốt, với tiêu chí đưa đến khách hàng sản phẩm chất lượng đảm bảo,
               giá cả phải chăng. Sự uy tín của shop được đưa lên hàng đầu. Dù vậy, nhưng với lượng sản phẩm bán đi
               hàng ngày của Lozano tại cửa hàng và các sàn thương mại điện tử rất nhiều, nên sẽ không tránh khỏi sai
               sót khi sản phẩm đến tay khách hàng. Lozano mong sự thông cảm đến từ quý khách và có cam kết về việc đổi
               hàng nếu hàng hóa bị lỗi CHÚNG TÔI CAM KẾT: Cam kết chất lượng và mẫu quần áo giống 100% trong hình ảnh
               và thông tin mô tả Cam kết được đổi sản phẩm trong vòng 14 ngày Liên hệ đổi hàng ngay với bộ phận bán
               hàng qua hotline 0775.922.123 Nhận ship COD toàn quốc, với dịch vụ giao hàng rẻ, tiết kiệm Chúng tôi
               mong quý khách khi nhận được sản phẩm sẽ đánh giá chúng tôi một cách khách quan nhất dựa vào những dấu *
               và những hình ảnh, video cụ thể. Đó là những đóng góp vô cùng quý giá để Lozano VietNam có thể thay đổi
               và hoàn thiện hơn Địa chỉ cửa hàng: 1148 Cách Mạng Tháng 8, Phường 4, Quận Tân Bình, TP. HCM Hotline:
               0775.922.123 #aothunnam #aopolo #thunpolo #aophongnam #aocobe #aonam #aothunnamcobe #thunnam #ao #polonam
               #aothuntay #aococtaynam #aothuntayngan #aothuncobe #aothun #aophong #aopolocosoc #aothunsoc #aopolosoc
               #polosoc #polotayngan #aothunpolo
            </p>
         </div>
         <div className='bg-secondary rounded-lg p-5 mt-5'>
            <h2 className='font-medium text-xl mb-5 p-3 rounded-lg bg-background'>Đánh giá sản phẩm</h2>
            <div className='space-y-4'>
               {[1, 2].map((_, index) => (
                  <div key={index} className='p-3 border rounded-lg'>
                     <div className='flex items-center gap-2 mb-2'>
                        {/* Đánh giá sao (static) */}
                        <ProductRating
                           rating={4.5}
                           classNameStar1='w-5 h-5 fill-yellow-300 text-yellow-300'
                           classNameStar2='w-5 h-5 fill-current text-gray-300'
                        />
                        <span className='text-sm text-gray-500'>1 ngày trước</span>
                     </div>
                     <p>Chất lượng sản phẩm tốt, màu sắc đẹp, giao hàng nhanh.</p>
                  </div>
               ))}
            </div>
            <div className='flex flex-col gap-4 mt-6'>
               <div className='flex items-center gap-2'>
                  <label>Đánh giá:</label>
                  <ProductRating
                     rating={0} // Giá trị tĩnh
                     classNameStar1='w-5 h-5 fill-yellow-300 text-yellow-300'
                     classNameStar2='w-5 h-5 fill-current text-gray-300'
                  />
               </div>
               <Textarea placeholder='Nhập đánh giá của bạn' className='border-primary' />
               <button className='px-4 py-2 bg-secondaryColor text-white rounded hover:bg-secondaryColor/90'>
                  Gửi đánh giá
               </button>
            </div>
         </div>
         <div className='bg-secondary rounded-lg p-5 mt-5'>
            <h2 className='font-medium text-xl mb-5 p-3 rounded-lg bg-background'>Có thể bạn cũng thích</h2>
            <Carousel
               plugins={[
                  Autoplay({
                     delay: 4000
                  })
               ]}
            >
               <CarouselContent>
                  {Array(10)
                     .fill(0)
                     .map((item, index) => (
                        <CarouselItem key={index} className='basis-1/6 pl-4'>
                           <ProductItem />
                        </CarouselItem>
                     ))}
               </CarouselContent>
               <CarouselPrevious />
               <CarouselNext />
            </Carousel>
         </div>
      </>
   )
}

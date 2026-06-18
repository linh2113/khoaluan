export interface PriceDisplay {
   originalPrice: number
   salePrice: number
   hasDiscount: boolean
   discountPercent: number
}

export function getProductPriceDisplay(product: {
   price: number
   discountedPrice?: number | null
}): PriceDisplay {
   const hasDiscount =
      product.discountedPrice != null && product.discountedPrice < product.price

   const originalPrice = product.price
   const salePrice = hasDiscount ? product.discountedPrice! : product.price
   const discountPercent = hasDiscount
      ? Math.round(((originalPrice - salePrice) / originalPrice) * 100)
      : 0

   return { originalPrice, salePrice, hasDiscount, discountPercent }
}

/** BE field `productPrice` = giá gốc (= Product.price) */
export function getWishlistPriceDisplay(item: {
   productPrice: number
   discountedPrice?: number | null
}): PriceDisplay {
   return getProductPriceDisplay({
      price: item.productPrice,
      discountedPrice: item.discountedPrice
   })
}

/** Cart: `originalPrice` = giá gốc, `price` = đơn giá sau giảm */
export function getCartPriceDisplay(item: { originalPrice: number; price: number }): PriceDisplay {
   const hasDiscount = item.price < item.originalPrice
   return {
      originalPrice: item.originalPrice,
      salePrice: item.price,
      hasDiscount,
      discountPercent: hasDiscount
         ? Math.round(((item.originalPrice - item.price) / item.originalPrice) * 100)
         : 0
   }
}

export function getCartSavedAmount(item: {
   originalPrice: number
   price: number
   quantity: number
}): number {
   const { hasDiscount, originalPrice, salePrice } = getCartPriceDisplay(item)
   return hasDiscount ? (originalPrice - salePrice) * item.quantity : 0
}

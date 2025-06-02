import type { NextConfig } from 'next'
import createNextIntlPlugin from 'next-intl/plugin'
const nextConfig: NextConfig = {
   images: {
      remotePatterns: [
         {
            protocol: 'https',
            hostname: 'cdn2.fptshop.com.vn',
            port: '',
            pathname: '/**'
         },
         {
            protocol: 'https',
            hostname: 'cdn.tgdd.vn',
            port: '',
            pathname: '/**'
         },
         {
            protocol: 'https',
            hostname: 'res.cloudinary.com',
            port: '',
            pathname: '/**'
         },
         {
            protocol: 'https',
            hostname: 'lh3.googleusercontent.com',
            port: '',
            pathname: '/**'
         }
      ]
   }
}

const withNextIntl = createNextIntlPlugin()
export default withNextIntl(nextConfig)

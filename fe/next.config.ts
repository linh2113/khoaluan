import type { NextConfig } from 'next'

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
         }
      ]
   }
}

export default nextConfig

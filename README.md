# ======================
# 🔧 Server Configuration
# ======================
server.port=8080
server.servlet.context-path=/api/v1
python.api.url=http://localhost:8001   

# ==========================
# 🗄️ Database Configuration
# ==========================
spring.datasource.url=jdbc:mysql://localhost:3306/techstorefinal?createDatabaseIfNotExist=true
spring.datasource.username=DB_USERNAME
spring.datasource.password=DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000

# ====================
# 🔐 JWT Configuration
# ====================
jwt.secret=BhdFp++FHu7jWopVw6x0tL0ntmvufd7wmvLsbMiGSu7c/CQe/ecyI9IHL49sdXDdUXdsp+HoTqDiUcNG3KsBbg==
jwt.expiration=86400000
jwt.refresh-token.expiration=604800000  

# =======================
# 📨 Mail Configuration
# =======================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=linhson208@gmail.com
spring.mail.password=towk gnyo yraf ohhh
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# =================================
# 🌐 OAuth2 Login (Google, Facebook, Discord)
# =================================
# Google
spring.security.oauth2.client.registration.google.client-id=66573799121-mtn3va166kro3k2d1e7p04iutm7i3i4o.apps.googleusercontent.com
spring.security.oauth2.client.registration.google.client-secret=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


# Discord
spring.security.oauth2.client.registration.discord.client-id=1255854814747689012
spring.security.oauth2.client.registration.discord.client-secret=WNIbPCy7EChlZt29JpggKjWjcyDoxOG1

# Common redirect URI
app.oauth2.authorized-redirect-uri=http://localhost:3000/oauth2/callback

# ====================
# ☁️ Cloudinary Config
# ====================
cloudinary.cloud-name=dvxnipyk4
cloudinary.api-key=692147617982258
cloudinary.api-secret=hfXoMc2I3MMYYDPEBQ1Iw_8kJwI

# ====================
# 💳 VNPay Integration
# ====================
vnpay.terminal-id=CO15G38U
vnpay.secret-key=E8D8CZ8PITHCQQBZQGMR11GXLOUXSC5K
vnpay.payment-url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html

# ==========================
# 💾 File Upload
# ==========================
app.upload.dir=${user.home}/techstore/uploads

# =======================
# 📊 Logging Configuration
# =======================
logging.file.name=logs/techstore.log
logging.level.root=WARN
logging.level.org.springframework.web=INFO
logging.level.org.hibernate=ERROR
logging.level.com.ecommerce.techstore=DEBUG

# ====================
# 🔒 Admin Access
# ====================
spring.security.user.name=admin
spring.security.user.password=admin

# ========================
# 🔧 Misc Configurations
# ========================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.open-in-view=false


# Cache
spring.cache.type=redis
spring.cache.redis.time-to-live=3600000

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when_authorized

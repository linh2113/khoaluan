FROM python:3.9-slim

WORKDIR /app


RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    g++ \
    libffi-dev \
    libpq-dev \
    libxml2-dev \
    libxslt1-dev \
    libjpeg-dev \
    zlib1g-dev \
    libc6-dev \
    build-essential \
    python3-dev \
    cython3 \
  && rm -rf /var/lib/apt/lists/*


COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt



COPY . .


ENV DB_HOST=mysql \
    DB_USER=root \
    DB_PASSWORD=son1234567890 \
    DB_NAME=techstorefinal \
    DB_AUTOCOMMIT=true

EXPOSE 8001

CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8001"]

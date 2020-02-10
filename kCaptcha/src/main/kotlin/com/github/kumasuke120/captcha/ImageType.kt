package com.github.kumasuke120.captcha

enum class ImageType(val formatName: String, val mimeType: String) {
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    BMP("bmp", "image/bmp"),
    GIF("gif", "image/gif"),
    PNG("png", "image/png")
}
package com.github.kumasuke120.captcha

class CaptchaGenerateException internal constructor(
        override val message: String,
        override val cause: Throwable
) : RuntimeException(message, cause) {
    internal constructor(cause: Throwable) : this(cause.toString(), cause)
}
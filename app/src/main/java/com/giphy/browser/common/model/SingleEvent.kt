package com.giphy.browser.common.model

class SingleEvent<T>(private val data: T) {
    private var consumed = false

    fun maybeConsume(consumer: (T) -> Unit) {
        if (!consumed) {
            consumed = true
            consumer(data)
        }
    }
}

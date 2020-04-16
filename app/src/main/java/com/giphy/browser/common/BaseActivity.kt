package com.giphy.browser.common

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity : AppCompatActivity() {
    protected inline fun <reified T : ViewModel> createViewModel(crossinline block: () -> T): T {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = block() as T
        }).get(T::class.java)
    }
}

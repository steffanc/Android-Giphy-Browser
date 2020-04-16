package com.giphy.browser.common.network


/**
 * Exception for an unexpected, non-2xx HTTP response.
 */
class ApiException(message: String) : Exception(message)

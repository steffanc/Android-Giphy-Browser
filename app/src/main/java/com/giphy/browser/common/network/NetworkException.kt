package com.giphy.browser.common.network

/**
 * Exception for a problem with the transport to the remote server.
 */
class NetworkException : Exception {
  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)
  constructor(cause: Throwable) : super(cause)
}

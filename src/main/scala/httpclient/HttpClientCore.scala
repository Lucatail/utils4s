package org.altynai.utils4s
package httpclient

import com.typesafe.scalalogging.Logger

private[httpclient] trait HttpClientCore {
  protected val Log: Logger = Logger(getClass)
}

private[httpclient] trait HttpClientCoreCreator[T <: HttpClientCore] {
  protected val Log: Logger = Logger(getClass)
  protected var core: T

  def apply(): T = {
    if (null == core)
      this.synchronized({
        if (null == core) {
          core = defaultCore
          Log.info("Successfully initialized the default {}!", getClass.getSimpleName)
        }
      })
    core
  }

  def init(maxTotal: Int, maxPerRoute: Int, socketTimeout: Int, connectionTimeout: Int): Unit = {
    if (null == core) {
      this.synchronized({
        if (null == core) {
          core = initCore(maxTotal, maxPerRoute, socketTimeout, connectionTimeout)
          Log.info("Successfully initialized the {}!", getClass.getSimpleName)
          return
        }
      })
    }
    Log.warn("Invalid initialization: repeated initialization")
  }

  protected def defaultCore: T

  protected def initCore(maxTotal: Int, maxPerRoute: Int, socketTimeout: Int, connectionTimeout: Int): T
}
package org.altynai.utils4s
package httpclient

/**
 * <h1>HTTP客户端</h1>
 *
 * <br>
 * 自定义参数启动: {{{
 *   HttpClient.initialize(HttpClientBuilder()
 *     .setMaxTotal(200)
 *     .setMaxPerRoute(20)
 *   )
 * }}}
 * 同步HTTP [[SyncApi]]
 * {{{
 *   HttpClient.sync.doGet(uri, params, heads)
 *   HttpClient.sync.doPost(uri, params, heads)
 * }}}
 * <br>
 * 异步HTTP [[AsyncApi]]
 * {{{
 *   HttpClient.async.doPost(uri, params, heads, callback = new FutureCallback[HttpResponse]() {
 *     override def completed(rsp: HttpResponse): Unit = EntityUtils.toString(rsp.getEntity)
 *     override def failed(ex: Exception): Unit = println(ex)
 *     override def cancelled(): Unit = ???
 *   })
 * }}}
 * <br>
 * HTTP客户端通过连接池实现, 连接池可以缓存连接
 */
object HttpClient {
  lazy val sync = new SyncApi(SyncCore())
  lazy val async = new AsyncApi(AsyncCore())

  /**
   * 初始化自定义参数的HTTP客户端
   *
   * @param builder 参数构建器
   */
  def initialize(builder: HttpClientBuilder): Unit = {
    builder.build()
  }

  /**
   * 启动HttpCore
   */
  private[httpclient] def initialize(maxTotal: Int, maxPerRoute: Int, socketTimeout: Int, connectionTimeout: Int): Unit = {
    SyncCore.init(maxTotal, maxPerRoute, socketTimeout, connectionTimeout)
    AsyncCore.init(maxTotal, maxPerRoute, socketTimeout, connectionTimeout)
  }
}

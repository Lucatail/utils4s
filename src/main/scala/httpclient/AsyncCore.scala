package org.altynai.utils4s
package httpclient

import org.apache.http.HttpResponse
import org.apache.http.client.config.RequestConfig
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.AbstractHttpEntity
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor

/**
 * 异步HTTP请求核心处理单元
 */
private[httpclient] class AsyncCore private(maxTotal: Int = DefaultMaxTotal,
                                            maxPerRoute: Int = DefaultMaxPerRoute,
                                            socketTimeout: Int = DefaultSocketTimeout,
                                            connectionTimeout: Int = DefaultConnectionTimeout) extends HttpClientCore {
  private[this] val httpclient: CloseableHttpAsyncClient = {
    val ioReactor = new DefaultConnectingIOReactor
    val cm = new PoolingNHttpClientConnectionManager(ioReactor)
    cm.setMaxTotal(maxTotal)
    cm.setDefaultMaxPerRoute(maxPerRoute)
    val httpclient = HttpAsyncClients.custom.setConnectionManager(cm).build
    httpclient.start()
    httpclient
  }
  private[this] val rc: RequestConfig = RequestConfig.custom()
    .setSocketTimeout(socketTimeout) // 单位ms 从服务器读取数据的timeout
    .setConnectTimeout(connectionTimeout) // 单位ms 和服务器建立连接的timeout
    //.setConnectionRequestTimeout(2000)  // 从连接池获取连接的timeout, 默认不超时
    .build()

  /**
   * 执行异步POST请求
   *
   * @param uri      请求地址
   * @param entity   请求体
   * @param heads    请求头
   * @param callback 回调函数
   */
  def doAsyncPost(uri: String, entity: AbstractHttpEntity, heads: Map[String, String], callback: FutureCallback[HttpResponse]): Unit = {
    val post = HttpUtil.createHttpPost(uri, entity, heads, rc)
    httpclient.execute(post, callback)
  }

  /**
   * 执行异步GET请求
   *
   * @param uri      请求地址
   * @param params   参数列表
   * @param heads    请求头
   * @param callback 回调函数
   */
  def doAsyncGet(uri: String, params: Map[String, String], heads: Map[String, String], callback: FutureCallback[HttpResponse]): Unit = {
    val get = HttpUtil.createHttpGet(uri, params, heads, rc)
    httpclient.execute(get, callback)
  }
}

private[httpclient] object AsyncCore extends HttpClientCoreCreator[AsyncCore] {
  override protected var core: AsyncCore = _

  override protected def defaultCore: AsyncCore = new AsyncCore

  override protected def initCore(maxTotal: Int, maxPerRoute: Int, socketTimeout: Int, connectionTimeout: Int): AsyncCore = {
    new AsyncCore(maxTotal, maxPerRoute, socketTimeout, connectionTimeout)
  }
}
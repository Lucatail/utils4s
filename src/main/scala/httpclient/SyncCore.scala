package org.altynai.utils4s
package httpclient

import org.apache.http.HttpEntity
import org.apache.http.client.HttpRequestRetryHandler
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.entity.AbstractHttpEntity
import org.apache.http.impl.client.{CloseableHttpClient, DefaultHttpRequestRetryHandler, HttpClients}
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.ssl.SSLContextBuilder
import org.apache.http.util.EntityUtils

import java.io.IOException
import java.security.cert.X509Certificate

/**
 * 同步HTTP请求核心处理单元
 */
private[httpclient] class SyncCore private(maxTotal: Int = DefaultMaxTotal,
                                           maxPerRoute: Int = DefaultMaxPerRoute,
                                           socketTimeout: Int = DefaultSocketTimeout,
                                           connectionTimeout: Int = DefaultConnectionTimeout) extends HttpClientCore {
  private[this] val cm: PoolingHttpClientConnectionManager = {
    val cm = new PoolingHttpClientConnectionManager()
    cm.setDefaultMaxPerRoute(maxPerRoute) // 单路由最大并发数
    cm.setMaxTotal(maxTotal) // 连接池最大并发连接数
    cm
  }
  private[this] val rh: HttpRequestRetryHandler = new DefaultHttpRequestRetryHandler(2, false)
  private[this] val rc: RequestConfig = RequestConfig.custom()
    .setSocketTimeout(socketTimeout)
    .setConnectTimeout(connectionTimeout)
    .build()

  private[this] def getClient: CloseableHttpClient = {
    val httpclient: CloseableHttpClient = HttpClients.custom()
      .setConnectionManager(cm)
      .setRetryHandler(rh)
      // 忽略https证书
      .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (_: Array[X509Certificate], _: String) => true).build())
      .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
      .build()

    /*
    leased : 当前用于执行请求的连接管理器跟踪的持久连接的数量。
    available : 连接池中空闲的连接
    pending : 请求中的连接数量≤
    max : 允许创建的最大连接数量
     */
    if (null != cm.getTotalStats) {
      Log.whenDebugEnabled({
        Log.debug("Http client Pool Stats : {}", cm.getTotalStats.toString)
      })
    }
    httpclient
  }

  def doPost[R](uri: String, entity: AbstractHttpEntity, heads: Map[String, String], f: HttpEntity => R): R = {
    var _response: CloseableHttpResponse = null
    val post = HttpUtil.createHttpPost(uri, entity, heads, rc)
    try {
      _response = getClient.execute(post)
      if (_response != null) {
        val entity = _response.getEntity
        return f(entity)
      }
    } catch {
      case e: Exception => Log.error("url [{}] cannot connect", uri, e)
    } finally {
      try {
        if (_response != null) {
          EntityUtils.consume(_response.getEntity)
          _response.close()
        }
        post.releaseConnection()
      } catch {
        case e: IOException => Log.error("HTTP request error", e)
      }
    }
    null.asInstanceOf[R]
  }

  def doGet(uri: String, params: Map[String, String], heads: Map[String, String]): String = {
    var _response: CloseableHttpResponse = null
    val get = HttpUtil.createHttpGet(uri, params, heads, rc)
    try {
      _response = getClient.execute(get)
      val entity = _response.getEntity
      EntityUtils.toString(entity, Encoding)
    } catch {
      case e: Exception => Log.error("url [{}] cannot connect", uri, e)
    } finally {
      get.releaseConnection()
      try {
        if (_response != null) _response.close()
      } catch {
        case e: IOException => Log.error("HTTP request error", e)
      }
    }
    null
  }
}

private[httpclient] object SyncCore extends HttpClientCoreCreator[SyncCore] {
  override protected var core: SyncCore = _

  override protected def defaultCore: SyncCore = new SyncCore

  override protected def initCore(maxTotal: Int, maxPerRoute: Int, socketTimeout: Int, connectionTimeout: Int): SyncCore = {
    new SyncCore(maxTotal, maxPerRoute, socketTimeout, connectionTimeout)
  }
}
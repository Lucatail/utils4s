package org.altynai.utils4s
package httpclient

import org.apache.http.conn.routing.HttpRoute

/**
 * HttpClient 构建器
 */
class HttpClientBuilder private() {

  /**
   * 整个连接池最大连接数, maxConnPerHost * 预计的路由数 >= maxTotalConn
   * 例: 服务预计会往10个常用的地址发送请求, maxConnPerHost=20, 则maxTotalConn应小于等于 10*20=200
   */
  private[this] var maxTotal: Int = DefaultMaxTotal
  /**
   * 每个路由基础的连接数, default=20 
   * [[HttpRoute]] 通常一个HttpRoute是一个连接地址, 这个参数可以限制往同一个地址发送请求的最大并发数
   */
  private[this] var maxPerRoute: Int = DefaultMaxPerRoute
  /**
   * 读取超时时间
   */
  private[this] var socketTimeout: Int = DefaultSocketTimeout
  /**
   * 连接超时时间
   */
  private[this] var connectionTimeout: Int = DefaultConnectionTimeout


  def setMaxTotal(maxTotal: Int): this.type = {
    this.maxTotal = maxTotal
    this
  }

  def setMaxPerRoute(maxPerRoute: Int): this.type = {
    this.maxPerRoute = maxPerRoute
    this
  }

  def setSocketTimeout(socketTimeout: Int): this.type = {
    this.socketTimeout = socketTimeout
    this
  }

  def setConnectionTimeout(connectionTimeout: Int): this.type = {
    this.connectionTimeout = connectionTimeout
    this
  }

  private[httpclient] def build(): Unit = {
    HttpClient.initialize(maxTotal, maxPerRoute, socketTimeout, connectionTimeout)
  }
}

object HttpClientBuilder {
  def apply(): HttpClientBuilder = new HttpClientBuilder()
}
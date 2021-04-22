package org.altynai.utils4s
package httpclient

import org.apache.http.HttpResponse
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.StringEntity

/**
 * 提供异步HTTP请求API
 */
final class AsyncApi private[httpclient](core: AsyncCore) {


  /**
   * GET 调用
   *
   * @param uri      请求地址
   * @param params   [可选] 参数列表
   * @param heads    [可选] 请求头
   * @param callback 回调函数
   */
  def doGet(uri: String,
            params: Map[String, String] = Map.empty,
            heads: Map[String, String] = Map.empty,
            callback: FutureCallback[HttpResponse]): Unit = {
    core.doAsyncGet(uri, params, heads, callback)
  }

  /**
   * POST 调用
   *
   * @param uri      请求地址
   * @param params   [可选] 参数列表
   * @param heads    [可选] 请求头
   * @param callback 回调函数
   */
  def doPost(uri: String,
             params: Map[String, String] = Map.empty,
             heads: Map[String, String] = Map.empty,
             callback: FutureCallback[HttpResponse]): Unit = {
    core.doAsyncPost(uri, HttpUtil.conversionParam(params), heads, callback)
  }

  /**
   * POST 调用, json格式
   *
   * @param uri      请求地址
   * @param json     content
   * @param heads    [可选] 请求头
   * @param callback 回调函数
   */
  def doPost$Json(uri: String, json: String,
                  heads: Map[String, String] = Map.empty,
                  callback: FutureCallback[HttpResponse]): Unit = {
    val entity = new StringEntity(json, Encoding)
    entity.setContentType("application/json") //设置为 json数据
    core.doAsyncPost(uri, entity, heads, callback)
  }

  /**
   * POST 调用, xml格式
   *
   * @param uri      请求地址
   * @param xml      content
   * @param heads    [可选] 请求头
   * @param callback 回调函数
   */
  def doPost$Xml(uri: String, xml: String,
                 heads: Map[String, String] = Map.empty,
                 callback: FutureCallback[HttpResponse]): Unit = {
    val entity = new StringEntity(xml, Encoding)
    entity.setContentType("application/xml") //设置为 xml数据
    core.doAsyncPost(uri, entity, heads, callback)
  }
}

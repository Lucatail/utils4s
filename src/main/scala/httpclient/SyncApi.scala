package org.altynai.utils4s
package httpclient

import httpclient.HttpUtil.conversionParam

import org.apache.http.entity.{ByteArrayEntity, StringEntity}
import org.apache.http.util.EntityUtils


/**
 * 提供同步HTTP请求API
 */
final class SyncApi private[httpclient](core: SyncCore) {


  /**
   * GET 调用
   *
   * @param uri    请求地址
   * @param params [可选] 参数列表
   * @param heads  [可选] heads
   * @return
   */
  def doGet(uri: String,
            params: Map[String, String] = Map.empty,
            heads: Map[String, String] = Map.empty): String = {
    core.doGet(uri, params, heads)
  }

  /**
   * POST 调用
   *
   * @param uri    请求地址
   * @param params [可选] 参数列表
   * @param heads  [可选] heads
   * @return
   */
  def doPost(uri: String,
             params: Map[String, String] = Map.empty,
             heads: Map[String, String] = Map.empty): String = {
    core.doPost(uri, conversionParam(params), heads)(EntityUtils.toString)
  }

  /**
   * POST 调用, json格式
   *
   * @param uri   请求地址
   * @param json  content
   * @param heads [可选] heads
   * @return
   */
  def doPost$Json(uri: String, json: String,
                  heads: Map[String, String] = Map.empty): String = {
    val entity: StringEntity = new StringEntity(json, Encoding)
    entity.setContentType("application/json") //设置为 json数据
    core.doPost(uri, entity, heads)(EntityUtils.toString)
  }

  /**
   * POST 调用, xml格式
   *
   * @param uri   请求地址
   * @param xml   content
   * @param heads [可选] heads
   * @return
   */
  def doPost$Xml(uri: String, xml: String,
                 heads: Map[String, String] = Map.empty): String = {
    val entity = new StringEntity(xml, Encoding)
    entity.setContentType("application/xml") //设置为 json数据
    core.doPost(uri, entity, heads)(EntityUtils.toString)
  }

  /**
   * POST 调用, byte数组格式
   *
   * @param uri   请求地址
   * @param bytes content
   * @param heads [可选] heads
   * @return
   */
  def doPost$Byte2String(uri: String, bytes: Array[Byte],
                         heads: Map[String, String] = Map.empty): String = {
    val entity = new ByteArrayEntity(bytes)
    core.doPost(uri, entity, heads)(EntityUtils.toString)
  }

  /**
   * POST 调用, byte数组格式
   *
   * @param uri   请求地址
   * @param bytes content
   * @param heads [可选] heads
   * @return
   */
  def doPost$Byte2Byte(uri: String, bytes: Array[Byte],
                       heads: Map[String, String] = Map.empty): Array[Byte] = {
    val byteArrayEntity = new ByteArrayEntity(bytes)
    core.doPost(uri, byteArrayEntity, heads)(EntityUtils.toByteArray)
  }
}

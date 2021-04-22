package org.altynai.utils4s
package httpclient

import org.apache.http.NameValuePair
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.entity.AbstractHttpEntity
import org.apache.http.message.BasicNameValuePair

import java.nio.charset.Charset
import java.util

/**
 * http工具
 */
object HttpUtil {

  /**
   * 将参数列表encode
   *
   * @param map 参数列表
   * @param c   编码格式
   * @return encode后的字符串
   */
  def toString(map: Map[String, String], c: Charset = Encoding): Option[String] = {
    if (null == map || map.isEmpty)
      None
    else {
      val original = map.foldLeft(new StringBuilder())((b, e) => {
        b.append("&").append(e._1).append("=").append(e._2)
      }).substring(1)
      Some(encodeString(original, c))
    }
  }

  /**
   * 编码转换
   *
   * @param s 源字符串
   * @param c 编码格式
   * @return encode后的字符串
   */
  def encodeString(s: String, c: Charset = Encoding): String = {
    val bytes = s.getBytes(c)
    val encoded = new Array[Byte](bytes.length * 3)
    var n: Int = 0
    var noEncode: Boolean = true

    def _add2encoded(byte: Byte): Unit = {
      encoded({
        n += 1
        n - 1
      }) = byte
    }

    def _nibble(nibble: Int): Unit = {
      nibble match {
        case b if b >= 10 => _add2encoded(('A' + b - 10).toByte)
        case _ => _add2encoded(('0' + nibble).toByte)
      }
    }

    bytes foreach {
      case ' ' =>
        noEncode = false
        _add2encoded('+')
      case b if b >= 'a' && b <= 'z' || b >= 'A' && b <= 'Z' || b >= '0' && b <= '9' =>
        _add2encoded(b)
      case b =>
        noEncode = false
        _add2encoded('%')
        _nibble((b & 0xf0) >> 4)
        _nibble(b & 0xf)
    }
    if (noEncode) s else new String(encoded, 0, n, c)
  }

  /**
   * 将参数map转换成 HttpEntity
   *
   * @param params 原始参数
   * @param c      编码格式
   * @return 转换后的HttpEntity
   */
  def conversionParam(params: Map[String, String], c: Charset = Encoding): UrlEncodedFormEntity = {
    val pairs = new util.ArrayList[NameValuePair]
    params foreach (e => pairs.add(new BasicNameValuePair(e._1, e._2)))
    new UrlEncodedFormEntity(pairs, c)
  }

  def createHttpGet(uri: String, params: Map[String, String], heads: Map[String, String], rc: RequestConfig): HttpGet = {
    val _uri = toString(params) match {
      case Some(value) => s"$uri?$value"
      case None => uri
    }
    val get = new HttpGet(_uri)
    get.setConfig(rc)
    heads.foreach(e => get.setHeader(e._1, e._2))
    get
  }

  def createHttpPost(uri: String, params: AbstractHttpEntity, heads: Map[String, String], rc: RequestConfig): HttpPost = {
    val post = new HttpPost(uri)
    post.setConfig(rc)
    post.addHeader("User-Agent", "Mozilla/4.0")
    post.setEntity(params)
    heads.foreach(e => post.setHeader(e._1, e._2))
    post
  }
}
package org.altynai.utils4s

import java.nio.charset.{Charset, StandardCharsets}
import scala.language.implicitConversions


package object httpclient {
  val Encoding: Charset = StandardCharsets.UTF_8
  val DefaultMaxTotal = 200
  val DefaultMaxPerRoute = 20
  val DefaultSocketTimeout = 5000
  val DefaultConnectionTimeout = 5000

}

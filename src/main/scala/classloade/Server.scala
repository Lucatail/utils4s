package org.altynai.utils4s
package classloade

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
 * 模拟服务端
 */
object Server extends App {

  ActorSystem("server", ConfigFactory.parseString(
    s"""
       |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname = "127.0.0.1"
       |akka.remote.netty.tcp.port = "7086"
       """.stripMargin))
    .actorOf(Props[ServerActor], "ref")

}

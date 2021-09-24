package org.altynai.utils4s
package classloade

import akka.actor.Actor


class ServerActor extends Actor {
  override def receive: Receive = {
    case redefine: Redefine =>
      sender ! doRedefine(redefine)
    case s: String =>
      sender ! printString(s)
  }

  def printString(s: String): String = {
    println(s"msg: $s")
    "success"
  }

  def doRedefine(redefine: Redefine): String = {
    Agent.redefine(redefine.clazz, redefine.body)
  }
}

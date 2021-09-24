package org.altynai.utils4s
package classloade

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}

import java.nio.file.{Files, Paths}
import java.util.Scanner
import javax.swing.filechooser.FileSystemView
import javax.swing.{JFileChooser, JFrame}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

/**
 * 模拟客户端
 */
object Client extends App {

  private val config: Config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname = "127.0.0.1"
       |akka.remote.netty.tcp.port = "7087"
       """.stripMargin)
  private val system = ActorSystem("client", config)

  val server = system.actorSelection("akka.tcp://server@127.0.0.1:7086/user/ref")
  implicit val timeout = Timeout(4.seconds)


  println("第一次测试:")
  server ? "test" map println

  private val scanner = new Scanner(System.in)

  println("请输入类路径:")
  val classname = scanner.next()
  println(classname)

  println("请选择class文件:")
  val fileChooser = new JFileChooser()
  val fsv = FileSystemView.getFileSystemView

  fileChooser.setCurrentDirectory(fsv.getHomeDirectory)
  fileChooser.setDialogTitle("请选择要上传的文件...")
  fileChooser.setApproveButtonText("确定")
  fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
  val result = fileChooser.showOpenDialog(new JFrame())
  if (JFileChooser.APPROVE_OPTION == result) {
    val path = fileChooser.getSelectedFile.getPath
    println(s"path: $path")

    val body = Files.readAllBytes(Paths.get(path))
    server ? Redefine(classname, body) map println
  }

  println("第二次测试:")
  server ? "test" map println
}

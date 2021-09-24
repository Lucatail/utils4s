package org.altynai.utils4s
package classloade

import com.sun.tools.attach.VirtualMachine
import org.apache.commons.io.{FileUtils, IOUtils}

import java.io.File
import java.lang.instrument.{ClassDefinition, Instrumentation}
import java.lang.management.ManagementFactory

/**
 * 热更新代理
 */
object Agent {
  val file = "agent-1.0.0.jar"
  var inst: Instrumentation = load()

  private[this] def load(): Instrumentation = {
    if (inst != null) {
      return inst
    }
    println("Agent load begin")

    val in = Agent.getClass.getClassLoader.getResourceAsStream(file)
    try {
      val agentJar = File.createTempFile("agent", ".jar")
      FileUtils.writeByteArrayToFile(agentJar, IOUtils.toByteArray(in))

      val pid = ManagementFactory.getRuntimeMXBean.getName.split("@")(0)
      val vm = VirtualMachine.attach(pid)
      vm.loadAgent(agentJar.getAbsolutePath)

      val clazz = Class.forName("org.altynai.JavaAgent")
      inst = clazz.getField("INST").get(null).asInstanceOf[Instrumentation]
      println("Agent load finish")
      inst
    } finally {
      if (null != in)
        in.close()
    }
  }

  def redefine(classname: String, body: Array[Byte]): String = {
    if (null == inst) {
      return "error!"
    }
    try {
      val clazz = Class.forName(classname)
      inst.redefineClasses(new ClassDefinition(clazz, body))
      val clazz2 = Class.forName(classname)
      s"redefineClasses Success,clazz:$clazz,newClazz:$clazz2"
    } catch {
      case e: Exception =>
        println(e)
        s"redefineClasses error: ${e.getMessage}"
    }
  }
}
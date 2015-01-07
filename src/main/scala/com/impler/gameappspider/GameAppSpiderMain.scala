package com.impler.gameappspider

import java.util.regex.{Pattern, Matcher}

import com.impler.gameappspider.invoke.Invoker
import com.impler.gameappspider.task.Tasks
import com.impler.gameappspider.utils.PropertyReader
import com.impler.gameappspider.xml.XmlConfiguration
import org.apache.logging.log4j.{LogManager, Logger}

/**
 * GameAppSpiderMain
 */
object GameAppSpiderMain {

  private final val logger: Logger= LogManager.getLogger(getClass)

  def main(args : Array[String]): Unit = {
    val siteXml = PropertyReader.getPropertyValue("config","site")
    logger.info("site = {}",siteXml)
    val appHome = System.getenv("APP_HOME")
    logger.info("APP_HOME = {}",appHome)
    val tasks: Tasks = XmlConfiguration.config(siteXml)
    val invoker: Invoker = new Invoker()
    invoker.invoke(tasks)
  }

}

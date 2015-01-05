package com.impler.gameappspider.utils

import org.apache.logging.log4j.{LogManager, Logger}

/**
 * AppStringUtil
 */
object AppStringUtil {

  private final val logger: Logger= LogManager.getLogger(getClass)

  def subString(org: String,subStart: String,subEnd: String): String = {
    var str = org
    if(subStart!=null&&subStart.length!=0){
      val idx = str.indexOf(subStart)
      if(idx > -1){
        str = str.substring(idx+subStart.length)
      }
    }
    if(subEnd!=null&&subEnd.length!=0){
      val idx = str.indexOf(subEnd)
      if(idx > -1){
        str = str.substring(0,idx)
      }
    }
    str
  }

  def getIntAtString(str: String): Int = {
    try{
      java.lang.Double.valueOf(str.replaceAll("[^0-9.]","")).intValue()
    }catch {
      case e: Throwable => logger.error("getIntAtString str="+str,e)
        -1
    }
  }

}

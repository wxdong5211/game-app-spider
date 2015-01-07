package com.impler.gameappspider.utils

import java.net.URLEncoder
import java.util.regex.{Matcher, Pattern}

import com.alibaba.fastjson.{JSONPath, JSONObject}
import org.apache.logging.log4j.{LogManager, Logger}

/**
 * ELUtil
 */
object ELUtil {

  private final val logger: Logger= LogManager.getLogger(getClass)

  val pattern: Pattern = Pattern.compile("\\$\\{([^\\}]+)\\}")

  def eval(org: String, context: Map[String,Any]): String = {
    if(org==null){
      return ""
    }
    if(!org.contains("$")){
      return org
    }
    try{
      var str: String = org
      var m: Matcher = pattern.matcher(str)
      var g: String = null
      var r: String = null
      while (m.find()) {
        g = m.group(1)
        if(g.contains(":")){
          val gs = g.split(":")
          if(gs.length>1){
            gs(0) match{
              case "url" =>
                r = contextString(gs(1),context)
                r = URLEncoder.encode(r,"UTF-8")
              case "sql" =>
                r = contextString(gs(1),context)
                r = r.replaceAll("'","''")
              case "time" => if(gs(1)=="ms") r = ""+System.currentTimeMillis()
            }
          }else{
            r = contextString(g,context)
          }
        }else{
          r = contextString(g,context)
        }

        if(r==null||r.length()==0){
          r = ""
        }
        r = adapterRegSpecChar(r)
        if(r.contains("\n")){
          r=r.replaceAll("\n", "")
        }
        str = m.replaceFirst(r)
        m = pattern.matcher(str)
      }
      str
    }catch {
      case any: Throwable =>
        logger.error("org = "+org+",context = "+context,any)
        ""
    }

  }

  def adapterRegSpecChar(org: String): String ={
    var dest = org
    // . \ + * ? [ ^ ] $ ( ) { } = ! < > | : -
    dest = adapterRegSpecChar(dest,"\\")
    dest = adapterRegSpecChar(dest,"$")
    dest
  }

  def adapterRegSpecChar(org: String, spec: String): String ={
    var dest = org
    if(dest.contains(spec)){
      dest=dest.replaceAll("\\"+spec, "\\\\\\"+spec)
    }
    dest
  }

  def contextString(el: String, context: Map[String,Any]): String = {
    var data = ""
    val els = el.split("\\.")
    if(els.length>1){
      context(els(0)) match {
        case json: JSONObject => data = JSONPath.eval(json,"$."+els(1)).toString
        case oo => data = oo.toString
      }
    }else{
      data = context(el).toString
    }
    data
  }

}

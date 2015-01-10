package com.impler.gameappspider.task

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import com.alibaba.fastjson.{JSONPath, JSON}
import com.impler.gameappspider.utils.AppStringUtil
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * Selector
 */
trait Selector {

  var select: String = null
  var vtype: String = null
  var attr: String = null
  var const: String = null
  var index: String = null
  var subStart: String = null
  var subEnd: String = null

  def getJsonValue(json: Any): String = {
    var str = getJsonStringValue(json)
    str = AppStringUtil.subString(str,subStart,subEnd)
    str
  }
  def getJsonStringValue(json: Any): String = {
    if(json==null)return ""
    vtype match {
      case "datetime" =>
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
      case "uuid" =>
        UUID.randomUUID().toString
      case "const" =>
        const
      case _ =>
        if(select==null||select.length==0)
          json.toString
        else {
          JSONPath.eval(json,select).toString
        }
    }
  }

  def getJsonElements(json: Any): Any = {
    if(select==null||select.length==0)
      json
    else JSONPath.eval(json,select)
  }

  def getValue(ele: Element): String = {
    var str = getStringValue(ele)
    str = AppStringUtil.subString(str,subStart,subEnd)
    str
  }
  def getStringValue(ele: Element): String = {
    if(ele==null)return ""
    vtype match {
      case "text" =>
        if(select==null||select.length==0)
          ele.text()
        else {
          getIndexElements(ele).text()
        }
      case "html" =>
        if(select==null||select.length==0)
          ele.html()
        else {
          getIndexElements(ele).html()
        }
      case "attr" =>
        if(attr==null||attr.length==0)
          ""
        else if(select==null||select.length==0)
          ele.attr(attr)
        else {
          getIndexElements(ele).attr(attr)
        }
      case "datetime" =>
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
      case "uuid" =>
        UUID.randomUUID().toString
      case "const" =>
        const
      case _ => ""
    }
  }

  def getElements(ele: Element): Elements = {
    if(select==null||select.length==0)
      new Elements()
    else getIndexElements(ele)
  }

  def getIndexElements(ele: Element): Elements = {
    val ret = new Elements()
    if(index==null||index.length==0) {
      return ele.select(select)
    }else if(index=="first"){
      ret.add(ele.select(select).first())
    }else if(index=="last"){
      ret.add(ele.select(select).last())
    }else{
      val eles = ele.select(select)
      val len = eles.size()
      if(index.contains(":")){
        val idxs = index.split(":")
        var start = 0
        var end = len-1
        if(idxs(0) != ""){
          start = (len+Integer.parseInt(idxs(0)))%len
        }
        if(idxs(1) != ""){
          end = (len+Integer.parseInt(idxs(1)))%len
        }
        var step = 1
        if(start>end)step = -1
        if(start==end)ret.add(eles.get(start))
        else{
          while(start<=end){
            ret.add(eles.get(start))
            start += step
          }
        }
      }else{
        ret.add(eles.get((len+Integer.parseInt(index))%len))
      }
    }
    ret
  }

}

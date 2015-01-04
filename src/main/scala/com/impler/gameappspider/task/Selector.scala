package com.impler.gameappspider.task

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import com.alibaba.fastjson.{JSONPath, JSON}
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
  def getJsonStringValue(json: Any): String = {
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
  def getStringValue(ele: Element): String = {
    vtype match {
      case "text" =>
        if(select==null||select.length==0)
          ele.text()
        else {
          if(index==null||index.length==0){
            ele.select(select).text()
          }else{
            if(index=="first"){
              ele.select(select).first().text()
            }else if(index=="last"){
              ele.select(select).last().text()
            }else{
              ele.select(select).get(Integer.parseInt(index)).text()
            }
          }
        }
      case "html" =>
        if(select==null||select.length==0)
          ele.html()
        else {
          if(index==null||index.length==0){
            ele.select(select).html()
          }else{
            if(index=="first"){
              ele.select(select).first().html()
            }else if(index=="last"){
              ele.select(select).last().html()
            }else{
              ele.select(select).get(Integer.parseInt(index)).html()
            }
          }
        }
      case "attr" =>
        if(attr==null||attr.length==0)
          ""
        else if(select==null||select.length==0)
          ele.attr(attr)
        else {
          if(index==null||index.length==0){
            ele.select(select).attr(attr)
          }else{
            if(index=="first"){
              ele.select(select).first().attr(attr)
            }else if(index=="last"){
              ele.select(select).last().attr(attr)
            }else{
              ele.select(select).get(Integer.parseInt(index)).attr(attr)
            }
          }
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
      null
    else ele.select(select)
  }

}

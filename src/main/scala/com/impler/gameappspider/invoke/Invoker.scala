package com.impler.gameappspider.invoke

import java.io.{File, FileWriter}
import java.util
import java.util.UUID

import com.alibaba.fastjson.{JSONPath, JSONArray, JSON, JSONObject}
import com.impler.gameappspider.task.{Blocks, PropOutput, Task, Tasks}
import com.impler.gameappspider.utils.ELUtil
import jdk.nashorn.internal.parser.JSONParser
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.apache.logging.log4j.{LogManager, Logger}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConversions._

/**
 * Invoker
 */
class Invoker {

  private final val logger: Logger= LogManager.getLogger(getClass)

  var host: String = null

  var tasks: Tasks = null

  var context: Map[String,Any] = Map()
  var prop: Map[String,Any] = Map()
  var taskStart: Int = 0

  val client: HttpClient = HttpClientBuilder.create().build()

  def invoke(tasks: Tasks): Unit = {
    host = tasks.host
    val boot: Task = tasks.tasks(tasks.boot)
    if(boot==null){
      return
    }
    this.tasks = tasks
    invoke(boot)
  }

  def invoke(task: Task): Unit = {
    if(task.untilEmpty!=null&&task.untilEmpty.length()!=0){
      context += task.stepVar -> task.step
      taskStart = task.start
      context += task.startVar -> taskStart
    }
    val uri = getUri(task)
    val log = "Task["+task.id+"];url="+uri
    logger.info(log)
    val html: String = get(uri)
    if("json".equals(task.dataType)){
      invoke(task,JSON.parseObject(getJsonSub(task,html)),uri,log)
    }else{
      invoke(task,Jsoup.parse(html),uri,log)
    }
  }

  def getUri(task: Task): String = {
    var uri = ELUtil.eval(task.uri,context)
    if(!uri.startsWith("http://")){
      uri = host + uri
    }
    uri
  }

  def getJsonSub(task: Task, html: String): String = {
    var str = html
    if(task.subStart!=null&&task.subStart.length!=0){
      val idx = str.indexOf(task.subStart)
      if(idx > -1){
        str = str.substring(idx+task.subStart.length)
      }
    }
    if(task.subEnd!=null&&task.subEnd.length!=0){
      val idx = str.indexOf(task.subEnd)
      if(idx > -1){
        str = str.substring(0,idx)
      }
    }
    if(task.wrap!=null&&task.wrap.length!=0){
      str = "{\""+task.wrap+"\":"+str+"}"
    }
    str
  }

  def invoke(task: Task, json: JSONObject,uri: String,log: String): Unit = {
    if(task.untilEmpty!=null&&task.untilEmpty.length()!=0){
      var size = JSONPath.size(json,task.untilEmpty)
      val max = task.max
      while(size>0&&(max <= 0 || taskStart <= max)){
        val uri = getUri(task)
        logger.info("Task["+task.id+"];url="+uri+",until="+task.start+"/"+task.step+"/"+taskStart)
        taskStart += task.step
        context += task.startVar -> taskStart
        val html: String = get(uri)
        val json: JSONObject = JSON.parseObject(getJsonSub(task,html))
        size = JSONPath.size(json,task.untilEmpty)
        task.blocks.foreach((block)=>{
          invokeJson(block,json)
        })
      }
    }else{
      task.blocks.foreach((block)=>{
        invokeJson(block,json)
      })
      if(task.pageParam!=null&&task.pageParam.length()!=0){
        val maxPageStr: String = task.maxPage.getJsonValue(json)
        if(maxPageStr!=null){
          var maxPage: Int = getIntAtString(task.maxPage.getJsonValue(json))
          val mod = task.maxPage.mod
          if(mod>0){
            if(maxPage%mod==0){
              maxPage = maxPage/mod
            }else{
              maxPage = maxPage/mod + 1
            }
          }
          var max = task.maxPage.max
          if(max<1||max>maxPage)max = maxPage
          for(i <- 2 to max){
            logger.info(log+",page="+i+"/"+max+"/"+maxPage)
            var connect: String = "&"
            if(uri.indexOf("?")<0){
              connect = "?"
            }
            val html: String = get(uri+connect+task.pageParam+"="+i)
            val json: JSONObject = JSON.parseObject(getJsonSub(task,html))
            task.blocks.foreach((block)=>{
              invokeJson(block,json)
            })
          }
        }
      }
    }
  }

  def invoke(task: Task, document: Document,uri: String,log: String): Unit = {
    task.blocks.foreach((block)=>{
      invoke(block,document)
    })
    if(task.pageParam!=null&&task.pageParam.length()!=0){
      val maxPageStr: String = task.maxPage.getValue(document)
      if(maxPageStr!=null){
        var maxPage: Int = getIntAtString(task.maxPage.getValue(document))
        val mod = task.maxPage.mod
        if(mod>0){
          if(maxPage%mod==0){
            maxPage = maxPage/mod
          }else{
            maxPage = maxPage/mod + 1
          }
        }
        var max = task.maxPage.max
        if(max<1||max>maxPage)max = maxPage
        for(i <- 2 to max){
          logger.info(log+",page="+i+"/"+max+"/"+maxPage)
          var connect: String = "&"
          if(uri.indexOf("?")<0){
            connect = "?"
          }
          val html: String = get(uri+connect+task.pageParam+"="+i)
          val document: Document = Jsoup.parse(html)
          task.blocks.foreach((block)=>{
            invoke(block,document)
          })
        }
      }
    }
  }

  def invokeJson(block: Blocks, json: Any): Unit = {
    block.getJsonElements(json) match {
      case list: JSONArray =>
        list.foreach((item)=>{
          invokeJsonSingle(block,item)
        })
      case item: Any => invokeJsonSingle(block,item)
    }
  }

  def invokeJsonSingle(block: Blocks, item: Any): Unit = {
    val appHome = System.getenv("APP_HOME")
    if(!checkJsonExclude(block,item)){
      block.prop.foreach{
        keyVal => prop += keyVal._1 -> keyVal._2.getJsonValue(item)
      }
      block.context.foreach{
        keyVal => context += keyVal._1 -> keyVal._2.getJsonValue(item)
      }
      block.propOutput.foreach((output)=>{
        propOutput(appHome,output)
      })
      block.blocks.foreach((block)=>{
        invokeJson(block,item)
      })
      if(block.taskRef!=null&&block.taskRef.length!=0){
        block.taskRef.split(",").foreach{
          (refStr)=>
            val ref: Task = this.tasks.tasks(refStr)
            if(ref!=null){
              invoke(ref)
            }
        }
      }
    }
  }

  def invoke(block: Blocks, ele: Element): Unit = {
    val appHome = System.getenv("APP_HOME")
    block.getElements(ele).foreach((item)=>{
      if(!checkExclude(block,item)){
        block.prop.foreach{
          keyVal => prop += keyVal._1 -> keyVal._2.getValue(item)
        }
        block.context.foreach{
          keyVal => context += keyVal._1 -> keyVal._2.getValue(item)
        }
        block.propOutput.foreach((output)=>{
          propOutput(appHome,output)
        })
        block.blocks.foreach((block)=>{
          invoke(block,item)
        })
        if(block.taskRef!=null&&block.taskRef.length!=0){
          block.taskRef.split(",").foreach{
            (refStr)=>
              val ref: Task = this.tasks.tasks(refStr)
              if(ref!=null){
                invoke(ref)
              }
          }
        }
      }
    })
  }

  def checkJsonExclude(block: Blocks, item: Any): Boolean = {
    var exclude: Boolean = false
    block.excludes.foreach{
      keyVal =>
        val vals = keyVal.getJsonValue(item)
        if(keyVal.value!=null){
          if("true".equals(keyVal.contains)){
            if(!vals.contains(keyVal.value)){
              exclude = true
            }
          }else if(keyVal.value.equals(vals)){
            exclude = true
          }
        }
    }
    exclude
  }

  def checkExclude(block: Blocks, item: Element): Boolean = {
    var exclude: Boolean = false
    block.excludes.foreach{
      keyVal =>
        val vals = keyVal.getValue(item)
        if(keyVal.value!=null){
          if("true".equals(keyVal.contains)){
            if(!vals.contains(keyVal.value)){
              exclude = true
            }
          }else if(keyVal.value.equals(vals)){
            exclude = true
          }
        }
    }
    exclude
  }

  def getIntAtString(str: String): Int = {
    try{
      java.lang.Double.valueOf(str.replaceAll("[^0-9.]","")).intValue()
    }catch {
      case e: Throwable => logger.error("getIntAtString str="+str,e)
        -1
    }
  }

  def propOutput(appHome: String, output: PropOutput): Unit = {
    val file: File = new File(appHome+ELUtil.eval(output.target,prop))
    if(!file.getParentFile.exists()){
      file.getParentFile.mkdirs()
    }
    val fw: FileWriter = new FileWriter(file,true)
    val split = output.split
    val splitBy = output.splitBy
    val json = output.json
    var data = ""
    if(split!=null&&split.length!=0&&splitBy!=null&&splitBy.length!=0){
      val vals = prop(split).toString.split(splitBy)
      vals.foreach{
        (v) =>
          prop += split -> v
          data += propOutputCommon(output)
      }
    }else if(json!=null&&json.length!=0){
      val jsons = json.split(":")
      jsons(0) match{
        case "object" =>
          prop += jsons(1) -> JSON.parseObject(prop(jsons(1)).toString)
          data = propOutputCommon(output)
        case "array" =>
          JSON.parseArray(prop(jsons(1)).toString).foreach{
            (v) => prop += jsons(1) -> v
                data += propOutputCommon(output)
          }
      }
    }else{
      data = propOutputCommon(output)
    }
    fw.write(data)
    fw.close()
  }

  def propOutputCommon(output: PropOutput): String = {
    if(output.uuid!=null&&output.uuid.length!=0){
      prop += output.uuid -> UUID.randomUUID().toString
    }
    var data = ELUtil.eval(output.value,prop)
    if(output.newline=="true"){
      data += "\n"
    }
    data
  }

  def get(uri: String): String = {
    try{
      val request: HttpGet = new HttpGet(uri)
      val response: HttpResponse = client.execute(request)
      val html: String = EntityUtils.toString(response.getEntity)
      EntityUtils.consume(response.getEntity)
      html
    }catch {
      case e: Throwable => logger.error("uri="+uri,e)
        ""
    }
  }

}

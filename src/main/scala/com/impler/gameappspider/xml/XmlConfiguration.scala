package com.impler.gameappspider.xml

import com.impler.gameappspider.task._
import org.dom4j.{Element, Document}
import org.dom4j.io.SAXReader

import scala.collection.JavaConversions._

/**
 * XmlConfiguration
 */
object XmlConfiguration {

  def config(siteXml: String): Tasks ={
    val saxReader: SAXReader = new SAXReader()
    val document: Document = saxReader.read(getClass.getResource(siteXml))
    val rootElement: Element = document.getRootElement
    val tasks: Tasks = new Tasks()
    tasks.boot = rootElement.attributeValue("boot")
    tasks.host = rootElement.attributeValue("host")
    var task: Task = null
    rootElement.elements("task").foreach((obj)=>{
      task = configTask(obj.asInstanceOf[Element])
      tasks.tasks += task.id -> task
    })
    tasks
  }

  def configTask(ele: Element): Task = {
    val task: Task = new Task()
    task.id = ele.attributeValue("id")
    task.uri = ele.attributeValue("uri")
    task.thread = ele.attributeValue("thread")
    task.newClient = ele.attributeValue("new-client")
    task.dataType = ele.attributeValue("data-type")
    task.wrap = ele.attributeValue("wrap")
    task.untilEmpty = ele.attributeValue("until-empty")
    if(task.untilEmpty!=null&&task.untilEmpty.length()!=0){
      task.step = Integer.parseInt(ele.attributeValue("step"))
      task.start = Integer.parseInt(ele.attributeValue("start"))
      task.max = Integer.parseInt(ele.attributeValue("max"))
      task.stepVar = ele.attributeValue("step-var")
      task.startVar = ele.attributeValue("start-var")
    }
    task.subStart = ele.attributeValue("subStart")
    task.subEnd = ele.attributeValue("subEnd")
    val pageParam: String = ele.attributeValue("page-param")
    if(pageParam!=null&&pageParam.length() !=0){
      task.pageParam = pageParam
      task.maxPage = configMaxPage(ele.element("max-page"))
    }
    ele.elements("blocks").foreach((obj)=>{
      task.blocks :+= configBlocks(obj.asInstanceOf[Element])
    })
    task
  }

  def configBlocks(ele: Element): Blocks = {
    val blocks: Blocks = new Blocks()
    configSelector(ele,blocks)
    blocks.taskRef = ele.attributeValue("task-ref")
    ele.elements("exclude").foreach((obj)=>{
      blocks.excludes :+= configExclude(obj.asInstanceOf[Element])
    })
    var prop: Prop = null
    ele.elements("prop").foreach((obj)=>{
      prop = configProp(obj.asInstanceOf[Element])
      blocks.prop += prop.name -> prop
    })
    var context: Context = null
    ele.elements("context").foreach((obj)=>{
      context = configContext(obj.asInstanceOf[Element])
      blocks.context += context.name -> context
    })
    ele.elements("prop-output").foreach((obj)=>{
      blocks.propOutput :+= configPropOutput(obj.asInstanceOf[Element])
    })
    ele.elements("blocks").foreach((obj)=>{
      blocks.blocks :+= configBlocks(obj.asInstanceOf[Element])
    })
    blocks
  }

  def configExclude(ele: Element): Exclude = {
    val exclude: Exclude = new Exclude()
    configSelector(ele,exclude)
    exclude.contains = ele.attributeValue("contains")
    exclude.value = ele.getText
    exclude
  }

  def configProp(ele: Element): Prop = {
    val prop: Prop = new Prop()
    configSelector(ele,prop)
    prop.name = ele.attributeValue("name")
    prop
  }

  def configContext(ele: Element): Context = {
    val context: Context = new Context()
    configSelector(ele,context)
    context.name = ele.attributeValue("name")
    context
  }

  def configMaxPage(ele: Element): MaxPage = {
    val context: MaxPage = new MaxPage()
    configSelector(ele,context)
    val maxStr: String =  ele.attributeValue("max")
    if(maxStr!=null&&maxStr.length()!=0){
      context.max = Integer.parseInt(maxStr)
    }
    val modStr: String =  ele.attributeValue("mod")
    if(modStr!=null&&modStr.length()!=0){
      context.mod = Integer.parseInt(modStr)
    }
    context
  }

  def configPropOutput(ele: Element): PropOutput = {
    val propOutput: PropOutput = new PropOutput()
    propOutput.target = ele.attributeValue("target")
    propOutput.newline = ele.attributeValue("newline")
    propOutput.split = ele.attributeValue("split")
    propOutput.splitBy = ele.attributeValue("splitBy")
    propOutput.uuid = ele.attributeValue("uuid")
    propOutput.json = ele.attributeValue("json")
    propOutput.value = ele.getTextTrim
    propOutput
  }

  def configSelector(ele: Element, selector: Selector): Unit = {
    selector.select = ele.attributeValue("select")
    selector.vtype = ele.attributeValue("vtype")
    selector.attr = ele.attributeValue("attr")
    selector.const = ele.attributeValue("const")
    selector.index = ele.attributeValue("index")
    selector.subStart = ele.attributeValue("subStart")
    selector.subEnd = ele.attributeValue("subEnd")
  }

}

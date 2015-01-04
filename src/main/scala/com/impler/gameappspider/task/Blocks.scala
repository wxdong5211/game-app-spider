package com.impler.gameappspider.task

/**
 * Blocks
 */
class Blocks extends Selector {

  var excludes: Array[Exclude] = Array()
  var blocks: Array[Blocks] = Array()
  var prop: Map[String,Prop] = Map()
  var context: Map[String,Context] = Map()
  var taskRef: String = null
  var propOutput: Array[PropOutput] = Array()

}

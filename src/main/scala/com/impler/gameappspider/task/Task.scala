package com.impler.gameappspider.task

/**
 * Task
 */
class Task {

  var id: String = null
  var uri: String = null
  var pageParam: String = null
  var pageVar: String = null
  var thread: String = null
  var newClient: String = null
  var dataType: String = null
  var wrap: String = null
  var untilEmpty: String = null
  var step: Int = 0
  var start: Int = 0
  var max: Int = 0
  var stepVar: String = null
  var startVar: String = null
  var subStart: String = null
  var subEnd: String = null
  var blocks: Array[Blocks] = Array()
  var maxPage: MaxPage = null
}

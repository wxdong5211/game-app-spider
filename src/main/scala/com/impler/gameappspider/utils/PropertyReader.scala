package com.impler.gameappspider.utils

import java.text.MessageFormat
import java.util.ResourceBundle


/**
 * PropertyReader
 */
object PropertyReader {

  def getPropertyValue(baseName: String, key: String, values: String *): String = {
    if (baseName==null|| baseName.length==0 || key==null || key.length==0)
      return "";
    val bundle: ResourceBundle  = ResourceBundle.getBundle(baseName)
    if(bundle.containsKey(key)){
      if(values!= null && values.length>0)
        MessageFormat.format(bundle.getString(key), values)
      else bundle.getString(key)
    }else{
      ""
    }
  }

}

package com.osinka.camel
package scala

import org.scalatest.Suite
import org.scalatest.BeforeAndAfterAll

trait CamelSpec extends BeforeAndAfterAll { self: Suite =>
  import org.apache.camel.CamelContext
  import org.apache.camel.impl.{DefaultCamelContext, DefaultExchange}
  import org.apache.camel.util.ServiceHelper
  
  protected var context: CamelContext = _

  override def beforeAll {
    super.beforeAll

    context = new DefaultCamelContext
    ServiceHelper.startService(context)
  }

  override def afterAll {
    ServiceHelper.stopService(context)
    super.afterAll
  }

  protected def createExchange = new DefaultExchange(context)
}

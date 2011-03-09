package com.osinka.camel
package scala

import org.scalatest.Suite
import org.scalatest.BeforeAndAfterAll

trait CamelSpec extends BeforeAndAfterAll { self: Suite =>
  import org.apache.camel.CamelContext
  
  protected var context: CamelContext = _

  override def beforeAll {
    super.beforeAll

    context = new org.apache.camel.impl.DefaultCamelContext
    context.start
  }

  override def afterAll {
    context.stop
    super.afterAll
  }
}

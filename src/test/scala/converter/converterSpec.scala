package com.osinka.camel.scala
package converter

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import reflect.Manifest

@RunWith(classOf[JUnitRunner])
class ConverterSpec extends Spec with CamelSpec with MustMatchers {
  describe("types converter") {
    it("must convert symbol to string") {
      to[String]('Test) must equal(Some("Test"))
    }
    it("must convert string to symbol") {
      to[Symbol]("Test") must equal(Some('Test))
    }
  }

  private def to[T](x: AnyRef)(implicit m: Manifest[T]): Option[T] = Option( context.getTypeConverter.mandatoryConvertTo(m.erasure, x).asInstanceOf[T] )
}

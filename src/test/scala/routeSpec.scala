package com.osinka.camel.scala

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import org.apache.camel.{Exchange,Processor,Predicate}
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.scala.dsl.{ScalaProcessor, ScalaPredicate}

@RunWith(classOf[JUnitRunner])
class RouteSpec extends Spec with CamelSpec with MustMatchers {
  val routeHelper = new RouteBuilder with RouteBuilderHelper {
    override def configure() {
    }
  }

  import routeHelper._

  describe("Processor/DSL") {
    it("should process in") {
      val p: Processor = routeHelper.in[Int] {1+}
      p.getClass must equal(classOf[routeHelper.FnProcessor])

      val e = processExchange(p) { _.in = 1 }
      e.in[Int] must equal(2)
    }
    it("should process in -> in") {
      val p: Processor = routeHelper.in[Int] {1+} .toIn
      p.getClass must equal(classOf[ScalaProcessor])

      val e = processExchange(p) { _.in = 1 }
      e.in[Int] must equal(2)
    }
    it("should process in -> out") {
      val p: Processor = routeHelper.in[Int] {1+} .toOut
      p.getClass must equal(classOf[ScalaProcessor])

      val e = processExchange(p) { _.in = 1 }
      e.out must equal(2)
    }
    it("should process out ->") {
      val p: Processor = routeHelper.out[Int] {1+}
      p.getClass must equal(classOf[routeHelper.FnProcessor])

      val e = processExchange(p) { _.out = 1 }
      e.in must equal(2)
    }
    it("should process out -> in") {
      val p: Processor = routeHelper.out[Int] {1+} .toIn
      p.getClass must equal(classOf[ScalaProcessor])

      val e = processExchange(p) { _.out = 1 }
      e.in must equal(2)
    }
    it("should process out -> out") {
      val p: Processor = routeHelper.out[Int] {1+} .toOut
      p.getClass must equal(classOf[ScalaProcessor])

      val e = processExchange(p) { _.out = 1 }
      e.out must equal(2)
    }
  }
  describe("Predicate/DSL") {
    it("should filter in") {
      val f: Predicate = routeHelper.in[Int] {1==}
      f.getClass must equal(classOf[ScalaPredicate])

      filterExchange(f) { _.in = 1 } must equal(true)
    }
  }

  def processExchange(p: Processor)(pre: Exchange => Unit) = {
    val e = createExchange
    pre(e)
    p.process(e)
    e
  }

  def filterExchange(f: Predicate)(pre: Exchange => Unit) = {
    val e = createExchange
    pre(e)
    f.matches(e)
  }
}

/**
 * Copyright (C) 2011 Alexander Azarov <azarov@osinka.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
      val p: Processor = routeHelper.in(classOf[Int]) {1+}
      p.getClass must equal(classOf[BodyExtractor[Int]#FnProcessor])

      val e = processExchange(p) { _.in = 1 }
      e.in[Int] must equal(2)
    }
    it("should process in -> in") {
      val p: Processor = routeHelper.in(classOf[Int]) {1+} .toIn
      p.getClass must equal(classOf[ScalaProcessor])

      val e = processExchange(p) { _.in = 1 }
      e.in[Int] must equal(2)
    }
    it("should process in -> out") {
      val p: Processor = routeHelper.in(classOf[Int]) {1+} .toOut
      p.getClass must equal(classOf[ScalaProcessor])

      val e = processExchange(p) { _.in = 1 }
      e.out must equal(2)
    }
    it("should process out ->") {
      val p: Processor = routeHelper.out(classOf[Int]) {1+}
      p.getClass must equal(classOf[BodyExtractor[Int]#FnProcessor])

      val e = processExchange(p) { _.out = 1 }
      e.in must equal(2)
    }
    it("should process out -> in") {
      val p: Processor = routeHelper.out(classOf[Int]) {1+} .toIn
      p.getClass must equal(classOf[ScalaProcessor])

      val e = processExchange(p) { _.out = 1 }
      e.in must equal(2)
    }
    it("should process out -> out") {
      val p: Processor = routeHelper.out(classOf[Int]) {1+} .toOut
      p.getClass must equal(classOf[ScalaProcessor])

      val e = processExchange(p) { _.out = 1 }
      e.out must equal(2)
    }
  }
  describe("Predicate/DSL") {
    it("should filter in") {
      val f: Predicate = routeHelper.in(classOf[Int]) {1==}
      f.getClass must equal(classOf[ScalaPredicate])

      filterExchange(f) { _.in = 1 } must equal(true)
    }
  }
  describe("PartialFunction/DSL") {
    sealed trait AlgoType
    case object LeafOne extends AlgoType
    case object LeafTwo extends AlgoType

    it("should leave message body if it's not in function domain") {
      val p: Processor = routeHelper.in(classOf[AlgoType]) collect {
        case LeafOne => LeafTwo
      }
      p.getClass must equal(classOf[BodyExtractor[AlgoType]#PfProcessor])

      val e = processExchange(p) { _.in = LeafTwo }
      e.in[AlgoType] must equal(LeafTwo)
    }
    it("should process body if it's in function domain") {
      val p: Processor = routeHelper.in(classOf[AlgoType]) collect {
        case LeafOne => LeafTwo
      }
      p.getClass must equal(classOf[BodyExtractor[AlgoType]#PfProcessor])

      val e = processExchange(p) { _.in = LeafOne }
      e.in[AlgoType] must equal(LeafTwo)
    }
    it("should filter") {
      val p: Predicate = routeHelper.in(classOf[AlgoType]) collect {
        case LeafOne => true
      }
      p.getClass must equal(classOf[ScalaPredicate])

      filterExchange(p) { _.in = LeafOne } must equal(true)
      filterExchange(p) { _.in = LeafTwo } must equal(false)
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

package com.osinka.camel.scala

import reflect.Manifest
import org.apache.camel.{Exchange, Message, Processor, CamelExecutionException}
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.scala.{Preamble, RichExchange, RichMessage}
import org.apache.camel.scala.dsl.{ScalaProcessor, ScalaPredicate}

/**
 * Mix-in into RouteBuilder
 */
trait RouteBuilderHelper extends Preamble { self: RouteBuilder =>
  /**
   * Preamble in Camel 2.6.0 already lifts:
   * - Int (to Period / Frequency);
   * - Exchange to RichExchange
   */

  implicit def enrichMessage(msg: Message) = new RichMessage(msg)

  implicit def enrichUnitF(f: Exchange => Unit) = new ScalaProcessor(f)
  implicit def enrichFn(f: Exchange => Any) = new ScalaPredicate(f)

  /**
   * process { in[String] { _+"11" } toIn }
   * process { in[Int] { 11+ } toOut }
   */
  def in[T](f: (T) => Any)(implicit m: Manifest[T]) =
    new ExchangeProcessorWrapper(exchange => f(exchange getIn m.erasure.asInstanceOf[Class[T]]))

  /**
   * process { out { (s: String) => s+"11" } toIn }
   * process { out[Int] { _+11 } toOut }
   */
  def out[T](f: (T) => Any)(implicit m: Manifest[T]) =
    new ExchangeProcessorWrapper(exchange => f(exchange getOut m.erasure.asInstanceOf[Class[T]]))

  /**
   * filter { in[Int] { _ % 2 == 0 } }
   * filter { out { (s: String) => s.startsWith("aa") } }
   */
  implicit def wrapperFilter(w: ExchangeProcessorWrapper): ScalaPredicate =
    (exchange: Exchange) => w.f(exchange)

  class ExchangeProcessorWrapper(val f: (Exchange) => Any) extends Processor {
    def toOut: ScalaProcessor =
      (exchange: Exchange) => {
        exchange.getOut setBody f(exchange)
      }

    def toIn: ScalaProcessor =
      (exchange: Exchange) => {
        exchange.getIn setBody f(exchange)
      }

    override def process(exchange: Exchange) {
      throw new CamelExecutionException("When using 'in'/'out' Scala DSLs, you should specify 'toIn'/'toOut', as there is no default behavior", exchange)
    }
  }
}

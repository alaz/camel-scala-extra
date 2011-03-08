package com.osinka.camel.scala

import org.apache.camel.{Exchange, Message}
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
}

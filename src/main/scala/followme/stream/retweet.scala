package followme.stream

import akka.actor.Actor
import akka.pattern.CircuitBreaker
import com.typesafe.scalalogging.LazyLogging
import followme.stream.TwitterBuilder.twitter
import twitter4j.Status

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

case class Retweet(status: Status)

class SerialRetweeter extends Actor with LazyLogging {

  override def receive: Receive = {
    case Retweet(status) => breaker.withSyncCircuitBreaker {
      twitter.retweetStatus(status.getId)
    }
  }

  private val breaker = {
    new CircuitBreaker(
      context.system.scheduler,
      maxFailures = 2,
      callTimeout = 10 seconds,
      resetTimeout = 50 minutes
    ).onOpen(notifyMeOnOpen())
  }

  private def notifyMeOnOpen() = logger.warn("Threshold reach detected")
}

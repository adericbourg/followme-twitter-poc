package followme.stream

import akka.actor.{Actor, Props}
import akka.pattern.CircuitBreaker
import com.typesafe.scalalogging.LazyLogging
import followme.stream.TwitterBuilder.twitter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

case object Leon {}

class FolloweeCleaner extends Actor with LazyLogging {
  override def receive: Receive = {
    case Leon =>
      val unfollow = context.actorOf(Props[Unfollower])
      breaker.withSyncCircuitBreaker {
        val followers = twitter.getFollowersIDs(-1).getIDs
        val followees = twitter.getFriendsIDs(-1).getIDs

        val maxFolloweeSize = (.9 * followers.length).toInt
        val unfollowSize = Math.max(0, followees.length - maxFolloweeSize)

        followees.take(unfollowSize).foreach(userId => unfollow ! UnfollowRequest(userId))
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

case class UnfollowRequest(userId: Long)

class Unfollower extends Actor with LazyLogging {
  override def receive: Receive = {
    case UnfollowRequest(userId) => breaker.withSyncCircuitBreaker {
      twitter.destroyFriendship(userId)
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

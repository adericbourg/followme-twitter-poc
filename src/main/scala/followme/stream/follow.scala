package followme.stream

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import followme.stream.TwitterBuilder.twitter
import twitter4j.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

case class Follow(user: User)


class SerialFollower extends Actor with LazyLogging {

  private[SerialFollower] case class Unfollow(user: User)

  override def receive: Receive = {
    case Follow(user) =>
      twitter.createFriendship(user.getId)
      context.system.scheduler.scheduleOnce(6 hours) {
        self ! Unfollow(user)
      }
    case Unfollow(user) =>
      twitter.destroyFriendship(user.getId)
  }
}

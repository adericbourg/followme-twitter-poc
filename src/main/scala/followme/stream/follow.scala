package followme.stream

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import followme.stream.TwitterBuilder.twitter
import twitter4j.User

case class Follow(user: User)

class SerialFollower extends Actor with LazyLogging {
  override def receive: Receive = {
    case Follow(user) => twitter.createFriendship(user.getId)
  }
}

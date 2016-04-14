package followme.stream

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import followme.stream.TwitterBuilder.twitter
import twitter4j.{Query, QueryResult}

import scala.collection.JavaConverters._
import scala.language.postfixOps

case class Search(query: String)

class TweetSearcher extends Actor with LazyLogging {

  override def receive: Receive = {
    case Search(queryTerm) =>
      val rt = context.actorOf(Props[SerialRetweeter])
      val follow = context.actorOf(Props[SerialFollower])

      val query = new Query(queryTerm)
      val result: QueryResult = twitter.search(query)
      val tweets = result.getTweets.asScala

      tweets.foreach { tweet =>
        rt ! Retweet(tweet)
        follow ! Follow(tweet.getUser)
      }
  }
}

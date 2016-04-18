package followme.stream

import akka.actor.{Actor, Props}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import followme.stream.TwitterBuilder.twitter
import twitter4j.{Query, QueryResult}

import scala.collection.JavaConverters._
import scala.language.postfixOps

case class Search(query: String)

class TweetSearcher extends Actor with LazyLogging {

  override def receive: Receive = {

    case Search(queryTerm) =>
      val conf = ConfigFactory.load()

      val query = new Query(queryTerm)
      val result: QueryResult = twitter.search(query)
      val tweets = result.getTweets.asScala

      if (conf.getBoolean("app.retweet.enable")) {
        val rt = context.actorOf(Props[SerialRetweeter])
        logger.info(s"Retweeting ${tweets.length} tweets")
        tweets.foreach { tweet => rt ! Retweet(tweet) }
      }
      if (conf.getBoolean("app.follow.enable")) {
        val follow = context.actorOf(Props[SerialFollower])
        logger.info(s"Following ${tweets.length} new users")
        tweets.foreach { tweet => follow ! Follow(tweet.getUser) }
      }
  }
}

package followme.stream

import java.util

import com.typesafe.config.ConfigFactory
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

import scala.collection.JavaConversions._

object TwitterReader extends App {

  val conf = ConfigFactory.load()

  val cb = new ConfigurationBuilder()
  cb.setDebugEnabled(true)
    .setOAuthConsumerKey(conf.getString("twitter.auth.consumer.key"))
    .setOAuthConsumerSecret(conf.getString("twitter.auth.consumer.secret"))
    .setOAuthAccessToken(conf.getString("twitter.auth.access.token"))
    .setOAuthAccessTokenSecret(conf.getString("twitter.auth.access.token_secret"))

  val tf = new TwitterFactory(cb.build())
  val twitter: Twitter = tf.getInstance()

  val query = new Query("lang:en digital")
  val result: QueryResult = twitter.search(query)
  val tweets: util.List[Status] = result.getTweets

  tweets.foreach(status => twitter.retweetStatus(status.getId))
}

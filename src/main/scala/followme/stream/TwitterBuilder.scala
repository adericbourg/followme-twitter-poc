package followme.stream

import com.typesafe.config.ConfigFactory
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory}

object TwitterBuilder {

  lazy val twitter: Twitter = {
    val conf = ConfigFactory.load()

    val cb = new ConfigurationBuilder()
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(conf.getString("twitter.auth.consumer.key"))
      .setOAuthConsumerSecret(conf.getString("twitter.auth.consumer.secret"))
      .setOAuthAccessToken(conf.getString("twitter.auth.access.token"))
      .setOAuthAccessTokenSecret(conf.getString("twitter.auth.access.token_secret"))

    val tf = new TwitterFactory(cb.build())
    tf.getInstance()
  }
}

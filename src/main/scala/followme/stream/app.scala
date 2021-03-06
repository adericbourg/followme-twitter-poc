package followme.stream

import java.util.concurrent.{Executors, TimeUnit}

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging

object FollowMe extends App {
  akka.Main.main(Array(classOf[Sauron].getName))
}

// One actor to rule them all.
class Sauron extends Actor with LazyLogging {

  override def preStart(): Unit = {
    val searcher = context.actorOf(Props[TweetSearcher], "searcher")
    Scheduling.every(searcher ! Search("lang:en digital"), 30 * 60 * 1000) // 30 minutes

    val unfollower = context.actorOf(Props[FolloweeCleaner], "leon")
    Scheduling.every(unfollower ! Leon, 12 * 60 * 60 * 1000) // 12 hours
  }

  def receive = {
    case message => logger.info(s"Got message $message")
  }
}

object Scheduling {

  private val scheduler = Executors.newSingleThreadScheduledExecutor()

  def schedule(f: => Unit, time: Long) {
    scheduler.schedule(new Runnable {
      def run() = f
    }, time, TimeUnit.MILLISECONDS)
  }

  def every(f: => Unit, period: Long) {
    scheduler.scheduleAtFixedRate(new Runnable {
      def run() = f
    }, 0, period, TimeUnit.MILLISECONDS)
  }
}
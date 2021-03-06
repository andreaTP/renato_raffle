package eu.unicredit

import scala.scalajs.js
import scala.util.Try
import js.Dynamic.{global => g, literal}

import akka.actor._

object TwitterMsgs {
  case class Track(topic: String)
}

class TwitterActor() extends Actor {
  import TwitterMsgs._

  val twitterModule = g.require("node-tweet-stream")

  val twitter = js.Dynamic.newInstance(twitterModule)(RaffleTwitterActor.credentials)

  twitter.on("tweet", (tweet: js.Dynamic) => {
    Try {
      //val json = js.JSON.stringify(tweet)
      context.parent ! tweet
    }
  })

  def receive = {
    case track: Track =>
      twitter.track(track.topic)
  }

  override def postStop() = {
    twitter.untrackAll()
    twitter.abort()
  }
}

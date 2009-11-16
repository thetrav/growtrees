package immutable


import actors.Actor

/**
 * Created by IntelliJ IDEA.
 * User: travis
 * Date: 11/11/2009
 * Time: 1:05:38 PM
 * To change this template use File | Settings | File Templates.
 */

object TestActors {
  def main(args: Array[String]) {
      SillyActor.start()
      CounterActor.start()

  }
}

object SillyActor extends Actor {
    def act() {
      for ( i<- 1 to 5) {
        
        println("YO STUFF!" + i)
        CounterActor ! "YO"
        Thread.sleep(100)
      }
    }
}

object CounterActor extends Actor {
  def act() {
       while(true) {
         receive {
            case "YO" => println("got yo in CounterActor");
         }
         println("waiting again")
       }
  }
}
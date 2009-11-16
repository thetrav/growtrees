/*
 * Main.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package immutable


import java.awt.Graphics2D
import scala.actors._
import scala.actors.Actor._
import swing.Swing

object ImmutableGrow extends scala.swing.SimpleGUIApplication {
  def top = new scala.swing.MainFrame {
        title = "immutable draw trees"  
        val startButton = new scala.swing.Button {
            text = "Start"
        }
        val drawPanel = new scala.swing.Panel {
            override def paint(g: java.awt.Graphics) = g match {
                case g: Graphics2D =>
                    super.paint(g)
                    Model.paint(g)
            }
            reactions += {
                case scala.swing.event.MouseClicked(c, p, x, y, b) =>
                println("click" + p + " " + x + " " + y)
            }
        }

        contents = new scala.swing.BoxPanel(scala.swing.Orientation.Vertical) {
            contents += drawPanel
            contents += new scala.swing.BoxPanel(scala.swing.Orientation.Horizontal) {
                contents += startButton
            }
            border = Swing.EmptyBorder(10, 10, 10, 10)
        }

        val modelTickActor = new Actor {
            def act() {
                loop {
                    react {
                        case "Tick" =>
                            Model.trees = Model.tick(Model.trees)
                            drawPanel.repaint()
                    }
                }
            }
        }

        val tickActor = new Actor {
            def act() {
                loop {
                    Thread.sleep(50)
                    modelTickActor ! "Tick"
                }
            }
        }
        modelTickActor.start()

        listenTo(startButton)
        reactions += {
            case scala.swing.event.ButtonClicked(b) => {
                if (b == startButton) {
                    tickActor.start()
                    deafTo(startButton)
                    startButton.visible = false
                }
            }
        }

        size = (300, 300)
  }
}

object Model {
    val maxTrees = 10
    var trees = new Tree(50, 50, 5) :: Nil

    def tick(oldTrees:List[Tree]):List[Tree] = {
        var newTrees = List[Tree]()
        for (oldTree <- oldTrees) {
            val newTree = oldTree.tick()
            newTrees = newTree :: newTrees
            if(newTree.age > 20 && Math.random > 0.98) {
                newTrees = addTree(newTree.x, newTree.y, newTrees, oldTrees)
            }
        }
        return newTrees
    }

    def paint(g:java.awt.Graphics2D) {
        for (tree <- trees) {
            tree.paint(g)
        }
    }

    def distance(x1:Int ,y1:Int,x2:Int,y2:Int) =
        (x2 -x1)*(x2 -x1) + (y2-y1)*(y2-y1)

//    def killTree(x:Int, y:Int, oldTrees:List[Tree]) : List[Tree] = {
//        var stillAlive = List[Tree]()
//        for (tree <- oldTrees){
//            if(distance(tree.x, x, tree.y, y) > tree.diameter)
//                stillAlive = tree :: stillAlive
//        }
//        return stillAlive
//    }

    def addTree(xOrigin:Int, yOrigin:Int, newTrees:List[Tree], oldTrees:List[Tree]) :List[Tree] = {
        println("oldtrees.length"+oldTrees.length + " max:"+maxTrees)
        if(oldTrees.length < maxTrees)
        {
            val xSpread = 100*scala.Math.random-50
            val ySpread = 100*scala.Math.random-50
            val x = xOrigin + xSpread.toInt
            val y = yOrigin + ySpread.toInt
            if(   x < 0 || x > 300
               || y < 0 || y > 300) {
                return newTrees
            }
            for(tree <- newTrees) {
               if( distance(tree.x, tree.y, x, y) < (tree.diameter * tree.diameter)) {
                return trees
               }
            }
            for(tree <- oldTrees) {
               if( distance(tree.x, tree.y, x, y) < (tree.diameter * tree.diameter)) {
                return trees
               }
            }
            return new Tree(x, y, 1) :: newTrees
        }
        return trees
    }
}



class Tree(xPos:Int, yPos:Int, a:Int) {
    val age = a
    val x = xPos
    val y = yPos
    val diameter = size()

    def size() = if (age <= 20) age * 2 else 40 + scala.Math.log(age-20).toInt
    def tick() :Tree = {
        return new Tree(x, y, age+1)
    }

    def paint(g:java.awt.Graphics2D) {
        val radius = diameter/2
        g.setColor(new java.awt.Color(0, 180, 0))
        g.fillOval(x-radius, y-radius, diameter, diameter)
        g.setColor(new java.awt.Color(0, 100, 0))
        g.setStroke(new java.awt.BasicStroke(3))
        g.drawOval(x-radius, y-radius, diameter, diameter)
    }
}
package net.canarymod.plugin.lang.scala

import net.canarymod.hook.player.{ChatHook, ConnectionHook}

/**
 * Scala example Plugin
 *
 * @author Pwootage
 */
class ScalaPluginExample extends ScalaPlugin {

  override def enable(): Boolean = {
    hook { h: ConnectionHook =>
      h.getPlayer.message("Welcome from scala!")
    }

    hook { h: ChatHook =>
      h.getPlayer.message("You said: " + h.getMessage)
    }

    command(
      aliases = Seq("scalaTest"),
      permissions = Seq(),
      description = "A scala test command",
      toolTip = "scalaTest",
      min = 0
    ) { (r, args) =>
      r.message("It worked! Your args: " + args)
    }

    true
  }


  override def disable(): Unit = {
    println("Scala is dying D:")
  }
}
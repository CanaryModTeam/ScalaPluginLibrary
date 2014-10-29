package net.canarymod.plugin.lang.scala

import java.util

import net.canarymod.chat.MessageReceiver
import net.canarymod.commandsys.{CanaryCommand, DynamicCommandAnnotation, TabCompleteDispatch}
import net.canarymod.hook.{Dispatcher, Hook}
import net.canarymod.plugin.{Plugin, PluginListener, Priority}
import net.canarymod.{Canary, Translator}

/**
 * Main class for scala-based plugins.
 * <br/><br/>
 * A standard Scala-based plugin will use the hook and command methods to listen for various things:
 *
 * @author Pwootage
 *
 * @see ScalaPluginExample
 */
abstract class ScalaPlugin extends Plugin {
  private lazy val hookListener = Canary.hooks()
  private lazy val commandManager = Canary.commands()

  lazy val priority = Priority.NORMAL

  /**
   * Register a hook with Canary
   * @param h Hook method
   * @tparam T Type of hook
   */
  def hook[T <: Hook : Manifest](h: T => Unit): Unit = {
    hookListener.registerHook(
      new PluginListener {},
      this,
      manifest[T].runtimeClass,
      new Dispatcher {
        override def execute(listener: PluginListener, hook: Hook): Unit = h(hook.asInstanceOf[T])
      },
      priority
    )
  }

  /**
   * Register a hook with Canary
   * @param h Hook method
   * @param clazz Class of hook
   * @tparam T Type of hook
   */
  def hook[T <: Hook](clazz: Class[T])(h: T => Unit): Unit = {
    hookListener.registerHook(
      new PluginListener {},
      this,
      clazz,
      new Dispatcher {
        override def execute(listener: PluginListener, hook: Hook): Unit = h(hook.asInstanceOf[T])
      },
      priority
    )
  }

  def command(aliases: Seq[String],
              permissions: Seq[String],
              description: String,
              toolTip: String,
              parent: String = "",
              helpLookup: String = "",
              searchTerms: Seq[String] = Seq(),
              min: Int = 1,
              max: Int = -1,
              version: Int = 1,
              tabComplete: (MessageReceiver, Seq[String]) => Seq[String] = (_, _) => Seq(),
              translator: Translator = Translator.getInstance()
               )(
               command: (MessageReceiver, Seq[String]) => Unit
               ): Unit = {

    val meta = new DynamicCommandAnnotation(
      aliases.toArray,
      permissions.toArray,
      description,
      toolTip,
      parent,
      helpLookup,
      searchTerms.toArray,
      min,
      max,
      "",
      version
    )
    val tcd = new TabCompleteDispatch() {

      import scala.collection.JavaConversions._

      override def complete(msgrec: MessageReceiver, args: Array[String]): util.List[String] = tabComplete(msgrec, args)
    }

    val cc = new CanaryCommand(meta, this, translator, tcd) {
      override protected def execute(caller: MessageReceiver, parameters: Array[String]): Unit = command(caller, parameters)
    }

    commandManager.registerCommand(cc, this, false)
  }
}

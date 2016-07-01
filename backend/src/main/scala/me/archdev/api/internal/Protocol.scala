package me.archdev.api.internal

/**
  * All internal commands and events must be mixed with this traits.
  * This is not necessary, but marking of classes with stuff like this will do code more clear and typesafe.
  */

trait Command
case class ShowErrorMessage(userId: String, message: String) extends Command

trait Event
case class ErrorMessageShowed(message: String) extends Event
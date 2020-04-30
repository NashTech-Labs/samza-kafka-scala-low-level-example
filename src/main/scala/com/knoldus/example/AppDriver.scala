package com.knoldus.example

import com.typesafe.config.ConfigFactory
import org.apache.samza.runtime.LocalApplicationRunner
import org.apache.samza.util.CommandLine

object AppDriver extends App {

  val config = ConfigFactory.load()
  val cmdLine = new CommandLine
  val options = cmdLine.parser.parse(config.getString("config.factory"), config.getString("config.path"))
  val conf = cmdLine.loadConfig(options)
  val runner = new LocalApplicationRunner(new HelloWorld, conf)
  runner.run()
  runner.waitForFinish()

}

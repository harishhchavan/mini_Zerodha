package com.scala.logger

import org.slf4j.LoggerFactory

trait Logg {

  protected val logger = LoggerFactory.getLogger(this.getClass)
}

package io.github.arthurfish.appender.blockservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BlockServiceApplication

fun main(args: Array<String>) {
  runApplication<BlockServiceApplication>(*args)
}

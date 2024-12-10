package io.github.arthurfish.appender.blockservice

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class BlockOperationMessageService(private val repository: BlockRepository, private val rabbitTemplate: RabbitTemplate) {
  val log = LoggerFactory.getLogger(BlockOperationMessageService::class.java)
  @RabbitListener(queues = ["#{blockOperationQueue.name}"])
  fun messageDispatch(message: Map<String, String>): Unit {
    log.info("Received message then dispatch. message: $message")
    val operation = message.getOrDefault("channel_block_operation", "no_operation_in_message")
    if(operation == "append"){
      val responseMessage = appendBlock(message);
      AppenderRabbitMqTemplate.sendToRabbitMq(rabbitTemplate, responseMessage, "block_content")
    }else if(operation == "sync"){
      val syncMessages = syncBlock(message)
      for(message in syncMessages){
        AppenderRabbitMqTemplate.sendToRabbitMq(rabbitTemplate, message, "block_content")
      }
      val syncCompleteMessage = message
        .minus("channel_block_operation")
        .plus("channel_block_result" to "sync_complete")
      AppenderRabbitMqTemplate.sendToRabbitMq(rabbitTemplate, syncCompleteMessage)
    }
  }

  fun appendBlock(message: Map<String, String>): Map<String, String> {
    val channelId = message["channel_id"]!!
    val blockContent = message["block_content"]!!
    repository.appendBlock(channelId, blockContent)
    return message.minus("channel_block_operation").plus("channel_block_result" to "success")
  }

  fun syncBlock(message: Map<String, String>): List<Map<String, String>> {
    log.info("SYNC block. message:${message}")
    val channelId = message["channel_id"]!!
    val ownedBlocks = message["owned_blocks"]!!.toLong()
    val retrievedBlocks = repository.getBlocksWhichOrderLagerThan(channelId, ownedBlocks)
    log.info("Retrieved block: $retrievedBlocks")
    return retrievedBlocks.map{ block ->
      message.minus("channel_block_operation")
        .plus(mapOf(
        "channel_block_result" to "syncing",
        "is_plaintext" to block.isPlaintext,
        "block_order" to block.blockOrder!!.toString(),
        "block_content" to block.blockContent,
        ))
    }.toList()
  }
}

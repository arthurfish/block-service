package io.github.arthurfish.appender.blockservice

import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BlockService(private val jdbcAggregateTemplate: JdbcAggregateTemplate) {

  fun insertBlock(channelId: String, isPlaintext: String, attachedBlockId: String, blockContent: String) {
    val block = BlockModel(
      blockId = UUID.randomUUID().toString(),
      channelId = channelId,
      isPlaintext = isPlaintext,
      attachedBlockId = attachedBlockId,
      blockContent = blockContent
    )
    jdbcAggregateTemplate.insert(block)
  }
}

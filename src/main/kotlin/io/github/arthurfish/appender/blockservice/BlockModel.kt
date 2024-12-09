package io.github.arthurfish.appender.blockservice

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("blocks")
class BlockModel (
  @Id
  val blockId: String,
  val blockOrder: Long? = -1,
  val channelId: String,
  val isPlaintext: String,
  val attachedBlockId: String?,
  val blockContent: String
)
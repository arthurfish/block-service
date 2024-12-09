package io.github.arthurfish.appender.blockservice

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import java.sql.ResultSet

class BlockRepository(private val jdbcClient: JdbcClient) {
  fun appendBlock(block: BlockModel){
    val sql = "INSERT INTO blocks (block_id, channel_id, is_plaintext, attached_block_id, block_content) VALUES (:block_id, :channel_id, :is_plaintext, :block_content)"
    jdbcClient.sql(sql).params(mapOf(
      "block_id" to block.blockId,
      "channel_id" to block.channelId,
      "is_plaintext" to block.isPlaintext,
      "attached_block_id" to block.attachedBlockId,
      "block_content" to block.blockContent))
      .update()
  }

  fun getBlocksWhichOrderLagerThan(channelId: String, currentOrder: Long): List<BlockModel> {
    val sql = "SELECT * from blocks WHERE block_order > :current_order ORDER BY block_order"
    val result = jdbcClient.sql(sql).params("current_order" to currentOrder).query(BlockRowMapper()).list()
    return result
  }

}

class BlockRowMapper : RowMapper<BlockModel> {
  override fun mapRow(rs: ResultSet, rowNum: Int): BlockModel = BlockModel(
      blockId = rs.getString("block_id"),
      blockOrder = rs.getLong("block_order"),
      channelId = rs.getString("channel_id"),
      isPlaintext = rs.getString("is_plaintext"),
      blockContent = rs.getString("block_content"),
      attachedBlockId = rs.getString("attached_block_id"),
    )
}
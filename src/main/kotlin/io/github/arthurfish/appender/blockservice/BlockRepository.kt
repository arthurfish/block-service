package io.github.arthurfish.appender.blockservice

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class BlockRepository(private val jdbcClient: JdbcClient) {
  fun appendBlock(channelId: String,  blockContent: String, isPlaintext: Boolean = true, attachedBlockId: String? = null){
    val sql = "INSERT INTO blocks (channel_id, is_plaintext, attached_block_id, block_content) VALUES (:channel_id::UUID, :is_plaintext, :attached_block_id, :block_content)"
    jdbcClient.sql(sql).params(mapOf(
      "channel_id" to channelId,
      "is_plaintext" to if (isPlaintext) "true" else "false",
      "attached_block_id" to attachedBlockId,
      "block_content" to blockContent))
      .update()
  }

  fun getBlocksWhichOrderLagerThan(channelId: String, currentOrder: Long): List<BlockModel> {
    val sql = """
    SELECT * FROM blocks 
    WHERE block_order > :current_order 
    AND channel_id = CAST(:channel_id AS UUID) 
    ORDER BY block_order
"""
    val result = jdbcClient.sql(sql)
      .params(mapOf("current_order" to currentOrder,
        "channel_id" to channelId))
      .query(BlockRowMapper()).list()
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
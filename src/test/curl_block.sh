curl -X POST http://localhost:8080/message \
     -H "Content-Type: application/json" \
     -d '{
           "headers": {
             "channel_block_operation": "append"
           },
           "topic": "operation.channel_block",
           "payload": {
             "channel_block_operation": "append",
             "channel_id": "3a1b6140-8fda-4331-908e-38d1b0a4f019",
             "block_content": "Fuck you day by day."
           }
         }'

curl -X POST http://localhost:8080/message \
     -H "Content-Type: application/json" \
     -d '{
           "headers": {
             "channel_block_operation": "sync"
           },
           "topic": "operation.channel_block",
           "payload": {
             "channel_block_operation": "sync",
             "channel_id": "3a1b6140-8fda-4331-908e-38d1b0a4f019",
             "owned_blocks": "1"
           }
         }'

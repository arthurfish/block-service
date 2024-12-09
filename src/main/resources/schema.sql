DROP TABLE IF EXISTS blocks;
CREATE TABLE blocks (
                        block_id UUID PRIMARY KEY,
                        block_order INTEGER,
                        channel_id UUID,
                        is_plaintext VARCHAR(64),
                        attached_block_id UUID,
                        block_content TEXT
);

CREATE OR REPLACE FUNCTION increment_block_order()
    RETURNS TRIGGER AS $$
DECLARE
    max_order INTEGER;
BEGIN
    SELECT COALESCE(MAX(block_order), 0) INTO max_order FROM blocks WHERE channel_id = NEW.channel_id;
    NEW.block_order := max_order + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_block_order
    BEFORE INSERT ON blocks
    FOR EACH ROW
EXECUTE FUNCTION increment_block_order();

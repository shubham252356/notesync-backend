-- V3: Create note_shares table
CREATE TYPE permission_type AS ENUM ('READ', 'WRITE');

CREATE TABLE note_shares (
    id         BIGSERIAL       PRIMARY KEY,
    note_id    BIGINT          NOT NULL REFERENCES notes(id) ON DELETE CASCADE,
    user_id    BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission permission_type NOT NULL,
    created_at TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_note_shares_note_user UNIQUE (note_id, user_id)
);

CREATE INDEX idx_note_shares_note_id ON note_shares (note_id);
CREATE INDEX idx_note_shares_user_id ON note_shares (user_id);

-- V12: Saved place sorgu performansı için indeks
-- - findByUserIdOrderByDateOfRecordedDesc: places listesi
-- - findByUserIdAndIsDefaultStartTrue / End: varsayılan başlangıç/bitiş

CREATE INDEX IF NOT EXISTS idx_user_saved_place_user_id
    ON user_saved_place (user_id);

CREATE INDEX IF NOT EXISTS idx_user_saved_place_user_default_start
    ON user_saved_place (user_id)
    WHERE is_default_start = TRUE;

CREATE INDEX IF NOT EXISTS idx_user_saved_place_user_default_end
    ON user_saved_place (user_id)
    WHERE is_default_end = TRUE;
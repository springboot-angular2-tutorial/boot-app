CREATE VIEW `feed` AS
  SELECT
    m.*,
    r.follower_id AS feed_user_id
  FROM micropost m
    INNER JOIN relationship r
      ON m.user_id = r.followed_id
  UNION
  SELECT
    m.*,
    u.id AS feed_user_id
  FROM micropost m
    INNER JOIN user u
      ON m.user_id = u.id;

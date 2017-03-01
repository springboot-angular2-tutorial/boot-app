CREATE VIEW `user_stats` AS
  SELECT
    u.id                         AS user_id,
    (SELECT count(*)
     FROM relationship r
     WHERE r.followed_id = u.id) AS follower_cnt,
    (SELECT count(*)
     FROM relationship r
     WHERE r.follower_id = u.id) AS following_cnt,
    (SELECT count(*)
     FROM micropost m
     WHERE m.user_id = u.id)     AS micropost_cnt
  FROM user u


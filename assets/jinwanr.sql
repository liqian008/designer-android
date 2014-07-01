CREATE TABLE `tb_album` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `link` text DEFAULT NULL,
  `user_id` integer NOT NULL,
  `status` short DEFAULT 1,
  `cover_large_img` text DEFAULT '',
  `cover_medium_img`text DEFAULT '',
  `cover_small_img` text DEFAULT '',
  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
)


CREATE TABLE `tb_album_latest` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `link` text DEFAULT NULL,
  `user_id` integer NOT NULL,
  `status` short DEFAULT 1,
  `cover_large_img` text DEFAULT '',
  `cover_medium_img`text DEFAULT '',
  `cover_small_img` text DEFAULT '',

  `browse_count` integer NOT NULL,
  `comment_count` integer NOT NULL,
  `like_count` integer NOT NULL,
  `favorite_count` integer NOT NULL,

  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
);


CREATE TABLE `tb_album_recommend` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `link` text DEFAULT NULL,
  `user_id` integer NOT NULL,
  `status` short DEFAULT 1,
  `cover_large_img` text DEFAULT '',
  `cover_medium_img`text DEFAULT '',
  `cover_small_img` text DEFAULT '',

  `browse_count` integer NOT NULL,
  `comment_count` integer NOT NULL,
  `like_count` integer NOT NULL,
  `favorite_count` integer NOT NULL,

  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
);

CREATE TABLE `tb_album_follow` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `link` text DEFAULT NULL,
  `user_id` integer NOT NULL,
  `status` short DEFAULT 1,
  `cover_large_img` text DEFAULT '',
  `cover_medium_img`text DEFAULT '',
  `cover_small_img` text DEFAULT '',

  `browse_count` integer NOT NULL,
  `comment_count` integer NOT NULL,
  `like_count` integer NOT NULL,
  `favorite_count` integer NOT NULL,

  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
);


CREATE TABLE `tb_album_mine` (
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
);

CREATE TABLE `tb_album_slide` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `album_id` integer NOT NULL,
  `user_id` integer NOT NULL,
  `is_cover` short DEFAULT NULL,
  `slide_large_img` text DEFAULT '',
  `slide_medium_img` text DEFAULT '',
  `slide_small_img` text DEFAULT '',
  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
);



CREATE TABLE `tb_comment` (
  `id` long NOT NULL PRIMARY KEY,
  `album_id` integer DEFAULT NULL,
  `album_slide_id` integer DEFAULT NULL,
  `title` text DEFAULT NULL,
  `comment` text DEFAULT NULL,
  `from_id` integer NOT NULL,
  `to_id` integer DEFAULT NULL,
  `designer_id` integer DEFAULT NULL,
  `user_head_img` text DEFAULT NULL,
  `nickname` text NOT NULL DEFAULT '',
  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
);




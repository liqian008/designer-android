
CREATE TABLE `tb_album_latest` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `price` long DEFAULT NULL,
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
  
  `is_favorite` integer default 0,
  `is_like` integer default 0,

  `designer_avatar` text DEFAULT '',
  `designer_nickname` text DEFAULT '',
  `designer_follow_status` integer default 0,
  
  `wx_share_title` text DEFAULT '',
  `wx_share_content` text DEFAULT '',
  `wx_share_icon_url` text DEFAULT '',
  `wx_share_link` text DEFAULT '',
  
  `weibo_share_title` text DEFAULT '',
  `weibo_share_content` text DEFAULT '',
  `weibo_share_icon_url` text DEFAULT '',
  `weibo_share_link` text DEFAULT '',
  
  `sort` long DEFAULT NULL,

  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
);


CREATE TABLE `tb_album_recommend` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `price` long DEFAULT NULL,
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
  
  `is_favorite` integer default 0,
  `is_like` integer default 0,

  `designer_avatar` text DEFAULT '',
  `designer_nickname` text DEFAULT '',
  `designer_follow_status` integer default 0,
  
  `wx_share_title` text DEFAULT '',
  `wx_share_content` text DEFAULT '',
  `wx_share_icon_url` text DEFAULT '',
  `wx_share_link` text DEFAULT '',
  
  `weibo_share_title` text DEFAULT '',
  `weibo_share_content` text DEFAULT '',
  `weibo_share_icon_url` text DEFAULT '',
  `weibo_share_link` text DEFAULT '',
  
  `sort` long DEFAULT NULL,

  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
);

CREATE TABLE `tb_album_follow` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `price` long DEFAULT NULL,
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
  
  `is_favorite` integer default 0,
  `is_like` integer default 0,

  `designer_avatar` text DEFAULT '',
  `designer_nickname` text DEFAULT '',
  `designer_follow_status` integer default 1,
  
  `wx_share_title` text DEFAULT '',
  `wx_share_content` text DEFAULT '',
  `wx_share_icon_url` text DEFAULT '',
  `wx_share_link` text DEFAULT '',
  
  `weibo_share_title` text DEFAULT '',
  `weibo_share_content` text DEFAULT '',
  `weibo_share_icon_url` text DEFAULT '',
  `weibo_share_link` text DEFAULT '',
  
  `sort` long DEFAULT NULL,

  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
);



CREATE TABLE `tb_hot_album_weekly` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `price` long DEFAULT NULL,
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
  
  `is_favorite` integer default 0,
  `is_like` integer default 0,

  `designer_avatar` text DEFAULT '',
  `designer_nickname` text DEFAULT '',
  `designer_follow_status` integer default 1,
  
  `wx_share_title` text DEFAULT '',
  `wx_share_content` text DEFAULT '',
  `wx_share_icon_url` text DEFAULT '',
  `wx_share_link` text DEFAULT '',
  
  `weibo_share_title` text DEFAULT '',
  `weibo_share_content` text DEFAULT '',
  `weibo_share_icon_url` text DEFAULT '',
  `weibo_share_link` text DEFAULT '',
  
  `sort` long DEFAULT NULL,

  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
);


CREATE TABLE `tb_hot_album_monthly` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `price` long DEFAULT NULL,
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
  
  `is_favorite` integer default 0,
  `is_like` integer default 0,

  `designer_avatar` text DEFAULT '',
  `designer_nickname` text DEFAULT '',
  `designer_follow_status` integer default 1,
  
  `wx_share_title` text DEFAULT '',
  `wx_share_content` text DEFAULT '',
  `wx_share_icon_url` text DEFAULT '',
  `wx_share_link` text DEFAULT '',
  
  `weibo_share_title` text DEFAULT '',
  `weibo_share_content` text DEFAULT '',
  `weibo_share_icon_url` text DEFAULT '',
  `weibo_share_link` text DEFAULT '',
  
  `sort` long DEFAULT NULL,

  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
);


CREATE TABLE `tb_hot_album_yearly` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `price` long DEFAULT NULL,
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
  
  `is_favorite` integer default 0,
  `is_like` integer default 0,

  `designer_avatar` text DEFAULT '',
  `designer_nickname` text DEFAULT '',
  `designer_follow_status` integer default 1,
  
  `wx_share_title` text DEFAULT '',
  `wx_share_content` text DEFAULT '',
  `wx_share_icon_url` text DEFAULT '',
  `wx_share_link` text DEFAULT '',
  
  `weibo_share_title` text DEFAULT '',
  `weibo_share_content` text DEFAULT '',
  `weibo_share_icon_url` text DEFAULT '',
  `weibo_share_link` text DEFAULT '',
  
  `sort` long DEFAULT NULL,

  `create_time` long DEFAULT NULL,
  `update_time` long DEFAULT NULL
);


CREATE TABLE `tb_album_mine` (
  `id` integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  `title` text DEFAULT NULL,
  `remark` text DEFAULT NULL,
  `price` long DEFAULT NULL,
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
  
  `is_favorite` integer default 0,
  `is_like` integer default 0,
  
  `wx_share_title` text DEFAULT '',
  `wx_share_content` text DEFAULT '',
  `wx_share_icon_url` text DEFAULT '',
  `wx_share_link` text DEFAULT '',
  
  `weibo_share_title` text DEFAULT '',
  `weibo_share_content` text DEFAULT '',
  `weibo_share_icon_url` text DEFAULT '',
  `weibo_share_link` text DEFAULT '',
  
  `sort` long DEFAULT NULL,
  
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




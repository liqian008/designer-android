package com.bruce.designer.exception;


public class ErrorCode {

	/* 正确请求 */
	// public static final int SUCCESS = 0;

	/* 系统错误分配100内的错误码 */
	
	public final static int CLIENT_NETWORK_UNAVAILABLE = -1;
	public final static int CLIENT_GUEST_ACCESS_DENIED = -2;
	
	
	/* 系统未知错误 */
	public final static int SYSTEM_ERROR = 1;

	/* 参数不正确 */
	public final static int SYSTEM_MISSING_PARAM = 2;

	/* 验证码不正确 */
	public final static int SYSTEM_VERIFYCODE_ERROR = 3;
	
	/* 没有更多数据 */
	public final static int SYSTEM_NO_MORE_DATA = 4;
	
	public final static int E_SYS_INVALID_APP_ID = 6;// invalid appid

    public final static int E_SYS_INVALID_TICKET = 7;// invalid tikect

    public final static int E_SYS_INVALID_SIG = 8;// invalid sig
	

	/* 权限相关分配100-200的错误码 */
	
	/* 权限不够，需登陆 */
	public static final int AUTHORIZE_NEED_LOGIN = 100; 
	/* 用户权限不够，需设计师权限 */
	public static final int AUTHORIZE_NEED_DESIGNER = 101;
	
	/* 账户相关分配200-300的错误码 */
	
	/* 获取用户失败 */
	public static final int USER_ERROR = 200;
	/* 用户已经被封禁 */
	public static final int USER_FORBIDDEN = 201;
	/* 用户名（email形式）格式错误 */
	public static final int USER_USERNAME_FORMAT_ERROR = 202;
	/* 用户名（email形式）已存在 */
	public static final int USER_USERNAME_EXISTS = 203;
	/* 昵称已存在 */
	public static final int USER_NICKNAME_EXISTS = 204;
	/* 账户密码不匹配，通常用于登录验证 */
	public static final int USER_PASSWORD_NOT_MATCH = 205;
	/* 旧密码输入错误，通常用与改密码情况 */
	public static final int USER_OLD_PASSWORD_NOT_MATCH = 206;
	/* 修改密码失败 */
	public static final int USER_CHANGE_PASSWORD_FAILED = 207;
	/* 用户不存在 */
	public static final int USER_NOT_EXIST = 208;
	/* 用户注册失败 */
	public static final int USER_REGISTER_ERROR = 209;
	/* 用户登录失败 */
	public static final int USER_LOGIN_ERROR = 210;

	/* OAuth未知错误 */
	public static final int OAUTH_ERROR = 230;
	/* Session不存在OAuth Token，无法进行绑定 */
	public static final int OAUTH_SESSIONTOKEN_NOT_EXISTS = 231;
	/* 已经绑定同类型token，无法再次绑定 */
	public static final int OAUTH_ALREADY_BIND = 232;
	
	
	/* 关系图谱分配300-400的错误码 */
	/* 未知错误 */
	public static final int GRAPH_ERROR = 300;
	/* 尚未关注 */
	public static final int GRAPH_HAS_NOT_FOLLOW = 301;
	/* 已经关注过了 */
	public static final int GRAPH_ALREADY_FOLLOW = 302;
	/* 自己不能关注自己 */
	public static final int GRAPH_FOLLOW_SELF_DENIED = 303;
	/* 不能关注一般用户（只能关注设计师） */
	public static final int GRAPH_FOLLOW_COMMONUSER_DENIED = 304;
	/* 取消关注失败 */
	public static final int GRAPH_UNFOLLOW_FAILED = 305;
	
	/* 消息分配400-500的错误码 */
	/* 消息未知错误 */
	public static final int MESSAGE_ERROR = 400;
	/* 不支持的消息类型 */
	public static final int MESSAGE_UNSUPPORT_TYPE = 401;
	/* 不能给自己发消息 */
	public static final int MESSAGE_TO_SELF = 402;
	

	/* 作品分配500-600的错误码 */
	/* 未知错误 */
	public static final int ALBUM_ERROR = 500;
	/* 作品已经被封禁 */
	public static final int ALBUM_FORBIDDEN = 501;
	/* 作品不存在 */
	public static final int ALBUM_NOT_EXIST = 502;
	/* 作品页不存在 */
	public static final int ALBUMSLIDE_NOT_EXIST = 503;
	/* 作者不匹配 */
	public static final int ALBUM_AUTHOR_NOT_MATCH = 504;
	/* 作品更新失败 */
	public static final int ALBUM_UPDATE_FAILED = 505;
	/* 作品删除失败 */
	public static final int ALBUM_DELETE_FAILED = 506;
	/* 作品创建失败 */
	public static final int ALBUM_CREATE_FAILED = 507;
	
	
	/* 上传分配x00-x00的错误码 */
	/* 未知错误 */
	public static final int UPLOAD_ERROR = 600;
	/* 文件格式不支持 */
	public static final int UPLOAD_FORMAT_NOT_SUPPORT = 601;
	/* 图片上传未知错误 */
	public static final int UPLOAD_IMAGE_ERROR = 602;
	/* 头像上传未知错误 */
	public static final int UPLOAD_AVATAR_ERROR = 603;
	/* 图片尺寸过大 */
	public static final int UPLOAD_IMAGE_OVERSIZE = 604;
	
	/* tag相关分配x00-x00的错误码 */
	/* 未知错误 */
	public static final int TAG_ERROR = 700;
	/* 未选择tag */
	public static final int TAG_EMPTY = 701;
	/* 选择的tag不存在 */
	public static final int TAG_NOT_EXISTS = 702;
	

}

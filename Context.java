package com.zan.framework.commons;

import java.io.Serializable;

/**
 * @author 小血哈迪斯
 * 
 * 
 */
public class Context implements Serializable{

	private static final long serialVersionUID = 9195017479883191621L;
	
	private String msg;
	private int code;
	private Object result;


	public static interface Message {

		String DEFAULT_SERVER_ERROR_MSG = "对不起服务出现异常";
		String DEFAULT_SUCCESS_MGS = "请求操作API成功";
		String DEFAULT_VALI_MSG = "传入数据不合法";
		String UN_fIND_USER_OF_ERP_MSG = "未从ERP中找到当前用户的信息";
		String WEIXIN_EXIST_MSG = "该微信已经注册过本系统";
		String ADD_FIREND_ERROR_MSG = "一天内只能进行一次加粉操作";
		String INVALID_USERPHONE_FILE = "上传文件不合法";
		String NO_ORDER_NUM_MSG = "输入的订单不存在";
		String VERIFY_CODE_ERROR_MSG = "校验码错误";
		String UN_AUTH_CODE_ERROR_MSG = "未登陆";
	}

	public static interface Code {
		int OK = 200;
		int ERROR = 500;
		int UN_AUTH = 501;
		int BAD_REQUEST = 400;
		int UNAUTHORIZED = 401;
		int UNAUTHENTICATED = 403;
		int UN_fIND_USER_OF_ERP = 405;
		int WEIXIN_EXIST = 406;
		int DELETE_FOLDER_FAIL = 407;
		int REQ_IS_NOT_FOLDER = 408;
		int ADD_FIREND_ERROR = 409;
	}

	public static Context builder() {
		return new Context();
	}

	public Context code(int code) {
		this.code = code;
		return this;
	}
	
	public Context msg(String msg) {
		this.msg = msg;
		return this;
	}
	
	public Context result(Object result) {
		this.result = result;
		return this;
	}

	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}


}

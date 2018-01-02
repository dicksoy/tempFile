package com.zan.framework.web.mvc;

import java.beans.PropertyEditorSupport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;

import com.zan.framework.commons.Context;

@ControllerAdvice
public class GlobalJsonHandlerAdvice {

	private static final Log LOGGER = LogFactory
			.getLog(GlobalJsonHandlerAdvice.class);

	static ObjectMapper mapper = new ObjectMapper();
	
	public GlobalJsonHandlerAdvice() {
		LOGGER.info("{} 全局异常处理类初始化成功");
	}

	/**
	 * 初始化数据绑定 1. 将所有传递进来的String进行HTML编码，防止XSS攻击 2. 将字段中Date类型转换为String类型
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(StringUtils.isBlank(text) ? null : StringEscapeUtils
						.escapeHtml4(StringUtils.trim(text.trim())));
			}

			@Override
			public String getAsText() {
				Object value = getValue();
				return value != null ? value.toString() : "";
			}
		});
	}

	@ExceptionHandler(BindException.class)
	public void handleBindException(HttpServletRequest request,
			HttpServletResponse response, BindException bindException) throws Exception {
		List<ObjectError> objectErrors = bindException.getAllErrors();
		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		for (ObjectError objectError : objectErrors) {
			if (!isFirst) {
				sb.append(",");
			} else {
				isFirst = !isFirst;
			}
			sb.append(objectError.getDefaultMessage());
		}
		
		LOGGER.error(bindException);
		handleError(request, response, bindException, Context.Code.BAD_REQUEST, sb.toString());
	}

	@ExceptionHandler(Exception.class)
	public void handleException(HttpServletRequest request,
			HttpServletResponse response, Exception e) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		e.printStackTrace(ps);
		LOGGER.info(request.getRequestURI());
		LOGGER.error(new String(bos.toByteArray()));
		IOUtils.closeQuietly(ps);
		IOUtils.closeQuietly(bos);
		handleError(request, response, e, Context.Code.ERROR, e.getMessage());
	}

	@ExceptionHandler(UnauthenticatedException.class)
	public void handleException(HttpServletRequest request,
			HttpServletResponse response, UnauthenticatedException e) throws Exception {
		LOGGER.error(e);
		LOGGER.info(request.getRequestURI());
		handleError(request, response, e, Context.Code.UN_AUTH, Context.Message.UN_AUTH_CODE_ERROR_MSG);
	}

	private void handleError(HttpServletRequest request,
			HttpServletResponse response, Exception e, int errorCode, String errorMsg) throws Exception {
		String uri = request.getRequestURI();
		String extendName = FilenameUtils.getExtension(uri);
		request.setAttribute("_exception", e);
		request.setAttribute("_requestUrl", request.getRequestURL().toString());
		if ("html".equals(extendName)) {
			throw e;
		} else {
			response.setContentType("application/json; charset=utf-8");
			PrintWriter out = null;
			try {
				out = response.getWriter();
				mapper.writeValue(out, Context.builder().code(errorCode).msg(errorMsg).result(""));
			} catch (IOException e1) {
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
	}
	
}

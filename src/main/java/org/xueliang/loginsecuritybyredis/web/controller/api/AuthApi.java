package org.xueliang.loginsecuritybyredis.web.controller.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xueliang.loginsecuritybyredis.web.model.JSONResponse;
import org.xueliang.loginsecuritybyredis.web.model.User;

import redis.clients.jedis.Jedis;

/**
 * 认证类
 * @author XueLiang
 * @date 2016年11月1日 下午4:11:59
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth/")
public class AuthApi {

	private static final Map<String, User> USER_DATA = new HashMap<String, User>();
	@Value("${auth.max_try_count}")
	private int MAX_TRY_COUNT = 0;
	@Value("${auth.max_disabled_seconds}")
	private int MAX_DISABLED_SECONDS = 0;
	
	@Value("${redis.host}")
	private String host;
	@Value("${redis.port}")
	private int port;
	private Jedis jedis;
	
	@PostConstruct
	public void init() throws FileNotFoundException, IOException {
		for (int i = 0; i < 3; i++) {
			String username = "username" + 0;
			String password = "password" + 0;
			USER_DATA.put(username + "_" + password, new User(username, "nickname" + i));
		}
		jedis = new Jedis(host, port);
	}
	
	@RequestMapping(value = {"login"})
	public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
		JSONResponse jsonResponse = new JSONResponse();
		String key = username;
		String countString = jedis.get(key);
		boolean exists = countString != null;
		int count = exists ? Integer.parseInt(countString) : 0;
		if (count >= MAX_TRY_COUNT) {
			checkoutMessage(key, count, jsonResponse);
			return jsonResponse.toString();
		}
		User user = USER_DATA.get(username + "_" + password);
		if (user == null) {
			count++;
			int secondsRemain = MAX_DISABLED_SECONDS;
			if (exists && count < 5) {
				secondsRemain = (int)(jedis.pttl(key) / 1000);
			}
			jedis.set(key, count + "");
			jedis.expire(key, secondsRemain);
			checkoutMessage(key, count, jsonResponse);
			return jsonResponse.toString();
		}
		count = 0;
		if (exists) {
			jedis.del(key);
		}
		checkoutMessage(key, count, jsonResponse);
		return jsonResponse.toString();
	}
	
	/**
	 * 
	 * @param key
	 * @param count 尝试次数，也可以改为从redis里直接读
	 * @param jsonResponse
	 * @return
	 */
	private void checkoutMessage(String key, int count, JSONResponse jsonResponse) {
		if (count == 0) {
			jsonResponse.setCode(0);
			jsonResponse.addMsg("success", "恭喜，登录成功！");
			return;
		}
		if (count >= MAX_TRY_COUNT) {
			long pttlSeconds = jedis.pttl(key) / 1000;
			long hours = pttlSeconds / 3600;
			long sencondsRemain = pttlSeconds - hours * 3600;
			long minutes = sencondsRemain / 60;
			long seconds = sencondsRemain - minutes * 60;
			jsonResponse.setCode(1);
			jsonResponse.addError("login_disabled", "登录超过" + MAX_TRY_COUNT + "次，请" + hours + "小时" + minutes + "分" + seconds + "秒后再试！");
			return;
		}
		jsonResponse.setCode(1);
		jsonResponse.addError("username_or_password_is_wrong", "密码错误，您还有 " + (MAX_TRY_COUNT - count) + " 次机会！");
		return;
	}
}

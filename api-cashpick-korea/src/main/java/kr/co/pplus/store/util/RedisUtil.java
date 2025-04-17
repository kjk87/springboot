package kr.co.pplus.store.util;

import kr.co.pplus.store.StoreApplication;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.type.model.SearchOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RedisUtil {
	private final static Logger logger = LoggerFactory.getLogger(StoreApplication.class);


	public RedisTemplate  redisTemplate;

	private static RedisUtil redisUtil = null ;

	public static RedisUtil getInstance() {

		if( RedisUtil.redisUtil == null ){
			RedisUtil.redisUtil = new RedisUtil() ;
		}

		return redisUtil ;
	}

	public  RedisTemplate getRedis() {

		if (redisTemplate == null) {
			redisTemplate = (RedisTemplate)ApplicationContextProvider.getBean("redisTemplate");
//			redisTemplate.setKeySerializer(new StringRedisSerializer());
//			redisTemplate.setValueSerializer(new StringRedisSerializer());
		}

		return redisTemplate;
	}
	
	public  void deleteObj(String key) {
		getRedis().opsForHash().getOperations().delete(key);
	}

	public  void deleteOpsHash(String key, String hashKey) {
		getRedis().opsForHash().delete(key, hashKey);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObj(String key) {
		return (T) getRedis().opsForValue().get(key);
	}

	public  List<String> hScan(String key, SearchOpt opt) {

		try {
			ScanOptions options = ScanOptions.scanOptions().match("*" + opt.getSearch() + "*").count(opt.getSz()).build();
			Cursor<Map.Entry<byte[], byte[]>> entries = getRedis().getConnectionFactory().getConnection().hScan(key.getBytes(), options);
			List<String> result = new ArrayList<String>();
			if (entries != null) {
				while (entries.hasNext()) {
					Map.Entry<byte[], byte[]> entry = entries.next();
					byte[] actualKey = entry.getKey();
					result.add(new String(actualKey));
				}
				return result;
			} else {
				return null;
			}
		} catch(Exception e) {
			logger.error(AppUtil.excetionToString(e)) ;
			return null ;
		}
	}

	public  List<Map<String,String>> hScanAll(String key) {

		try {
			ScanOptions options = ScanOptions.scanOptions().match("*").count(1000).build();
			Cursor<Map.Entry<byte[], byte[]>> entries = getRedis().getConnectionFactory().getConnection().hScan(key.getBytes(), options);
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			if (entries != null) {
				while (entries.hasNext()) {
					Map.Entry<byte[], byte[]> entry = entries.next();
					Map<String, String> map = new HashMap<String, String>();
					map.put("key", new String(entry.getKey(), "UTF-8"));
					map.put("value", new String(entry.getValue(), "UTF-8"));
					list.add(map) ;
				}
				return list;
			} else {
				return null;
			}
		} catch(Exception e) {
			logger.error(AppUtil.excetionToString(e)) ;
			return null ;
		}
	}


	public  List<String> hScanAllKeys(String key) {

		try {
			ScanOptions options = ScanOptions.scanOptions().match("*").count(1000).build();
			Cursor<Map.Entry<byte[], byte[]>> entries = getRedis().getConnectionFactory().getConnection().hScan(key.getBytes(), options);
			List<String> list = new ArrayList<String>();
			if (entries != null) {
				while (entries.hasNext()) {
					Map.Entry<byte[], byte[]> entry = entries.next();
					list.add(new String(entry.getKey(), "UTF-8"));
				}
				return list;
			} else {
				return null;
			}
		} catch(Exception e) {
			logger.error(AppUtil.excetionToString(e)) ;
			return null ;
		}
	}

	public  String hGet(String key, String hashKey) {
		RedisConnection redisConnection = getRedis().getConnectionFactory().getConnection();
		try {
			byte[] result = redisConnection.hGet(key.getBytes(), hashKey.getBytes("UTF-8")) ;
			redisConnection.close();
			return new String(result) ;
		} catch(Exception e) {
			logger.error(AppUtil.excetionToString(e)) ;
			redisConnection.close();
			return null ;
		}
	}

	@SuppressWarnings("unchecked")
	public  <T> T getOpsHash(String key, String hashKey) {
		return (T) getRedis().opsForHash().get(key, hashKey);
	}

	public  List<String> getKeys(String pattern) {
		RedisConnection redisConnection = getRedis().getConnectionFactory().getConnection();
		Set<byte[]> redisKeys = redisConnection.keys(("*"+pattern+"*").getBytes());
		List<String> keysList = new ArrayList<>();
		Iterator<byte[]> it = redisKeys.iterator();
		while (it.hasNext()) {
			byte[] data = it.next();
			keysList.add(new String(data, 0, data.length));
		}
		redisConnection.close();
		return keysList;
	}

	public  <T> void putOpsHash(String key, String hashKey, T object) {
		getRedis().opsForHash().put(key, hashKey, object);
	}

	public void hSet(String key, String field, String value) {
		RedisConnection redisConnection = getRedis().getConnectionFactory().getConnection();
		try {

			redisConnection.hSet(key.getBytes(), field.getBytes("UTF-8"), value.getBytes());
		} catch (Exception e) {
			logger.error(AppUtil.excetionToString(e)) ;
		}
		redisConnection.close();
	}

	public void hDel(String key, String field) {
		RedisConnection redisConnection = getRedis().getConnectionFactory().getConnection();
		try {

			redisConnection.hDel(key.getBytes(), field.getBytes("UTF-8"));
		} catch (Exception e) {
			logger.error(AppUtil.excetionToString(e)) ;
		}
		redisConnection.close();
	}

	public  <T> void putOpsListLeft(String key, String value) {
		getRedis().opsForList().leftPush(key, value);
	}

	public  void hashExpire(String key, int value, TimeUnit unit) {
		getRedis().expire(key, value, unit);
	}

	public  void redisConnectionExpire(String key, long value) {
		RedisConnection redisConnection = getRedis().getConnectionFactory().getConnection();
		redisConnection.expire(key.getBytes(), value);
		redisConnection.close();
	}
}

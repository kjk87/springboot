package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.exception.SessionNotFoundException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.RedisUtil;
import kr.co.pplus.store.util.SecureUtil;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(transactionManager = "transactionManager")
public class RootService {

    @Autowired
    public SqlSession sqlSession;

    @Value("${spring.profiles.active}")
    String activeSpringProfile = "local";


    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Value("${STORE.REDIS_PREFIX}")
    String REDIS_PREFIX = "pplus-";

    @Value("${STORE.SESSION_TIMEOUT}")
    Integer SESSION_TIMEOUT = 1800;

    @Value("${STORE.REFRESH_KEY_TIMEOUT}")
    Integer REFRESH_KEY_TIMEOUT = 1800;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @PostConstruct
    public void init() {
        System.out.println("service from @service");
    }

    public void registRefreshKey(Session session) {

        String key = REDIS_PREFIX + "refresh-key" + session.getNo();

        String refreshKey = StoreUtil.getRandomKeyByUUID();
        session.setRefreshKey(refreshKey);
        RedisUtil.getInstance().putOpsHash(key, "refreshKey", refreshKey);
        RedisUtil.getInstance().hashExpire(key, REFRESH_KEY_TIMEOUT, TimeUnit.DAYS);

    }

    public String getRefreshKey(String memberSeqNo) {
        String key = REDIS_PREFIX + "refresh-key" + memberSeqNo;

        return RedisUtil.getInstance().getOpsHash(key, "refreshKey");
    }

    public Integer registSession(Session session) {


        if (StringUtils.isEmpty(session.getSessionKey())) {
            String sessionKey = session.getNo() + "-" + StoreUtil.getRandomKeyByUUID();
            session.setSessionKey(SecureUtil.encryptMobileNumber(sessionKey));
        }

        String key = REDIS_PREFIX + session.getSessionKey();
        RedisUtil.getInstance().putOpsHash(key, "session", session);
        if (session.getProperties() == null || !session.getProperties().containsKey("sessionTimeout"))
            RedisUtil.getInstance().hashExpire(key, SESSION_TIMEOUT, TimeUnit.MINUTES);
        else
            RedisUtil.getInstance().hashExpire(key, (Integer) session.getProperties().get("sessionTimeout"), TimeUnit.MINUTES);

        return Const.E_SUCCESS;
    }

    public void reloadSession(Session session) {
        if (!StringUtils.isEmpty(session.getSessionKey())) {
            String key = REDIS_PREFIX + session.getSessionKey();
            RedisUtil.getInstance().putOpsHash(key, "session", session);
            if (session.getProperties() == null || !session.getProperties().containsKey("sessionTimeout"))
                RedisUtil.getInstance().hashExpire(key, SESSION_TIMEOUT, TimeUnit.MINUTES);
            else
                RedisUtil.getInstance().hashExpire(key, (Integer) session.getProperties().get("sessionTimeout"), TimeUnit.MINUTES);
        }
    }

    public Session getSession(String sessionKey) throws SessionNotFoundException {
        String key = REDIS_PREFIX + sessionKey;
        Session session = null;
        try {
            session = (Session) RedisUtil.getInstance().getOpsHash(key, "session");
        } catch (Exception e) {
            key = "pplus-pms-" + sessionKey;
            session = (Session) RedisUtil.getInstance().getOpsHash(key, "session");
        }


        if (session == null)
            throw new SessionNotFoundException();

        if (session.getProperties() == null || !session.getProperties().containsKey("sessionTimeout"))
            RedisUtil.getInstance().hashExpire(key, SESSION_TIMEOUT, TimeUnit.MINUTES);
        else
            RedisUtil.getInstance().hashExpire(key, (Integer) session.getProperties().get("sessionTimeout"), TimeUnit.MINUTES);
        return session;
    }

    public Pageable nativePageable(HttpServletRequest request, Pageable pageable, Map<String, String> sortMap) {


        return new Pageable() {
            @Override
            public int getPageNumber() {
                return pageable.getPageNumber();
            }

            @Override
            public int getPageSize() {
                return pageable.getPageSize();
            }

            @Override
            public long getOffset() {
                return pageable.getOffset();
            }

            @Override
            public Sort getSort() {

                String sort = request.getParameter("sort");
                if (sort == null && sortMap.get("#SORT#") != null) {
                    sort = sortMap.get("#SORT#");
                }
                Sort.Direction dir = Sort.Direction.DESC;
                if (sort != null && sort.toLowerCase().trim().endsWith(",asc")) {
                    dir = Sort.Direction.ASC;
                }


                if (sort == null || sort.trim().isEmpty()) {
                    sort = "seq_no";

                    return Sort.by(dir, sort);
                } else {
                    sort = sort.replaceAll("seqNo", "seq_no")
                            .replaceAll("Datetime", "_datetime")
                            .replaceAll(",\\s*asc", "")
                            .replaceAll(",\\s*ASC", "")
                            .replaceAll(",\\s*desc", "")
                            .replaceAll(",\\s*DESC", "");

                    Set<Map.Entry<String, String>> entrySet = sortMap.entrySet();
                    for (Map.Entry<String, String> entry : sortMap.entrySet()) {

                        String key = entry.getKey();
                        if (key.equals("#SORT#")) continue;
                        String value = entry.getValue();
                        sort = sort.replaceAll(key, value);
                    }

                    String[] orders = sort.split(",");

                    return Sort.by(dir, orders);
                }
            }

            @Override
            public Pageable next() {
                return pageable.next();
            }

            @Override
            public Pageable previousOrFirst() {
                return pageable.previousOrFirst();
            }

            @Override
            public Pageable first() {
                return pageable.first();
            }

            @Override
            public boolean hasPrevious() {
                return pageable.hasPrevious();
            }
        };
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}

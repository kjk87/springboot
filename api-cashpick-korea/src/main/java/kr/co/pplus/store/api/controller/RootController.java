package kr.co.pplus.store.api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.co.pplus.store.api.jpa.controller.BootPayApi;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Cancel;
import kr.co.pplus.store.api.jpa.model.type.OpenDayType;
import kr.co.pplus.store.api.jpa.repository.PageClosedRepository;
import kr.co.pplus.store.api.jpa.repository.PageOpentimeRepository;
import kr.co.pplus.store.api.util.AppUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import kr.co.pplus.store.api.util.ResultBuilder;

import javax.servlet.http.HttpServletRequest;

@Controller
public abstract class RootController {

	private static final Logger logger = LoggerFactory.getLogger(RootController.class);

	public static final String baseUri = "/api" ;
	public static final String ADMIN_TOKEN = "5v8y/B?E(G+KbPeShVmYq3t6w9z$C&F)";


	public static Map<String, Integer> dayOfWeek = new HashMap<String, Integer>() ;

	static {
		dayOfWeek.put("mon",1) ;
		dayOfWeek.put("tue",2) ;
		dayOfWeek.put("wed",3) ;
		dayOfWeek.put("thu",4) ;
		dayOfWeek.put("fri",5) ;
		dayOfWeek.put("sat",6) ;
		dayOfWeek.put("sun",7) ;
	}

	@Autowired
	PageOpentimeRepository pageOpentimeRepository ;

	@Autowired
	PageClosedRepository pageClosedRepository ;


	protected Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().create();

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

//	public static boolean bootPayCancel(Map<String, String>map, String reason){
//		try {
//			bootPayApi.getAccessToken(false);
//			HttpResponse res = bootPayApi.verify((String)map.get("receipt_id")) ;
//			String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8") ;
//			logger.debug("bootPayCancel : receipt data : " + str) ;
//
//			Cancel cancel = new Cancel();
//			cancel.receipt_id =  (String)map.get("receipt_id") ;
//			cancel.cancel_username = (String)map.get("name") ;
//			cancel.cancel_message = reason ;
//			cancel.cancel_price = Integer.parseInt(map.get("price")) ;
//
//			res = bootPayApi.cancel(cancel);
//			str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
//			logger.debug("bootPayCancel Cancel : response data : " + str);
//
///*
//            SubscribeBilling subscribeBilling = new SubscribeBilling();
//            subscribeBilling.billing_key = "???" ;
//            subscribeBilling.item_name = map.get("name") ;
//            subscribeBilling.price = Integer.parseInt(map.get("price"));
//            subscribeBilling.order_id = map.get("order_id") ;
//
//
//             res = api.subscribe_billing(subscribeBilling);
//             str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
//            logger.debug("bootPayCancel  SubscribeBilling: response data : " + str);
//            logger.debug("bootPayCancel  SubscribeBilling: response data : " + new Gson().toJson(subscribeBilling));
//            */
//			return true ;
//		}
//		catch(Exception e){
//			logger.error("bootPayCancel Error : " + AppUtil.excetionToString(e)) ;
//			return false ;
//		}
//	}

	public boolean checkOpenTime(PageOpentime pot, String curTime) {
		//ToDo 영업 시간 계산하기....
		if( pot.getNextDay() == false ) {
			if( pot.getStartTime().compareTo(curTime) <= 0 && curTime.compareTo(pot.getEndTime()) <= 0 ) {
				return true ;
			} else {
				return false ;
			}
		} else {
			if( pot.getStartTime().compareTo(curTime) <= 0 && curTime.compareTo("24:00:00") <= 0 ) {
				return true ;
			} else if( "00:00:00".compareTo(curTime) <= 0 && curTime.compareTo(pot.getEndTime()) <= 0 ) {
				return true ;
			} else {
				return false ;
			}
		}
	}

//	public boolean checkGoodsUseDuration(Goods goods, String curTime) {
//		if( goods != null && goods.getStartTime() != null && goods.getEndTime() != null ) {
//			logger.debug("isOpenTime() : 상품 영업시간 체크 " + goods.getStartTime() + "," + curTime + "," + goods.getEndTime()) ;
//
//			int startHour = Integer.valueOf(goods.getStartTime().split(":")[0]);
//			int startMin = Integer.valueOf(goods.getStartTime().split(":")[1]);
//
//			int endHour = Integer.valueOf(goods.getEndTime().split(":")[0]);
//			int endMin = Integer.valueOf(goods.getEndTime().split(":")[1]);
//
//			int currentHour = Integer.valueOf(curTime.split(":")[0]);
//			int currentMin = Integer.valueOf(curTime.split(":")[1]);
//
//			int current = currentHour*60+currentMin;
//			int start = startHour*60+startMin;
//			int end = endHour*60+endMin;
//
//			//종료 시간이 끝나는시간인지 체크
//			boolean endNextDay = false;
//			if(start >= end){
//				endNextDay = true;
//			}
//
//			//현재시간이 12시 지났는지 체크
//			boolean isNext = false;
//			if(current < start && current < end){
//				isNext = true;
//			}
//
//			if(endNextDay){
//				if(isNext){
//					return current <= end;
//				}else{
//					return start <= current;
//				}
//			}else{
//				return (start <= current) && (current <= end);
//			}
//		} else {
//			return true ;
//		}
//	}

//	public Boolean isOpenTime(Long pageSeqNo, Buy buy, Goods goods, Integer process) throws Exception{
//
//        logger.debug("isOpenTime() (pageSeqNo, buySeqNo) : " + pageSeqNo + "," + buy.getSeqNo()) ;
//		LocalDateTime currentDateTime = LocalDateTime.now();
//		Calendar calendar = Calendar.getInstance(TimeZone.getDefault()) ;
//
//		if( buy.getBookDatetime() != null && process != BuyProcess.USE.getProcess()) {
//			currentDateTime = LocalDateTime.parse(buy.getBookDatetime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) ;
//			calendar.setTime(Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant()));
//		}
//
//
//		int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH) ;
//		logger.debug("isOpenTime() weekOfMonth : " + weekOfMonth) ;
//
//		DayOfWeek wday = currentDateTime.getDayOfWeek() ;
//		String time = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm")) ;
//        logger.debug("isOpenTime() time : " + time) ;
//
//        if( !checkGoodsUseDuration(goods, time) ) {
//        	return false ;
//		}
//
//
//		// 매주 쉬는 요일이 있을 경우 체크
//		List<PageClosed> closedList = pageClosedRepository.findAllByPageSeqNo(pageSeqNo) ;
//		if( closedList != null && closedList.size() > 0 ) {
//			for (PageClosed pageClosed : closedList) {
//				if( (pageClosed.getEveryWeek() == 0 || pageClosed.getEveryWeek() == weekOfMonth) && dayOfWeek.get(pageClosed.getWeekDay()) == wday.getValue() ) {
//					return false ;
//				}
//			}
//		}
//
//		// 요일별 영업 시간 체크
//		// ToDo type(0,1,2)가 고정이 아니라 하나의 매장에서 여러개의 type opentime 이 존재할 수 있게 Client 에서 수정이 필요하다.
//		// ToDo 임시로 서버에서 Client 에 맞게 임시 적용
//		List<PageOpentime> list = pageOpentimeRepository.findAllByPageSeqNo(pageSeqNo) ;
//		if( list == null || list.size() == 0 )
//			return true ;
//
//		final int type = list.get(0).getType() ;
//		switch(type) {
//			case 0:
//
//				if( list.size() > 1 ) {
//					throw new Exception("주중 영업시간 고정 상점의  opentime 중복이 있습니다.") ;
//				}
//				PageOpentime weekPageOT = list.get(0) ;
//				logger.debug("isOpenTime() : 주중 영업시간 체크 " + weekPageOT.getStartTime() + "," + time + "," + weekPageOT.getEndTime()) ;
//				if( wday.getValue() < DayOfWeek.SATURDAY.getValue() ) {
//					return checkOpenTime(weekPageOT, time) ;
//				} else {
//					return false ;
//				}
//			case 1:
//				if( list.size() != 2 ){
//					throw new Exception("주중/주말 영업시간 분리된 상점의 opentime 이 유효하지 않습니다.") ;
//				}
//				weekPageOT = list.get(0) ;
//				PageOpentime weekEndPageOT = list.get(1) ;
//				if( wday.getValue() < DayOfWeek.SATURDAY.getValue() ) {
//					logger.debug("isOpenTime() : 주중/주말 영업시간 분리 체크 " + weekPageOT.getStartTime() + "," + time + "," + weekPageOT.getEndTime()) ;
//					return checkOpenTime(weekPageOT, time) ;
//				} else {
//					logger.debug("isOpenTime() : 주중/주말 영업시간 분리 체크 " + weekEndPageOT.getStartTime() + "," + time + "," + weekEndPageOT.getEndTime()) ;
//					return checkOpenTime(weekEndPageOT, time) ;
//				}
//			case 2:
//				if( list.size() != 7 ){
//					throw new Exception("요일별 상점 opentime 이 유효하지 않습니다.") ;
//				}
//				for(PageOpentime pageOT : list) {
//
//					if( wday.getValue() == dayOfWeek.get(pageOT.getWeekDay()) ) {
//						logger.debug("isOpenTime() : 요일별 영업시간 체크 " + pageOT.getStartTime() + "," + time + "," + pageOT.getEndTime()) ;
//						return checkOpenTime(pageOT, time);
//					}
//				}
//				return false ;
//			default:
//				return false ;
//
//		}
//
//		/* ToDO 장기적으로 아래 코드로 변경이 필요 ...
//		for(PageOpentime pageOT : list) {
//			if( pageOT.getType().intValue() == OpenDayType.WEEK_DAY.getType() && wday.getValue() < DayOfWeek.SATURDAY.getValue() ) {
//				return checkOpenTime(pageOT, time);
//			} else if( pageOT.getType().intValue() == OpenDayType.WEEK_END.getType() && wday.getValue() > DayOfWeek.FRIDAY.getValue() ) {
//				return checkOpenTime(pageOT, time);
//			} else if( pageOT.getType().intValue() == OpenDayType.EACH_DAY.getType() && wday.getValue() == dayOfWeek.get(pageOT.getWeekDay()) ) {
//				return checkOpenTime(pageOT, time);
//			}
//		}
//		*/
//
//	}

	public String getUri(HttpServletRequest request) {
		return "[" + request.getMethod() + "]" + request.getRequestURI() + " : ";
	}

	public Pageable nativePageable(HttpServletRequest request, Pageable pageable, Map<String,String> sortMap) {


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

				String sort = request.getParameter("sort") ;
				logger.debug("nativePageable() : sort : " + sort + " :#SORT# : " + sortMap.get("#SORT#")) ;
				if( sort == null && sortMap.get("#SORT#") != null ) {
					sort = sortMap.get("#SORT#") ;
				}
				logger.debug(getUri(request) + sort) ;
				Sort.Direction dir = Sort.Direction.DESC;
				if ( sort != null && sort.toLowerCase().trim().endsWith(",asc")) {
					dir = Sort.Direction.ASC;
				}


				if( sort == null || sort.trim().isEmpty() ){
					sort = "seq_no" ;

					return Sort.by(dir, sort) ;
				}
				else {
					sort = sort.replaceAll("seqNo", "seq_no")
							.replaceAll("Datetime", "_datetime")
							.replaceAll(",\\s*asc", "")
							.replaceAll(",\\s*ASC", "")
							.replaceAll(",\\s*desc", "")
							.replaceAll(",\\s*DESC", "") ;

					Set<Map.Entry<String, String>> entrySet = sortMap.entrySet() ;
					for(Map.Entry<String, String> entry : sortMap.entrySet() ){

						String key = entry.getKey() ;
						if( key.equals("#SORT#")) continue ;
						String value = entry.getValue() ;
						sort = sort.replaceAll(key, value) ;
					}

					String[] orders = sort.split(",") ;

					return Sort.by(dir, orders) ;
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

	@InitBinder
	protected void dateBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
		binder.registerCustomEditor(Date.class, editor);
	}
	
	protected Map<String,Object> about() {
		Map<String, Object> about = new HashMap<>();
		about.put("Company", "PPlus");
		about.put("Service", "PPlus");
		return about;
	}
	
	protected Map<String,Object> result(Integer resultCode) {
		return ResultBuilder.build(resultCode);
	}
	
	protected Map<String,Object> result(Integer resultCode, Map<String, Object> map) {
		return ResultBuilder.build(resultCode, map);
	}
	
	protected Map<String,Object> result(Integer resultCode, Object...args) {
		return ResultBuilder.build(resultCode, args);
	}

}

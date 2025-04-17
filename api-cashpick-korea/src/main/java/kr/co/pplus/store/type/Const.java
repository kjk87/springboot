package kr.co.pplus.store.type;

public class Const {
	public static final Integer E_SUCCESS = 200;
	public static final Integer E_MINERRBOUND = 500;
	public static final Integer E_UNKNOWN = 501;
	public static final Integer E_INVALID_ARG = 502;
	public static final Integer E_NOTFOUND = 503;
	public static final Integer E_ALREADYEXISTS = 504;
	public static final Integer E_NOTPERMISSION = 505;
	public static final Integer E_NOTIMPLEMENT = 509;
	public static final Integer E_EXPIREDEXCEPTION = 510;
	public static final Integer E_NOTPOSSIBLECHANGESTATUS = 511;
	public static final Integer E_SAMESTATUS = 512;
	public static final Integer E_NOTPOSSIBLEDELETE = 513;
	public static final Integer E_NOTMATCHED_VALUE = 514;
	public static final Integer E_NOTPOSSIBLEVALUE = 515;
	public static final Integer E_NOTPOSSIBLETIME = 516;
	public static final Integer E_LACK_COST = 517;
	public static final Integer E_NOT_JOIN_TIME = 518;

	public static final Integer E_SESSIONEXPIRED = 560;
	public static final Integer E_REFRESH_KEY_EXPIRED = 561;
	public static final Integer E_GUESTDENY = 570;
	
	public static final Integer E_NOTMATCHEDPWD = 580;
	public static final Integer E_NOTMATCHEDUSER = 581;
	public static final Integer E_UNKNOWNRECOMMEND = 582;
	public static final Integer E_DUPL_LOGINID = 583;
	public static final Integer E_DUPL_NICKNAME = 584;
	public static final Integer E_MAX_PASSWORD_FAILED = 585;
	public static final Integer E_NOTMATCH_VERIFICATION = 586;
	public static final Integer E_NEED_COMPULSORY_TERMS = 587;
	public static final Integer E_DUPL_MOBILE = 588;
	public static final Integer E_NEED_APP_UPDATE = 589;
	public static final Integer E_INVALID_SNS_VERIFICATION = 590;
	public static final Integer E_NOTMATCHED_MOBILE = 591;
	public static final Integer E_NOTMATCHED_NAME = 592;
	public static final Integer E_NOTALLOWED_CHAR = 593;
	
	
	public static final Integer E_INVALID_PAGE = 600;
	public static final Integer E_IMPOSSIBLE_NUMBER_LENGTH = 610;
	public static final Integer E_RESERVED_NUMBER = 611;
	public static final Integer E_INVALID_SHORTURL = 612;
	
	public static final Integer E_NOTENOUGH_CASH = 621;
	public static final Integer E_NOTMATCHED_CASH = 622;
	public static final Integer E_NOTENOUGH_BOL = 623;
	public static final Integer E_NOTMATCHED_BOL = 624;

	public static final Integer E_NOTMATCHED_PRICE = 626;
	
	public static final Integer E_PAYMENT_FAIL = 631;
	public static final Integer E_PAYMENT_VALIDATION_FAIL = 632;
	public static final Integer E_CANCEL_FAIL = 633;


	public static final Integer E_NOTPERMISSIONFOROFFER = 651;
	
	public static final Integer E_ALREADY_JOIN = 661;
	public static final Integer E_ALREADY_LIMIT = 662;
	public static final Integer E_ALREADY_USER_LIMIT = 663;

	public static final Integer E_ALREADY_USE = 671;
	
	public static final Integer E_NOT_YET_ELAPSE_TIME = 681;

	public static final Integer E_INVALID_CART = 690;
	public static final Integer E_INVALID_GOODS = 691;
	public static final Integer E_INVALID_GOODS_IMAGE = 692;
	public static final Integer E_INVALID_GOODS_REVIEW = 693;
	public static final Integer E_INVALID_BUY = 694;
	public static final Integer E_INVALID_BUY_GOODS = 695;
	public static final Integer E_INVALID_BUY_GOODS_PROCESS = 696;
	public static final Integer E_INVALID_BUY_GOODS_LIKE = 697;
	public static final Integer E_INVALID_GOODS_DELETE = 698;
	public static final Integer E_INVALID_PAGE_SELLER = 699;
	public static final Integer E_INVALID_PAGE_GOODS_CATEGORY = 700;
	public static final Integer E_INVALID_GOODS_CATEGORY = 701;
	public static final Integer E_REMOVE_BASIC_GOODS_CATEGORY = 702;
	public static final Integer E_INVALID_PAGE_GOODS_INFO = 703;
	public static final Integer E_LPNG_CANCEL_PERIOD_EXPIRED = 704;
	public static final Integer E_INVALID_CARD = 705;
	public static final Integer E_INVALID_CASH_CHARGE = 706;
	public static final Integer E_INVALID_SHIPPING_SITE = 707;
	public static final Integer E_INVALID_EVENT_REVIEW = 708;
	public static final Integer E_INVALID_PRODUCT = 709;
	public static final Integer E_INVALID_POINT_CANCEL = 710;

	public static final Integer E_INVALID_CASH_EXCAHNGE = 730 ;

	public static final Integer E_INVALID_DELIVERY = 740;
	public static final Integer E_STORE_IS_CLOSED = 741;

	public static final Integer E_GIFTISHOW_ERROR = 801;


	public static final Integer E_INVALID_SQL = 998;
	public static final Integer E_INVALID_OAUTH = 999;
	
	public static final String VALUE_UNDEFINED = "[Undefined]";
	
	public static final int MKT_MSG_INDEX = 0;
	public static final int MKT_EMAIL_INDEX = 1;
	public static final int MKT_PUSH_INDEX = 2;
	public static final int MKT_POST_INDEX = 3;
	
	public static final Long BOUND_BOARD = 10000L;
	public static final Long BOUND_ADMIN = 10000L;
	public static final Long BOUND_ATTACHMENT = 1000000L;
	public static final Long BOUND_DEFAULTIMAGE = 1000L;
	public static final Long BOUND_USER = 1000000L;
	public static final Long BOUND_COOPERATION = 10000L;
	public static final Long BOUND_PAGE = 1000000L;
	public static final Long BOUND_DEVICE = 100000L;
	public static final Long BOUND_COUPON_TEMPLATE = 100000L;
	public static final Long BOUND_COUPON = 10000L;
	public static final Long BOUND_PAGE_CATEGORY = 1000L;
	public static final Long BOUND_ARTICLE = 1000000L;
	public static final Long BOUND_REPORT = 10000L;
	public static final Long BOUND_PRODUCT = 10000000L;
	
	public static final int USER_PUSH_SENDPUSH = 1;
	public static final int USER_PUSH_PROMOTION = 2;
	public static final int USER_PUSH_PAGE = 3;
	public static final int USER_PUSH_YESTERDAYBOL = 4;
	public static final int USER_PUSH_RECVBOL = 5;
	public static final int USER_PUSH_NOTE = 6;
	public static final int USER_PUSH_OFFERRES = 7;
	public static final int USER_PUSH_ADARTICLE = 8;
	public static final int USER_PUSH_ADCOUPON = 9;
	public static final int USER_PUSH_EVENT = 10;
	
	public static final int BIZ_PUSH_SENDPUSH = 1;
	public static final int BIZ_PUSH_REVIEW = 2;
	public static final int BIZ_PUSH_COMMENT = 3;
	public static final int BIZ_PUSH_SHAREPAGE = 4;
	public static final int BIZ_PUSH_SHARTPOST = 5;
	public static final int BIZ_PUSH_PLUSME = 6;
	public static final int BIZ_PUSH_ZEROCASH = 7;
	public static final int BIZ_PUSH_NOTE = 8;
	public static final int BIZ_PUSH_OFFER = 9;
	public static final int BIZ_PUSH_OFFER_RES = 10;
	public static final int BIZ_PUSH_EXPIRE_TARGETFEED = 11;
	
	public static final int PUSH_FORCED = 99;
	
	public static final String MSG_PAY_TYPE_NONE = "none";
	public static final String MSG_PAY_TYPE_PAY = "pay";
	
	public static final int PAGE_LEVEL_NORMAL = 1;
	public static final int PAGE_LEVEL_PRO = 100;
	
	public static final String APP_TYPE_USER = "user";
	public static final String APP_TYPE_BIZ = "biz";
	public static final String APP_TYPE_LUCKYBOL = "luckyball";
	public static final String APP_TYPE_LUCKYPICK = "luckypick";
	public static final String APP_TYPE_ORDER = "order";

	public static final String ARTICLE_TYPE_REVIEW = "review";
	public static final String ARTICLE_TYPE_PR = "pr";

	public static final String MSG_INPUT_SYSTEM = "system" ;

	public static final String MSG_STATUS_READY = "ready" ;

	public static final String MSG_TYPE_PUSH = "push" ;

	public static final Integer ADS_COST = 300;
	public static final Float ADS_RECOMMEND_PROFIT = 100f;
	public static final Integer SUBSCRIPTION_ADS_COST = 300;
	public static final Integer SUBSCRIPTION_PUBLISH_ADS_COST = 1000;

	public static final String MOVE_TYPE_EVENTLIST = "eventList";
	public static final String MOVE_TYPE_EVENTDETAIL = "eventDetail";
	public static final String MOVE_TYPE_PAGEDETAIL = "pageDetail";
	public static final String MOVE_TYPE_POSTDETAIL = "postDetail";
	public static final String MOVE_TYPE_SEARCHMAIN = "searchMain";
	public static final String MOVE_TYPE_PLUSMAIN = "plusMain";
	public static final String MOVE_TYPE_MYPAGEMAIN = "mypageMain";
	public static final String MOVE_TYPE_CONTACTMAIN = "contactListMain";
	public static final String MOVE_TYPE_NOTICEDETAIL = "noticeDetail";
	public static final String MOVE_TYPE_KEYWORDSEARCHRESULT = "keywordSearchResult";
	public static final String MOVE_TYPE_HOME = "home";
	public static final String MOVE_TYPE_SHARE = "share";
	public static final String MOVE_TYPE_SHARE_MY_PAGE = "share_my_page";
	public static final String MOVE_TYPE_PUSH_STORE = "pushStore";
	public static final String MOVE_TYPE_REALTIME_EVENT = "realTimeEvent";
	public static final String MOVE_TYPE_INNER = "inner";
	public static final String MOVE_TYPE_CREATE_VIRTUALNUMBER = "prNumberCreate";
	public static final String MOVE_TYPE_THEME_MAIN = "themeMain";
	public static final String MOVE_TYPE_THEME_LIST = "themeList";
	public static final String MOVE_TYPE_COUPONDETAIL = "couponDetail";
	public static final String MOVE_TYPE_OFFERDETAIL = "offerDetail";
	public static final String MOVE_TYPE_CASHHISTORY = "cashHistory";
	public static final String MOVE_TYPE_CASHDETAIL = "cashDetail";
	public static final String MOVE_TYPE_BOLHISTORY = "bolHistory";
	public static final String MOVE_TYPE_BOLDETAIL = "bolDetail";
	public static final String MOVE_TYPE_ADARTICLE = "adPostDetail";
	public static final String MOVE_TYPE_ADCOUPON = "adCouponDetail";
	public static final String MOVE_TYPE_BUY = "buy" ;
	public static final String MOVE_TYPE_BUY_QR = "buyQr" ;
	public static final String MOVE_TYPE_BUY_SHIPPING = "buyShipping" ;
	public static final String MOVE_TYPE_CANCEL_SHIPPING = "cancelShipping" ;
	public static final String MOVE_TYPE_BUYGOODS_PAY = "buyGoodsPay" ;
	public static final String MOVE_TYPE_BUYGOODS_USE = "buyGoodsUse" ;
	public static final String MOVE_TYPE_BUYGOODS_USER_CANCEL = "buyGoodsUserCancel" ;
	public static final String MOVE_TYPE_BUYGOODS_BIZ_CANCEL = "buyGoodsBizCancel" ;
	public static final String MOVE_TYPE_BUYGOODS_REFUND = "buyGoodsRefund" ;
	public static final String MOVE_TYPE_BUYGOODS_EXPIRED = "buyGoodsExpired" ;
	public static final String MOVE_TYPE_BUYGOODS_USE_WAIT = "buyGoodsUseWait" ;
	public static final String MOVE_TYPE_BUYGOODS_USER_CANCEL_WAIT = "buyGoodsUserCancelWait" ;
	public static final String MOVE_TYPE_BUYGOODS_BIZ_CANCEL_WAIT = "buyGoodsBizCancelWait" ;
	public static final String MOVE_TYPE_BUYGOODS_REFUND_WAIT = "buyGoodsRefundWait" ;
	public static final String MOVE_TYPE_BUYGOODS_EXPIRE_WAIT = "buyGoodsExpireWait" ;
	public static final String MOVE_TYPE_ORDER = "order" ;
	public static final String MOVE_TYPE_ORDER_CONFIRM = "orderConfirm" ;
	public static final String MOVE_TYPE_ORDER_CANCEL = "orderCancel" ;
	public static final String MOVE_TYPE_PAY_COMPLETE = "payComplete";
	public static final String MOVE_TYPE_BUY_USE = "buyUse";
	public static final String MOVE_TYPE_SUBSCRIPTION_DETAIL = "subscriptionDetail";
	public static final String MOVE_TYPE_PREPAYMENT_DETAIL = "prepaymentDetail";
	public static final String MOVE_TYPE_PREPAYMENT_USE = "prepaymentUse";
	public static final String MOVE_TYPE_SUBSCRIPTION_USE = "subscriptionUse";

	public static final String COUPON_DISCOUNT_PERCENT = "percent";
	public static final String COUPON_DISCOUNT_COST = "cost";
	
	public static final Long GUEST_USER_NO = 90L;
}

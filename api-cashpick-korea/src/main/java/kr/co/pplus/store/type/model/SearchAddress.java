package kr.co.pplus.store.type.model;

import lombok.Data;

import java.util.List;

@Data
public class SearchAddress extends AbstractModel {


	private Common common;
	private List<SearchAddressJuso> juso;

	@Data
	public static class Common{

		private String errorMessage;
		private String countPerPage;
		private Integer totalCount;
		private Integer errorCode;
		private Integer currentPage;
	}

	@Data
	public static class SearchAddressJuso{
		private String detBdNmList;
		private String engAddr;
		private String rn;
		private String emdNm;
		private String zipNo;
		private String roadAddrPart2;
		private String emdNo;
		private String sggNm;
		private String jibunAddr;
		private String siNm;
		private String roadAddrPart1;
		private String bdNm;
		private String admCd;
		private String udrtYn;
		private String lnbrMnnm;
		private String roadAddr;
		private String lnbrSlno;
		private String buldMnnm;
		private String bdKdcd;
		private String liNm;
		private String rnMgtSn;
		private String mtYn;
		private String bdMgtSn;
		private String buldSlno;
	}
}

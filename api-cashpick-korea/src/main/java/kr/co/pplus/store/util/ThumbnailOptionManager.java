package kr.co.pplus.store.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.pplus.store.type.model.ThumbnailOption;
import kr.co.pplus.store.type.model.code.AttachTargetType;
import net.coobird.thumbnailator.geometry.Positions;

public abstract class ThumbnailOptionManager {

	private static final Map<AttachTargetType, List<ThumbnailOption>> map = new HashMap<AttachTargetType, List<ThumbnailOption>>();

	static {
		List<ThumbnailOption> list = new ArrayList<ThumbnailOption>();
		list.add(new ThumbnailOption(324, 324, Positions.CENTER));
		map.put(AttachTargetType.memberProfile, list);
		list = new ArrayList<ThumbnailOption>();
		list.add(new ThumbnailOption(324, 324, Positions.CENTER));
		map.put(AttachTargetType.pageProfile, list);
	}

	public static List<ThumbnailOption> get(String targetType, int width, int height) {

		List<ThumbnailOption> list = null;
		if (targetType.equals("pageBackground")) {
			list = new ArrayList<>();
			list.add(createResizeThumnailOption(width, height));
			list.add(new ThumbnailOption(324, 324));
		} else if (targetType.equals("postList") || targetType.equals("goods")) { //MGK_CHG
			list = new ArrayList<>();

			if (width > height) {
				list.add(new ThumbnailOption(672, 378, Positions.TOP_CENTER));
			} else {
				ThumbnailOption option = createResizeByWidthThumnailOption(width, height, 672, 1194);
				list.add(option);
			}

			list.add(new ThumbnailOption(332, 332, Positions.CENTER));
			list.add(new ThumbnailOption(344, 258, Positions.TOP_CENTER));
			list.add(createResizeThumnailOption(width, height));
		} else {
			list = map.get(targetType);
		}
		return list;
	}

	// private static final int MAX_WIDTH = 1080;
	// private static final int MAX_HEIGHT = 1920;

	public static final int MAX_WIDTH = 720;
	public static final int MAX_HEIGHT = 1280;

	private static ThumbnailOption createResizeThumnailOption(int width, int height) {
		return createResizeThumnailOption(width, height, MAX_WIDTH, MAX_HEIGHT);
	}

	private static ThumbnailOption createResizeThumnailOption(int width, int height, int maxWidth, int maxHeight) {

		int newWidth = width;
		int newHeight = height;

		if (width == 0 || height == 0) {
			return new ThumbnailOption(newWidth, newHeight);
		}

		if (maxWidth < width || maxHeight < height) {
			// 이미지의 가로 세로 비율에 맞게 조절
			if (width > height) {
				// 가로가 크다
				newHeight = height * maxWidth / width;
				newWidth = maxWidth;
			} else {
				// 세로가 크다
				newWidth = width * maxHeight / height;
				newHeight = maxHeight;
			}
		} else {

			if (width < height) {
				newWidth = maxWidth;
				newHeight = height * maxWidth / width;

			} else {
				// 세로가 작다.
				newHeight = maxHeight;
				newWidth = width * maxHeight / height;
			}
		}

		return new ThumbnailOption(newWidth, newHeight);
	}

	private static ThumbnailOption createResizeByWidthThumnailOption(int width, int height, int maxWidth, int maxHeight) {

		if (width == 0 || height == 0) {
			return new ThumbnailOption(width, height);
		}

		int newWidth = maxWidth;
		int newHeight = newWidth * height / width;
		
		if (newHeight > maxHeight)
			return new ThumbnailOption(maxWidth, maxHeight, Positions.TOP_CENTER);
		else
			return new ThumbnailOption(newWidth, newHeight);
	}

}
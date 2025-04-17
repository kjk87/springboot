package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbnailOption {

	private int width;

	private int height;

	private Position position;

	public ThumbnailOption(boolean adjust) {

	}

	public ThumbnailOption(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	public ThumbnailOption(int width, int height, Position position) {
		super();
		this.width = width;
		this.height = height;
		this.position = position;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
}
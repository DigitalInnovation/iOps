package org.ht.iops.rest.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Attachment {
	private String fallback;
	private String pretext;
	private String title;
	private String title_link;
	private String text;
	private String color;
	private List<Fields> fields;

	public Attachment() {
		fields = new ArrayList<>();
	}

	/**
	 * @return the fallback
	 */
	public String getFallback() {
		return fallback;
	}

	/**
	 * @param fallback
	 *            the fallback to set
	 */
	public void setFallback(String fallback) {
		this.fallback = fallback;
	}

	/**
	 * @return the pretext
	 */
	public String getPretext() {
		return pretext;
	}

	/**
	 * @param pretext
	 *            the pretext to set
	 */
	public void setPretext(String pretext) {
		this.pretext = pretext;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the title_link
	 */
	public String getTitle_link() {
		return title_link;
	}

	/**
	 * @param title_link
	 *            the title_link to set
	 */
	public void setTitle_link(String title_link) {
		this.title_link = title_link;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	public List<Fields> getFields() {
		return fields;
	}

	public void addField(Fields fields) {
		this.fields.add(fields);
	}

	@Override
	public String toString() {
		return "Attachment [fallback=" + fallback + ", pretext=" + pretext
				+ ", title=" + title + ", title_link=" + title_link + ", text="
				+ text + ", color=" + color + ", fields=" + fields + "]";
	}
}
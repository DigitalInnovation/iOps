package org.ht.iops.rest.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Fields {
	private String title;
	private String value;
	@JsonProperty("short")
	private boolean shortText;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isShortText() {
		return shortText;
	}

	public void setShortText(boolean shortText) {
		this.shortText = shortText;
	}

	@Override
	public String toString() {
		return "Fields [title=" + title + ", value=" + value + ", shortText="
				+ shortText + "]";
	}
}

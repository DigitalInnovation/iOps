package org.ht.iops.events;

import org.springframework.context.ApplicationEvent;

public class AppEvent<T> extends ApplicationEvent {
	private static final long serialVersionUID = 6682448088322520546L;

	public AppEvent(T source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	public T getSource() {
		return (T) source;
	}
}

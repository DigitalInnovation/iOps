package org.ht.iops.events.adapters;

import org.ht.iops.events.AppEvent;
import org.ht.iops.events.EmailEvent;
import org.ht.iops.events.IOpsEmailEvent;
import org.ht.iops.events.IOpsEvent;

public interface RequestAdapter<Req, Res> {
	Req transformRequest(IOpsEvent iOpsEvent, String... additionalAttributes);
	Class<Res> getResponseClass();
	EmailEvent<IOpsEmailEvent> createEmailEvent(AppEvent<IOpsEvent> createEvent,
			Res response);
}

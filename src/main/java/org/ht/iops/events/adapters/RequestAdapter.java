package org.ht.iops.events.adapters;

import org.ht.iops.events.IOpsEvent;
import org.ht.iops.rest.response.RestResponse;

public interface RequestAdapter<Req, Res> {
	Req transformRequest(IOpsEvent iOpsEvent);
	RestResponse transformResponse(Res response);
	Class<Res> getResponseClass();
}

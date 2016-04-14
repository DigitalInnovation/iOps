package org.ht.iops.framework.mail.reader.instruction;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class InvalidInstructions extends OpsInstructions {
	@Override
	protected String getInstructionName() {
		return "Invalid Instructions";
	}

	@Override
	protected void sendRestRequest(List<String> subjectTokens,
			Map<String, String> bodyTokens) {
	}
}

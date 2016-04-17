package org.ht.iops.framework.mail.reader.instruction;

import java.util.List;
import java.util.Map;

import org.ht.iops.exception.ApplicationValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JiraInstructions extends OpsInstructions {
	@Override
	protected String getInstructionName() {
		return "Jira";
	}

	@Override
	protected void validateSubjectTokens(List<String> subjectTokens) {
		if (subjectTokens.size() < 3
				|| StringUtils.isEmpty(subjectTokens.get(2))) {
			throw new ApplicationValidationException(
					"Jira summary is a required field.", getInstructionName());
		}
	}

	@Override
	protected void validateBodyTokens(Map<String, String> bodyTokens) {
		String description = bodyTokens.get("description");
		String owner = bodyTokens.get("owner");
		if (StringUtils.isEmpty(description) || StringUtils.isEmpty(owner)) {
			throw new ApplicationValidationException(
					"Jira description and owner is a required field.",
					getInstructionName());
		}
	}

	@Override
	protected void addSubjectTokens(final Map<String, String> instructions,
			final List<String> subjectTokens) {
		instructions.put("summary", subjectTokens.get(2));
	}
}
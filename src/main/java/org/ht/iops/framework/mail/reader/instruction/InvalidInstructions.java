package org.ht.iops.framework.mail.reader.instruction;

import java.util.List;
import java.util.Map;

import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.events.publisher.EventPublisher;
import org.springframework.stereotype.Component;

@Component
public class InvalidInstructions extends OpsInstructions {
	public InvalidInstructions(final StatusRepository statusRepository,
			final EventPublisher eventPublisher) {
		super(statusRepository, eventPublisher);
	}

	@Override
	protected String getInstructionName() {
		return "Invalid Instructions";
	}

	@Override
	protected void validateSubjectTokens(List<String> subjectTokens) {
	}

	@Override
	protected void validateBodyTokens(Map<String, String> bodyTokens) {
	}

	@Override
	protected void addSubjectTokens(Map<String, String> instructions,
			List<String> subjectTokens) {
	}
}

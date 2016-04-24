package org.ht.iops.framework.mail.reader.instruction;

import java.util.List;
import java.util.Map;

import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.framework.mail.reader.MimeMessageReader;
import org.springframework.stereotype.Component;

@Component
public class InvalidInstructions extends OpsInstructions {
	public InvalidInstructions(final MimeMessageReader mimeMessageReader,
			final StatusRepository statusRepository,
			final AppConfigRepository appConfigRepository,
			final EventPublisher eventPublisher) {
		super(mimeMessageReader, statusRepository, appConfigRepository,
				eventPublisher);
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

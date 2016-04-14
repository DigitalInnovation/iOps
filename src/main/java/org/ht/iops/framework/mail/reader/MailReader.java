package org.ht.iops.framework.mail.reader;

import javax.mail.internet.MimeMessage;

import org.ht.iops.db.beans.Status;

public interface MailReader {
	public void readMail(final MimeMessage message);

	public Status saveStatus(final String... strings);
}

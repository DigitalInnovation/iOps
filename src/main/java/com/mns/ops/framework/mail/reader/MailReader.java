package com.mns.ops.framework.mail.reader;

import javax.mail.internet.MimeMessage;

import com.mns.ops.db.beans.Status;

public interface MailReader {
	public void readMail(final MimeMessage message);

	public Status saveStatus(final String... strings);
}

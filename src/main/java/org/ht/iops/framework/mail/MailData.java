package org.ht.iops.framework.mail;

import java.util.List;

import javax.activation.DataSource;
import javax.mail.Address;

import org.jsoup.nodes.Document;

public class MailData {

	/** Mail message from. **/
	private String messageFrom;

	/** Mail TO address. **/
	private List<Address> toAddresses;

	/** Mail CC address. **/
	private List<Address> ccAddresses;

	/** Mail BCC address. **/
	private List<Address> bccAddresses;

	/** Mail subject. **/
	private String subject;

	/** Mail plain content (for simple text/plain content). **/
	private String plainContent;

	/** Mail HTML content. **/
	private String htmlContent;

	/** Mail attachment list. **/
	private List<DataSource> attachments;

	/** {@link Document} from email HTML. **/
	private Document htmlDocument;

	public MailData() {
	}

	/**
	 * @return the messageFrom
	 */
	public String getMessageFrom() {
		return messageFrom;
	}

	/**
	 * @param messageFrom
	 *            the messageFrom to set
	 */
	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

	/**
	 * @return the toAddresses
	 */
	public List<Address> getToAddresses() {
		return toAddresses;
	}

	/**
	 * @param toAddresses
	 *            the toAddresses to set
	 */
	public void setToAddresses(List<Address> toAddresses) {
		this.toAddresses = toAddresses;
	}

	/**
	 * @return the ccAddresses
	 */
	public List<Address> getCcAddresses() {
		return ccAddresses;
	}

	/**
	 * @param ccAddresses
	 *            the ccAddresses to set
	 */
	public void setCcAddresses(List<Address> ccAddresses) {
		this.ccAddresses = ccAddresses;
	}

	/**
	 * @return the bccAddresses
	 */
	public List<Address> getBccAddresses() {
		return bccAddresses;
	}

	/**
	 * @param bccAddresses
	 *            the bccAddresses to set
	 */
	public void setBccAddresses(List<Address> bccAddresses) {
		this.bccAddresses = bccAddresses;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the plainContent
	 */
	public String getPlainContent() {
		return plainContent;
	}

	/**
	 * @param plainContent
	 *            the plainContent to set
	 */
	public void setPlainContent(String plainContent) {
		this.plainContent = plainContent;
	}

	/**
	 * @return the htmlContent
	 */
	public String getHtmlContent() {
		return htmlContent;
	}

	/**
	 * @param htmlContent
	 *            the htmlContent to set
	 */
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	/**
	 * @return the attachments
	 */
	public List<DataSource> getAttachments() {
		return attachments;
	}

	/**
	 * @param attachments
	 *            the attachments to set
	 */
	public void setAttachments(List<DataSource> attachments) {
		this.attachments = attachments;
	}

	/**
	 * @return the htmlDocument
	 */
	public Document getHtmlDocument() {
		return htmlDocument;
	}

	/**
	 * @param htmlDocument
	 *            the htmlDocument to set
	 */
	public void setHtmlDocument(Document htmlDocument) {
		this.htmlDocument = htmlDocument;
	}
}
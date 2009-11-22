package net.xytra.wobmail.export;

import er.javamail.ERMailAttachment;

public interface MailExportVisitor extends ExportVisitor
{
	/**
	 * @return an ERMailAttachment enclosing the underlying data.
	 */
	public ERMailAttachment getMailAttachment();
}

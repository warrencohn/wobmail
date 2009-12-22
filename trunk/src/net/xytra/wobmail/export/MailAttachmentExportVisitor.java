package net.xytra.wobmail.export;

import er.javamail.ERMailAttachment;
import er.javamail.ERMailDataAttachment;

public abstract class MailAttachmentExportVisitor implements MailExportVisitor
{
	public ERMailAttachment getMailAttachment() {
		return (new ERMailDataAttachment(getFileName(), null, getFileContent()));
	}

}

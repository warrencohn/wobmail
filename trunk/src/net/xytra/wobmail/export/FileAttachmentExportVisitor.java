package net.xytra.wobmail.export;

import com.webobjects.foundation.NSData;

public class FileAttachmentExportVisitor extends MailAttachmentExportVisitor
{
	private NSData fileData;
	private String fileName;
	private String fileMimeType;

	public FileAttachmentExportVisitor(NSData fileData, String fileName, String fileMimeType) {
		this.fileData = fileData;
		this.fileName = fileName;
		this.fileMimeType = fileMimeType;
	}

	public NSData getFileContent() {
		return (fileData);
	}

	public String getFileName() {
		return (fileName);
	}

	public String getFileType() {
		return (fileMimeType);
	}

}

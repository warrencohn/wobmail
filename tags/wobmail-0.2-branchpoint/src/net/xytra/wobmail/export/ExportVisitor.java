package net.xytra.wobmail.export;

import com.webobjects.foundation.NSData;

public interface ExportVisitor
{
	public NSData getFileContent();
	public String getFileName();
	public String getFileType();
}

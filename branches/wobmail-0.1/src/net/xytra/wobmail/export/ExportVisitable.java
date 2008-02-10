package net.xytra.wobmail.export;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import er.extensions.ERXNonSynchronizingComponent;

public abstract class ExportVisitable extends ERXNonSynchronizingComponent
{
	private ExportVisitor v;

	public ExportVisitable(WOContext context)
	{
		super(context);
	}

	public void accept(ExportVisitor v) {
		this.v = v;
	}

	protected ExportVisitor visitor()
	{
		return (this.v);
	}

	public void appendToResponse(WOResponse response, WOContext context)
	{
		if (visitor() == null)
			return;

		response.setHeader(visitor().getFileType(), "Content-Type");
		response.setHeader(filenameDesignator() + '"' + v.getFileName() + '"', "Content-Disposition");
		response.setContent(visitor().getFileContent());
	}

	protected abstract String filenameDesignator();

}

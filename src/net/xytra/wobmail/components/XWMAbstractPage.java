package net.xytra.wobmail.components;

import net.xytra.wobmail.application.Session;

import com.webobjects.appserver.WOContext;

import er.extensions.ERXNonSynchronizingComponent;

public class XWMAbstractPage extends ERXNonSynchronizingComponent
{
	public XWMAbstractPage(WOContext context) {
		super(context);
	}

	public String pageWrapperName() {
		return (XWMPageWrapper.class.getName());
	}

	/**
	 * @see com.webobjects.appserver.WOComponent#session()
	 * @return the session but as Session.
	 */
	@Override
	public Session session() {
		return ((Session)super.session());
	}

}

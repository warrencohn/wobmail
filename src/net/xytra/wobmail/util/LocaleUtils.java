package net.xytra.wobmail.util;

import java.util.Locale;

import er.extensions.ERXProperties;

public class LocaleUtils {
	protected static final String ERXLOCALIZER_DEFAULT_LANGUAGE = "en_CA";

	protected static final String ERXLOCALIZER_DEFAULT_LANGUAGE_PROPERTY = "er.extensions.ERXLocalizer.defaultLanguage";

	public static Locale defaultERXLocalizerLocale() {
		return (localeForLocaleName(defaultERXLocalizerLocaleName()));
	}

	public static String defaultERXLocalizerLocaleName() {
		return (ERXProperties.stringForKeyWithDefault(
				ERXLOCALIZER_DEFAULT_LANGUAGE_PROPERTY,
				ERXLOCALIZER_DEFAULT_LANGUAGE));
	}

	public static Locale localeForLocaleName(String name) {
		// TODO: Consider caching these
		String[] parts = name.split("_");
		Locale locale;

		if (parts.length == 0) {
			locale = Locale.getDefault();
		} else if (parts.length == 1) {
			locale = new Locale(parts[0]);
		} else if (parts.length == 2) {
			locale = new Locale(parts[0], parts[1]);
		} else if (parts.length == 3) {
			locale = new Locale(parts[0], parts[1], parts[2]);
		} else {
			throw (new IllegalArgumentException("Too many parts in locale name: " + name));
		}

		return (locale);
	}

}

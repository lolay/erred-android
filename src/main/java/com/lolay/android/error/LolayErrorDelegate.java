/*
 * Created by Lolay, Inc.
 * Copyright 2011 Lolay, Inc. All rights reserved.
 */
package com.lolay.android.error;

public interface LolayErrorDelegate {
	public void exceptionPresented(LolayErrorManager manager, LolayException exception);
	public String stringForKey(LolayErrorManager manager, String key);
	public String titleForException(LolayErrorManager manager, LolayException exception);
	public String messageForException(LolayErrorManager manager, LolayException exception);
	public String buttonTextForException(LolayErrorManager manager, LolayException exception);
	public String titleForCode(LolayErrorManager manager, int code);
	public String descriptionforCode(LolayErrorManager manager, int code);
	public String recoverySuggestionForCode(LolayErrorManager manager, int code);
	public int dialogViewForException(LolayErrorManager manager, LolayException exception);
}

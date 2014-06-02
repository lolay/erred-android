//
//  Copyright 2012, 2013 Lolay, Inc.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//
package com.lolay.android.error;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class LolayException extends Exception {
	private static final long serialVersionUID = 1L;
	private int code;
    private String title;
    private String description;
    private String recoverySuggestion;
    
    private static String formatMessage(int code, String title, String description, String recoverySuggestion) {
    	StringBuilder builder = new StringBuilder();
    	
    	builder.append(code);
    	
    	if (title != null) {
        	builder.append(": ");
        	builder.append(title);
    	}
    	
    	if (description != null) {
        	builder.append(": ");
        	builder.append(description);
    	}

    	if (description != null) {
        	builder.append(": ");
        	builder.append(recoverySuggestion);
    	}
    	
    	return builder.toString();
}
    
    public LolayException(int code, String title, String description) {
    	this(code, title, description, (String) null);
    }

    public LolayException(int code, String title, String description, Throwable cause) {
        this(code, title, description, (String) null, cause);
    }
    
    public LolayException(int code, String title, String description, String recoverySuggestion) {
    	super(formatMessage(code, title, description, null));
        this.code = code;
        this.title = title;
        this.description = description;
        this.recoverySuggestion = recoverySuggestion;
    }

    public LolayException(int code, String title, String description, String recoverySuggestion, Throwable cause) {
    	super(formatMessage(code, title, description, null), cause);
        this.code = code;
        this.title = title;
        this.description = description;
        this.recoverySuggestion = recoverySuggestion;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
    
    public String getRecoverySuggestion() {
    	return recoverySuggestion;
    }

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(code);
	}

	@Override
	public boolean equals(Object obj) {
	   return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
	   return ToStringBuilder.reflectionToString(this);
	}
}

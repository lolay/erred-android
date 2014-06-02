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

import java.lang.ref.WeakReference;

import com.lolay.android.log.LolayLog;

import android.app.Application;
import android.content.res.Resources;

public class LolayDefaultErrorDelegate implements LolayErrorDelegate {
	private static final String TAG = LolayLog.buildTag(LolayDefaultErrorDelegate.class);
    protected WeakReference<Application> applicationReference;

    public LolayDefaultErrorDelegate(Application application) {
        this.applicationReference = new WeakReference<Application>(application);
    }
    
	@Override
	public void exceptionPresented(LolayErrorManager manager, LolayException exception) { }
	
    protected int identifierForName(Application application, String name) {
        int identifier = 0;
        Resources resources = application.getResources();
        
        if (resources != null) {
            identifier = resources.getIdentifier(name, "string", application.getPackageName());
        }
        
        if (identifier == 0) {
        	LolayLog.w(TAG, "identifierForName", "Did not find resource identifier for resource named %s", name);
        }
        
        return identifier;
    }

	@Override
	public String stringForKey(LolayErrorManager manager, String key) {
		Application application = applicationReference.get();
		String string = null;
		if (application != null) {
	    	Resources resources = application.getResources();
	    	int id = identifierForName(application, key);
	    	
	        if (resources != null && id != 0) {
	            try {
	                string = resources.getString(id);
	            } catch (Resources.NotFoundException e) {
	                LolayLog.e(TAG, "stringForKey", "Did not find String with Android resource ID %d", id, e);
	            }
	        }
		}
        return string;
	}

	@Override
	public String titleForException(LolayErrorManager manager, LolayException exception) {
		String title = exception.getTitle();
		return title != null ? title : "Whoops";
	}

	@Override
	public String messageForException(LolayErrorManager manager, LolayException exception) {
		int code = exception.getCode();
		String description = exception.getDescription();
		String recoverySuggestion = exception.getRecoverySuggestion();
		
		StringBuilder builder = new StringBuilder();
		if (description != null) {
			builder.append(description);
		}
		
		if (description != null && recoverySuggestion != null) {
			builder.append("\n");
		}
		
		if (recoverySuggestion != null) {
			builder.append(recoverySuggestion);
		}
		
		if (description != null || recoverySuggestion != null) {
			builder.append("\n ");
		}
		
		builder.append("[");
		builder.append(code);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public String buttonTextForException(LolayErrorManager manager, LolayException exception) {
		return "Ok";
	}
	
	@Override
	public String titleForCode(LolayErrorManager manager, int code) {
	    return stringForCode(manager, code, "error_%d_title", true);
	}
	
	@Override
	public String descriptionforCode(LolayErrorManager manager, int code) {
	    return stringForCode(manager, code, "error_%d_description", true);
	}
	
	@Override
	public String recoverySuggestionForCode(LolayErrorManager manager, int code) {
	    return stringForCode(manager, code, "error_%d_recoverySuggestion", true);
	}
	
	protected String stringForCode(LolayErrorManager manager, int code, String format, boolean fallback) {
        String key = String.format(format, code);
        String string = stringForKey(manager, key);
        if (string == null && fallback) {
        	string = stringForCode(manager, 0, format, false);
        }
        return string;
	}

	@Override
	public int dialogViewForException(LolayErrorManager manager, LolayException exception) {
		return 0;
	}
}

/*
 * Created by Lolay, Inc.
 * Copyright 2011 Lolay, Inc. All rights reserved.
 */
package com.lolay.android.error;

import java.lang.ref.WeakReference;

import com.lolay.android.log.LolayLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LolayErrorManager {
	private static final String TAG = LolayLog.buildTag(LolayErrorManager.class);
	private static final String TITLE = "title";
	private static final String MESSAGE = "message";
	private static final String BUTTON = "button";
	private WeakReference<Application> applicationReference;
    private LolayErrorDelegate delegate = null;

    public LolayErrorManager(Application application) {
    	this(application, new LolayDefaultErrorDelegate(application));
    }
    
    public LolayErrorManager(Application application, LolayErrorDelegate delegate) {
    	this.applicationReference = new WeakReference<Application>(application);
        this.delegate = delegate;
    }

    public void presentExceptionToast(Exception exception) {
    	presentExceptionToast(null, exception);
    }

    public void presentExceptionToast(Activity activity, Exception exception) {
    	Application application = applicationReference.get();
    	if (application != null) {
        	LolayException lolayException;
        	if (exception instanceof LolayException) {
        		lolayException = (LolayException) exception;
        	} else {
        		LolayLog.v(TAG, "presentExceptionToast", "The exception wasn't a LolayException, creating as one");
        		lolayException = createException(0, exception);
        	}
    		
        	String title = delegate.titleForException(this, lolayException);
        	String message = delegate.messageForException(this, lolayException);
        	
        	Context context;
        	if (activity != null) {
        		context = activity;
        	} else {
        		LolayLog.v(TAG, "presentExceptionToast", "No activity, so defaulting to use the application as a context");
        		context = application;
        	}
        	
        	Toast toast = Toast.makeText(context, title + ":\n" + message, Toast.LENGTH_LONG);
        	toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        	toast.show();
        	
        	delegate.exceptionPresented(this, lolayException);
    	} else {
    		LolayLog.w(TAG, "presentExceptionToast", "The application reference is null, this shouldn't occur");
    	}
    }
    
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

	public View getLayoutById(Application application, int id) {
		LayoutInflater inflater = LayoutInflater.from(application.getBaseContext());
		return inflater.inflate(id, null, false);
	}
	
    public void presentExceptionDialog(Activity activity, Exception exception) {
    	Application application = applicationReference.get();
    	if (application != null) {
        	LolayException lolayException;
        	if (exception instanceof LolayException) {
        		lolayException = (LolayException) exception;
        	} else {
        		LolayLog.v(TAG, "presentExceptionDialog", "The exception wasn't a LolayException, creating as one");
        		lolayException = createException(0, exception);
        	}
        	
        	String title = delegate.titleForException(this, lolayException);
        	String message = delegate.messageForException(this, lolayException);
        	String buttonText = delegate.buttonTextForException(this, lolayException);
        	
        	int viewId = delegate.dialogViewForException(this, lolayException);
        	
        	if (viewId != 0) {
        		LolayLog.v(TAG, "presentExceptionDialog", "The viewId was %s so using a custom view", viewId);
            	View view = getLayoutById(application, viewId);
            	
            	TextView titleView = null;
        		int titleId = identifierForName(application, TITLE);
        		if (titleId != 0) {
            		titleView = (TextView) view.findViewById(titleId);
        		}
        		TextView messageView = null;
        		int messageId = identifierForName(application, MESSAGE);
        		if (messageId != 0) {
            		messageView = (TextView) view.findViewById(messageId);
        		}
        		Button button = null;
        		int buttonId = identifierForName(application, BUTTON);
        		if (buttonId != 0) {
        			button = (Button) view.findViewById(buttonId);
        		}
        		
        		if (titleView != null) {
                	titleView.setText(title);
        		}
        		
        		if (messageView != null) {
            		messageView.setText(message);
        		}
        		
        		if (button != null) {
            		button.setText(buttonText);
        		}
            	
        		final AlertDialog dialog = new AlertDialog.Builder(activity).setView(view).create();
        		
        		button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});
        	} else {
        		LolayLog.v(TAG, "presentExceptionDialog", "No custom view so using the default alert dialog");
        		new AlertDialog.Builder(activity).setTitle(title).setMessage(message).setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
        	}
        	
        	delegate.exceptionPresented(this, lolayException);
    	} else {
    		LolayLog.w(TAG, "presentExceptionDialog", "The application reference is null, this shouldn't occur");
    	}
    }
    
    public void presentErrorDialog(Activity activity, int code) {
    	presentExceptionDialog(activity, createException(code));
    }
    
    public void presentErrorDialog(Activity activity, int code, Throwable cause) {
    	presentExceptionDialog(activity, createException(code, cause));
    }
    
    public void presentErrorDialog(Activity activity, int code, String title, String description) {
    	presentExceptionDialog(activity, createException(code, title, description));
    }
    
    public void presentErrorDialog(Activity activity, int code, String title, String description, Throwable cause) {
    	presentExceptionDialog(activity, createException(code, title, description, cause));
    }
    
    public void presentErrorDialog(Activity activity, int code, String title, String description, String suggestion) {
    	presentExceptionDialog(activity, createException(code, title, description, suggestion));
    }
    
    public void presentErrorDialog(Activity activity, int code, String title, String description, String suggestion, Throwable cause) {
    	presentExceptionDialog(activity, createException(code, title, description, suggestion, cause));
    }
    
    public void presentErrorToast(int code) {
    	presentExceptionToast(createException(code));
    }
    
    public void presentErrorToast(int code, Throwable cause) {
    	presentExceptionToast(createException(code, cause));
    }
    
    public void presentErrorToast(int code, String title, String description) {
    	presentExceptionToast(createException(code, title, description));
    }
    
    public void presentErrorToast(int code, String title, String description, Throwable cause) {
    	presentExceptionToast(createException(code, title, description, cause));
    }
    
    public void presentErrorToast(int code, String title, String description, String suggestion) {
    	presentExceptionToast(createException(code, title, description, suggestion));
    }
    
    public void presentErrorToast(int code, String title, String description, String suggestion, Throwable cause) {
    	presentExceptionToast(createException(code, title, description, suggestion, cause));
    }
    
    public void presentErrorToast(Activity activity, int code) {
    	presentExceptionToast(activity, createException(code));
    }
    
    public void presentErrorToast(Activity activity, int code, Throwable cause) {
    	presentExceptionToast(activity, createException(code, cause));
    }
    
    public void presentErrorToast(Activity activity, int code, String title, String description) {
    	presentExceptionToast(activity, createException(code, title, description));
    }
    
    public void presentErrorToast(Activity activity, int code, String title, String description, Throwable cause) {
    	presentExceptionToast(activity, createException(code, title, description, cause));
    }
    
    public void presentErrorToast(Activity activity, int code, String title, String description, String suggestion) {
    	presentExceptionToast(activity, createException(code, title, description, suggestion));
    }
    
    public void presentErrorToast(Activity activity, int code, String title, String description, String suggestion, Throwable cause) {
    	presentExceptionToast(activity, createException(code, title, description, suggestion, cause));
    }
    
    /**
     * Creates {@link LolayException} given a code. Looks up
     * localized title and descriptions from Android resources
     * using the format of error_1000_title, error_1000_description,
     * error_1000_recoverySuggestion
     */
    public LolayException createException(int code) {
        String title = delegate.titleForCode(this, code);
        String description = delegate.descriptionforCode(this, code);
        String recoverySuggestion = delegate.recoverySuggestionForCode(this, code);
        
        return createException(code, title, description, recoverySuggestion);
    }

    /**
     * Creates {@link LolayException} given a code. Looks up
     * localized title and descriptions from Android resources
     * using the format of error_1000_title, error_1000_description,
     * error_1000_recoverySuggestion
     */
    public LolayException createException(int code, Throwable cause) {
        String title = delegate.titleForCode(this, code);
        String description = delegate.descriptionforCode(this, code);
        String recoverySuggestion = delegate.recoverySuggestionForCode(this, code);
        
        return createException(code, title, description, recoverySuggestion);
    }

    /**
     * Creates {@link LolayException} given a code and a description
     */
    public LolayException createException(int code, String description) {
    	return createException(code, null, description, (String) null);
    }

    /**
     * Creates {@link LolayException} given a code and a description
     */
    public LolayException createException(int code, String title, String description) {
    	return createException(code, title, description, (String) null);
    }

    /**
     * Creates {@link LolayException} given a code and a description
     */
    public LolayException createException(int code, String title, String description, Throwable cause) {
    	return createException(code, title, description, null, cause);
    }

    /**
     * Creates {@link LolayException} given a code, title and description.
     */
    public LolayException createException(int code, String title, String description, String suggestion) {
        return new LolayException(code, title, description, suggestion);
    }
    
    /**
     * Creates {@link LolayException} given a code, title and description.
     */
    public LolayException createException(int code, String title, String description, String suggestion, Throwable cause) {
        return new LolayException(code, title, description, suggestion, cause);
    }
}

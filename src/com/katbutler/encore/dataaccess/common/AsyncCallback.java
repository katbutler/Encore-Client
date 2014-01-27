package com.katbutler.encore.dataaccess.common;

import com.katbutler.encore.model.EncoreError;

public interface AsyncCallback<T> {

	public void onSuccess(final T result);
	public void onFailure(final EncoreError error);
	
}

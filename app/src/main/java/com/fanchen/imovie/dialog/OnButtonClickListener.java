package com.fanchen.imovie.dialog;

/**
 * @author fanchen
 */
public interface OnButtonClickListener {
	int LIFT = 0;
	int CENTRE = 1;
	int RIGHT = 2;
	void onButtonClick(BaseAlertDialog<?> dialog, int btn);
}

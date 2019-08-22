package io.nodehome.svm.common;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CoinUtil {

	public static double DISPLAY_COIN_UNIT = 1000;	// kilo
	public static String DISPLAY_COIN_WORD = "";
	
	public static String calcDisplayCoin(double num) {
		double d = num / DISPLAY_COIN_UNIT;
		DecimalFormat df = new DecimalFormat("#.#");
        df.setMaximumFractionDigits(8);
		return df.format(d);
	}
	
}

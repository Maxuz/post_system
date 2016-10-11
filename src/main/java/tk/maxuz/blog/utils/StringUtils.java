package tk.maxuz.blog.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {

	public static String getStringFormatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return sdf.format(date);
	}
}

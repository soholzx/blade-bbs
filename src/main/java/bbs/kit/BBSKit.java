package bbs.kit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.pegdown.PegDownProcessor;

import blade.kit.PropertyKit;

public class BBSKit {

	private static final PegDownProcessor PA_DOWN_PROCESSOR = new PegDownProcessor();;

	public static String mdToHtml(String md) {
		if (null != md) {
			return PA_DOWN_PROCESSOR.markdownToHtml(md);
		}
		return "";
	}

	public static boolean isUsername(String username) {
		String regex = "^[a-z0-9]+$";
		return Pattern.matches(regex, username);
	}

	public static boolean isEmail(String email) {
		String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
		return Pattern.matches(regex, email);
	}

	private static final Hashids hashids = new Hashids();

	public static String userAvatar(Integer uid) {
		if (null != uid) {
			return hashids.encode(5, 5, uid);
		}
		return "";
	}

	public static boolean isImage(String suffix) {
		if (suffix.endsWith(".png") || suffix.endsWith(".jpg") || suffix.endsWith(".jpeg") || suffix.endsWith(".bmp")) {
			return true;
		}
		return false;
	}

	public static void writeDB(Map<String, Object> map) {
		String filePath = BBSKit.class.getResource("/").getPath() + "ds.properties";
		Properties properties = PropertyKit.getProperty("ds.properties");
		try {
			OutputStream outputStream = new FileOutputStream(filePath);
			properties.putAll(map);
			properties.store(outputStream, "");
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isInstall() {
		URL url = BBSKit.class.getResource("/install.lock");
		return null != url;
	}
	
	public static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}
	
}

import blade.kit.DateKit;

public class AA {


	public static void main(String[] args) {
		System.out.println(DateKit.getTomorrow().toLocaleString());
		System.out.println(DateKit.getYesterday(DateKit.getTomorrow()).toLocaleString());
	}
}

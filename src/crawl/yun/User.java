package crawl.yun;

public class User {
	public final long uk;
	public final String uname;
	public final String intro;
	public final int follows;
	public final int fans;
	public final int shares;
	
	public User(long uk, String uname, String intro, int follows, int fans, int shares) {
		this.uk = uk;
		this.uname = uname;
		this.intro = intro;
		this.follows = follows;
		this.fans = fans;
		this.shares = shares;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("uk=" + uk + ",");
		sb.append("uname=" + uname + ",");
		sb.append("intro=" + intro + ",");
		sb.append("follows=" + follows + ",");
		sb.append("fans=" + fans + ",");
		sb.append("shares=" + shares + ",");
		return sb.toString();
	}
}

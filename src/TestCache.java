import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestCache {
	private static Record getFromDB(PreparedStatement pstmt, int k) throws SQLException {
		String v;
		ResultSet rs = null;
		pstmt.setInt(1, k);
		rs = pstmt.executeQuery();
		rs.next();
		v = rs.getString("v");
		
		return new Record(k, v);
	}
	
	private static Record find(PreparedStatement pstmt, Cache c, int k) throws SQLException {
		Record r = c.lookUp(k);
		//캐시를 탐색했으나, 거기서 레코드 r을 찾지 못했다.
		if(r == null) {
			r = getFromDB(pstmt, k);
			c.store(r);
		}
		return r;
	}
	
	public static void main(String args[]) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String addr = "jdbc:mysql://10.73.45.67/redit";
		String user = "jongun";
		String pwd = "1234";

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Driver Error" + e.getMessage());
		}
		System.out.println("Driver Loading Success");

		try {
			conn = DriverManager.getConnection(addr, user, pwd);
			pstmt = conn.prepareStatement("select * from cachetest where k=?");

			System.out.println(new File(".").getAbsolutePath());
			System.out.println("Connect Success");
			BufferedReader in = new BufferedReader(new FileReader("input.txt"));
			String s;
			Cache c = new Cache();

			long startTime = System.currentTimeMillis();
			int howmany = 0;
			while ((s = in.readLine()) != null) {
				int key = Integer.parseInt(s);
				find(pstmt, c, key);
				howmany++;
				if (howmany % 100 == 0) {
					System.out.println(howmany);
				}
			}

			long endTime = System.currentTimeMillis();

			System.out.println("Elapsed milliseconds : "
					+ (endTime - startTime));
		} catch (Exception e) {
			System.out.println("Driver Error");
			e.printStackTrace();
		} finally {
			if (conn != null) conn.close();
			if (pstmt != null) pstmt.close();
		}
	}
}

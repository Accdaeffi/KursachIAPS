package lab6;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class sqlIAPS {
	
	Connection c;
    Statement stmt;
	
    /**
	 *  ����������� ��� ��������� ������������� ������� � ��.
	 */
    
	public sqlIAPS () {
		try {
			c = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/hell","postgres", "");
			c.setAutoCommit(false);
			stmt = c.createStatement();
		} catch (SQLException e) {
			System.out.println ("�������� � �������� � ��?");
			System.exit(0);
		}
	}
	
	/**
	 *  ����������� ������ ��������
	 *  @param Tormented � ������ id, name, email, password
	 *  @throws InvalidEmailException 
	 */
	
	void sqlInsertTorm (Tormented t) throws InvalidEmailException {
		if (sqlSelectTorm (t.email).id>0) throw new InvalidEmailException(); else {
			String sql = "INSERT INTO TORMENTED (TormentedName, TormentedEmail, TormentedPassword, TormentedIdDistrict, TormentedSelectedSin) VALUES (";
			sql = sql+" '"+t.name+"',";
			sql = sql+" '"+t.email+"',";
			sql = sql+" '"+t.password+"',0,0);";
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *  ���������� ���������� � ��������
	 *  @param Tormented � ������ id, name, email, password, district
	 */
	
	void sqlUpdateTorm (Tormented t) {
		String sql = "UPDATE TORMENTED SET ";
		sql = sql+"TormentedName='"+t.name+"', ";
		sql = sql+"TormentedEmail='"+t.email+"', ";
		sql = sql+"TormentedPassword='"+t.password+"', ";
		sql = sql+"TormentedIdDistrict="+t.district+", ";
		sql = sql+"TormentedSelectedSin="+t.selectedsin+" ";
		sql = sql + "WHERE idTormented=" + t.id+";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  ��������� ���������� � �������� �� email
	 *  @param String email
	 *  @return Tormented
	 */
	
	Tormented sqlSelectTorm (String email) {
		String sql = "SELECT * FROM TORMENTED WHERE ";
		sql = sql+"TormentedEmail='"+email+"';";
    	Tormented t = new Tormented();
		try {
			ResultSet rs = stmt.executeQuery(sql);
	        t.id=rs.getInt(1);
	        t.name=rs.getString(2);
	        t.email=rs.getString(3);
	        t.password=rs.getString(4);
	        t.district=rs.getInt(5);
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return t;
	}
	
	/**
	 *  ��������� ���������� � �������� �� ID
	 *  @param int ID ��������
	 *  @return Tormented
	 */
	
	Tormented sqlSelectTorm (int id) {
		String sql = "SELECT * FROM TORMENTED WHERE ";
		sql = sql+"idTormented="+id+";";
    	Tormented t = new Tormented();
		try {
			ResultSet rs = stmt.executeQuery(sql);
	        t.id=rs.getInt(1);
	        t.name=rs.getString(2);
	        t.email=rs.getString(3);
	        t.password=rs.getString(4);
	        t.district=rs.getInt(5);
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return t;
	}
	
	/**
	 *  ������ ��������� �� ������������ ������
	 *  @param Tormented �������, �������� ���������
	 *  @param String ����� ��������
	 */
	
	void sqlInsertSinAppl (Tormented t, String s) {
		String sql = "INSERT INTO SinApplication (idTormented, SinApplicationInfo) VALUES (";
		sql = sql+" "+t.id+",";
		sql = sql+" '"+s+"');";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  ����� ��������� �� ������������
	 *  @return ArrayList<Application> ���� �� ������� ���������
	 */
	
	ArrayList<Application> sqlSelectSinAppl () {
		String sql = "SELECT * FROM SinApplication;";
		ArrayList<Application> apps = new ArrayList<Application>();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next() ) {
				Application app = new Application();
				app.id=rs.getInt(1);
				app.author=rs.getInt(2);
				app.text=rs.getString(3);
				apps.add(app);
	        }
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return apps;
	}
	
	/**
	 *  ���� �� ������������
	 *  @param Application ������
	 *  @param Watcher �����������, ������� �� ������������
	 */
	
	void sqlStartSinApp(Application app, Watcher watch) {
		String sql = "UPDATE SinApplication SET ";
		sql = sql+"idWatcher="+watch.id+" ";
		sql = sql + "WHERE idSinApplication=" + app.id+";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  ��������� ������������
	 *  @param Application ������
	 */
	
	void sqlStopSinApp(Application app) {
		String sql = "UPDATE SinApplication SET ";
		sql = sql+"idWatcher="+0+" ";
		sql = sql + "WHERE idSinApplication=" + app.id+";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
			
	/**
	 *  ���������� ���������� � ������ ��������
	 *  @param Tormented t
	 *  @param float[] sins
	 */
	
	void sqlInsertTormSins (Tormented t, float[] sins) {
		for (int i=0; i<sins.length;i++) {
			if (sins[i]>0) {
				int[] hours = sqlSelectTormSins (t, (i+1));
				if (hours[0]==0) {
					String sql = "INSERT INTO Tormented_has_Sin VALUES (";
					sql = sql+" "+t.id+",";
					sql = sql+" "+(i+1)+",";
					sql = sql+" "+sins[i]+",";
					sql = sql+" 0);";
					try {
						stmt.executeUpdate(sql);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					String sql = "UPDATE Tormented_has_Sin SET";
					sql = sql+" TormentedSinHoursTotal="+(hours[0]+sins[i])+",";
					sql = sql + "WHERE idTormented=" + t.id+" AND idSin="+(i+1)+";";
					try {
						stmt.executeUpdate(sql);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
		
	/**
	 *  �������� ���������� � ����� ��������
	 *  @param Tormented t
	 *  @param int idSin
	 *  @return int[] int[0] - ����� �����, int[1] - ������������ �����
	 */
	
	int[] sqlSelectTormSins(Tormented t, int i) {
		int[] hours = {0,0};
		String sql = "SELECT * FROM Tormented_has_Sin WHERE ";
		sql = sql+"idTormented="+t.id+" AND idSin="+i+";";
		try {
			ResultSet rs = stmt.executeQuery(sql);
	        hours[0]=rs.getInt(3);
	        hours[1]=rs.getInt(4);
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return hours;
	}
	
	/**
	 * �������� ������ ID ������������, ���������� ��������
	 * @param int[] ������ �� 7 �����, ������ ��� ������� �������� ������, �������� �� ������� � ��� �����
	 * @return ArrayList<Integer> ���� �� �� ������������
	 */
	
	ArrayList<Integer> sqlSelectTime(int[] times) {
		ArrayList<Integer> watchers = new ArrayList<Integer>();
		String sql = "SELECT * FROM Time";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next() ) {
				if (((rs.getInt(2)&times[0])>0)||((rs.getInt(3)&times[1])>0)||((rs.getInt(4)&times[2])>0)||((rs.getInt(5)&times[3])>0)||((rs.getInt(6)&times[4])>0)||((rs.getInt(7)&times[5])>0)||((rs.getInt(8)&times[6])>0))
	        	watchers.add(rs.getInt(1));
	        }
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return watchers;
	}
	
	/**
	 *  ����� ������ �� id
	 *  @param int ID ������
	 *  @return String �������� ������
	 */
	
	String sqlSelectDistrictl (int id) {
		String sql = "SELECT DistrictName FROM SinApplication WHERE idDistrict="+id+";";
		String name = new String("Not Found");
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next() ) {
				name=rs.getString(2);
	        }
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return name;
	}
	
	/**
	 * ���������� ������ �����������
	 * @param Watcher �����������
	 * @throws InvalidEmailException 
	 */
	
	void sqlInsertWatch (Watcher w) throws InvalidEmailException {
		if (sqlSelectWatch (w.email).id>0) throw new InvalidEmailException(); else {
			String sql = "INSERT INTO WATCHER (WatcherName, WatcherEmail, WatcherPassword, WatcherIdDistrict, WatcherEmployed, WatcherEvaluation, WatcherScores) VALUES (";
			sql = sql+" '"+w.name+"',";
			sql = sql+" '"+w.email+"',";
			sql = sql+" '"+w.password+"',0,0,0,0);";
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *  ���������� ���������� � �����������
	 *  @param Watcher �����������
	 */
	
	void sqlUpdateWatch (Watcher w) {
		String sql = "UPDATE TORMENTED SET ";
		sql = sql+"WatcherName='"+w.name+"', ";
		sql = sql+"WatcherEmail='"+w.email+"', ";
		sql = sql+"WatcherPassword='"+w.password+"', ";
		sql = sql+"WatcherIdDistrict="+w.district+", ";
		sql = sql+"WatcherEmployed="+w.employed+" ";
		sql = sql+"WatcherEvaluation="+w.evaluation+", ";
		sql = sql+"WatcherScores="+w.scores+" ";
		sql = sql + "WHERE idWatcher=" + w.id+";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  ��������� ���������� � ����������� �� email
	 *  @param String email
	 *  @return Watcher
	 */
	
	Watcher sqlSelectWatch (String email) {
		String sql = "SELECT * FROM Watcher WHERE WatcherEmail='"+email+"';";
    	Watcher w = new Watcher();
		try {
			ResultSet rs = stmt.executeQuery(sql);
	        w.id=rs.getInt(1);
	        w.name=rs.getString(2);
	        w.email=rs.getString(3);
	        w.password=rs.getString(4);
	        w.district=rs.getInt(5);
	        w.employed=rs.getBoolean(6);
	        w.evaluation=rs.getFloat(7);
	        w.scores=rs.getInt(8);
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return w;
	}
	
	/**
	 *  ��������� ���������� � ����������� �� ID
	 *  @param int ID �����������
	 *  @return Watcher
	 */
	
	Watcher sqlSelectWatch (int id) {
		String sql = "SELECT * FROM Watcher WHERE idWatcher="+id+";";
    	Watcher w = new Watcher();
		try {
			ResultSet rs = stmt.executeQuery(sql);
	        w.id=rs.getInt(1);
	        w.name=rs.getString(2);
	        w.email=rs.getString(3);
	        w.password=rs.getString(4);
	        w.district=rs.getInt(5);
	        w.employed=rs.getBoolean(6);
	        w.evaluation=rs.getFloat(7);
	        w.scores=rs.getInt(8);
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return w;
	}
	
	/**
	 * ��������� ������ ���������� ������������
	 * @return ArrayList<Watcher> ������ ������������
	 */
	
	ArrayList<Watcher> sqlSelectUnEmployed () {
		ArrayList<Watcher> watchers = new ArrayList<Watcher>(); 
		String sql = "SELECT * FROM WATCHER WHERE WatcherEmployed=FALSE";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next() ) {
				Watcher w = new Watcher();
				w.id=rs.getInt(1);
		        w.name=rs.getString(2);
		        w.email=rs.getString(3);
		        w.password=rs.getString(4);
		        w.district=rs.getInt(5);
		        w.employed=rs.getBoolean(6);
		        w.evaluation=rs.getFloat(7);
		        w.scores=rs.getInt(8);
		        watchers.add(w);
	        }
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return watchers;
		
	}
	
	/**
	 *  ���������� ���������� � ��������������� �����������
	 *  @param Watcher �����������
	 *  @param int[] ������ �����������, ����� �� ����������� �������� ��� ������	
	 */
	
	void sqlInsertWatchSins (Watcher w, int[] sins) {
		for (int i=0; i<sins.length;i++) {
			if (sins[i]>0) {
				String sql = "INSERT INTO Watcher_Allow_Work VALUES (";
				sql = sql+" "+w.id+",";
				sql = sql+" "+(i+1)+";";
				try {
					stmt.executeUpdate(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 *  ������ ��������� �� ������
	 *  @param Watcher �����������, �������� ���������
	 *  @param String ��������
	 */
	
	void sqlInsertRepairAppl (Watcher w, String s) {
		String sql = "INSERT INTO RepairApplication (idWatcher, RepairApplicationDescription, RepairApplicationStatus, RepairApplicationPayment) VALUES (";
		sql = sql+" "+w.id+",";
		sql = sql+" '"+s+"',0,0);";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  ��������� ������ ��������� �� ������ ��� ����������
	 *  @return ArrayList<Application> ������ ���������
	 */
	
	ArrayList<Application> sqlSelectRepairApplRepair () {
		String sql = "SELECT * FROM RepairApplication WHERE RepairApplicationStatus<2;";
		ArrayList<Application> apps = new ArrayList<Application>();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next() ) {
				Application app = new Application();
				app.id=rs.getInt(1);
				app.author=rs.getInt(2);
				app.text=rs.getString(3);
				apps.add(app);
	        }
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return apps;
	}
	
	/**
	 *  ��������� ������ ��������� �� ������ ��� ��������������
	 *  @return ArrayList<Application> ������ ���������
	 */
	
	ArrayList<Application> sqlSelectRepairApplAdmin () {
		String sql = "SELECT * FROM RepairApplication WHERE (RepairApplicationStatus=2) AND (RepairApplicationPayment=0);";
		ArrayList<Application> apps = new ArrayList<Application>();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next() ) {
				Application app = new Application();
				app.id=rs.getInt(1);
				app.author=rs.getInt(2);
				app.text=rs.getString(3);
				apps.add(app);
	        }
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return apps;
	}
	
	/**
	 *  ��������� ������� �������
	 *  @param Application ������
	 *  @param int ������ (0 - �� �������, 1 - �������, 2 - ������)
	 */
	
	void sqlRepairRepairApp(Application app, int status) {
		String sql = "UPDATE RepairApplication SET ";
		sql = sql+"RepairApplicationStatus="+status+" ";
		sql = sql + "WHERE idRepairApplication=" + app.id+";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  ��������� ����� ������
	 *  @param Application ������
	 *  @param int ������ (0 - �� ��������, 1 - ��������)
	 */
	
	void sqlSPaymentRepairApp(Application app, int status) {
		String sql = "UPDATE RepairApplication SET ";
		sql = sql+"RepairApplicationPayment="+status+" ";
		sql = sql + "WHERE idRepairApplication=" + app.id+";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  ��������� ������� �����������
	 *  @param Watcher �����������
	 *  @param int[] ������ �� 7 �����, ������ ��� ������� �������� ������, �������� �� ����������� � ��� �����
	 */
	
	void sqlInsertTime (Watcher w, int[] times) {
		String sql = "INSERT INTO Time (idWatcher, TimeMonday, TimeTuesday, TimeWednesday, TimeThursday, TimeFriday, TimeSaturday, TimeSunday) VALUES (";
		sql = sql+" "+w.id+",";
		sql = sql+" "+times[0]+",";
		sql = sql+" "+times[1]+",";
		sql = sql+" "+times[2]+",";
		sql = sql+" "+times[3]+",";
		sql = sql+" "+times[4]+",";
		sql = sql+" "+times[5]+",";
		sql = sql+" "+times[6]+");";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  ������ � ����� ������ ���������
	 *  @param Session �����, ����������� ������
	 */
	
	void sqlInsertSession(Session s) {
		String sql = "INSERT INTO Session (idWatcher, idTormented, SessionTime, idSin, SessionApproved, SessionEvaluation) VALUES (";
		sql = sql+" "+s.w.id+",";
		sql = sql+" "+s.t.id+",";
		sql = sql+" "+s.time+",";
		sql = sql+" "+s.sin+",";
		sql = sql+" "+s.approved+",";
		sql = sql+" "+s.evaluation+");";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��������� ������ ������� ������ ��� �����������
	 * @param Watcher �����������
	 * @return ArrayList<Session> ������ ������
	 */
	
	ArrayList<Session> sqlSelectSession (Watcher w) {
		String sql = "SELECT * FROM Session WHERE idWatcher="+w.id+";";
		ArrayList<Session> sessions = new ArrayList<Session>();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next() ) {
				Session s = new Session();
				s.w=w;
				s.t=sqlSelectTorm(rs.getInt(2));
				s.time=rs.getInt(3);
				s.sin=rs.getInt(4);
				s.approved=rs.getBoolean(5);
				s.evaluation=rs.getFloat(6);
				sessions.add(s);
	        }
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return sessions;
	}
	
	/**
	 * ��������� ������ ������� ������ ��� ��������
	 * @param Tormented 
	 * @return ArrayList<Session> ������ ������
	 */
	
	ArrayList<Session> sqlSelectSession (Tormented t) {
		String sql = "SELECT * FROM Session WHERE idTormented="+t.id+";";
		ArrayList<Session> sessions = new ArrayList<Session>();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next() ) {
				Session s = new Session();
				s.t=t;
				s.w=sqlSelectWatch(rs.getInt(1));
				s.time=rs.getInt(3);
				s.sin=rs.getInt(4);
				s.approved=rs.getBoolean(5);
				s.evaluation=rs.getFloat(6);
				sessions.add(s);
	        }
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return sessions;
	}
	
	/**
	 * ������ ������. � ���� approved ������������ -1.
	 * @param Session ������
	 */
	
	void sqlDeclineSession(Session s) {
		String sql = "UPDATE Session SET ";
		sql = sql+"SessionApproved="+(-1)+" ";
		sql = sql + "WHERE idWatcher=" + s.w.id+" AND idTormented="+s.t.id+" AND SessionTime="+s.time+";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ������������� ������. � ���� approved ������������ 1.
	 * @param Session ������
	 */
	
	void sqlApproveSession(Session s) {
		String sql = "UPDATE Session SET ";
		sql = sql+"SessionApproved="+1+" ";
		sql = sql + "WHERE idWatcher=" + s.w.id+" AND idTormented="+s.t.id+" AND SessionTime="+s.time+";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������� ������.
	 * @param Session ������
	 */
	
	void sqlEvaluateSession(Session s) {
		String sql = "UPDATE Session SET ";
		sql = sql+"SessionEvaluation="+s.evaluation+" ";
		sql = sql + "WHERE idWatcher=" + s.w.id+" AND idTormented="+s.t.id+" AND SessionTime="+s.time+";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Watcher w = sqlSelectWatch(s.w.id);
	
		sql = "UPDATE Watcher SET ";
		sql = sql+"WatcherEvaluation="+((w.evaluation*w.scores)+s.evaluation)+", ";
		sql = sql +"WatcherScores="+(w.scores+1)+" ";
		sql = sql + "WHERE idWatcher=" + s.w.id+" AND idTormented="+s.t.id+" AND SessionTime="+s.time+";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������� ������ ������� � ������������
	 * @param String �������� ��������
	 */
	
	void sqlInsertTechSupport(String s) {
		String sql = "INSERT INTO TechSupport (TechSupportDescription, TechSupportStatus) VALUES (";
		sql = sql+" '"+s+"', ";
		sql = sql+" "+0+");";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��������� ������ �������� � ������������
	 * @return 
	 * @return ArrayList<Application> ������ ��������
	 */
	
	ArrayList<Application> sqlSelectTechSupport() {
		String sql = "SELECT * FROM TechSupport WHERE TechSupportStatus<2;";
		ArrayList<Application> supp = new ArrayList<Application>();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while ( rs.next() ) {
				Application a = new Application ();
				a.id=rs.getInt(1);
				a.text=rs.getString(2);
				supp.add(a);
	        }
		} catch (SQLException e) {
			System.out.println("�������� � �������� � ���� ������.");
			e.printStackTrace();
		}
		return supp;
	}
	
	/**
	 * ��������� ������� ������� � ������������
	 * @param Application ������, ������ �������� ���� ��������
	 * @param int ������ (0 - �� ����������, 1 - ���������������, 2 - ������)
	 */
	
	void sqlUpdateTechSupport(Application supp, int s) {
		String sql = "UPDATE TechSupport SET ";
		sql = sql+"TechSupportStatus="+s+" ";
		sql = sql + "WHERE idTechSupport=" + supp.id+";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void finalize() {
		try {
			stmt.close();
			c.commit();
		} catch (SQLException e) {
			System.out.println("������ ��� �������� ����� � ��!");
			e.printStackTrace();
		}
	}
	
}

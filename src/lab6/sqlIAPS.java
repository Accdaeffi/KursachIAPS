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
	 *  Конструктор для начальной инициализации доступа к БД.
	 */
    
	public sqlIAPS () {
		try {
			c = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/hell","postgres", "");
			c.setAutoCommit(false);
			stmt = c.createStatement();
		} catch (SQLException e) {
			System.out.println ("Проблемы с доступом к бд?");
			System.exit(0);
		}
	}
	
	/**
	 *  Регистрация нового грешника
	 *  @param Tormented с полями id, name, email, password
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
	 *  Обновление информации о грешнике
	 *  @param Tormented с полями id, name, email, password, district
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
	 *  Получение информации о грешнике по email
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return t;
	}
	
	/**
	 *  Получение информации о грешнике по ID
	 *  @param int ID грешника
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return t;
	}
	
	/**
	 *  Подача заявления на рассмотрение грехов
	 *  @param Tormented грешник, подавший заявление
	 *  @param String текст описания
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
	 *  Вывод заявлений на рассмотрение
	 *  @return ArrayList<Application> лист со списком заявлений
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return apps;
	}
	
	/**
	 *  Приём на рассмотрение
	 *  @param Application Заявка
	 *  @param Watcher Надзиратель, взявший на расммотрение
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
	 *  Остановка рассмотрения
	 *  @param Application Заявка
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
	 *  Записывает информацию о грехах грешника
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
	 *  Получает информацию о грехе грешника
	 *  @param Tormented t
	 *  @param int idSin
	 *  @return int[] int[0] - общее время, int[1] - выстраданное время
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return hours;
	}
	
	/**
	 * Получает список ID надзирателей, подходящих грешнику
	 * @param int[] Массив из 7 интов, каждый бит каждого является флагом, свободен ли грешник в это время
	 * @return ArrayList<Integer> Лист из ид надзирателей
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return watchers;
	}
	
	/**
	 *  Поиск района по id
	 *  @param int ID района
	 *  @return String Название района
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return name;
	}
	
	/**
	 * Добавление нового надзирателя
	 * @param Watcher Надзиратель
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
	 *  Обновление информации о надзирателе
	 *  @param Watcher Надзиратель
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
	 *  Получение информации о надзирателе по email
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return w;
	}
	
	/**
	 *  Получение информации о надзирателе по ID
	 *  @param int ID надзирателя
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return w;
	}
	
	/**
	 * Получение списка непринятых надзирателей
	 * @return ArrayList<Watcher> Список надзирателей
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return watchers;
		
	}
	
	/**
	 *  Записывает информацию о профпригодности надзирателя
	 *  @param Watcher Надзиратель
	 *  @param int[] Массив описывающий, можно ли надзирателю работать над грехом	
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
	 *  Подача заявления на ремонт
	 *  @param Watcher надзиратель, подавший заявление
	 *  @param String описание
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
	 *  Получение списка заявлений на ремонт для ремонтника
	 *  @return ArrayList<Application> Список заявлений
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return apps;
	}
	
	/**
	 *  Получение списка заявлений на ремонт для администратора
	 *  @return ArrayList<Application> Список заявлений
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return apps;
	}
	
	/**
	 *  Установка статуса починки
	 *  @param Application Заявка
	 *  @param int Статус (0 - не тронута, 1 - чинится, 2 - готово)
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
	 *  Установка флага оплаты
	 *  @param Application Заявка
	 *  @param int Статус (0 - не оплачено, 1 - оплачено)
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
	 *  Занесение времени надзирателя
	 *  @param Watcher Надзиратель
	 *  @param int[] Массив из 7 интов, каждый бит каждого является флагом, свободен ли надзиратель в это время
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
	 *  Запись о новой сессии страдания
	 *  @param Session Класс, описывающий сессию
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
	 * Получение списка будущих сессий для надзирателя
	 * @param Watcher надзиратель
	 * @return ArrayList<Session> список сессий
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return sessions;
	}
	
	/**
	 * Получение списка будущих сессий для грешника
	 * @param Tormented 
	 * @return ArrayList<Session> список сессий
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return sessions;
	}
	
	/**
	 * Отмена сессии. В поле approved выставляется -1.
	 * @param Session Сессия
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
	 * Подтверждение сессии. В поле approved выставляется 1.
	 * @param Session Сессия
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
	 * Оценивание сессии.
	 * @param Session Сессия
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
	 * Добавление нового запроса в техподдержку
	 * @param String Описание проблемы
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
	 * Получение списка запросов в техподдержку
	 * @return 
	 * @return ArrayList<Application> Массив запросов
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
			System.out.println("Проблемы с доступом к базе данных.");
			e.printStackTrace();
		}
		return supp;
	}
	
	/**
	 * Изменение статуса запроса в техподдержку
	 * @param Application Запрос, статус которого надо изменить
	 * @param int Статус (0 - не расмотрено, 1 - рассматривается, 2 - готово)
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
			System.out.println("Ошибка при закрытии связи с БД!");
			e.printStackTrace();
		}
	}
	
}

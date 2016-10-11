package tk.maxuz.blog.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import tk.maxuz.blog.beans.ActionController;
import tk.maxuz.blog.beans.AddEditController;
import tk.maxuz.blog.beans.CategoryController;
import tk.maxuz.blog.beans.NoteBean;
import tk.maxuz.blog.beans.NoteListController;
import tk.maxuz.blog.beans.TagController;
import tk.maxuz.blog.connection.JDBCConnectionHelper;
import tk.maxuz.blog.connection.SessionFactoryProvider;
import tk.maxuz.blog.entity.Category;
import tk.maxuz.blog.entity.Note;
import tk.maxuz.blog.entity.Tag;
import tk.maxuz.blog.entity.dao.CategoryDao;
import tk.maxuz.blog.entity.dao.NoteDao;
import tk.maxuz.blog.entity.dao.TagDao;
import tk.maxuz.blog.exception.BlogException;

public class BaseOperation {

	protected static SessionFactoryProvider sessionFactoryProvider;

	protected static NoteDao noteDao;
	protected static CategoryDao categoryDao;
	protected static TagDao tagDao;

	protected static NoteListController noteListController;
	protected static CategoryController categoryController;
	protected static TagController tagController;
	protected static ActionController actionController;

	protected static Connection connection;

	private static JDBCConnectionHelper jdbcConnectionHelper;

	@BeforeClass
	public static void initialize() {
		sessionFactoryProvider = new SessionFactoryProvider();

		noteDao = new NoteDao();
		categoryDao = new CategoryDao();
		tagDao = new TagDao();

		categoryController = new CategoryController();
		categoryController.setSessionFactoryProvider(sessionFactoryProvider);

		tagController = new TagController();
		tagController.setSessionFactoryProvider(sessionFactoryProvider);

		noteListController = new NoteListController();
		noteListController.setSessionFactoryProvider(sessionFactoryProvider);
		noteListController.setTagController(tagController);
		noteListController.initialize();

		actionController = new ActionController();
		actionController.setNoteListController(noteListController);

		jdbcConnectionHelper = new JDBCConnectionHelper();
		jdbcConnectionHelper.initialize();

		connection = jdbcConnectionHelper.getConnection();
	}

	@AfterClass
	public static void finish() {
		sessionFactoryProvider.getSessionFactory().close();
		jdbcConnectionHelper.closeConnection();
	}

	@Before
	public void before() throws BlogException {
		clearDatabase();

		// rcarver - setup the jndi context and the datasource
		// https://blogs.oracle.com/randystuph/entry/injecting_jndi_datasources_for_junit
		/*try {
			// Create initial context
			System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
			System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
			InitialContext ic = new InitialContext();

			ic.createSubcontext("java:");
			ic.createSubcontext("java:/comp");
			ic.createSubcontext("java:/comp/env");
			ic.createSubcontext("java:/comp/env/jdbc");

			// Construct DataSource
			Jdbc3PoolingDataSource ds = new Jdbc3PoolingDataSource();
			ds.setServerName("localhost");
			ds.setDatabaseName("test_post_system");
			ds.setUser("testuser");
			ds.setPassword("user");
			ds.setMaxConnections(10);

			ic.bind("java:jboss/env/jdbc/test_post_system", ds);
		} catch (NamingException ex) {
			ex.printStackTrace();
		}*/
	}

	@After
	public void after() throws BlogException {
		clearDatabase();
	}

	protected void clearDatabase() {
		try {
			Connection con = jdbcConnectionHelper.getConnection();
			Statement st = con.createStatement();
			st.executeUpdate("delete from note_tag");
			st.executeUpdate("delete from note");
			st.executeUpdate("delete from tag");
			st.executeUpdate("delete from category");

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	protected NoteBean createNewNoteBean(String title, String body, String category, String tags) {
		NoteBean noteBean = new NoteBean();
		noteBean.setTitle(title);
		noteBean.setBody(body);
		noteBean.setCategory(category);
		noteBean.setTags(tags);
		return noteBean;
	}

	protected AddEditController createAddEditController() {
		AddEditController addEditController = new AddEditController();
		addEditController.setActionController(actionController);
		addEditController.setCategoryController(categoryController);
		addEditController.setSessionFactoryProvider(sessionFactoryProvider);
		addEditController.setNoteListController(noteListController);
		addEditController.setTagController(tagController);
		addEditController.initialize();
		return addEditController;
	}

	protected static Note addNoteToDatabase(String title, String body, String categoryName, List<String> tagLabelList)
			throws BlogException {
		Session session = sessionFactoryProvider.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();

		Note note = new Note();

		note.setTitle(title);
		note.setBody(body);

		Category category = categoryDao.getCategoryByName(session, categoryName, true);
		note.setCategory(category);

		noteDao.addNote(session, note);

		for (String tagLabel : tagLabelList) {
			if (tagLabel.trim().isEmpty()) {
				continue;
			}
			Tag tag = tagDao.getTagByLabel(session, tagLabel, true);
			note.addTag(tag);
		}

		tx.commit();

		return note;
	}

	protected List<String> getTagLabelsByNoteIdByJDBC(long noteId) throws BlogException {
		try {
			List<String> tags = new ArrayList<>();

			PreparedStatement ps = connection.prepareStatement("select t.label as label from note_tag nt "
					+ "inner join tag t on nt.tag_id = t.tag_id where note_id = ?");

			ps.setLong(1, noteId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				tags.add(rs.getString("label"));
			}
			return tags;
		} catch (SQLException ex) {
			throw new BlogException("Ошибка при выполнении sql запроса", ex);
		}

	}

	protected String getTagsAsString(List<String> tagList) {
		StringBuilder tagsStr = new StringBuilder();
		for (String tag : tagList) {
			if (tagsStr.length() > 0) {
				tagsStr.append(TagController.TAG_DELIMITER);
			}
			tagsStr.append(tag);
		}
		return tagsStr.toString();
	}
}

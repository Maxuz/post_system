package tk.maxuz.blog.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.hibernate.Session;

import tk.maxuz.blog.connection.SessionProvider;
import tk.maxuz.blog.db.entity.Category;
import tk.maxuz.blog.db.entity.Note;
import tk.maxuz.blog.db.entity.dao.CategoryDao;
import tk.maxuz.blog.exception.BlogException;

@Default
@SessionScoped
public class CategoryController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String EMPTY_OR_NULL_CATEGORY_NAME_ERROR_MSG = "Note category can't be null or empty";

	@Inject
	private SessionProvider sessionProvider;

	private CategoryDao categoryDao;

	public CategoryController() {
		categoryDao = new CategoryDao();
	}
	
	public CategoryController(SessionProvider sessionProvider) {
		this();
		this.sessionProvider = sessionProvider;
	}

	public void setCategory(Note note, String categoryName) throws BlogException {
		if (categoryName == null || categoryName.trim().isEmpty()) {
			throw new BlogException(EMPTY_OR_NULL_CATEGORY_NAME_ERROR_MSG);
		}
		Session session = sessionProvider.getCurrentSession();
		Category category = categoryDao.getCategoryByName(session, categoryName, true);
		note.setCategory(category);
	}

	public SessionProvider getSessionProvider() {
		return sessionProvider;
	}

	public void setSessionProvider(SessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

}

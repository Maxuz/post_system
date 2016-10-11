package tk.maxuz.blog.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.hibernate.Session;

import tk.maxuz.blog.connection.SessionFactoryProvider;
import tk.maxuz.blog.entity.Category;
import tk.maxuz.blog.entity.Note;
import tk.maxuz.blog.entity.dao.CategoryDao;
import tk.maxuz.blog.exception.BlogException;

@Default
@SessionScoped
public class CategoryController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String EMPTY_OR_NULL_CATEGORY_NAME_ERROR_MSG = "Note category can't be null or empty";

	@Inject
	private SessionFactoryProvider sessionFactoryProvider;

	private CategoryDao categoryDao;

	public CategoryController() {
		categoryDao = new CategoryDao();
	}

	public void setCategory(Note note, String categoryName) throws BlogException {
		if (categoryName == null || categoryName.trim().isEmpty()) {
			throw new BlogException(EMPTY_OR_NULL_CATEGORY_NAME_ERROR_MSG);
		}
		Session session = sessionFactoryProvider.getSessionFactory().getCurrentSession();
		Category category = categoryDao.getCategoryByName(session, categoryName, true);
		note.setCategory(category);
	}

	public SessionFactoryProvider getSessionFactoryProvider() {
		return sessionFactoryProvider;
	}

	public void setSessionFactoryProvider(SessionFactoryProvider sessionFactoryProvider) {
		this.sessionFactoryProvider = sessionFactoryProvider;
	}

}
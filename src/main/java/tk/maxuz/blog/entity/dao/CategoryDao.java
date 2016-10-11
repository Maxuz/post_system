package tk.maxuz.blog.entity.dao;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import tk.maxuz.blog.entity.Category;
import tk.maxuz.blog.exception.BlogException;

public class CategoryDao {

	public void addCategory(Session session, Category category) throws BlogException {
		session.save(category);
	}

	public Category getCategoryByName(Session session, String name) {
		return (Category) session.createCriteria(Category.class).add(Restrictions.eqOrIsNull("name", name)).uniqueResult();
	}

	public Category getCategoryByName(Session session, String name, boolean createIfNotExist) throws BlogException {
		Category result = getCategoryByName(session, name);

		if (result == null && createIfNotExist) {
			result = new Category();
			result.setName(name);
			addCategory(session, result);
		}
		return result;
	}
}

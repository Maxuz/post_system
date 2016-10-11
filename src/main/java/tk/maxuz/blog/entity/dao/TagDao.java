package tk.maxuz.blog.entity.dao;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import tk.maxuz.blog.entity.Tag;
import tk.maxuz.blog.exception.BlogException;

public class TagDao {

	public void addTag(Session session, Tag tag) throws BlogException {
		session.save(tag);
	}

	public Tag getTagByLabel(Session session, String label) throws BlogException {
		return (Tag) session.createCriteria(Tag.class).add(Restrictions.eq("label", label)).uniqueResult();
	}

	public Tag getTagByLabel(Session session, String label, boolean createIfNotExist) throws BlogException {
		Tag result = getTagByLabel(session, label);
		if (result == null && createIfNotExist) {
			result = new Tag();
			result.setLabel(label);
			addTag(session, result);
		}
		return result;
	}
}

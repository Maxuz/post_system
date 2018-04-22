package tk.maxuz.blog.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Session;

import tk.maxuz.blog.connection.SessionProvider;
import tk.maxuz.blog.db.entity.Note;
import tk.maxuz.blog.db.entity.Tag;
import tk.maxuz.blog.db.entity.dao.TagDao;
import tk.maxuz.blog.exception.BlogException;

@Named
@SessionScoped
public class TagController implements Serializable {

	public static final String TAG_DELIMITER = "*, ";
	public static final String TAG_DELIMITER_REGEX = "\\*, ";

	private static final long serialVersionUID = 1L;

	@Inject
	private SessionProvider sessionProvider;

	private TagDao tagDao;

	public TagController() {
		tagDao = new TagDao();
	}
	
	public TagController(SessionProvider sessionProvider) {
		this();
		this.sessionProvider = sessionProvider;
	}

	public List<Tag> setTags(Note note, String tags) throws BlogException {
		List<Tag> result = new ArrayList<>();
		Session session = sessionProvider.getCurrentSession();
		String[] newTagLabels = tags.split(TAG_DELIMITER_REGEX);

		List<String> tagLabels = new ArrayList<>();
		for (String label : newTagLabels) {
			if (label.trim().isEmpty()) {
				continue;
			}

			if (!containsTag(note, label)) {
				tagLabels.add(label);
			}
		}

		for (Tag tag : getTagsByLabel(session, tagLabels)) {
			note.addTag(tag);
		}

		Iterator<Tag> currentTagsIterator = note.getTags().iterator();

		while (currentTagsIterator.hasNext()) {
			Tag tag = currentTagsIterator.next();
			if (!isArrayContainsString(newTagLabels, tag.getLabel())) {
				currentTagsIterator.remove();

				note.removeTag(tag);
			}
		}
		return result;
	}

	private List<Tag> getTagsByLabel(Session session, List<String> tagLabels) throws BlogException {
		List<Tag> result = new ArrayList<>(tagLabels.size());

		for (String label : tagLabels) {
			Tag tag = tagDao.getTagByLabel(session, label, true);
			result.add(tag);
		}

		return result;
	}

	private boolean containsTag(Note note, String tagLabel) {
		for (Tag tag : note.getTags()) {
			if (tag.getLabel().equals(tagLabel)) {
				return true;
			}
		}
		return false;
	}

	private boolean isArrayContainsString(String[] array, String string) {
		for (String val : array) {
			if (string.equals(val)) {
				return true;
			}
		}
		return false;
	}

	public String getTagsAsString(Note note) {
		StringBuilder sb = new StringBuilder();
		for (Tag t : note.getTags()) {
			if (sb.length() > 0) {
				sb.append(TAG_DELIMITER);
			}
			sb.append(t.getLabel());
		}
		return sb.toString();
	}

	public SessionProvider getSessionProvider() {
		return sessionProvider;
	}

	public void setSessionProvider(SessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

}

package tk.maxuz.blog.db.entity.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import tk.maxuz.blog.db.entity.Category;
import tk.maxuz.blog.db.entity.Note;
import tk.maxuz.blog.db.entity.Tag;
import tk.maxuz.blog.exception.BlogException;

public class NoteDao {

	@SuppressWarnings("unchecked")
	public List<Note> getAllNotes(Session session) throws BlogException {
		return session.createQuery("from Note n order by n.createDate desc").list();
	}

	public Note getNoteByTitle(Session session, String title) throws BlogException {
		return (Note) session.createCriteria(Note.class).add(Restrictions.eq("title", title)).uniqueResult();
	}

	public void removeNote(Session session, String noteTitle) throws BlogException {
		removeNote(session, getNoteByTitle(session, noteTitle));
	}

	public void removeNote(Session session, Note note) throws BlogException {
		session.delete(note);
		removeCategory(session, note);
		removeTag(session, note);
	}

	private void removeCategory(Session session, Note note) {
		Category category = note.getCategory();
		category.removeNote(note);

		if (category.getNotes().isEmpty()) {
			session.delete(category);
		}
	}

	private void removeTag(Session session, Note note) {
		List<Tag> tags = note.getTags();
		for (Tag tag : tags) {
			tag.getNotes().remove(note);
			if (tag.getNotes().isEmpty()) {
				session.delete(tag);
			}
		}
	}

	public void addNote(Session session, Note note) throws BlogException {
		session.save(note);
	}

	public void updateNote(Session session, Note note) throws BlogException {
		session.update(note);
	}

}

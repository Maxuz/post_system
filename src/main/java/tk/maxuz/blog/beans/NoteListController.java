package tk.maxuz.blog.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Session;
import org.hibernate.Transaction;

import tk.maxuz.blog.connection.SessionProvider;
import tk.maxuz.blog.entity.Note;
import tk.maxuz.blog.entity.dao.NoteDao;
import tk.maxuz.blog.exception.BlogException;
import tk.maxuz.blog.navigation.HtmlPage;
import tk.maxuz.blog.utils.StringUtils;

@Named
@SessionScoped
public class NoteListController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String REMOVE_NOTE_ERROR_MSG = "Remove note error";

	private List<NoteBean> noteBeanList;

	@Inject
	private SessionProvider sessionProvider;

	@Inject
	private TagController tagController;

	private NoteDao noteDao;
	private NoteBean selectedNoteBean;

	public NoteListController() {
	}
	
	public NoteListController(SessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	@PostConstruct
	public void initialize() {
		noteBeanList = new ArrayList<>();
	}

	public List<NoteBean> getNoteBeanList() throws BlogException {
		noteBeanList.clear();
		noteDao = new NoteDao();
		Session session = sessionProvider.getCurrentSession();
		Transaction tx = session.beginTransaction();
		for (Note note : noteDao.getAllNotes(session)) {
			NoteBean nb = createNoteBeanFromNote(note);
			noteBeanList.add(nb);
		}
		tx.commit();
		return noteBeanList;
	}

	public String deleteNoteBean() throws BlogException {
		Session session = sessionProvider.getCurrentSession();
		Transaction tx = session.beginTransaction();
		try {
			noteDao.removeNote(session, selectedNoteBean.getTitle());
		} catch (BlogException ex) {
			throw new BlogException(REMOVE_NOTE_ERROR_MSG, ex);
		}
		tx.commit();
		return HtmlPage.INDEX;
	}

	public boolean isReadyToEdit() {
		return selectedNoteBean != null;
	}

	public NoteBean getSelectedNoteBean() {
		return selectedNoteBean;
	}
	
	public void setSelectedNoteBean(NoteBean noteBean) {
		selectedNoteBean = noteBean;
	}

	public SessionProvider getSessionProvider() {
		return sessionProvider;
	}

	public void setSessionProvider(SessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public TagController getTagController() {
		return tagController;
	}

	public void setTagController(TagController tagController) {
		this.tagController = tagController;
	}

	private NoteBean createNoteBeanFromNote(Note note) {
		NoteBean nb = new NoteBean();
		nb.setTitle(note.getTitle());
		nb.setBody(note.getBody());
		nb.setCategory(note.getCategory().getName());
		nb.setTags(tagController.getTagsAsString(note));
		nb.setCreateDate(StringUtils.getStringFormatDate(note.getCreateDate()));
		return nb;
	}

}

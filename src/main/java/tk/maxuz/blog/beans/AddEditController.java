package tk.maxuz.blog.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Session;
import org.hibernate.Transaction;

import tk.maxuz.blog.connection.SessionFactoryProvider;
import tk.maxuz.blog.entity.Note;
import tk.maxuz.blog.entity.dao.NoteDao;
import tk.maxuz.blog.exception.BlogException;
import tk.maxuz.blog.navigation.HtmlPage;

@Named
@RequestScoped
public class AddEditController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String ADD_EDIT_SAVE_ERROR_MSG = "Save after add or edit error";
	private static final String NULL_OR_EMPTY_TITLE_ERROR_MSG = "Title can't null or be empty";
	private static final String EMPTY_CATEGORY_NAME_ERROR_MSG = "Category name can't be empty";

	@Inject
	private SessionFactoryProvider sessionFactoryProvider;

	@Inject
	private CategoryController categoryController;

	@Inject
	private TagController tagController;
	
	@Inject
	private NoteListController noteListController;

	@Inject
	private ActionController actionController;

	private NoteBean noteBean;
	private String noteTitleBeforeEdit;

	@PostConstruct
	public void initialize() {
		if (actionController.getCurrentMode() == ActionMode.EDIT) {
			noteBean = noteListController.getSelectedNoteBean();
			noteTitleBeforeEdit = noteBean.getTitle();
		} else {
			noteBean = new NoteBean();
		}
	}

	public AddEditController() {
	}

	public String save() throws BlogException {
		Session session = sessionFactoryProvider.getSessionFactory().getCurrentSession();
		Transaction transaction = session.beginTransaction();
		try {
			Note note;
			NoteDao noteDao = new NoteDao();
			switch (actionController.getCurrentMode()) {
			case ADD:
				note = createNoteFromNoteBean(noteBean);
				noteDao.addNote(session, note);
				break;
			case EDIT:
				Note editingNote = new NoteDao().getNoteByTitle(session, noteTitleBeforeEdit);
				synchronizeNoteWithNoteBean(noteBean, editingNote);
				noteDao.updateNote(session, editingNote);
				break;
			default:
				break;
			}
			transaction.commit();

			return HtmlPage.INDEX;
		} catch (BlogException ex) {
			throw new BlogException(ADD_EDIT_SAVE_ERROR_MSG, ex);
		}
	}

	private Note createNoteFromNoteBean(NoteBean noteBean) throws BlogException {
		String title = noteBean.getTitle();
		if (title == null || title.trim().isEmpty()) {
			throw new BlogException(NULL_OR_EMPTY_TITLE_ERROR_MSG);
		}
		String category = noteBean.getCategory();
		if (category == null || category.trim().isEmpty()) {
			throw new BlogException(EMPTY_CATEGORY_NAME_ERROR_MSG);
		}
		Note n = new Note();
		n.setTitle(title);
		n.setBody(noteBean.getBody());
		categoryController.setCategory(n, category);
		tagController.setTags(n, noteBean.getTags());
		return n;
	}

	private void synchronizeNoteWithNoteBean(NoteBean noteBean, Note note) throws BlogException {
		note.setTitle(noteBean.getTitle());
		note.setBody(noteBean.getBody());
		if (!note.getCategory().getName().equals(noteBean.getCategory())) {
			categoryController.setCategory(note, noteBean.getCategory());
		}
		tagController.setTags(note, noteBean.getTags());
	}

	public ActionController getActionController() {
		return actionController;
	}

	public void setActionController(ActionController actionController) {
		this.actionController = actionController;
	}

	public SessionFactoryProvider getSessionFactoryProvider() {
		return sessionFactoryProvider;
	}

	public void setSessionFactoryProvider(SessionFactoryProvider sessionFactoryProvider) {
		this.sessionFactoryProvider = sessionFactoryProvider;
	}

	public CategoryController getCategoryController() {
		return categoryController;
	}

	public void setCategoryController(CategoryController categoryController) {
		this.categoryController = categoryController;
	}

	public TagController getTagController() {
		return tagController;
	}

	public void setTagController(TagController tagController) {
		this.tagController = tagController;
	}

	public NoteBean getNoteBean() {
		return noteBean;
	}

	public void setNoteBean(NoteBean noteBean) {
		this.noteBean = noteBean;
	}

	public NoteListController getNoteListController() {
		return noteListController;
	}

	public void setNoteListController(NoteListController noteListController) {
		this.noteListController = noteListController;
	}

}

package tk.maxuz.blog.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import tk.maxuz.blog.navigation.HtmlPage;

@Named
@SessionScoped
public class ActionController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private NoteListController noteListController;

	private ActionMode currentMode;
	
	public ActionController() {
	}

	public ActionMode getCurrentMode() {
		return currentMode;
	}

	public void setCurrentMode(ActionMode currentMode) {
		this.currentMode = currentMode;
	}

	public String setEditMode() {
		if (!noteListController.isReadyToEdit()) {
			return HtmlPage.INDEX;
		}
		currentMode = ActionMode.EDIT;
		return HtmlPage.ADD_EDIT;
	}

	public String setAddMode() {
		currentMode = ActionMode.ADD;
		return HtmlPage.ADD_EDIT;
	}

	public void setBrowsingMode() {
		currentMode = ActionMode.BROWSING;
	}

	public NoteListController getNoteListController() {
		return noteListController;
	}

	public void setNoteListController(NoteListController noteListController) {
		this.noteListController = noteListController;
	}
}

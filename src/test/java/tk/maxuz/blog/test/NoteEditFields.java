package tk.maxuz.blog.test;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import tk.maxuz.blog.beans.NoteBean;
import tk.maxuz.blog.beans.TagController;
import tk.maxuz.blog.exception.BlogException;

@RunWith(Parameterized.class)
public class NoteEditFields extends BaseUpdate {

	private int currentEditNoteNumber;

	public NoteEditFields(int currentEditNoteNumber) {
		super();
		this.currentEditNoteNumber = currentEditNoteNumber;
	}

	@Parameterized.Parameters
	public static Collection<Object> values() {
		return Arrays.asList(new Object[] { Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2),
				Integer.valueOf(3), Integer.valueOf(4) });
	}

	@Test
	public void editTitleTest() throws BlogException {
		String newTitle = "Edited title";

		NoteBean selectedNoteBean = edit(new IEditable() {
			@Override
			public void edit(NoteBean selectedNoteBean) {
				selectedNoteBean.setTitle(newTitle);
			}
		}, currentEditNoteNumber);
		testWithInitialNoteList(noteListController.getNoteBeanList(), currentEditNoteNumber, newTitle,
				selectedNoteBean.getBody(), selectedNoteBean.getCategory(), selectedNoteBean.getTags());
		testWithDatabase(currentEditNoteNumber, newTitle, selectedNoteBean.getBody(), selectedNoteBean.getCategory(),
				selectedNoteBean.getTags());
	}

	@Test
	public void addTwoTags() throws BlogException {
		NoteBean selectedNoteBean = edit(new IEditable() {
			@Override
			public void edit(NoteBean selectedNoteBean) {
				StringBuilder tags = new StringBuilder(selectedNoteBean.getTags());
				if (tags.length() != 0) {
					tags.append(TagController.TAG_DELIMITER);
				}
				tags.append("new tag1").append(TagController.TAG_DELIMITER).append("new tag2");
				selectedNoteBean.setTags(tags.toString());
			}
		}, currentEditNoteNumber);
		testWithInitialNoteList(noteListController.getNoteBeanList(), currentEditNoteNumber,
				selectedNoteBean.getTitle(), selectedNoteBean.getBody(), selectedNoteBean.getCategory(),
				selectedNoteBean.getTags());
		testWithDatabase(currentEditNoteNumber, selectedNoteBean.getTitle(), selectedNoteBean.getBody(),
				selectedNoteBean.getCategory(), selectedNoteBean.getTags());
	}
	
	@Test
	public void removeAllTags() throws BlogException {
		NoteBean selectedNoteBean = edit(new IEditable() {
			@Override
			public void edit(NoteBean selectedNoteBean) {
				selectedNoteBean.setTags("");
			}
		}, currentEditNoteNumber);
		testWithInitialNoteList(noteListController.getNoteBeanList(), currentEditNoteNumber,
				selectedNoteBean.getTitle(), selectedNoteBean.getBody(), selectedNoteBean.getCategory(),
				selectedNoteBean.getTags());
		testWithDatabase(currentEditNoteNumber, selectedNoteBean.getTitle(), selectedNoteBean.getBody(),
				selectedNoteBean.getCategory(), selectedNoteBean.getTags());
	}

	@Test
	public void editAllFieldsTest() throws BlogException {
		String newTitle = "Edited title";
		String newCategory = "Edited category";
		String newBody = "New body";
		String newTag = "newTag";

		edit(new IEditable() {
			public void edit(NoteBean selectedNoteBean) {
				selectedNoteBean.setTitle(newTitle);
				selectedNoteBean.setCategory(newCategory);
				selectedNoteBean.setBody(newBody);
				selectedNoteBean.setTags(newTag);
			}
		}, currentEditNoteNumber);

		testWithInitialNoteList(noteListController.getNoteBeanList(), currentEditNoteNumber, newTitle, newBody,
				newCategory, newTag);
		testWithDatabase(currentEditNoteNumber, newTitle, newBody, newCategory, newTag);
	}

	@Test
	public void editCategoryTest() throws BlogException {
		String newCategory = "Edited categoty";

		NoteBean selectedNoteBean = edit(new IEditable() {
			@Override
			public void edit(NoteBean selectedNoteBean) {
				selectedNoteBean.setCategory(newCategory);
			}
		}, currentEditNoteNumber);

		testWithInitialNoteList(noteListController.getNoteBeanList(), currentEditNoteNumber,
				selectedNoteBean.getTitle(), selectedNoteBean.getBody(), newCategory, selectedNoteBean.getTags());

		testWithDatabase(currentEditNoteNumber, selectedNoteBean.getTitle(), selectedNoteBean.getBody(), newCategory,
				selectedNoteBean.getTags());
	}

}

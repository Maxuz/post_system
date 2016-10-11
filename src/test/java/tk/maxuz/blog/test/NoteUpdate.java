package tk.maxuz.blog.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;

import tk.maxuz.blog.beans.NoteBean;
import tk.maxuz.blog.beans.TagController;
import tk.maxuz.blog.exception.BlogException;

public class NoteUpdate extends BaseUpdate {

	public NoteUpdate() {
	}

	@Test
	public void removeTwoTags() throws BlogException {
		int currentEditIndex = 4;
		NoteBean selectedNoteBean = edit(new IEditable() {

			@Override
			public void edit(NoteBean selectedNoteBean) {
				List<String> tags = new ArrayList<>(
						Arrays.asList(selectedNoteBean.getTags().split(TagController.TAG_DELIMITER_REGEX)));

				if (tags.size() != 5) {
					throw new IllegalArgumentException("Получены неверные данные, ожидется запись с 5 тегами");
				}

				tags.remove(tags.size() - 1);
				tags.remove(tags.size() - 1);

				selectedNoteBean.setTags(getTagsAsString(tags));
			}
		}, currentEditIndex); // 4 - must be note with 5 tags

		testWithInitialNoteList(noteListController.getNoteBeanList(), currentEditIndex, selectedNoteBean.getTitle(),
				selectedNoteBean.getBody(), selectedNoteBean.getCategory(), selectedNoteBean.getTags());
		testWithDatabase(currentEditIndex, selectedNoteBean.getTitle(), selectedNoteBean.getBody(),
				selectedNoteBean.getCategory(), selectedNoteBean.getTags());
	}

	@Test
	public void addTagsInNoteWithoutTags() throws BlogException {
		int currentEditIndex = 1;
		NoteBean selectedNoteBean = edit(new IEditable() {

			@Override
			public void edit(NoteBean selectedNoteBean) {

				if (!selectedNoteBean.getTags().isEmpty()) {
					throw new IllegalArgumentException("Получены неверные данные, ожидется запись с без тегов");
				}

				selectedNoteBean.setTags(getTagsAsString(Arrays.asList("%WoW%", "G@me$", "Хобби#")));
			}
		}, currentEditIndex);

		testWithInitialNoteList(noteListController.getNoteBeanList(), currentEditIndex, selectedNoteBean.getTitle(),
				selectedNoteBean.getBody(), selectedNoteBean.getCategory(), selectedNoteBean.getTags());
		testWithDatabase(currentEditIndex, selectedNoteBean.getTitle(), selectedNoteBean.getBody(),
				selectedNoteBean.getCategory(), selectedNoteBean.getTags());
	}

	@Test
	public void noteRemoveOne() throws BlogException {
		List<NoteBean> sourceNoteBeanList = new ArrayList<>(noteListController.getNoteBeanList());
		NoteBean selectedNoteBean = sourceNoteBeanList.get(3); // chosen by fair
																// dice roll.
																// guaranteed to
																// be random :)
		noteListController.setSelectedNoteBean(selectedNoteBean);
		noteListController.deleteNoteBean();

		List<NoteBean> actualNoteBeanList = noteListController.getNoteBeanList();

		assertEquals((sourceNoteBeanList.size() - 1), actualNoteBeanList.size());
		for (NoteBean expectedNoteBean : sourceNoteBeanList) {
			if (expectedNoteBean.getTitle().equals(selectedNoteBean.getTitle())) {
				continue;
			}
			boolean founded = false;
			for (NoteBean actualNoteBean : actualNoteBeanList) {
				if (actualNoteBean.getTitle().equals(expectedNoteBean.getTitle())) {
					founded = true;
					break;
				}
			}
			assertTrue(founded);
		}
	}

	@Test
	public void editNoteSetSameTitle() throws BlogException {
		thrown.expect(ConstraintViolationException.class);
		thrown.expectMessage("could not execute statement");

		List<NoteBean> sourceNoteBeanList = new ArrayList<>(noteListController.getNoteBeanList());
		NoteBean existedNoteBean = sourceNoteBeanList.get(2); // chosen by fair
																// dice roll.
																// guaranteed to
																// be random :)

		edit(new IEditable() {

			@Override
			public void edit(NoteBean selectedNoteBean) {
				selectedNoteBean.setTitle(existedNoteBean.getTitle());
			}
		}, 4);

	}

}

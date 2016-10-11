package tk.maxuz.blog.test;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import tk.maxuz.blog.beans.AddEditController;
import tk.maxuz.blog.beans.NoteBean;
import tk.maxuz.blog.exception.BlogException;

public class NoteCreation extends BaseOperation {

	@Rule
    public ExpectedException thrown = ExpectedException.none();
	private static final String ADD_EDIT_SAVE_ERROR_MSG = "Save after add or edit error";
	
	@Test
	public void noteCreationWithoutTags() throws BlogException {
		noteCreationTagTest("Vim и его \"прелести\".",
				"Намного меньше толщины галактики, так как только принадлежащие после открытия дискретных. Бы каждая из выше, динамическими соображениями кроме немногочисленных близких которые.",
				"Учеба", Arrays.asList(), 0);
	}

	@Test
	public void noteCreationWithEmptyTag() throws BlogException {
		noteCreationTagTest("Что должен знать каждый junior",
				"Бы каждая из выше, динамическими соображениями кроме немногочисленных близких которые.", "Tutorial",
				Arrays.asList(""), 0);
	}

	@Test
	public void noteCreationWithOneTag() throws BlogException {
		noteCreationTagTest("Vim и его \"прелести\".",
				"Намного меньше толщины галактики, так как только принадлежащие после открытия дискретных. Бы каждая из выше, динамическими соображениями кроме немногочисленных близких которые.",
				"Учеба", Arrays.asList("AWT"), 1);
	}

	@Test
	public void noteCreationWithThreeTags() throws BlogException {
		noteCreationTagTest("Основы использования Hibernate",
				"Именно из них посылает зарегистрированное радиоизлучение которого достаточно сильное кроме немногочисленных.",
				"Работа", Arrays.asList("Hibernate", "БД", "tutorial"), 3);
	}

	@Test
	public void noteCreationWithTwoExistTags() throws BlogException, SQLException {
		List<String> tags = Arrays.asList("Nyam", "БыстроБлюда");

		PreparedStatement pst = connection.prepareStatement("insert into tag(label) values (?), (?);");
		pst.setString(1, tags.get(0));
		pst.setString(2, tags.get(1));

		pst.executeUpdate();

		noteCreationTagTest("Кулинария это просто",
				"Именно из них посылает зарегистрированное радиоизлучение которого достаточно сильное кроме немногочисленных.",
				"Кулинария", tags, 2);
	}

	@Test
	public void noteCreationWithSameCategory() throws BlogException, SQLException {
		List<String> tags = Arrays.asList("Swing");
		String category = "Java";

		noteCreationTest("Swing - забытая технология",
				"Намного меньше толщины галактики, так как только принадлежащие после открытия дискретных. Бы каждая из выше, динамическими соображениями кроме немногочисленных близких которые.",
				category, tags);

		noteCreationTest("Композиция или наследование",
				"Бы каждая из выше, динамическими соображениями кроме немногочисленных близких которые.", category,
				tags);

		PreparedStatement pst = connection.prepareStatement("select count(1) from category c where c.name = ?");
		pst.setString(1, category);

		ResultSet rs = pst.executeQuery();
		while (rs.next()) {
			assertEquals(1, rs.getInt(1));
		}
	}

	@Test
	public void noteCreationWithTwoExistAndTwoNotExistTags() throws BlogException, SQLException {
		List<String> expectedTags = Arrays.asList("Nyam", "Быстро блюда", "breakfest", "#люблюпокушать");

		PreparedStatement pst = connection.prepareStatement("insert into tag(label) values (?), (?);");
		pst.setString(1, expectedTags.get(0));
		pst.setString(2, expectedTags.get(1));

		pst.executeUpdate();

		long noteId = noteCreationTest("Кулинария это просто",
				"Именно из них посылает зарегистрированное радиоизлучение которого достаточно сильное кроме немногочисленных.",
				"Кулинария", expectedTags);

		tagCreationTest(noteId, expectedTags, 4);

	}

	@Test
	public void noteCreationListOrder() throws BlogException {
		String firstTitle = "Title 1";
		String secondTitle = "Title 2";
		String thirdTitle = "Title 3";

		createNote(firstTitle, "some body 1", "category 1", Arrays.asList("tag 1"));
		createNote(secondTitle, "some body 2", "category 2", Arrays.asList("tag 2"));
		createNote(thirdTitle, "some body 3", "category 3", Arrays.asList("tag 3"));

		List<NoteBean> noteList = noteListController.getNoteBeanList();
		assertEquals(thirdTitle, noteList.get(0).getTitle());
		assertEquals(secondTitle, noteList.get(1).getTitle());
		assertEquals(firstTitle, noteList.get(2).getTitle());
	}
	
	@Test
	public void createNoteWithSameTitle() throws BlogException {
		thrown.expect(ConstraintViolationException.class);
		thrown.expectMessage("could not execute statement");
		String title = "Создание веб-сайта. Курс молодого бойца";
		String body = "Проработка макета проекта. После того, как мы определились со структурой проекта можно составить макет проекта (схематично).\nДля отрисовки наброска можно использовать бумагу и ручку, Photoshop, любой другой редактор графики (раньше часто использовали Adobe Fireworks). Важно отметить, что данный этап – это не отрисовка готового дизайн-макета, а всего лишь схематичный набросок, выполненный для понимания того, как на сайте будут располагаться основные информационные блоки, графика и прочие элементы дизайна.";
		String category = "Разработка";
		List<String> tagList = Arrays.asList("Разработка веб-сайтов", "JavaScript", "HTML", "CSS", "Программирование");
		
		createNote(title, body, category, tagList);
		createNote(title, body, category, tagList);
	}
	
	@Test
	public void createNoteWithoutTitle() throws BlogException {
		thrown.expect(BlogException.class);
		thrown.expectMessage(ADD_EDIT_SAVE_ERROR_MSG);
		String title = null;
		String body = "Проработка макета проекта. После того, как мы определились со структурой проекта можно составить макет проекта (схематично).\nДля отрисовки наброска можно использовать бумагу и ручку, Photoshop, любой другой редактор графики (раньше часто использовали Adobe Fireworks). Важно отметить, что данный этап – это не отрисовка готового дизайн-макета, а всего лишь схематичный набросок, выполненный для понимания того, как на сайте будут располагаться основные информационные блоки, графика и прочие элементы дизайна.";
		String category = "Разработка";
		List<String> tagList = Arrays.asList("Разработка веб-сайтов", "JavaScript", "HTML", "CSS", "Программирование");
		
		createNote(title, body, category, tagList);
	}
	
	@Test
	public void createNoteWithEmptyTitle() throws BlogException {
		thrown.expect(BlogException.class);
		thrown.expectMessage(ADD_EDIT_SAVE_ERROR_MSG);
		String title = "";
		String body = "Проработка макета проекта. После того, как мы определились со структурой проекта можно составить макет проекта (схематично).\nДля отрисовки наброска можно использовать бумагу и ручку, Photoshop, любой другой редактор графики (раньше часто использовали Adobe Fireworks). Важно отметить, что данный этап – это не отрисовка готового дизайн-макета, а всего лишь схематичный набросок, выполненный для понимания того, как на сайте будут располагаться основные информационные блоки, графика и прочие элементы дизайна.";
		String category = "Разработка";
		List<String> tagList = Arrays.asList("Разработка веб-сайтов", "JavaScript", "HTML", "CSS", "Программирование");
		
		createNote(title, body, category, tagList);
	}
	
	@Test
	public void createNoteWithoutCategory() throws BlogException {
		thrown.expect(BlogException.class);
		thrown.expectMessage("Save after add or edit error");
		String title = "Проработка макета проекта";
		String body = "Проработка макета проекта. После того, как мы определились со структурой проекта можно составить макет проекта (схематично).\nДля отрисовки наброска можно использовать бумагу и ручку, Photoshop, любой другой редактор графики (раньше часто использовали Adobe Fireworks). Важно отметить, что данный этап – это не отрисовка готового дизайн-макета, а всего лишь схематичный набросок, выполненный для понимания того, как на сайте будут располагаться основные информационные блоки, графика и прочие элементы дизайна.";
		String category = null;
		List<String> tagList = Arrays.asList("Разработка веб-сайтов", "JavaScript", "HTML", "CSS", "Программирование");
		
		createNote(title, body, category, tagList);
	}
	
	@Test
	public void createNoteWitEmptyCategory() throws BlogException {
		thrown.expect(BlogException.class);
		thrown.expectMessage("Save after add or edit error");
		String title = "Проработка макета проекта";
		String body = "Проработка макета проекта. После того, как мы определились со структурой проекта можно составить макет проекта (схематично).\nДля отрисовки наброска можно использовать бумагу и ручку, Photoshop, любой другой редактор графики (раньше часто использовали Adobe Fireworks). Важно отметить, что данный этап – это не отрисовка готового дизайн-макета, а всего лишь схематичный набросок, выполненный для понимания того, как на сайте будут располагаться основные информационные блоки, графика и прочие элементы дизайна.";
		String category = "";
		List<String> tagList = Arrays.asList("Разработка веб-сайтов", "JavaScript", "HTML", "CSS", "Программирование");
		
		createNote(title, body, category, tagList);
	}

	private void noteCreationTagTest(String title, String body, String category, List<String> tags,
			int expectedTagAmount) throws BlogException {
		long noteId = noteCreationTest(title, body, category, tags);
		tagCreationTest(noteId, tags, expectedTagAmount);
	}

	private long noteCreationTest(String title, String body, String category, List<String> tags) throws BlogException {
		try {
			createNote(title, body, category, tags);

			PreparedStatement pst = connection
					.prepareStatement("select n.note_id, n.title as title, n.body as body, c.name as category "
							+ "from note n inner join category c on c.category_id = n.category_id where n.title = ?;");

			pst.setString(1, title);
			ResultSet rs = pst.executeQuery();

			int rowCount = 0;
			long noteId = 0;

			while (rs.next()) {
				rowCount++;

				noteId = rs.getLong("note_id");
				String titleDb = rs.getString("title");
				String bodyDb = rs.getString("body");
				String categoryDb = rs.getString("category");

				assertEquals(title, titleDb);
				assertEquals(body, bodyDb);
				assertEquals(category, categoryDb);
			}
			assertEquals(1, rowCount);

			return noteId;
		} catch (SQLException ex) {
			throw new BlogException("Ошибка при исполнении sql запроса", ex);
		}
	}

	private void tagCreationTest(long noteId, List<String> tags, int tagAmount) throws BlogException {
		List<String> actualTags = getTagLabelsByNoteIdByJDBC(noteId);

		assertEquals(tagAmount, actualTags.size());

		Collections.sort(tags);
		Collections.sort(actualTags);

		for (int i = 0; i < actualTags.size(); i++) {
			String expected = tags.get(i);
			String actual = actualTags.get(i);
			assertEquals(expected, actual);
		}
	}

	private void createNote(String title, String body, String category, List<String> tagList) throws BlogException {
		NoteBean noteBean = createNewNoteBean(title, body, category, getTagsAsString(tagList));

		actionController.setAddMode();

		AddEditController addEditController = createAddEditController();
		addEditController.initialize();
		addEditController.setNoteBean(noteBean);
		addEditController.save();

		actionController.setBrowsingMode();
	}

}

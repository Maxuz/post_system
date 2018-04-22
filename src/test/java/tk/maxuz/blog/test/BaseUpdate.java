package tk.maxuz.blog.test;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import tk.maxuz.blog.beans.AddEditController;
import tk.maxuz.blog.beans.NoteBean;
import tk.maxuz.blog.db.entity.Note;
import tk.maxuz.blog.exception.BlogException;

public class BaseUpdate extends BaseOperation {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	protected List<Note> initialNoteList = new ArrayList<>();

	protected interface IEditable {
		void edit(NoteBean selectedNoteBean);
	}

	@Before
	@Override
	public void before() throws BlogException {
		super.before();
		prepareData();
	}

	protected NoteBean edit(IEditable editable, int indexNoteToEdit) throws BlogException {
		List<NoteBean> noteBeanList = noteListController.getNoteBeanList();
		NoteBean selectedNoteBean = noteBeanList.get(indexNoteToEdit);
		noteListController.setSelectedNoteBean(selectedNoteBean);

		actionController.setEditMode();

		AddEditController addEditController = createAddEditController();
		selectedNoteBean = addEditController.getNoteBean();

		editable.edit(selectedNoteBean);

		addEditController.save();

		actionController.setBrowsingMode();

		return selectedNoteBean;
	}

	protected String getTagsByNoteId(long noteId) throws BlogException {
		return getTagsAsString(getTagLabelsByNoteIdByJDBC(noteId));
	}

	protected void testWithInitialNoteList(List<NoteBean> actual, int currentEditIndex, String expectedTitle,
			String expectedBody, String expectedCategory, String expectedTags) {
		assertEquals(initialNoteList.size(), actual.size());

		for (int i = 0; i < initialNoteList.size(); i++) {
			NoteBean actualNoteBean = actual.get(i);
			if (i == currentEditIndex) {
				assertEquals(expectedTitle, actualNoteBean.getTitle());
				assertEquals(expectedBody, actualNoteBean.getBody());
				assertEquals(expectedCategory, actualNoteBean.getCategory());
				assertEquals(expectedTags, actualNoteBean.getTags());
			} else {
				Note expectedNote = initialNoteList.get(i);
				assertEquals(expectedNote.getTitle(), actualNoteBean.getTitle());
				assertEquals(expectedNote.getBody(), actualNoteBean.getBody());
				assertEquals(expectedNote.getCategory().getName(), actualNoteBean.getCategory());
				assertEquals(tagController.getTagsAsString(expectedNote), actualNoteBean.getTags());
			}
		}
	}

	protected void testWithDatabase(int currentEditIndex, String expectedTitle, String expectedBody,
			String expectedCategory, String expectedTags) throws BlogException {
		try {
			List<NoteBean> actualNoteBeanList = new ArrayList<>();
			Statement st = connection.createStatement();
			String sql = "select n.note_id as note_id, n.title as title, n.body as body, c.name as category from note n inner join category c on n.category_id = c.category_id order by n.create_date desc";

			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				NoteBean nb = new NoteBean();
				nb.setTitle(rs.getString("title"));
				nb.setBody(rs.getString("body"));
				nb.setCategory(rs.getString("category"));
				nb.setTags(getTagsByNoteId(rs.getLong("note_id")));
				actualNoteBeanList.add(nb);
			}

			testWithInitialNoteList(actualNoteBeanList, currentEditIndex, expectedTitle, expectedBody, expectedCategory,
					expectedTags);
		} catch (SQLException ex) {
			throw new BlogException("Ошибка при исполнении sql запроса", ex);
		}
	}

	private void prepareData() throws BlogException {
		initialNoteList.add(addNoteToDatabase("Создание веб-сайта. Курс молодого бойца",
				"Проработка макета проекта. После того, как мы определились со структурой проекта можно составить макет проекта (схематично).\nДля отрисовки наброска можно использовать бумагу и ручку, Photoshop, любой другой редактор графики (раньше часто использовали Adobe Fireworks). Важно отметить, что данный этап – это не отрисовка готового дизайн-макета, а всего лишь схематичный набросок, выполненный для понимания того, как на сайте будут располагаться основные информационные блоки, графика и прочие элементы дизайна.",
				"Разработка", Arrays.asList("Разработка веб-сайтов", "JavaScript", "HTML", "CSS", "Программирование")));

		initialNoteList.add(addNoteToDatabase("Искусственные нейронные сети для новичков, часть 1",
				"Линейная регрессия — метод восстановления зависимости между двумя переменными. Линейная означает, что мы предполагаем, что переменные выражаются через уравнение вида:  Эпсилон здесь — это ошибка модели. Также для наглядности и простоты будем иметь дело с одномерной моделью — многомерность не прибавляет сложности, но иллюстрации сделать не выйдет. ",
				"Разработка", Arrays.asList("Машинное обучение", "Python")));

		initialNoteList.add(addNoteToDatabase("Как мы NoSQL в «реляционку» реплицировали",
				"Evolve читает журналы транзакций, трансформирует их и записывает строки в таблицы уже на реляционном приемнике.",
				"Базы данных", Arrays.asList("NoSQL", "Oracle")));

		initialNoteList.add(addNoteToDatabase("ВАйти в АйТи",
				"Но фоне роста зарплат в ИТ отрасли стало популярным движение под названием \"вайти в айти\". Это когда живешь себе такой, учишься или работашь — врачом или юристом, или вообще не работаешь и тут такой внезапно понимаешь, что все тлен, надо войти в айти. Там можно и покемонов поганять, и смузи попить, и пропитчить кого-то на кухне (когда никто не видит), а еще и платят за это. И вот как после дождя начали появляться ИТ курсы, школы повышения квалификации, резко все стали экспертами в образовании, выучили страшное слово \"курсера\", а некоторые особенно удачливые еще и пару грантов под это дело взяли. Чтобы все вошли в айти.",
				"Управление", Arrays.asList("")));

		initialNoteList.add(addNoteToDatabase("Подключение шлюзов Intel для интернета вещей к IBM Watson",
				"Когда речь идёт о разработке для интернета вещей, на первый план выходят скорость и качество. Чем быстрее идея превратится в стабильный рабочий прототип, тем больше у неё шансов развиться дальше, пойти в производство и стать настоящей «вещью», которую, вполне возможно, сочтут полезной миллионы. Что нужно для такого превращения? Intel и IBM подготовили ответ на этот и многие другие насущные вопросы IoT-разработчиков. А именно, речь идёт о совместной инициативе компаний, которая направлена на то, чтобы дать всем желающим современные аппаратные решения (Intel IoT Developer Kit) и облачную платформу, рассчитанную на нужды интернета вещей (IBM Watson IoT). ",
				"Разработка", Arrays.asList("Программирование")));

		Collections.reverse(initialNoteList);
	}
}

package tk.maxuz.blog.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
public class Note implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "note_id")
	@SequenceGenerator(name = "note_note_id_seq", sequenceName = "note_note_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "note_note_id_seq")
	private int id;

	@Column(name = "create_date", updatable = false)
	@Generated(GenerationTime.INSERT)
	private Timestamp createDate;

	@NotNull
	@Column(unique = true)
	private String title;

	@NotNull
	private String body;

	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToMany(mappedBy = "notes")
	private List<Tag> tags = new ArrayList<>();

	public Note() {
	}

	public int getId() {
		return id;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void addTag(Tag tag) {
		tags.add(tag);
		tag.getNotes().add(this);
	}

	public void removeTag(Tag tag) {
		tag.getNotes().remove(this);
		tags.remove(tag);
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	/*
	 * @Override public boolean equals(Object o) {
	 * 
	 * if (o == this) return true; if (!(o instanceof Note)) { return false; }
	 * Note note = (Note) o; return title.equals(note.title) &&
	 * body.equals(note.body) &&
	 * category.getName().equals(note.category.getName()) &&
	 * createDate.getTime() == note.createDate.getTime(); }
	 * 
	 * @Override public int hashCode() { return Objects.hash(title, body,
	 * category.getName(), createDate.getTime()); }
	 */

}

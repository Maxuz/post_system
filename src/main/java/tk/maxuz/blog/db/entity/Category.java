package tk.maxuz.blog.db.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;

@Entity
@Table
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "category_id")
	@SequenceGenerator(name = "category_category_id_seq", sequenceName = "category_category_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_category_id_seq")
	private int id;

	@NotNull
	private String name;

	@OneToMany(mappedBy = "category")
	@Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private List<Note> notes;

	public Category() {
	}

	public Category(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void addNote(Note note) {
		notes.add(note);
		note.setCategory(this);
	}

	public void removeNote(Note note) {
		notes.remove(note);
		note.setCategory(null);
	}
}

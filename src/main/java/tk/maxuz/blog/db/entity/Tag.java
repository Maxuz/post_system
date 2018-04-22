package tk.maxuz.blog.db.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

@Entity
public class Tag implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "tag_id")
	@SequenceGenerator(name = "tag_tag_id_seq", sequenceName = "tag_tag_id_seq", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_tag_id_seq")
	private int id;

	@NotNull
	private String label;

	@ManyToMany
	@JoinTable(name = "note_tag", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "note_id"))
	private List<Note> notes = new ArrayList<>();

	public Tag() {
	}

	public Tag(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Note> getNotes() {
		return notes;
	}

	// @Override
	// public boolean equals(Object o) {
	//
	// if (o == this)
	// return true;
	// if (!(o instanceof Tag)) {
	// return false;
	// }
	// Tag tag = (Tag) o;
	// return label.equals(tag.label);
	// }
	//
	// @Override
	// public int hashCode() {
	// return Objects.hash(label);
	// }

}

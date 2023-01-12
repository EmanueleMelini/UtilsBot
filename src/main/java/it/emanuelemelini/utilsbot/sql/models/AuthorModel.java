package it.emanuelemelini.utilsbot.sql.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "authors")
public class AuthorModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_author")
	private Long authorId;

	@Column(name = "discord_id")
	private String discordId;

	@Column(name = "discord_name")
	private String name;

	@Column(name = "deleted")
	private Boolean deleted;

}

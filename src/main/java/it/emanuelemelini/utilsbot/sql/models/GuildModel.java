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
@Entity(name = "guild")
public class GuildModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_guild")
	private Long guildId;

	@Column(name = "discord_id")
	private String discordId;

	@Column(name = "discord_name")
	private String name;

	@Column(name = "category_id")
	private String categoryId;

	@Column(name = "voice_name")
	private String voiceName;

	@Column(name = "deleted")
	private Boolean deleted;

}
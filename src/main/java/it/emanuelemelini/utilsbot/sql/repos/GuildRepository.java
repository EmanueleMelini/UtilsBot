package it.emanuelemelini.utilsbot.sql.repos;

import it.emanuelemelini.utilsbot.sql.models.GuildModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuildRepository extends CrudRepository<GuildModel, Long> {

	GuildModel getGuildByGuildIdAndDeleted(Long guildId, Boolean deleted);

	GuildModel getGuildByDiscordIdAndDeleted(String discordId, Boolean deleted);

	List<GuildModel> getGuildsByDeleted(Boolean deleted);

}

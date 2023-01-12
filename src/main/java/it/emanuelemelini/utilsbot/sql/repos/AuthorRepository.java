package it.emanuelemelini.utilsbot.sql.repos;

import it.emanuelemelini.utilsbot.sql.models.AuthorModel;
import it.emanuelemelini.utilsbot.sql.models.GuildModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends CrudRepository<AuthorModel, Long> {

	AuthorModel getAuthorByAuthorIdAndDeleted(Long authorId, Boolean deleted);

	AuthorModel getAuthorByDiscordIdAndDeleted(String discordId, Boolean deleted);

	List<AuthorModel> getAuthorsByDeleted(Boolean deleted);

}
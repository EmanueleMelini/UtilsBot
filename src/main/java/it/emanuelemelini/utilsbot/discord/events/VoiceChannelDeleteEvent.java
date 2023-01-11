package it.emanuelemelini.utilsbot.discord.events;

import it.emanuelemelini.utilsbot.UtilsBotApplication;
import it.emanuelemelini.utilsbot.sql.models.GuildModel;
import it.emanuelemelini.utilsbot.sql.repos.GuildRepository;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;

import java.util.ArrayList;
import java.util.List;

import static it.emanuelemelini.utilsbot.UtilsBotApplication.REPLACE_CHAR;

@Log4j2
public class VoiceChannelDeleteEvent extends CoreChannelEvent {

	private final GuildRepository guildRepository;

	public VoiceChannelDeleteEvent() {
		super("voicedelete", "On Voice Channel delete event");
		guildRepository = UtilsBotApplication.getApplicationContext()
				.getBean(GuildRepository.class);
	}

	@Override
	public void exec(ChannelDeleteEvent event) {
		if (!event.getChannelType()
				.equals(ChannelType.VOICE))
			return;

		Guild discordGuild = event.getGuild();

		VoiceChannel voiceChannel = event.getChannel()
				.asVoiceChannel();

		GuildModel guildModel = guildRepository.getGuildByDiscordIdAndDeleted(discordGuild.getId(), false);
		if (guildModel == null)
			return;

		Category discordCategory = voiceChannel.getParentCategory();
		if (discordCategory == null || !discordCategory.getId()
				.equals(guildModel.getCategoryId()) || guildModel.getVoiceName()
				.isBlank())
			return;

		if (voiceChannel.getName()
				.replaceAll("[^0-9]", "")
				.isBlank())
			return;

		int channelNumber = -1;

		List<VoiceChannel> voiceChannels = new ArrayList<>();
		voiceChannels.add(voiceChannel);
		voiceChannels.addAll(discordCategory.getVoiceChannels());

		for (VoiceChannel channel : voiceChannels) {

			String channelName = channel.getName();

			String channelNumberBefore = channelName.replaceAll("[^0-9]", "");

			if (!channelNumberBefore.isBlank())
				try {
					int channelNumberList = Integer.parseInt(channelNumberBefore);
					if (channelNumberList > channelNumber)
						channelNumber = channelNumberList;

				} catch (Exception e) {
					e.printStackTrace();
				}

		}

		if (channelNumber == -1)
			return;

		String newVoiceName = guildModel.getVoiceName();
		newVoiceName = newVoiceName.replace(REPLACE_CHAR, String.valueOf(channelNumber + 1));

		//VoiceChannel newVoiceChannel = voiceChannel.createCopy().complete();
		//newVoiceChannel.getManager().setName(newVoiceName).queue();

		VoiceChannel newVoiceChannel = discordCategory.createVoiceChannel(newVoiceName)
				.complete();

		newVoiceChannel.getManager()
				.setUserLimit(voiceChannel.getUserLimit())
				.setBitrate(voiceChannel.getBitrate())
				.setNSFW(voiceChannel.isNSFW())
				//.setPosition(voiceChannel.getPosition())
				.setRegion(voiceChannel.getRegion())
				.queue();

		for (PermissionOverride permissionOverride : voiceChannel.getPermissionOverrides()) {
			if (permissionOverride.getPermissionHolder() != null) {
				newVoiceChannel.upsertPermissionOverride(permissionOverride.getPermissionHolder())
						.setPermissions(permissionOverride.getAllowed(), permissionOverride.getDenied())
						.queue();

			}
		}

	}

}

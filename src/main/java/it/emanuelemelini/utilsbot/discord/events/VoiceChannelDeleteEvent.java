package it.emanuelemelini.utilsbot.discord.events;

import it.emanuelemelini.utilsbot.UtilsBotApplication;
import it.emanuelemelini.utilsbot.sql.models.GuildModel;
import it.emanuelemelini.utilsbot.sql.repos.GuildRepository;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public @Nullable RestAction<?> exec(ChannelDeleteEvent event) {
		if (!event.getChannelType()
				.equals(ChannelType.VOICE))
			return null;

		Guild discordGuild = event.getGuild();

		VoiceChannel voiceChannel = event.getChannel()
				.asVoiceChannel();

		GuildModel guildModel = guildRepository.getGuildByDiscordIdAndDeleted(discordGuild.getId(), false);
		if (guildModel == null)
			return null;

		Category discordCategory = voiceChannel.getParentCategory();
		if (discordCategory == null || !discordCategory.getId()
				.equals(guildModel.getCategoryId()) || guildModel.getVoiceName()
				.isBlank())
			return null;

		List<Integer> oldChannelNumberList = getNumbersFromString(voiceChannel.getName());
		if (oldChannelNumberList.isEmpty())
			return null;

		int oldChannelNumber = oldChannelNumberList.stream()
				.mapToInt(n -> n)
				.max()
				.orElse(-1);
		if (oldChannelNumber == -1)
			return null;

		List<VoiceChannel> voiceChannels = new ArrayList<>();
		voiceChannels.add(voiceChannel);
		voiceChannels.addAll(discordCategory.getVoiceChannels()
				.stream()
				.filter(ch -> !getNumbersFromString(ch.getName()).isEmpty())
				.toList());

		List<Integer> voiceChannelsNumberList = voiceChannels.stream()
				.map(Channel::getName)
				.map(this::getNumbersFromString)
				.map(names -> names.stream()
						.mapToInt(n -> n)
						.max()
						.orElse(-1))
				.toList();

		int higherChannelNumber = voiceChannelsNumberList.stream()
				.mapToInt(n -> n)
				.max()
				.orElse(-1);

		int lowerChannelNumber = voiceChannelsNumberList.stream()
				.mapToInt(n -> n)
				.min()
				.orElse(-1);

		if (lowerChannelNumber != oldChannelNumber) {
			List<VoiceChannel> voiceChannelToUpdate = new ArrayList<>(voiceChannels);
			voiceChannelToUpdate.remove(voiceChannel);

			if (higherChannelNumber != oldChannelNumber) {
				voiceChannelToUpdate.removeAll(voiceChannelToUpdate.stream()
						.filter(channel -> getNumbersFromString(channel.getName()).stream()
								.mapToInt(n -> n)
								.max()
								.orElse(-1) > oldChannelNumber)
						.toList());
			}

			voiceChannelToUpdate.forEach(channel -> {
				int oldNumber = getNumbersFromString(channel.getName()).stream()
						.mapToInt(n -> n)
						.max()
						.orElse(-1);
				channel.getManager()
						.setName(guildModel.getVoiceName()
								.replace(REPLACE_CHAR, String.valueOf(oldNumber + 1)))
						.queue();
			});

			discordCategory.modifyVoiceChannelPositions()
					.sortOrder((channel1, channel2) -> {
						if (getNumbersFromString(channel1.getName()).isEmpty())
							return -1;
						else {
							int n1 = getNumbersFromString(channel1.getName()).stream().mapToInt(n -> n).max().orElse(1);
							int n2 = getNumbersFromString(channel2.getName()).stream().mapToInt(n -> n).max().orElse(1);
							return Integer.compare(n1, n2);
						}
					}).queue();

		}

		if (higherChannelNumber == -1)
			return null;

		String newVoiceChannelName = guildModel.getVoiceName();
		newVoiceChannelName = newVoiceChannelName.replace(REPLACE_CHAR, String.valueOf(higherChannelNumber + 1));

		VoiceChannel newVoiceChannel = discordCategory.createVoiceChannel(newVoiceChannelName)
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

		return null;

	}

	@NotNull
	private List<Integer> getNumbersFromString(String string) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(string);
		List<Integer> list = new ArrayList<>();
		while (matcher.find()) {
			String matched = matcher.group();
			try {
				list.add(Integer.parseInt(matched));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

}

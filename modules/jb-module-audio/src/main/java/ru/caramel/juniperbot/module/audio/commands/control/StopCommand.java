/*
 * This file is part of JuniperBotJ.
 *
 * JuniperBotJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * JuniperBotJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JuniperBotJ. If not, see <http://www.gnu.org/licenses/>.
 */
package ru.caramel.juniperbot.module.audio.commands.control;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import ru.caramel.juniperbot.core.command.model.BotContext;
import ru.caramel.juniperbot.core.command.model.DiscordCommand;
import ru.caramel.juniperbot.module.audio.commands.AudioCommand;

@DiscordCommand(
        key = StopCommand.KEY,
        description = "discord.command.stop.desc",
        group = "discord.command.group.music",
        priority = 107)
public class StopCommand extends AudioCommand {

    public static final String KEY = "discord.command.stop.key";

    @Override
    public boolean doInternal(GuildMessageReceivedEvent message, BotContext context, String content) {
        if (playerService.stop(message.getMember(), message.getGuild())) {
            return ok(message, "discord.command.audio.stop.member", message.getMember().getEffectiveName());
        }
        fail(message);
        messageManager.onMessage(message.getChannel(), "discord.command.audio.notStarted");
        return true;
    }
}

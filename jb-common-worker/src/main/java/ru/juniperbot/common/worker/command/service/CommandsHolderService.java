/*
 * This file is part of JuniperBot.
 *
 * JuniperBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * JuniperBot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JuniperBot. If not, see <http://www.gnu.org/licenses/>.
 */
package ru.juniperbot.common.worker.command.service;

import ru.juniperbot.common.worker.command.model.Command;
import ru.juniperbot.common.worker.command.model.DiscordCommand;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface CommandsHolderService {

    Map<String, Command> getCommands();

    Map<String, Command> getPublicCommands();

    Map<String, List<DiscordCommand>> getDescriptors();

    Command getByLocale(String localizedKey);

    Command getByLocale(String localizedKey, String locale);

    Command getByLocale(String localizedKey, Locale locale);

    Command getByLocale(String localizedKey, boolean anyLocale);

    boolean isAnyCommand(String key);

    Set<String> getPublicCommandKeys();
}

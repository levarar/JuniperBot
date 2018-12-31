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
package ru.caramel.juniperbot.core.messaging.placeholder;

import lombok.NonNull;
import net.dv8tion.jda.core.entities.Member;
import org.springframework.context.ApplicationContext;
import ru.caramel.juniperbot.core.messaging.placeholder.node.FunctionalNodePlaceholderResolver;
import ru.caramel.juniperbot.core.messaging.placeholder.node.SingletonNodePlaceholderResolver;
import ru.caramel.juniperbot.core.service.MessageService;
import ru.caramel.juniperbot.core.utils.TriFunction;

import java.util.Locale;
import java.util.Map;

public class MemberPlaceholderResolver extends FunctionalNodePlaceholderResolver<Member> {

    private final static Map<String, TriFunction<Member, Locale, ApplicationContext, ?>> ACCESSORS = Map.of(
            "id", (m, l, c) -> m.getUser().getId(),
            "mention", (m, l, c) -> m.getAsMention(),
            "nickname", (m, l, c) -> m.getEffectiveName(),
            "name", (m, l, c) -> m.getUser().getName(),
            "discriminator", (m, l, c) -> m.getUser().getDiscriminator(),
            "joinedAt", (m, l, c) -> DateTimePlaceholderResolver.of(m.getJoinDate(), l, m.getGuild(), c),
            "createdAt", (m, l, c) -> DateTimePlaceholderResolver.of(m.getUser().getCreationTime(), l, m.getGuild(), c),
            "status", (m, l, c) -> c.getBean(MessageService.class).getEnumTitle(m.getOnlineStatus()),
            "avatarUrl", (m, l, c) -> m.getUser().getEffectiveAvatarUrl()
    );

    private final Member member;

    public MemberPlaceholderResolver(@NonNull Member member,
                                     @NonNull Locale locale,
                                     @NonNull ApplicationContext applicationContext) {
        super(ACCESSORS, locale, applicationContext);
        this.member = member;
    }

    @Override
    protected Member getObject() {
        return member;
    }

    @Override
    public Object getValue() {
        return member.getAsMention();
    }

    public static SingletonNodePlaceholderResolver of(@NonNull Member member,
                                                      @NonNull Locale locale,
                                                      @NonNull ApplicationContext context,
                                                      @NonNull String name) {
        return new SingletonNodePlaceholderResolver(name, new MemberPlaceholderResolver(member, locale, context));
    }
}

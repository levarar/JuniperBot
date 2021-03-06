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
package ru.juniperbot.common.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.juniperbot.common.persistence.entity.YouTubeChannel;
import ru.juniperbot.common.persistence.entity.YouTubeConnection;
import ru.juniperbot.common.persistence.repository.base.BaseSubscriptionRepository;

import java.util.Date;
import java.util.List;

@Repository
public interface YouTubeConnectionRepository extends BaseSubscriptionRepository<YouTubeConnection> {

    @Query("SELECT c FROM YouTubeConnection c WHERE c.channel.channelId = :channelId " +
            "AND c.webHook IN (SELECT w FROM WebHook w WHERE w.enabled = true AND w.hookId IS NOT NULL AND w.token IS NOT NULL)")
    List<YouTubeConnection> findActiveConnections(@Param("channelId") String channelId);

    @Query("SELECT DISTINCT c.channel FROM YouTubeConnection c WHERE (c.channel.expiresAt IS NULL OR c.channel.expiresAt <= :expirationDate) " +
            "AND c.webHook IN (SELECT w FROM WebHook w WHERE w.enabled = true AND w.hookId IS NOT NULL AND w.token IS NOT NULL)")
    List<YouTubeChannel> findToResubscribe(@Param("expirationDate") Date date);
}

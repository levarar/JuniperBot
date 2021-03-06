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
package ru.juniperbot.common.worker.modules.audit.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.juniperbot.common.model.AuditActionType;
import ru.juniperbot.common.persistence.entity.AuditAction;
import ru.juniperbot.common.persistence.entity.AuditConfig;
import ru.juniperbot.common.persistence.repository.AuditActionRepository;
import ru.juniperbot.common.service.AuditConfigService;
import ru.juniperbot.common.worker.configuration.WorkerProperties;
import ru.juniperbot.common.worker.feature.service.FeatureSetService;
import ru.juniperbot.common.worker.modules.audit.model.AuditActionBuilder;
import ru.juniperbot.common.worker.modules.audit.provider.AuditForwardProvider;
import ru.juniperbot.common.worker.modules.audit.provider.ForwardProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuditServiceImpl
        implements AuditService {

    @Autowired
    private WorkerProperties workerProperties;

    @Autowired
    private AuditActionRepository actionRepository;

    @Autowired
    private AuditConfigService configService;

    @Autowired
    private FeatureSetService featureSetService;

    private Map<AuditActionType, AuditForwardProvider> forwardProviders;

    @Override
    public AuditAction save(AuditAction action, Map<String, byte[]> attachments) {
        AuditConfig config = configService.getByGuildId(action.getGuildId());
        if (config != null && config.isEnabled()) {
            if (featureSetService.isAvailable(action.getGuildId())) {
                action = actionRepository.save(action);
            }
            if (MapUtils.isNotEmpty(forwardProviders)) {
                AuditForwardProvider forwardProvider = forwardProviders.get(action.getActionType());
                if (forwardProvider != null) {
                    forwardProvider.send(config, action, attachments);
                }
            }
        }
        return action;
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void runCleanUp() {
        runCleanUp(this.workerProperties.getAudit().getKeepMonths());
    }

    @Override
    @Transactional
    public void runCleanUp(int durationMonths) {
        log.info("Starting audit cleanup for {} months old", durationMonths);
        actionRepository.deleteByActionDateBefore(DateTime.now().minusMonths(durationMonths).toDate());
        log.info("Audit cleanup finished");
    }

    @Override
    public AuditActionBuilder log(long guildId, AuditActionType type) {
        return new AuditActionBuilder(guildId, type) {
            @Override
            @Transactional
            public AuditAction save() {
                return AuditServiceImpl.this.save(this.action, attachments);
            }
        };
    }

    @Autowired(required = false)
    private void setForwardProviders(List<AuditForwardProvider> forwardProviders) {
        this.forwardProviders = forwardProviders.stream().collect(Collectors.toMap(
                e -> e.getClass().isAnnotationPresent(ForwardProvider.class)
                        ? e.getClass().getAnnotation(ForwardProvider.class).value() : null, e -> e));
    }
}

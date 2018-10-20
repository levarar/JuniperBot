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
package ru.caramel.juniperbot.web.controller.pub;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vk.api.sdk.callback.objects.messages.CallbackMessage;
import com.vk.api.sdk.callback.objects.messages.CallbackMessageType;
import com.vk.api.sdk.objects.wall.Wallpost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.caramel.juniperbot.core.model.exception.AccessDeniedException;
import ru.caramel.juniperbot.core.utils.GsonUtils;
import ru.caramel.juniperbot.module.social.persistence.entity.VkConnection;
import ru.caramel.juniperbot.module.social.service.VkService;
import ru.caramel.juniperbot.module.social.service.YouTubeService;
import ru.caramel.juniperbot.web.controller.base.BasePublicRestController;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class CallbackController extends BasePublicRestController {

    private static final Logger log = LoggerFactory.getLogger(CallbackController.class);

    private final Gson gson = GsonUtils.create();

    private final static Map<String, Type> CALLBACK_TYPES;

    static {
        Map<String, Type> types = new HashMap<>();

        types.put(CallbackMessageType.WALL_POST_NEW.getValue(), new TypeToken<CallbackMessage<Wallpost>>() {
        }.getType());

        types.put(CallbackMessageType.CONFIRMATION.getValue(), new TypeToken<CallbackMessage>() {
        }.getType());

        CALLBACK_TYPES = Collections.unmodifiableMap(types);
    }

    @Autowired
    private VkService vkService;

    @Autowired
    private YouTubeService youTubeService;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/vk/callback/{token}", method = RequestMethod.POST)
    @Transactional
    public String vkCallback(@RequestBody String content, @PathVariable("token") String token, HttpServletResponse response) {
        JsonObject json = gson.fromJson(content, JsonObject.class);
        String type = json.get("type").getAsString();
        Type typeOfClass = CALLBACK_TYPES.get(type);

        VkConnection connection = vkService.getForToken(token);
        if (connection == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        if (typeOfClass != null) {
            CallbackMessage message = gson.fromJson(json, typeOfClass);

            if (!CallbackMessageType.CONFIRMATION.equals(message.getType()) &&
                    !connection.getGroupId().equals(message.getGroupId())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }

            switch (message.getType()) {
                case CONFIRMATION:
                    return vkService.confirm(connection, message);
                case WALL_POST_NEW:
                    vkService.post(connection, message);
                    break;
            }
        }
        return "ok";
    }

    @RequestMapping(value = "/youtube/callback/publish", method = RequestMethod.POST)
    @Transactional
    public void youTubeCallback(@RequestBody String content,
                                @RequestParam("hub.verify_token") String secret) {
        if (!Objects.equals(youTubeService.getPubSubSecret(), secret)) {
            return;
        }
        log.info("YouTube request: {}", content);
    }

    @RequestMapping(value = "/youtube/callback/publish", method = RequestMethod.GET)
    @Transactional
    public String youTubeCallbackChallenge(@RequestParam("hub.verify_token") String secret,
                                           @RequestParam("hub.challenge") String challenge) {
        if (!Objects.equals(youTubeService.getPubSubSecret(), secret)) {
            throw new AccessDeniedException();
        }
        return challenge;
    }
}
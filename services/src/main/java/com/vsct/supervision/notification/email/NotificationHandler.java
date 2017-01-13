/*
 * This file is part of the Cerebro distribution.
 * (https://github.com/voyages-sncf-technologies/cerebro)
 * Copyright (C) 2017 VSCT.
 *
 * Cerebro is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, version 3 of the License.
 *
 * Cerebro is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.vsct.supervision.notification.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.vsct.supervision.config.CerebroConfiguration;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Subscription;
import com.vsct.supervision.seyren.api.SubscriptionType;

@Service
public class NotificationHandler {

    private final Sender sender;
    private final CerebroConfiguration configuration;
    private VelocityEngine velocityEngine;

    @Autowired
    public NotificationHandler(Sender sender, CerebroConfiguration configuration) {
        this.sender = sender;
        this.configuration = configuration;
        initVelocityEngine();
    }

    private void send(String title, String text, List<String> recipients){
        if(this.configuration.isUpdateNotificationsEnable()) {
            sender.send(title, text, recipients);
        }
    }

    public void sendAlarmHasBeenModified(Alarm alarm) {
        Map<String, String> model = new HashMap<>();
        model.put("status", "changed");
        model.put("alert", alarm.getName());
        model.put("link", configuration.getDashboardBaseUrl() + "/notifications/" + alarm.getId());
        String text = processTemplate("alarmModified.vm", model);
        send("One of your alerts has been changed", text, getRecipients(alarm));
    }

    public void sendAlarmHasBeenDeactivated(Alarm alarm) {
        Map<String, String> model = new HashMap<>();
        model.put("status", "disabled");
        model.put("alert", alarm.getName());
        model.put("link", configuration.getDashboardBaseUrl() + "/notifications/" + alarm.getId());
        String text = processTemplate("alarmModified.vm", model);
        send("One of your alerts has been disabled", text, getRecipients(alarm));
    }

    public String processTemplate(String templateName, Map model){
        return VelocityEngineUtils.mergeTemplateIntoString(
                velocityEngine, "templates/"+templateName, "UTF-8", model);
    }

    private List<String> getRecipients(Alarm alarm){
        return alarm.getSubscriptions()
            .stream()
            .filter(subscription -> subscription.getType() == SubscriptionType.EMAIL && subscription.isEnabled())
            .map(Subscription::getTarget)
            .collect(Collectors.toList());
    }

    private void initVelocityEngine(){
        velocityEngine = new VelocityEngine(sender.getProperties());
        velocityEngine.init();
    }

}

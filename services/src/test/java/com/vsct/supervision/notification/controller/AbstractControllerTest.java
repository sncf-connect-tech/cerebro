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

package com.vsct.supervision.notification.controller;

import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.vsct.supervision.notification.AppTest;
import com.vsct.supervision.notification.model.SelectOption;
import com.vsct.supervision.notification.model.Stat;
import com.vsct.supervision.seyren.api.Alert;
import com.vsct.supervision.seyren.api.AlertType;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Subscription;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AppTest.class)
public abstract class AbstractControllerTest {

    public static Alarm getAlarm(String id){
        Alarm alarm = new Alarm();
        alarm.setId(id);
        alarm.setName("name");
        alarm.setTarget("target");
        return alarm;
    }

    public static Alarm getAlarm(String id, int idx){
        Alarm alarm = new Alarm();
        alarm.setId(id+idx);
        alarm.setName("name"+idx);
        alarm.setTarget("target"+idx);
        return alarm;
    }

    public static Subscription getSubscription(String id){
        Subscription subscription = new Subscription();
        subscription.setId(id);
        return subscription;
    }

    public static Alert getAlert(String id){
        Alert alert = new Alert();
        alert.setId(id);
        return alert;
    }

    public static List<Stat> getStats(int nb, AlertType alertType){
        final Stat.StatBuilder stat = new Stat.StatBuilder();
        for(int i=0; i < nb; i++){
            stat.increment("alarm"+i,alertType);
        }
        return stat.build();
    }

    public static SelectOption getSelectOption(String id){
        return new SelectOption(id,"label-"+id);

    }
}

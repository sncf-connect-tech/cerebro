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

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import com.vsct.supervision.notification.log.Loggable;

@Loggable(service = "email", method = "send")
@Component
public class MailSenderImpl extends Sender {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailSenderImpl.class);

    private JavaMailSenderImpl mailSender;
    private Properties properties;

    public MailSenderImpl() {
        properties = getProperties();
        initMailSender();
    }


    @Override
    public void send(final String title, final String text, final List<String> recipients) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(toInternetAddresses(recipients));
            message.setFrom(properties.getProperty("sender"));
            message.setSubject(title);
            message.setSentDate(new Date());

            message.setText(text, true);
        };
        LOGGER.debug("send mail with title: {}", title);
        mailSender.send(preparator);
    }

    private InternetAddress[] toInternetAddresses(List<String> recipients){
        InternetAddress[] addresses = new InternetAddress[recipients.size()];
        for(int i=0; i<recipients.size(); i++){
            try {
                addresses[i] = new InternetAddress(recipients.get(i));
            } catch (AddressException e) {
                LOGGER.error("Invalid email address",e);
                addresses[i] = new InternetAddress();
            }
        }
        return addresses;
    }

    private void initMailSender(){
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(properties.getProperty("server"));
        mailSender.setPort(Integer.parseInt(properties.getProperty("port")));
        mailSender.setDefaultEncoding("UTF-8");
    }
}
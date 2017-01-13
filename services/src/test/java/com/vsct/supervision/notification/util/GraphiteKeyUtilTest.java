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

package com.vsct.supervision.notification.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class GraphiteKeyUtilTest {
    @Test
    public void testExtractGraphiteKeys() throws Exception {
        assertThat(GraphiteKeyUtil.extractGraphiteKeys("log.hdfs.adminreport.dfsRemaining"))
            .containsOnly("log.hdfs.adminreport.dfsRemaining");

        assertThat(GraphiteKeyUtil.extractGraphiteKeys("keepLastValue(DT.log.flume.central.*-bck_63002.channelMut*.ChannelSize)"))
            .containsOnly("DT.log.flume.central.*-bck_63002.channelMut*.ChannelSize");

        assertThat(GraphiteKeyUtil.extractGraphiteKeys(
            "keepLastValue(alias(sumSeries(keepLastValue(nagios.*.Check_Socket_TUX.60004)), 'Nbre de socket port : 60004'))"))
            .containsOnly("nagios.*.Check_Socket_TUX.60004");

        assertThat(GraphiteKeyUtil.extractGraphiteKeys(
            "keepLastValue(summarize(nonNegativeDerivative(sumSeries(Zenith.MPD.MPD.*.*.PRD1.WAS.*.any.any.soap_finTransaction-POST.POST"
                + ".io.any.any.vol.any.1min.count)), '1h', 'sum', false))"))
            .containsOnly("Zenith.MPD.MPD.*.*.PRD1.WAS.*.any.any.soap_finTransaction-POST.POST.io.any.any.vol.any.1min.count");

        assertThat(GraphiteKeyUtil.extractGraphiteKeys(
            "keepLastValue(asPercent(summarize(nonNegativeDerivative(DT.sum.flume.sink.piedipartino-bck_63101.channel_SUM_AQMI"
                + ".EventTakeSuccessCount),'1h','avg'),averageSeries(timeShift(summarize(nonNegativeDerivative(DT.sum.flume.sink"
                + ".piedipartino-bck_63101.channel_SUM_AQMI.EventTakeSuccessCount),'1h','avg'),'1d'))))"))
            .containsOnly("DT.sum.flume.sink.piedipartino-bck_63101.channel_SUM_AQMI.EventTakeSuccessCount",
                "DT.sum.flume.sink.piedipartino-bck_63101.channel_SUM_AQMI.EventTakeSuccessCount");

        assertThat(GraphiteKeyUtil.extractGraphiteKeys(
            "keepLastValue(sumSeriesWithWildcards(aliasSub(sumSeriesWithWildcards(atos.VSL.*.PRD*.{LIL|SDN}.{BANQUE_POPULAIRE,"
                + "BANQUE_POSTALE,BNP-PARIBAS,CAISSE_D_EPARGNE,CREDIT_AGRICOLE,CREDIT_MUTUEL,GIE,HSBC,OTHERS,SOCIETE_GENERALE|SG,"
                + "CREDIT_COOP}, 4), 'atos.VSL.*.ATO.(\\w+)', '\1'), 2))"))
            .containsOnly(
                "atos.VSL.*.PRD*.{LIL|SDN}.{BANQUE_POPULAIRE,BANQUE_POSTALE,BNP-PARIBAS,CAISSE_D_EPARGNE,CREDIT_AGRICOLE,CREDIT_MUTUEL,"
                    + "GIE,HSBC,OTHERS,SOCIETE_GENERALE|SG,CREDIT_COOP}",
                "atos.VSL.*.ATO.");

        assertThat(GraphiteKeyUtil.extractGraphiteKeys(
            "keepLastValue(scale(divideSeries(diffSeries(functional.monitoring.omniture.VSL.CCL.frequentation,averageSeries(timeShift"
                + "(functional.monitoring.omniture.VSL.CCL.frequentation, '7d'), timeShift(functional.monitoring.omniture.VSL.CCL"
                + ".frequentation, '14d'), timeShift(functional.monitoring.omniture.VSL.CCL.frequentation, '21d'), timeShift(functional"
                + ".monitoring.omniture.VSL.CCL.frequentation, '28d'))), functional.monitoring.omniture.VSL.CCL.frequentation), 100))"))
            .containsOnly("functional.monitoring.omniture.VSL.CCL.frequentation");

        assertThat(GraphiteKeyUtil.extractGraphiteKeys("keepLastValue(asPercent(DT.MPD.collectd.corone.df-MONGODB.df_complex-used, "
            + "sumSeries(DT.MPD.collectd.corone.df-MONGODB.df_complex-free, DT.MPD.collectd.corone.df-MONGODB.df_complex-used)))"))
            .containsOnly("DT.MPD.collectd.corone.df-MONGODB.df_complex-free", "DT.MPD.collectd.corone.df-MONGODB.df_complex-used");

        assertThat(GraphiteKeyUtil.extractGraphiteKeys(
            "summarize(aliasByNode(DT.SUM.PREP1.collectd.{rea,villalba,zambone}.*-election_rate.gauge-1-minutes, 4),'30min','avg')"))
            .containsOnly("DT.SUM.PREP1.collectd.{rea,villalba,zambone}.*-election_rate.gauge-1-minutes");

        assertThat(GraphiteKeyUtil.extractGraphiteKeys(
            "diffSeries(summarize(aliasByNode(DT.SUM.PREP1.collectd{rea}.*-election_rate.gauge-1-minutes, 4),'30min','avg'),timeShift"
                + "(summarize(aliasByNode(DT.SUM.PREP1.collectd.{rea,villalba,zambone}.*-election_rate.gauge-1-minutes, 4),'30min','avg')"
                + ", '10min'))")).containsOnly("DT.SUM.PREP1.collectd{rea}.*-election_rate.gauge-1-minutes",
            "DT.SUM.PREP1.collectd.{rea,villalba,zambone}.*-election_rate.gauge-1-minutes");
    }
}

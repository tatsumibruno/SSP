/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
Ext.define('Ssp.view.tools.profile.CurrentSchedule', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.profilecurrentschedule',
    mixins: ['Deft.mixin.Injectable', 'Deft.mixin.Controllable'],
    inject: {
        store: 'currentScheduleStore',
        textStore: 'sspTextStore'
    },
    controller: 'Ssp.controller.tool.profile.CurrentScheduleViewController',
    width: '100%',
    height: '100%',
	minHeight: 615,
    autoScroll: true,
    initComponent: function(){
        var me = this;

        Ext.applyIf(me, {
            xtype: 'gridcolumn',
          	title: this.textStore.getValueByCode('ssp.label.main.current-schedule.title','Current Schedule'),
            columns: [{
                dataIndex: 'termCode',
                text: me.textStore.getValueByCode('ssp.label.main.current-schedule.term-code','Term'),
                flex: 0.10
            }, {
                dataIndex: 'formattedCourse',
                text: me.textStore.getValueByCode('ssp.label.main.current-schedule.formatted-course','Course'),
				flex: 0.14
            }, {
                dataIndex: 'sectionNumber',
                text: me.textStore.getValueByCode('ssp.label.main.current-schedule.section-number','Section'),
				flex: 0.10
            }, {
                dataIndex: 'title',
                text: me.textStore.getValueByCode('ssp.label.main.current-schedule.course-title','Course Title'),
				flex: 0.25
            }, {
                xtype: 'numbercolumn',
                dataIndex: 'creditEarned',
                text: me.textStore.getValueByCode('ssp.label.main.current-schedule.credit-earned','Cr Hrs'),
                format: '0.00',
				flex: 0.07
            }, {
                dataIndex: 'facultyName',
                text: this.textStore.getValueByCode('ssp.label.main.current-schedule.faculty-name','Instructor'),
                flex: 0.20
            }, {
                dataIndex: 'statusCode',
                text: me.textStore.getValueByCode('ssp.label.main.current-schedule.status-code','Status'),
                flex: 0.07
            }, {
                dataIndex: 'audited',
                text: me.textStore.getValueByCode('ssp.label.main.current-schedule.audited','Audited'),
                flex: 0.07
            }, {
                dataIndex: 'participation',
                text: me.textStore.getValueByCode('ssp.label.main.current-schedule.participation','Participation'),
                flex: 0.10
            }, {
                dataIndex: 'evaluatedParticipationIndicator',
                text: '',
                flex: 0.001,
                renderer: function(value, metaData) {
                   if (value == 2) {
                       metaData.tdAttr = 'data-qtip="Course Participation is High"';
                       metaData.style = 'background-color: #008a00; background-image: none; margin:0px 0px 0px 0px;';
                       return '<i class="fa fa-check-circle" style="color: #FFF"></i>';

                   } else if (value == 1) {
                       metaData.tdAttr = 'data-qtip="Course Participation is Medium"';
                       metaData.style = 'background-color: #7a7a00; background-image: none; margin:0px 0px 0px 0px;';
                       return '<i class="fa fa-minus-circle" style="color: #FFF"></i>';

                   } else if (value == 0) {
                       metaData.tdAttr = 'data-qtip="Course Participation is Low"';
                       metaData.style = 'background-color: #990000; background-image: none; margin:0px 0px 0px 0px;';
                       return '<i class="fa fa-times-circle" style="color: #FFF"></i>';

                   } else if (value == -1) {
                       metaData.tdAttr = 'data-qtip="Course Participation is Not Available"';
                       metaData.style = 'background-color: #767676; background-image: none; margin:0px 0px 0px 0px;';
                       return '<i class="fa fa-ban" style="color: #FFF"></i>';

                   } else {
                       //do nothing here because participation indicator may be disabled
                   }
                }
            }],
            viewConfig: {
                markDirty:false
            }
        });
        
        me.callParent(arguments);
    }
});
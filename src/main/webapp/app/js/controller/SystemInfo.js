/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function () {
    'use strict';

    var pageSize = 10;

    Ext.define('listdata.controller.SystemInfo', {
        extend: 'Ext.app.Controller',

        requires: ['listdata.view.AutoGrid', 'listdata.view.GridParams', 'Ext.toolbar.Paging', 'Ext.util.Point'],

        views: ['AutoGrid'],

        refs: [
            {
                ref: 'container',
                selector: 'listdata-application-container'
            }
        ],

        buildGrid: function (windowParams) {
            var me = this;
            var url = 'rest/dataQuery/data/' + windowParams.id;

            function createWindow(item, title) {
                var panel = Ext.create('Ext.Panel', {
                    title: title,
                    layout: 'fit',
                    closeAction: 'destroy',
                    closable: true,
                    items: item,
                    listeners: {
                        close: function () {
                            if (windowParams.id) {
                                Ext.Ajax.request({
                                    url: 'rest/dataQuery/data/' + windowParams.id,
                                    method: 'DELETE'
                                });
                            }
                        }
                    }
                });
                me.getContainer().add(panel);
                me.getContainer().setActiveTab(panel);
            }

            if (windowParams.error) {
                createWindow({
                    xtype: 'panel',
                    closable: true,
                    html: windowParams.error
                });
            } else {
                Ext.Ajax.request({
                    url: url,
                    method: 'GET',
                    params: {
                        start: 0,
                        limit: pageSize
                    },
                    success: function (response, opts) {
                        var data = Ext.decode(response.responseText);
                        createWindow(Ext.create('listdata.view.AutoGrid', {
                            url: url,
                            initData: data,
                            limit: pageSize
                        }), data.title);
                    },
                    failure: function (response) {
                        createWindow({
                            xtype: 'panel',
                            html: response.responseText
                        }, windowParams.id);
                    }
                });
            }
        },

        loadData: function () {
            var me = this;
            Ext.Ajax.request({
                url: 'rest/dataQuery/id',
                method: 'GET',
                success: function (response) {
                    var data = Ext.decode(response.responseText);
                    Ext.each(data, function (id) {
                        me.buildGrid({
                            id: id
                        });
                    });
                }
            });
        },

        init: function () {
            var me = this;
            me.control({
                'listdata-application-container': {
                    'ux-add-panel': function () {
                        var win = Ext.create('Ext.window.Window', {
                            title: listdata.i18n.get('grid.params'),
                            height: 600,
                            width: 800,
                            layout: 'fit',
                            modal: true,
                            closeAction: 'destroy',
                            items: Ext.create('listdata.view.GridParams', {
                                listeners: {
                                    'ux-cancel': function () {
                                        win.close();
                                    },
                                    'ux-save': function (params) {
                                        Ext.Ajax.request({
                                            url: 'rest/dataQuery/data',
                                            method: 'POST',
                                            params: params,
                                            success: function (response) {
                                                var data = Ext.decode(response.responseText);
                                                me.buildGrid({
                                                    id: data.id
                                                });
                                            }
                                        });
                                        win.close();
                                    }
                                }
                            })
                        }).show();
                    }
                }
            });
            me.loadData();
        }

    });

}());

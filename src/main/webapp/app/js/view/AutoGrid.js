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

    Ext.define('querydata.view.AutoGrid', {
        extend: 'Ext.grid.Panel',
        alias: 'widget.querydata-autogrid',
        constructor: function (config) {
            var me = this;
            var columns = [];
            var fields = [];
            Ext.each(config.initData.columns, function (columnData) {
                columnData.text =  querydata.i18n.get(columnData.text, {}, columnData.text);
                columns.push(columnData);
                fields.push(columnData.dataIndex);
            });
            var store = Ext.create('Ext.data.Store', {
                proxy: {
                    type: 'ajax',
                    url: config.url,
                    reader: {
                        type: 'json',
                        root: 'rows',
                        totalProperty: 'total'
                    }
                },
                autoLoad: false,
                fields: fields,
                remoteSort: true,
                pageSize: config.limit
            });
            store.loadRawData(config.initData);

            config.store = store;
            config.columns = columns;
            config.dockedItems = [
                {
                    xtype: 'pagingtoolbar',
                    store: store,
                    dock: 'bottom',
                    displayInfo: true
                }
            ];

            querydata.view.AutoGrid.superclass.constructor.call(me, config);
        }
    });
}());

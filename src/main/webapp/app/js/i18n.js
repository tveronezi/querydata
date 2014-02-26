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

    // Add your messages to this list.
    // This is a list of extjs Templates, therefore you can use parameters in it.
    var messages = {
        'application.name': 'QueryData',

        'system.info.title': 'System Properties',
        'sysprop.key': 'Key',
        'sysprop.value': 'Value',

        'threads.info.title': 'Threads',
        'thread.id': 'Id',
        'thread.name': 'Name',
        'thread.state': 'State',
        'thread.lock.name': 'Lock',

        'grid.script': 'Script',
        'grid.title': 'Title',
        'grid.params': 'Grid Parameters',

        'cancel': 'Cancel',
        'save': 'Save',

        'dummy': ''
    };

    Ext.define('listdata.i18n', {
        singleton: true,
        get: function (key, values, defaultValue) {
            var template = this.messages[key];
            var cfg = values;
            if (!template) {
                if (defaultValue) {
                    return defaultValue;
                }
                template = this.missing;
                cfg = {
                    key: key
                };
                console.error('Missing i18n message.', key);
            }
            return template.apply(cfg);
        },
        constructor: function () {
            Ext.Object.each(messages, function (key, value) {
                var t = new Ext.Template(value);
                t.compile();
                messages[key] = t;
            });
            this.messages = messages;
            this.missing = new Ext.Template('[!{key}!]');
        }
    });
}());

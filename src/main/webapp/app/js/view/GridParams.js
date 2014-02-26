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

    Ext.define('listdata.view.GridParams', {
        extend: 'Ext.form.Panel',
        alias: 'widget.listdata-autogridparams',
        bodyPadding: 5,
        layout: 'anchor',
        defaults: {
            anchor: '100%'
        },
        defaultType: 'textfield',
        items: [
            {
                fieldLabel: listdata.i18n.get('grid.title'),
                name: 'name',
                allowBlank: false
            },
            {
                fieldLabel: listdata.i18n.get('grid.script'),
                name: 'script',
                xtype: 'textarea',
                height: 400,
                allowBlank: false
            }
        ],
        // Reset and Submit buttons
        buttons: [
            {
                text: listdata.i18n.get('cancel'),
                handler: function () {
                    this.up('listdata-autogridparams').fireEvent('ux-cancel');
                }
            },
            {
                text: listdata.i18n.get('save'),
                formBind: true,
                disabled: true,
                handler: function () {
                    var form = this.up('form').getForm();
                    this.up('listdata-autogridparams').fireEvent('ux-save', form.getValues());
                }
            }
        ]
    });
}());

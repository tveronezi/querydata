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

package listdata.service

import listdata.data.dto.info.DataSetDto

import javax.annotation.PostConstruct
import javax.ejb.*

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@javax.ejb.Singleton
@Startup
class DataSetsServiceImpl {

    private List<DataSetDto> dataSets = []
    private int id = 0

    @PostConstruct
    void init() {
        dataSets << new DataSetDto(
                id: 'ds_sys',
                name: 'System Info',
                script: """
System.properties.each { key, value ->
    infoDto.rows << [
            key: key,
            value: value
    ]
}
infoDto.columns << [
        dataIndex: 'key',
        text: 'sysprop.key'
]
infoDto.columns << [
        dataIndex: 'value',
        text: 'sysprop.value',
        flex: 1
]
"""
        )
        dataSets << new DataSetDto(
                id: 'ds_thread',
                name: 'Threads Info',
                script: """
import java.lang.management.ManagementFactory
def mbean = ManagementFactory.threadMXBean
infoDto.rows = mbean.allThreadIds.collect {
    def threadInfo = mbean.getThreadInfo(it)
    [
            tId: threadInfo.threadId,
            tName: threadInfo.threadName,
            tState: threadInfo.threadState.name(),
            tLockName: threadInfo.lockName
    ]
}
infoDto.columns << [
        dataIndex: 'tId',
        text: 'thread.id',
        type: 'integer',
        hidden: true
]
infoDto.columns << [
        dataIndex: 'tName',
        text: 'thread.name',
        flex: 1
]
infoDto.columns << [
        dataIndex: 'tState',
        text: 'thread.state'
]
infoDto.columns << [
        dataIndex: 'tLockName',
        text: 'thread.lock.name'
]
"""
        )
    }

    @Lock(LockType.READ)
    List getDataSets() {
        dataSets.collect { it }
    }

    @Lock(LockType.READ)
    DataSetDto getDataSet(String id) {
        dataSets.find({
            it.id == id
        })
    }

    @Lock(LockType.WRITE)
    DataSetDto saveDataSet(DataSetDto dto) {
        dto.id = dto.id ?: "ds_${id++}"
        removeDataSet(dto.id) // remove existing one
        dataSets << dto
        dto
    }

    @Lock(LockType.WRITE)
    void removeDataSet(String id) {
        def dto = getDataSet(id)
        if (dto) {
            dataSets.remove(dto)
        }
    }

    @Lock(LockType.READ)
    List<String> getIds() {
        dataSets*.id
    }

}

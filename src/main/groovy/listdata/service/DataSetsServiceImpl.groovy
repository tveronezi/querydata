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

    private DataSetDto loadDtoFromResource(String path) {
        def records = new XmlSlurper().parse(this.class.classLoader.getResourceAsStream(path))
        new DataSetDto(
                id: records.getProperty('id'),
                name: records.getProperty('name'),
                script: records.getProperty('script')
        )
    }

    @PostConstruct
    void init() {
        dataSets << loadDtoFromResource('/scripts/SystemInfo.xml')
        dataSets << loadDtoFromResource('/scripts/ThreadsInfo.xml')
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

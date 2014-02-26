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

import listdata.data.dto.info.InfoDto

import javax.ejb.EJB
import javax.ejb.Stateless
import javax.ejb.TransactionAttribute
import javax.ejb.TransactionAttributeType
import javax.script.ScriptContext
import javax.script.ScriptEngineManager
import javax.script.SimpleScriptContext

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
class SystemInfoServiceImpl {
    private static PAGE_SIZE = 10

    @EJB
    private DataSetsServiceImpl dataSetsService

    private <T> List<T> getPage(List<T> rows, Integer start, Integer pageSize) {
        def startValue = start ?: 0
        if (startValue > rows.size() - 1) {
            return []
        }
        def pageSizeValue = pageSize ?: PAGE_SIZE
        def endIndex = startValue + pageSizeValue
        if (endIndex > rows.size() - 1) {
            endIndex = rows.size() - 1
        }
        rows[startValue..endIndex]
    }

    private getValue(def bean, String fieldName) {
        bean[fieldName]
    }

    private <T> List<T> sort(List<T> rows, String sortColumn, String sortDirection) {
        if (sortColumn) {
            def dir = sortDirection?.toUpperCase() == 'DESC' ? -1 : 1
            return rows.sort { a, b ->
                (getValue(a, sortColumn) <=> getValue(b, sortColumn)) * dir
            }
        }
        rows
    }

    InfoDto getData(Integer start, Integer pageSize, String sortColumn, String sortDirection, String id) {
        def dto = dataSetsService.getDataSet(id)
        if (!dto) {
            return null
        }
        def result = query(start, pageSize, sortColumn, sortDirection, dto.script)
        result.id = dto.id
        result.title = dto.name
        result.script = dto.script
        result
    }

    private InfoDto query(Integer start, Integer pageSize, String sortColumn, String sortDirection, String script) {
        def result = new InfoDto()
        executeScript(script, result)
        result.total = result.rows.size()
        result.rows = sort(result.rows, sortColumn, sortDirection)
        result.rows = getPage(result.rows, start, pageSize)
        result
    }

    private void executeScript(String script, InfoDto dto) {
        def factory = new ScriptEngineManager()
        def engine = factory.getEngineByName('groovy')
        def scriptContext = new SimpleScriptContext()
        scriptContext.setAttribute('infoDto', dto, ScriptContext.ENGINE_SCOPE)
        engine.eval(script, scriptContext)
    }


}

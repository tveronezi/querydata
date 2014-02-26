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

package listdata.rest

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import listdata.data.dto.info.DataSetDto
import listdata.service.DataSetsServiceImpl
import listdata.service.SystemInfoServiceImpl

import javax.inject.Inject
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/dataQuery')
class DataQueryRest {
    @Inject
    SystemInfoServiceImpl service

    @Inject
    DataSetsServiceImpl datasets

    private String getSortValue(String sortParam, String sortKey) {
        if (!sortParam) {
            return null
        }
        def json = new JsonSlurper().parseText(sortParam)
        def result = null
        json.find({
            result = it[sortKey]
            if (result) {
                return true
            }
            false
        })
        result
    }

    @POST
    @Path('/data')
    Response saveDataset(
            @FormParam('id') String id,
            @FormParam('name') String name,
            @FormParam('script') String script
    ) {
        def dto = datasets.saveDataSet(new DataSetDto(
                id: id,
                name: name,
                script: script
        ))
        Response.ok(new JsonBuilder(dto).toString(), MediaType.APPLICATION_JSON).build()
    }

    @DELETE
    @Path('/data/{id}')
    Response deleteDataset(
            @PathParam('id') String id
    ) {
        datasets.removeDataSet(id)
        Response.ok().build()
    }

    @GET
    @Path('/id')
    Response listIds(
            @Context HttpServletResponse response) {
        Response.ok(new JsonBuilder(datasets.ids).toString(), MediaType.APPLICATION_JSON).build()
    }

    @GET
    @Path('/data/{id}')
    Response listData(
            @Context HttpServletResponse response,
            @PathParam('id') String id,
            @QueryParam('start') Integer start,
            @QueryParam('limit') Integer pageSize,
            @QueryParam('sort') String sort) {
        def info = service.getData(start, pageSize, getSortValue(sort, 'property'), getSortValue(sort, 'direction'), id)
        Response.ok(new JsonBuilder(info).toString(), MediaType.APPLICATION_JSON).build()
    }

}
